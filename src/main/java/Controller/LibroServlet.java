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
        libroDAO = new LibroDAO();
    }
    
    /**
     * Maneja las peticiones GET
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        
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
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Muestra la lista de todos los libros (solo para usuarios logueados)
     */
    private void mostrarListaLibros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<LibroDTO> libros = libroDAO.obtenerTodosLibros();
        List<String> categorias = libroDAO.obtenerCategorias();
        
        request.setAttribute("libros", libros);
        request.setAttribute("categorias", categorias);
        request.setAttribute("totalLibros", libros.size());
        
        request.getRequestDispatcher("/libros.jsp").forward(request, response);
    }
    
    /**
     * Muestra el formulario para crear un nuevo libro
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<String> categorias = libroDAO.obtenerCategorias();
        request.setAttribute("categorias", categorias);
        request.setAttribute("accion", "nuevo");
        
        request.getRequestDispatcher("/libro-form.jsp").forward(request, response);
    }
    
    /**
     * Muestra el formulario para editar un libro existente
     */
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            LibroDTO libro = libroDAO.obtenerLibroPorId(id);
            
            if (libro != null) {
                List<String> categorias = libroDAO.obtenerCategorias();
                request.setAttribute("libro", libro);
                request.setAttribute("categorias", categorias);
                request.setAttribute("accion", "editar");
                
                request.getRequestDispatcher("/libro-form.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Libro no encontrado");
                mostrarListaLibros(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de libro inválido");
            mostrarListaLibros(request, response);
        }
    }
    
    /**
     * Crea un nuevo libro
     */
    private void crearLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos del formulario
            String titulo = request.getParameter("titulo");
            String autor = request.getParameter("autor");
            String editorial = request.getParameter("editorial");
            int anio = Integer.parseInt(request.getParameter("anio"));
            String categoria = request.getParameter("categoria");
            boolean disponible = "on".equals(request.getParameter("disponible"));
            
            // Validar datos básicos
            if (titulo == null || titulo.trim().isEmpty() || 
                autor == null || autor.trim().isEmpty()) {
                
                request.setAttribute("error", "Título y autor son obligatorios");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Crear objeto LibroDTO
            LibroDTO libro = new LibroDTO(titulo.trim(), autor.trim(), editorial, anio, categoria, disponible);
            
            // Guardar en la base de datos
            if (libroDAO.crearLibro(libro)) {
                request.setAttribute("mensaje", "Libro creado exitosamente");
                response.sendRedirect(request.getContextPath() + "/libros");
            } else {
                request.setAttribute("error", "Error al crear el libro");
                mostrarFormularioNuevo(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El año debe ser un número válido");
            mostrarFormularioNuevo(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioNuevo(request, response);
        }
    }
    
    /**
     * Actualiza un libro existente
     */
    private void actualizarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos del formulario
            int id = Integer.parseInt(request.getParameter("id"));
            String titulo = request.getParameter("titulo");
            String autor = request.getParameter("autor");
            String editorial = request.getParameter("editorial");
            int anio = Integer.parseInt(request.getParameter("anio"));
            String categoria = request.getParameter("categoria");
            boolean disponible = "on".equals(request.getParameter("disponible"));
            
            // Validar datos básicos
            if (titulo == null || titulo.trim().isEmpty() || 
                autor == null || autor.trim().isEmpty()) {
                
                request.setAttribute("error", "Título y autor son obligatorios");
                mostrarFormularioEditar(request, response);
                return;
            }
            
            // Crear objeto LibroDTO
            LibroDTO libro = new LibroDTO(id, titulo.trim(), autor.trim(), editorial, anio, categoria, disponible);
            
            // Actualizar en la base de datos
            if (libroDAO.actualizarLibro(libro)) {
                request.setAttribute("mensaje", "Libro actualizado exitosamente");
                response.sendRedirect(request.getContextPath() + "/libros");
            } else {
                request.setAttribute("error", "Error al actualizar el libro");
                mostrarFormularioEditar(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID y año deben ser números válidos");
            mostrarFormularioEditar(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioEditar(request, response);
        }
    }
    
    /**
     * Elimina un libro
     */
    private void eliminarLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            if (libroDAO.eliminarLibro(id)) {
                request.setAttribute("mensaje", "Libro eliminado exitosamente");
            } else {
                request.setAttribute("error", "Error al eliminar el libro");
            }
            
            response.sendRedirect(request.getContextPath() + "/libros");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de libro inválido");
            response.sendRedirect(request.getContextPath() + "/libros");
        }
    }
    
    /**
     * Busca libros según criterios (para usuarios autenticados)
     */
    private void buscarLibros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String termino = request.getParameter("termino");
        String tipoBusqueda = request.getParameter("tipo");
        
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
        
        request.setAttribute("libros", libros);
        request.setAttribute("categorias", categorias);
        request.setAttribute("terminoBusqueda", termino);
        request.setAttribute("tipoBusqueda", tipoBusqueda);
        request.setAttribute("totalLibros", libros.size());
        
        request.getRequestDispatcher("/libros.jsp").forward(request, response);
    }
    
    /**
     * Consulta pública de libros (sin necesidad de login)
     */
    private void consultaPublica(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String termino = request.getParameter("termino");
        String tipoBusqueda = request.getParameter("tipo");
        
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
        
        request.setAttribute("libros", libros);
        request.setAttribute("categorias", categorias);
        request.setAttribute("terminoBusqueda", termino);
        request.setAttribute("tipoBusqueda", tipoBusqueda);
        request.setAttribute("totalLibros", libros.size());
        request.setAttribute("consultaPublica", true); // Flag para la vista
        
        request.getRequestDispatcher("/consulta-publica.jsp").forward(request, response);
    }
}