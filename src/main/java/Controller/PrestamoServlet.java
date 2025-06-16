package Controller;

import DAO.PrestamoDAO;
import DAO.LibroDAO;
import DAO.UsuarioDAO;
import DTO.PrestamoDTO;
import DTO.LibroDTO;
import DTO.UsuarioDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet para manejar operaciones con préstamos - Versión mejorada
 * @author HAWLETH
 */
@WebServlet(name = "PrestamoServlet", urlPatterns = {
    "/prestamos", "/prestamo-nuevo", "/prestamo-editar", "/prestamo-devolver", 
    "/prestamo-eliminar", "/mis-prestamos", "/prestamos-vencidos", 
    "/generar-pdf", "/generar-excel"
})
public class PrestamoServlet extends HttpServlet {
    
    private PrestamoDAO prestamoDAO;
    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() throws ServletException {
        System.out.println("🔥 PrestamoServlet.init() - Inicializando servlet");
        prestamoDAO = new PrestamoDAO();
        libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();
        System.out.println("✅ PrestamoServlet.init() - DAOs inicializados");
    }
    
    /**
     * Maneja las peticiones GET
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        System.out.println("🔍 PrestamoServlet.doGet() - Action: " + action);
        
        switch (action) {
            case "/prestamos":
                mostrarListaPrestamos(request, response);
                break;
            case "/prestamo-nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "/prestamo-editar":
                mostrarFormularioEditar(request, response);
                break;
            case "/prestamo-devolver":
                devolverLibro(request, response);
                break;
            case "/prestamo-eliminar":
                eliminarPrestamo(request, response);
                break;
            case "/mis-prestamos":
                mostrarMisPrestamos(request, response);
                break;
            case "/prestamos-vencidos":
                mostrarPrestamosVencidos(request, response);
                break;
            case "/generar-pdf":
                generarComprobantePDF(request, response);
                break;
            case "/generar-excel":
                generarReporteExcel(request, response);
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
        System.out.println("📝 PrestamoServlet.doPost() - Action: " + action);
        
        switch (action) {
            case "/prestamo-nuevo":
                crearPrestamo(request, response);
                break;
            case "/prestamo-editar":
                actualizarPrestamo(request, response);
                break;
            default:
                System.out.println("❌ Acción POST no reconocida: " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Muestra la lista de todos los préstamos (solo para admin)
     */
    private void mostrarListaPrestamos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📚 mostrarListaPrestamos() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para ver préstamos");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            List<PrestamoDTO> prestamos = prestamoDAO.obtenerTodosPrestamos();
            String[] estadisticas = prestamoDAO.obtenerEstadisticasPrestamos();
            
            System.out.println("📊 Datos cargados:");
            System.out.println("   - Total préstamos: " + prestamos.size());
            System.out.println("   - Estadísticas: " + java.util.Arrays.toString(estadisticas));
            
            request.setAttribute("prestamos", prestamos);
            request.setAttribute("totalPrestamos", estadisticas[0]);
            request.setAttribute("prestamosActivos", estadisticas[1]);
            request.setAttribute("prestamosDevueltos", estadisticas[2]);
            request.setAttribute("prestamosVencidos", estadisticas[3]);
            
            request.getRequestDispatcher("/prestamos.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarListaPrestamos(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar la lista de préstamos");
            request.getRequestDispatcher("/prestamos.jsp").forward(request, response);
        }
    }
    
    /**
     * Muestra el formulario para crear un nuevo préstamo
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("➕ mostrarFormularioNuevo() - Iniciando");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener libros disponibles
            List<LibroDTO> librosDisponibles = libroDAO.obtenerLibrosDisponibles();
            System.out.println("📚 Libros disponibles: " + librosDisponibles.size());
            
            // Si es admin, puede prestar a cualquier usuario
            List<UsuarioDTO> usuarios = null;
            if (LoginServlet.usuarioEsAdmin(request)) {
                usuarios = usuarioDAO.obtenerTodosUsuarios();
                System.out.println("👥 Usuarios disponibles: " + usuarios.size());
            }
            
            request.setAttribute("librosDisponibles", librosDisponibles);
            request.setAttribute("usuarios", usuarios);
            
            request.getRequestDispatcher("/prestamo-form.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarFormularioNuevo(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar el formulario");
            request.getRequestDispatcher("/prestamo-form.jsp").forward(request, response);
        }
    }
    
    /**
     * Muestra el formulario para editar un préstamo existente
     */
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("✏️ mostrarFormularioEditar() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para editar préstamos");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de préstamo a editar: " + idStr);
            
            int id = Integer.parseInt(idStr);
            PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(id);
            
            if (prestamo != null) {
                // Obtener listas para los select
                List<LibroDTO> librosDisponibles = libroDAO.obtenerLibrosDisponibles();
                List<UsuarioDTO> usuarios = usuarioDAO.obtenerTodosUsuarios();
                
                request.setAttribute("prestamo", prestamo);
                request.setAttribute("librosDisponibles", librosDisponibles);
                request.setAttribute("usuarios", usuarios);
                request.setAttribute("accion", "editar");
                
                System.out.println("📝 Mostrando formulario editar préstamo ID: " + id);
                request.getRequestDispatcher("/prestamo-form.jsp").forward(request, response);
            } else {
                System.out.println("❌ Préstamo no encontrado con ID: " + id);
                request.getSession().setAttribute("error", "Préstamo no encontrado");
                response.sendRedirect(request.getContextPath() + "/prestamos");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de préstamo inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarFormularioEditar(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error al cargar el formulario de edición");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Muestra los préstamos del usuario logueado
     */
    private void mostrarMisPrestamos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("👤 mostrarMisPrestamos() - Iniciando");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            UsuarioDTO usuario = LoginServlet.obtenerUsuarioLogueado(request);
            List<PrestamoDTO> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(usuario.getId());
            
            System.out.println("📊 Préstamos del usuario " + usuario.getNombre() + ": " + prestamos.size());
            
            request.setAttribute("prestamos", prestamos);
            request.setAttribute("esMisPrestamos", true);
            
            request.getRequestDispatcher("/mis-prestamos.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarMisPrestamos(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar sus préstamos");
            request.getRequestDispatcher("/mis-prestamos.jsp").forward(request, response);
        }
    }
    
    /**
     * Muestra los préstamos vencidos (solo para admin)
     */
    private void mostrarPrestamosVencidos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("⏰ mostrarPrestamosVencidos() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para ver préstamos vencidos");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            List<PrestamoDTO> prestamosVencidos = prestamoDAO.obtenerPrestamosVencidos();
            
            System.out.println("⚠️ Préstamos vencidos encontrados: " + prestamosVencidos.size());
            
            request.setAttribute("prestamos", prestamosVencidos);
            request.setAttribute("esVencidos", true);
            
            request.getRequestDispatcher("/prestamos-vencidos.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarPrestamosVencidos(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar préstamos vencidos");
            request.getRequestDispatcher("/prestamos-vencidos.jsp").forward(request, response);
        }
    }
    
    /**
     * Crea un nuevo préstamo
     */
    private void crearPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📚 crearPrestamo() - Iniciando creación de préstamo");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos del formulario
            String idLibroStr = request.getParameter("idLibro");
            String idUsuarioStr = request.getParameter("idUsuario");
            String fechaDevolucionStr = request.getParameter("fechaDevolucion");
            
            System.out.println("📝 Datos recibidos:");
            System.out.println("   - ID Libro: " + idLibroStr);
            System.out.println("   - ID Usuario: " + idUsuarioStr);
            System.out.println("   - Fecha Devolución: " + fechaDevolucionStr);
            
            // Validar datos básicos
            if (idLibroStr == null || idLibroStr.trim().isEmpty() ||
                fechaDevolucionStr == null || fechaDevolucionStr.trim().isEmpty()) {
                
                System.out.println("❌ Datos incompletos");
                request.setAttribute("error", "Los campos libro y fecha de devolución son obligatorios");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            int idLibro = Integer.parseInt(idLibroStr);
            int idUsuario;
            
            // Si es admin, puede elegir el usuario; si no, es el usuario logueado
            if (LoginServlet.usuarioEsAdmin(request)) {
                if (idUsuarioStr == null || idUsuarioStr.trim().isEmpty()) {
                    System.out.println("❌ Usuario no seleccionado (admin)");
                    request.setAttribute("error", "Debe seleccionar un usuario");
                    mostrarFormularioNuevo(request, response);
                    return;
                }
                idUsuario = Integer.parseInt(idUsuarioStr);
            } else {
                UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
                idUsuario = usuarioLogueado.getId();
            }
            
            // Validar que el libro esté disponible
            LibroDTO libro = libroDAO.obtenerLibroPorId(idLibro);
            if (libro == null || !libro.isDisponible()) {
                System.out.println("❌ Libro no disponible: " + idLibro);
                request.setAttribute("error", "El libro seleccionado no está disponible");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Verificar que el libro no esté ya prestado
            if (prestamoDAO.libroEstaPrestado(idLibro)) {
                System.out.println("❌ Libro ya prestado: " + idLibro);
                request.setAttribute("error", "El libro ya está prestado a otro usuario");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Crear fechas
            Date fechaPrestamo = Date.valueOf(LocalDate.now());
            Date fechaDevolucion = Date.valueOf(fechaDevolucionStr);
            
            // Validar que la fecha de devolución sea futura
            if (fechaDevolucion.before(fechaPrestamo)) {
                System.out.println("❌ Fecha de devolución en el pasado");
                request.setAttribute("error", "La fecha de devolución debe ser posterior a hoy");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Validar que no exceda el límite de días (30 días)
            long diferenciaDias = (fechaDevolucion.getTime() - fechaPrestamo.getTime()) / (1000 * 60 * 60 * 24);
            if (diferenciaDias > 30) {
                System.out.println("❌ Período de préstamo excede 30 días: " + diferenciaDias);
                request.setAttribute("error", "El período de préstamo no puede exceder 30 días");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Crear préstamo
            PrestamoDTO prestamo = new PrestamoDTO(idUsuario, idLibro, fechaPrestamo, fechaDevolucion, false);
            
            System.out.println("💾 Guardando préstamo en base de datos...");
            
            if (prestamoDAO.crearPrestamo(prestamo)) {
                // Marcar el libro como no disponible
                libroDAO.cambiarDisponibilidad(idLibro, false);
                
                System.out.println("✅ Préstamo creado exitosamente");
                request.getSession().setAttribute("mensaje", "Préstamo creado exitosamente para el libro: " + libro.getTitulo());
                
                // Redirigir según el rol
                if (LoginServlet.usuarioEsAdmin(request)) {
                    response.sendRedirect(request.getContextPath() + "/prestamos");
                } else {
                    response.sendRedirect(request.getContextPath() + "/mis-prestamos");
                }
            } else {
                System.out.println("❌ Error al guardar préstamo en BD");
                request.setAttribute("error", "Error al crear el préstamo en la base de datos");
                mostrarFormularioNuevo(request, response);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Datos numéricos inválidos: " + e.getMessage());
            request.setAttribute("error", "Datos inválidos en el formulario");
            mostrarFormularioNuevo(request, response);
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en crearPrestamo(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioNuevo(request, response);
        }
    }
    
    /**
     * Actualiza un préstamo existente
     */
    private void actualizarPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("✏️ actualizarPrestamo() - Iniciando actualización de préstamo");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para editar préstamos");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            String fechaDevolucionStr = request.getParameter("fechaDevolucion");
            
            System.out.println("📝 Datos recibidos para actualización:");
            System.out.println("   - ID: " + idStr);
            System.out.println("   - Nueva fecha devolución: " + fechaDevolucionStr);
            
            // Validar ID
            int id = Integer.parseInt(idStr);
            
            // Validar fecha
            if (fechaDevolucionStr == null || fechaDevolucionStr.trim().isEmpty()) {
                request.setAttribute("error", "La fecha de devolución es obligatoria");
                mostrarFormularioEditar(request, response);
                return;
            }
            
            Date nuevaFechaDevolucion = Date.valueOf(fechaDevolucionStr);
            
            // Validar que la fecha sea futura (solo para préstamos no devueltos)
            PrestamoDTO prestamoActual = prestamoDAO.obtenerPrestamoPorId(id);
            if (prestamoActual != null && !prestamoActual.isDevuelto()) {
                Date hoy = Date.valueOf(LocalDate.now());
                if (nuevaFechaDevolucion.before(hoy)) {
                    request.setAttribute("error", "La fecha de devolución debe ser posterior a hoy para préstamos activos");
                    mostrarFormularioEditar(request, response);
                    return;
                }
            }
            
            System.out.println("💾 Actualizando fecha de devolución...");
            
            if (prestamoDAO.actualizarFechaDevolucion(id, nuevaFechaDevolucion)) {
                System.out.println("✅ Préstamo actualizado exitosamente");
                request.getSession().setAttribute("mensaje", "Fecha de devolución actualizada exitosamente");
                response.sendRedirect(request.getContextPath() + "/prestamos");
            } else {
                System.out.println("❌ Error al actualizar préstamo en BD");
                request.setAttribute("error", "Error al actualizar el préstamo");
                mostrarFormularioEditar(request, response);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID inválido: " + request.getParameter("id"));
            request.setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en actualizarPrestamo(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Marca un préstamo como devuelto
     */
    private void devolverLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🔄 devolverLibro() - Iniciando devolución");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de préstamo a devolver: " + idStr);
            
            int idPrestamo = Integer.parseInt(idStr);
            
            // Obtener información del préstamo
            PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(idPrestamo);
            
            if (prestamo == null) {
                System.out.println("❌ Préstamo no encontrado: " + idPrestamo);
                request.getSession().setAttribute("error", "Préstamo no encontrado");
            } else if (prestamo.isDevuelto()) {
                System.out.println("❌ Libro ya devuelto: " + idPrestamo);
                request.getSession().setAttribute("error", "Este libro ya fue devuelto");
            } else {
                // Verificar permisos: admin puede devolver cualquier libro, 
                // usuario normal solo sus propios préstamos
                UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
                
                if (!LoginServlet.usuarioEsAdmin(request) && 
                    prestamo.getIdUsuario() != usuarioLogueado.getId()) {
                    System.out.println("❌ Sin permisos para devolver este libro");
                    request.getSession().setAttribute("error", "No tienes permisos para devolver este libro");
                } else {
                    // Marcar como devuelto
                    Date fechaDevolucion = Date.valueOf(LocalDate.now());
                    
                    System.out.println("💾 Marcando como devuelto...");
                    
                    if (prestamoDAO.marcarComoDevuelto(idPrestamo, fechaDevolucion)) {
                        // Marcar el libro como disponible
                        libroDAO.cambiarDisponibilidad(prestamo.getIdLibro(), true);
                        
                        System.out.println("✅ Libro devuelto exitosamente");
                        request.getSession().setAttribute("mensaje", "Libro devuelto exitosamente");
                    } else {
                        System.out.println("❌ Error al procesar devolución en BD");
                        request.getSession().setAttribute("error", "Error al procesar la devolución");
                    }
                }
            }
            
            // Redirigir según el contexto
            if (LoginServlet.usuarioEsAdmin(request)) {
                response.sendRedirect(request.getContextPath() + "/prestamos");
            } else {
                response.sendRedirect(request.getContextPath() + "/mis-prestamos");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de préstamo inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en devolverLibro(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error inesperado al procesar la devolución");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Elimina un préstamo (solo admin)
     */
    private void eliminarPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🗑️ eliminarPrestamo() - Iniciando eliminación");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para eliminar préstamos");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de préstamo a eliminar: " + idStr);
            
            int id = Integer.parseInt(idStr);
            
            // Obtener información del préstamo antes de eliminarlo
            PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(id);
            
            if (prestamo != null && !prestamo.isDevuelto()) {
                // Si el préstamo no estaba devuelto, hacer el libro disponible
                libroDAO.cambiarDisponibilidad(prestamo.getIdLibro(), true);
                System.out.println("📚 Libro marcado como disponible: " + prestamo.getIdLibro());
            }
            
            System.out.println("💾 Eliminando préstamo de BD...");
            
            if (prestamoDAO.eliminarPrestamo(id)) {
                System.out.println("✅ Préstamo eliminado exitosamente");
                request.getSession().setAttribute("mensaje", "Préstamo eliminado exitosamente");
            } else {
                System.out.println("❌ Error al eliminar préstamo de BD");
                request.getSession().setAttribute("error", "Error al eliminar el préstamo");
            }
            
            response.sendRedirect(request.getContextPath() + "/prestamos");
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de préstamo inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en eliminarPrestamo(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error inesperado al eliminar el préstamo");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Genera comprobante de préstamo en PDF
     */
    private void generarComprobantePDF(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📄 generarComprobantePDF() - Iniciando");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de préstamo para PDF: " + idStr);
            
            int idPrestamo = Integer.parseInt(idStr);
            PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(idPrestamo);
            
            if (prestamo == null) {
                System.out.println("❌ Préstamo no encontrado: " + idPrestamo);
                request.getSession().setAttribute("error", "Préstamo no encontrado");
                response.sendRedirect(request.getContextPath() + "/prestamos");
                return;
            }
            
            // Verificar permisos
            UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
            if (!LoginServlet.usuarioEsAdmin(request) && 
                prestamo.getIdUsuario() != usuarioLogueado.getId()) {
                System.out.println("❌ Sin permisos para generar comprobante");
                request.getSession().setAttribute("error", "No tienes permisos para generar este comprobante");
                response.sendRedirect(request.getContextPath() + "/prestamos");
                return;
            }
            
            // Generar comprobante HTML (como placeholder para PDF)
            System.out.println("📄 Generando comprobante HTML...");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(generarComprobanteHTML(prestamo));
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de préstamo inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en generarComprobantePDF(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error al generar el comprobante");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Genera reporte de inventario en Excel
     */
    private void generarReporteExcel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📊 generarReporteExcel() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para generar reportes");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos para el reporte
            List<PrestamoDTO> prestamos = prestamoDAO.obtenerTodosPrestamos();
            String[] estadisticas = prestamoDAO.obtenerEstadisticasPrestamos();
            
            System.out.println("📊 Generando reporte con " + prestamos.size() + " préstamos");
            
            // Generar reporte HTML (como placeholder para Excel)
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(generarReporteHTML(prestamos, estadisticas));
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en generarReporteExcel(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error al generar el reporte");
            response.sendRedirect(request.getContextPath() + "/admin-dashboard");
        }
    }
    
    /**
     * Genera el HTML del comprobante de préstamo
     */
    private String generarComprobanteHTML(PrestamoDTO prestamo) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Comprobante de Préstamo</title>");
        html.append("<style>");
        html.append("body{font-family:Arial,sans-serif;margin:40px;background:#f8f9fa;}");
        html.append(".header{text-align:center;border-bottom:2px solid #007bff;padding-bottom:20px;margin-bottom:30px;}");
        html.append(".logo{font-size:24px;font-weight:bold;color:#007bff;margin-bottom:10px;}");
        html.append(".comprobante{background:white;padding:30px;border-radius:10px;box-shadow:0 5px 15px rgba(0,0,0,0.1);}");
        html.append(".info-row{display:flex;justify-content:space-between;margin-bottom:15px;padding:10px;background:#f8f9fa;border-radius:5px;}");
        html.append(".label{font-weight:bold;color:#495057;}");
        html.append(".value{color:#212529;}");
        html.append(".footer{text-align:center;margin-top:30px;padding-top:20px;border-top:1px solid #dee2e6;color:#6c757d;}");
        html.append(".status{padding:5px 15px;border-radius:20px;color:white;font-weight:bold;}");
        html.append(".status-activo{background:#ffc107;}");
        html.append(".status-devuelto{background:#28a745;}");
        html.append(".status-vencido{background:#dc3545;}");
        html.append("@media print{body{margin:0;background:white;}.comprobante{box-shadow:none;}}");
        html.append("</style></head><body>");
        
        html.append("<div class='comprobante'>");
        html.append("<div class='header'>");
        html.append("<div class='logo'>📚 Sistema de Biblioteca ADSO</div>");
        html.append("<h2>📄 Comprobante de Préstamo</h2>");
        html.append("<p>Comprobante oficial de préstamo de libro</p>");
        html.append("</div>");
        
        html.append("<div class='info-row'>");
        html.append("<span class='label'>🆔 ID Préstamo:</span>");
        html.append("<span class='value'>#").append(prestamo.getId()).append("</span>");
        html.append("</div>");
        
        html.append("<div class='info-row'>");
        html.append("<span class='label'>📖 Libro:</span>");
        html.append("<span class='value'>").append(prestamo.getTituloLibro()).append("</span>");
        html.append("</div>");
        
        html.append("<div class='info-row'>");
        html.append("<span class='label'>✍️ Autor:</span>");
        html.append("<span class='value'>").append(prestamo.getAutorLibro()).append("</span>");
        html.append("</div>");
        
        html.append("<div class='info-row'>");
        html.append("<span class='label'>👤 Usuario:</span>");
        html.append("<span class='value'>").append(prestamo.getNombreUsuario()).append("</span>");
        html.append("</div>");
        
        html.append("<div class='info-row'>");
        html.append("<span class='label'>📅 Fecha Préstamo:</span>");
        html.append("<span class='value'>").append(prestamo.getFechaPrestamo()).append("</span>");
        html.append("</div>");
        
        html.append("<div class='info-row'>");
        html.append("<span class='label'>🔄 Fecha Devolución:</span>");
        html.append("<span class='value'>").append(prestamo.getFechaDevolucion()).append("</span>");
        html.append("</div>");
        
        html.append("<div class='info-row'>");
        html.append("<span class='label'>📊 Estado:</span>");
        if (prestamo.isDevuelto()) {
            html.append("<span class='status status-devuelto'>✅ Devuelto</span>");
        } else {
            // Verificar si está vencido
            java.util.Date hoy = new java.util.Date();
            if (prestamo.getFechaDevolucion().before(hoy)) {
                html.append("<span class='status status-vencido'>⚠️ Vencido</span>");
            } else {
                html.append("<span class='status status-activo'>⏰ Activo</span>");
            }
        }
        html.append("</div>");
        
        html.append("<div class='footer'>");
        html.append("<p><strong>📍 Biblioteca ADSO</strong></p>");
        html.append("<p>Tecnología en Análisis y Desarrollo de Software</p>");
        html.append("<p>📧 biblioteca@adso.edu.co | ☎️ (123) 456-7890</p>");
        html.append("<p style='font-size:12px;margin-top:20px;'>Comprobante generado el: ").append(new java.util.Date()).append("</p>");
        html.append("<div style='margin-top:20px;'>");
        html.append("<button onclick='window.print()' style='background:#007bff;color:white;border:none;padding:10px 20px;border-radius:5px;margin-right:10px;cursor:pointer;'>🖨️ Imprimir</button>");
        html.append("<button onclick='history.back()' style='background:#6c757d;color:white;border:none;padding:10px 20px;border-radius:5px;cursor:pointer;'>← Volver</button>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</body></html>");
        
        return html.toString();
    }
    
    /**
     * Genera el HTML del reporte de inventario
     */
    private String generarReporteHTML(List<PrestamoDTO> prestamos, String[] estadisticas) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Reporte de Préstamos</title>");
        html.append("<style>");
        html.append("body{font-family:Arial,sans-serif;margin:20px;background:#f8f9fa;}");
        html.append(".reporte{background:white;padding:30px;border-radius:10px;box-shadow:0 5px 15px rgba(0,0,0,0.1);}");
        html.append(".header{text-align:center;border-bottom:2px solid #28a745;padding-bottom:20px;margin-bottom:30px;}");
        html.append(".stats{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:20px;margin-bottom:30px;}");
        html.append(".stat-card{background:#f8f9fa;padding:20px;border-radius:10px;text-align:center;border-left:4px solid #28a745;}");
        html.append(".stat-number{font-size:2em;font-weight:bold;color:#28a745;}");
        html.append(".stat-label{color:#6c757d;margin-top:5px;}");
        html.append("table{width:100%;border-collapse:collapse;margin-top:20px;}");
        html.append("th,td{padding:12px;text-align:left;border-bottom:1px solid #ddd;}");
        html.append("th{background:#28a745;color:white;}");
        html.append("tr:hover{background:#f5f5f5;}");
        html.append(".badge{padding:5px 10px;border-radius:15px;font-size:0.8em;font-weight:bold;}");
        html.append(".badge-activo{background:#ffc107;color:#000;}");
        html.append(".badge-devuelto{background:#28a745;color:white;}");
        html.append(".badge-vencido{background:#dc3545;color:white;}");
        html.append("@media print{body{margin:0;background:white;}.reporte{box-shadow:none;}}");
        html.append("</style></head><body>");
        
        html.append("<div class='reporte'>");
        html.append("<div class='header'>");
        html.append("<h1>📊 Reporte de Préstamos - Sistema de Biblioteca ADSO</h1>");
        html.append("<p>Reporte generado el: ").append(new java.util.Date()).append("</p>");
        html.append("</div>");
        
        // Estadísticas
        html.append("<div class='stats'>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(estadisticas[0]).append("</div>");
        html.append("<div class='stat-label'>📚 Total Préstamos</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(estadisticas[1]).append("</div>");
        html.append("<div class='stat-label'>⏰ Activos</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(estadisticas[2]).append("</div>");
        html.append("<div class='stat-label'>✅ Devueltos</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(estadisticas[3]).append("</div>");
        html.append("<div class='stat-label'>⚠️ Vencidos</div>");
        html.append("</div>");
        html.append("</div>");
        
        // Tabla de préstamos
        html.append("<h3>📋 Detalle de Préstamos</h3>");
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Usuario</th>");
        html.append("<th>Libro</th>");
        html.append("<th>Autor</th>");
        html.append("<th>Fecha Préstamo</th>");
        html.append("<th>Fecha Devolución</th>");
        html.append("<th>Estado</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        
        java.util.Date hoy = new java.util.Date();
        for (PrestamoDTO prestamo : prestamos) {
            html.append("<tr>");
            html.append("<td>#").append(prestamo.getId()).append("</td>");
            html.append("<td>").append(prestamo.getNombreUsuario()).append("</td>");
            html.append("<td>").append(prestamo.getTituloLibro()).append("</td>");
            html.append("<td>").append(prestamo.getAutorLibro()).append("</td>");
            html.append("<td>").append(prestamo.getFechaPrestamo()).append("</td>");
            html.append("<td>").append(prestamo.getFechaDevolucion()).append("</td>");
            html.append("<td>");
            
            if (prestamo.isDevuelto()) {
                html.append("<span class='badge badge-devuelto'>Devuelto</span>");
            } else if (prestamo.getFechaDevolucion().before(hoy)) {
                html.append("<span class='badge badge-vencido'>Vencido</span>");
            } else {
                html.append("<span class='badge badge-activo'>Activo</span>");
            }
            
            html.append("</td>");
            html.append("</tr>");
        }
        
        html.append("</tbody>");
        html.append("</table>");
        
        // Footer
        html.append("<div style='text-align:center;margin-top:40px;padding-top:20px;border-top:1px solid #dee2e6;color:#6c757d;'>");
        html.append("<p><strong>Sistema de Biblioteca ADSO</strong></p>");
        html.append("<p>Tecnología en Análisis y Desarrollo de Software</p>");
        html.append("<div style='margin-top:20px;'>");
        html.append("<button onclick='window.print()' style='background:#28a745;color:white;border:none;padding:10px 20px;border-radius:5px;margin-right:10px;cursor:pointer;'>🖨️ Imprimir Reporte</button>");
        html.append("<button onclick='history.back()' style='background:#6c757d;color:white;border:none;padding:10px 20px;border-radius:5px;cursor:pointer;'>← Volver al Dashboard</button>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</body></html>");
        
        return html.toString();
    }
}