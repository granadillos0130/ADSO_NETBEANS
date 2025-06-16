package Controller;

import DAO.LibroDAO;
import DTO.LibroDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet para manejar operaciones con libros
 * @author HAWLETH
 */
@WebServlet(name = "LibroServlet", urlPatterns = {
    "/libros", "/libro-nuevo", "/libro-editar", "/libro-eliminar", 
    "/libros-buscar", "/consulta-publica"
})
public class LibroServlet extends HttpServlet {
    
    private LibroDAO libroDAO;
    
    @Override
    public void init() throws ServletException {
        System.out.println("🔥 LibroServlet.init() - Inicializando servlet");
        libroDAO = new LibroDAO();
        System.out.println("✅ LibroServlet.init() - LibroDAO inicializado");
    }
    
    /**
     * Maneja las peticiones GET
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        System.out.println("🔍 LibroServlet.doGet() - Action: " + action);
        
        switch (action) {
            case "/libros":
                mostrarListaLibros(request, response);
                break;
            case "/libro-nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "/libro-editar":
                mostrarFormularioEditar(request, response);
                break;
            case "/libro-eliminar":
                eliminarLibro(request, response);
                break;
            case "/libros-buscar":
                buscarLibros(request, response);
                break;
            case "/consulta-publica":
                consultaPublica(request, response);
                break;
            default:
                System.out.println("❌ Acción no reconocida: " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Maneja las peticiones POST
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        System.out.println("📝 LibroServlet.doPost() - Action: " + action);
        
        switch (action) {
            case "/libro-nuevo":
                crearLibro(request, response);
                break;
            case "/libro-editar":
                actualizarLibro(request, response);
                break;
            case "/libros-buscar":
                buscarLibros(request, response);
                break;
            case "/consulta-publica":
                consultaPublica(request, response);
                break;
            default:
                System.out.println("❌ Acción POST no reconocida: " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Muestra la lista de todos los libros (solo para usuarios logueados)
     */
    private void mostrarListaLibros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📚 mostrarListaLibros() - Iniciando");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            List<LibroDTO> libros = libroDAO.obtenerTodosLibros();
            List<String> categorias = libroDAO.obtenerCategorias();
            
            System.out.println("📊 Datos cargados:");
            System.out.println("   - Total libros: " + libros.size());
            System.out.println("   - Total categorías: " + categorias.size());
            
            request.setAttribute("libros", libros);
            request.setAttribute("categorias", categorias);
            request.setAttribute("totalLibros", libros.size());
            
            request.getRequestDispatcher("/libros.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarListaLibros(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar la lista de libros");
            request.getRequestDispatcher("/libros.jsp").forward(request, response);
        }
    }
    
    /**
     * Muestra el formulario para crear un nuevo libro
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("➕ mostrarFormularioNuevo() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para crear libros");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            List<String> categorias = libroDAO.obtenerCategorias();
            request.setAttribute("categorias", categorias);
            request.setAttribute("accion", "nuevo");
            
            System.out.println("📝 Mostrando formulario nuevo libro");
            request.getRequestDispatcher("/libro-form.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarFormularioNuevo(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar el formulario");
            request.getRequestDispatcher("/libro-form.jsp").forward(request, response);
        }
    }
    
    /**
     * Muestra el formulario para editar un libro existente
     */
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("✏️ mostrarFormularioEditar() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para editar libros");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de libro a editar: " + idStr);
            
            int id = Integer.parseInt(idStr);
            LibroDTO libro = libroDAO.obtenerLibroPorId(id);
            
            if (libro != null) {
                List<String> categorias = libroDAO.obtenerCategorias();
                request.setAttribute("libro", libro);
                request.setAttribute("categorias", categorias);
                request.setAttribute("accion", "editar");
                
                System.out.println("📝 Mostrando formulario editar libro: " + libro.getTitulo());
                request.getRequestDispatcher("/libro-form.jsp").forward(request, response);
            } else {
                System.out.println("❌ Libro no encontrado con ID: " + id);
                request.getSession().setAttribute("error", "Libro no encontrado");
                response.sendRedirect(request.getContextPath() + "/libros");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de libro inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de libro inválido");
            response.sendRedirect(request.getContextPath() + "/libros");
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarFormularioEditar(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error al cargar el formulario de edición");
            response.sendRedirect(request.getContextPath() + "/libros");
        }
    }
    
    /**
     * Crea un nuevo libro
     */
    private void crearLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📚 crearLibro() - Iniciando creación de libro");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para crear libros");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos del formulario
            String titulo = request.getParameter("titulo");
            String autor = request.getParameter("autor");
            String editorial = request.getParameter("editorial");
            String anioStr = request.getParameter("anio");
            String categoria = request.getParameter("categoria");
            String categoriaPersonalizada = request.getParameter("categoriaPersonalizada");
            boolean disponible = "on".equals(request.getParameter("disponible"));
            
            System.out.println("📝 Datos recibidos:");
            System.out.println("   - Título: " + titulo);
            System.out.println("   - Autor: " + autor);
            System.out.println("   - Editorial: " + editorial);
            System.out.println("   - Año: " + anioStr);
            System.out.println("   - Categoría: " + categoria);
            System.out.println("   - Disponible: " + disponible);
            
            // Validar datos básicos
            if (titulo == null || titulo.trim().isEmpty() || 
                autor == null || autor.trim().isEmpty()) {
                
                System.out.println("❌ Datos incompletos: título o autor vacío");
                request.setAttribute("error", "Título y autor son obligatorios");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Procesar año
            int anio = 0;
            if (anioStr != null && !anioStr.trim().isEmpty()) {
                try {
                    anio = Integer.parseInt(anioStr);
                    if (anio < 1000 || anio > 2030) {
                        System.out.println("❌ Año fuera de rango: " + anio);
                        request.setAttribute("error", "El año debe estar entre 1000 y 2030");
                        mostrarFormularioNuevo(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Año inválido: " + anioStr);
                    request.setAttribute("error", "El año debe ser un número válido");
                    mostrarFormularioNuevo(request, response);
                    return;
                }
            }
            
            // Procesar categoría
            if ("Otro".equals(categoria) && categoriaPersonalizada != null && !categoriaPersonalizada.trim().isEmpty()) {
                categoria = categoriaPersonalizada.trim();
                System.out.println("📂 Usando categoría personalizada: " + categoria);
            }
            
            // Crear objeto LibroDTO
            LibroDTO libro = new LibroDTO(
                titulo.trim(), 
                autor.trim(), 
                editorial != null ? editorial.trim() : "", 
                anio, 
                categoria != null ? categoria.trim() : "", 
                disponible
            );
            
            System.out.println("💾 Guardando libro en base de datos...");
            
            // Guardar en la base de datos
            if (libroDAO.crearLibro(libro)) {
                System.out.println("✅ Libro creado exitosamente");
                request.getSession().setAttribute("mensaje", "Libro creado exitosamente");
                response.sendRedirect(request.getContextPath() + "/libros");
            } else {
                System.out.println("❌ Error al guardar libro en BD");
                request.setAttribute("error", "Error al crear el libro. Verifique que el título no esté duplicado.");
                mostrarFormularioNuevo(request, response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en crearLibro(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioNuevo(request, response);
        }
    }
    
    /**
     * Actualiza un libro existente
     */
    private void actualizarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📝 actualizarLibro() - Iniciando actualización de libro");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para editar libros");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos del formulario
            String idStr = request.getParameter("id");
            String titulo = request.getParameter("titulo");
            String autor = request.getParameter("autor");
            String editorial = request.getParameter("editorial");
            String anioStr = request.getParameter("anio");
            String categoria = request.getParameter("categoria");
            String categoriaPersonalizada = request.getParameter("categoriaPersonalizada");
            boolean disponible = "on".equals(request.getParameter("disponible"));
            
            System.out.println("📝 Datos recibidos para actualización:");
            System.out.println("   - ID: " + idStr);
            System.out.println("   - Título: " + titulo);
            System.out.println("   - Autor: " + autor);
            System.out.println("   - Disponible: " + disponible);
            
            // Validar ID
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ ID inválido: " + idStr);
                request.setAttribute("error", "ID de libro inválido");
                mostrarListaLibros(request, response);
                return;
            }
            
            // Validar datos básicos
            if (titulo == null || titulo.trim().isEmpty() || 
                autor == null || autor.trim().isEmpty()) {
                
                System.out.println("❌ Datos incompletos: título o autor vacío");
                request.setAttribute("error", "Título y autor son obligatorios");
                request.setAttribute("id", id);
                mostrarFormularioEditar(request, response);
                return;
            }
            
            // Procesar año
            int anio = 0;
            if (anioStr != null && !anioStr.trim().isEmpty()) {
                try {
                    anio = Integer.parseInt(anioStr);
                    if (anio < 1000 || anio > 2030) {
                        System.out.println("❌ Año fuera de rango: " + anio);
                        request.setAttribute("error", "El año debe estar entre 1000 y 2030");
                        request.setAttribute("id", id);
                        mostrarFormularioEditar(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Año inválido: " + anioStr);
                    request.setAttribute("error", "El año debe ser un número válido");
                    request.setAttribute("id", id);
                    mostrarFormularioEditar(request, response);
                    return;
                }
            }
            
            // Procesar categoría
            if ("Otro".equals(categoria) && categoriaPersonalizada != null && !categoriaPersonalizada.trim().isEmpty()) {
                categoria = categoriaPersonalizada.trim();
                System.out.println("📂 Usando categoría personalizada: " + categoria);
            }
            
            // Crear objeto LibroDTO
            LibroDTO libro = new LibroDTO(
                id,
                titulo.trim(), 
                autor.trim(), 
                editorial != null ? editorial.trim() : "", 
                anio, 
                categoria != null ? categoria.trim() : "", 
                disponible
            );
            
            System.out.println("💾 Actualizando libro en base de datos...");
            
            // Actualizar en la base de datos
            if (libroDAO.actualizarLibro(libro)) {
                System.out.println("✅ Libro actualizado exitosamente");
                request.getSession().setAttribute("mensaje", "Libro actualizado exitosamente");
                response.sendRedirect(request.getContextPath() + "/libros");
            } else {
                System.out.println("❌ Error al actualizar libro en BD");
                request.setAttribute("error", "Error al actualizar el libro");
                request.setAttribute("id", id);
                mostrarFormularioEditar(request, response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en actualizarLibro(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioEditar(request, response);
        }
    }
    
    /**
     * Elimina un libro
     */
    private void eliminarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🗑️ eliminarLibro() - Iniciando eliminación de libro");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para eliminar libros");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de libro a eliminar: " + idStr);
            
            int id = Integer.parseInt(idStr);
            
            // Verificar si el libro existe y obtener información
            LibroDTO libro = libroDAO.obtenerLibroPorId(id);
            if (libro == null) {
                System.out.println("❌ Libro no encontrado con ID: " + id);
                request.getSession().setAttribute("error", "Libro no encontrado");
                response.sendRedirect(request.getContextPath() + "/libros");
                return;
            }
            
            System.out.println("📖 Eliminando libro: " + libro.getTitulo());
            
            if (libroDAO.eliminarLibro(id)) {
                System.out.println("✅ Libro eliminado exitosamente");
                request.getSession().setAttribute("mensaje", "Libro '" + libro.getTitulo() + "' eliminado exitosamente");
            } else {
                System.out.println("❌ Error al eliminar libro de BD");
                request.getSession().setAttribute("error", "Error al eliminar el libro. Puede que tenga préstamos asociados.");
            }
            
            response.sendRedirect(request.getContextPath() + "/libros");
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de libro inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de libro inválido");
            response.sendRedirect(request.getContextPath() + "/libros");
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en eliminarLibro(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error inesperado al eliminar el libro");
            response.sendRedirect(request.getContextPath() + "/libros");
        }
    }
    
    /**
     * Busca libros según criterios (para usuarios autenticados)
     */
    private void buscarLibros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🔍 buscarLibros() - Iniciando búsqueda");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado para búsqueda");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String termino = request.getParameter("termino");
            String tipoBusqueda = request.getParameter("tipo");
            
            System.out.println("🔍 Búsqueda:");
            System.out.println("   - Término: " + termino);
            System.out.println("   - Tipo: " + tipoBusqueda);
            
            List<LibroDTO> libros;
            
            if (termino != null && !termino.trim().isEmpty()) {
                switch (tipoBusqueda) {
                    case "titulo":
                        libros = libroDAO.buscarPorTitulo(termino.trim());
                        break;
                    case "autor":
                        libros = libroDAO.buscarPorAutor(termino.trim());
                        break;
                    case "categoria":
                        libros = libroDAO.buscarPorCategoria(termino.trim());
                        break;
                    default:
                        libros = libroDAO.busquedaGeneral(termino.trim());
                        break;
                }
            } else {
                libros = libroDAO.obtenerTodosLibros();
            }
            
            List<String> categorias = libroDAO.obtenerCategorias();
            
            System.out.println("📊 Resultados: " + libros.size() + " libros encontrados");
            
            request.setAttribute("libros", libros);
            request.setAttribute("categorias", categorias);
            request.setAttribute("terminoBusqueda", termino);
            request.setAttribute("tipoBusqueda", tipoBusqueda);
            request.setAttribute("totalLibros", libros.size());
            
            request.getRequestDispatcher("/libros.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en buscarLibros(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al realizar la búsqueda");
            request.getRequestDispatcher("/libros.jsp").forward(request, response);
        }
    }
    
    /**
     * Consulta pública de libros (sin necesidad de login)
     */
    private void consultaPublica(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🌐 consultaPublica() - Iniciando consulta pública");
        
        try {
            String termino = request.getParameter("termino");
            String tipoBusqueda = request.getParameter("tipo");
            
            System.out.println("🔍 Consulta pública:");
            System.out.println("   - Término: " + termino);
            System.out.println("   - Tipo: " + tipoBusqueda);
            
            List<LibroDTO> libros;
            
            if (termino != null && !termino.trim().isEmpty()) {
                switch (tipoBusqueda) {
                    case "titulo":
                        libros = libroDAO.buscarPorTitulo(termino.trim());
                        break;
                    case "autor":
                        libros = libroDAO.buscarPorAutor(termino.trim());
                        break;
                    case "categoria":
                        libros = libroDAO.buscarPorCategoria(termino.trim());
                        break;
                    default:
                        libros = libroDAO.busquedaGeneral(termino.trim());
                        break;
                }
            } else {
                libros = libroDAO.obtenerLibrosDisponibles(); // Solo mostrar disponibles
            }
            
            List<String> categorias = libroDAO.obtenerCategorias();
            
            System.out.println("📊 Resultados públicos: " + libros.size() + " libros encontrados");
            
            request.setAttribute("libros", libros);
            request.setAttribute("categorias", categorias);
            request.setAttribute("terminoBusqueda", termino);
            request.setAttribute("tipoBusqueda", tipoBusqueda);
            request.setAttribute("totalLibros", libros.size());
            request.setAttribute("consultaPublica", true); // Flag para la vista
            
            request.getRequestDispatcher("/consulta-publica.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en consultaPublica(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al realizar la consulta");
            request.getRequestDispatcher("/consulta-publica.jsp").forward(request, response);
        }
    }
}