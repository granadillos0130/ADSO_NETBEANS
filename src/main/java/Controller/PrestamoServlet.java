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
 * Servlet para manejar operaciones con préstamos
 * @author HAWLETH
 */
@WebServlet(name = "PrestamoServlet", urlPatterns = {
    "/prestamos", "/prestamo-nuevo", "/prestamo-devolver", "/prestamo-eliminar",
    "/mis-prestamos", "/prestamos-vencidos", "/generar-pdf", "/generar-excel"
})
public class PrestamoServlet extends HttpServlet {
    
    private PrestamoDAO prestamoDAO;
    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() throws ServletException {
        prestamoDAO = new PrestamoDAO();
        libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Maneja las peticiones GET
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        
        switch (action) {
            case "/prestamos":
                mostrarListaPrestamos(request, response);
                break;
            case "/prestamo-nuevo":
                mostrarFormularioNuevo(request, response);
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
            case "/prestamo-nuevo":
                crearPrestamo(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Muestra la lista de todos los préstamos (solo para admin)
     */
    private void mostrarListaPrestamos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<PrestamoDTO> prestamos = prestamoDAO.obtenerTodosPrestamos();
        String[] estadisticas = prestamoDAO.obtenerEstadisticasPrestamos();
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("totalPrestamos", estadisticas[0]);
        request.setAttribute("prestamosActivos", estadisticas[1]);
        request.setAttribute("prestamosDevueltos", estadisticas[2]);
        request.setAttribute("prestamosVencidos", estadisticas[3]);
        
        request.getRequestDispatcher("/prestamos.jsp").forward(request, response);
    }
    
    /**
     * Muestra el formulario para crear un nuevo préstamo
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Obtener listas para los select
        List<LibroDTO> librosDisponibles = libroDAO.obtenerLibrosDisponibles();
        List<UsuarioDTO> usuarios = null;
        
        // Si es admin, puede prestar a cualquier usuario
        if (LoginServlet.usuarioEsAdmin(request)) {
            usuarios = usuarioDAO.obtenerTodosUsuarios();
        }
        
        request.setAttribute("librosDisponibles", librosDisponibles);
        request.setAttribute("usuarios", usuarios);
        
        request.getRequestDispatcher("/prestamo-form.jsp").forward(request, response);
    }
    
    /**
     * Muestra los préstamos del usuario logueado
     */
    private void mostrarMisPrestamos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        UsuarioDTO usuario = LoginServlet.obtenerUsuarioLogueado(request);
        List<PrestamoDTO> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(usuario.getId());
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("esMisPrestamos", true);
        
        request.getRequestDispatcher("/mis-prestamos.jsp").forward(request, response);
    }
    
    /**
     * Muestra los préstamos vencidos (solo para admin)
     */
    private void mostrarPrestamosVencidos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<PrestamoDTO> prestamosVencidos = prestamoDAO.obtenerPrestamosVencidos();
        
        request.setAttribute("prestamos", prestamosVencidos);
        request.setAttribute("esVencidos", true);
        
        request.getRequestDispatcher("/prestamos-vencidos.jsp").forward(request, response);
    }
    
    /**
     * Crea un nuevo préstamo
     */
    private void crearPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            int idLibro = Integer.parseInt(request.getParameter("idLibro"));
            int idUsuario;
            
            // Si es admin, puede elegir el usuario; si no, es el usuario logueado
            if (LoginServlet.usuarioEsAdmin(request)) {
                idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
            } else {
                UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
                idUsuario = usuarioLogueado.getId();
            }
            
            String fechaDevolucionStr = request.getParameter("fechaDevolucion");
            
            // Validar que el libro esté disponible
            LibroDTO libro = libroDAO.obtenerLibroPorId(idLibro);
            if (libro == null || !libro.isDisponible()) {
                request.setAttribute("error", "El libro seleccionado no está disponible");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Verificar que el libro no esté ya prestado
            if (prestamoDAO.libroEstaPrestado(idLibro)) {
                request.setAttribute("error", "El libro ya está prestado");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Crear fechas
            Date fechaPrestamo = Date.valueOf(LocalDate.now());
            Date fechaDevolucion = Date.valueOf(fechaDevolucionStr);
            
            // Validar que la fecha de devolución sea futura
            if (fechaDevolucion.before(fechaPrestamo)) {
                request.setAttribute("error", "La fecha de devolución debe ser posterior a hoy");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Crear préstamo
            PrestamoDTO prestamo = new PrestamoDTO(idUsuario, idLibro, fechaPrestamo, fechaDevolucion, false);
            
            if (prestamoDAO.crearPrestamo(prestamo)) {
                // Marcar el libro como no disponible
                libroDAO.cambiarDisponibilidad(idLibro, false);
                
                request.setAttribute("mensaje", "Préstamo creado exitosamente");
                
                // Redirigir según el rol
                if (LoginServlet.usuarioEsAdmin(request)) {
                    response.sendRedirect(request.getContextPath() + "/prestamos");
                } else {
                    response.sendRedirect(request.getContextPath() + "/mis-prestamos");
                }
            } else {
                request.setAttribute("error", "Error al crear el préstamo");
                mostrarFormularioNuevo(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Datos inválidos en el formulario");
            mostrarFormularioNuevo(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioNuevo(request, response);
        }
    }
    
    /**
     * Marca un préstamo como devuelto
     */
    private void devolverLibro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            int idPrestamo = Integer.parseInt(request.getParameter("id"));
            
            // Obtener información del préstamo
            PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(idPrestamo);
            
            if (prestamo == null) {
                request.setAttribute("error", "Préstamo no encontrado");
            } else if (prestamo.isDevuelto()) {
                request.setAttribute("error", "Este libro ya fue devuelto");
            } else {
                // Verificar permisos: admin puede devolver cualquier libro, 
                // usuario normal solo sus propios préstamos
                UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
                
                if (!LoginServlet.usuarioEsAdmin(request) && 
                    prestamo.getIdUsuario() != usuarioLogueado.getId()) {
                    request.setAttribute("error", "No tienes permisos para devolver este libro");
                } else {
                    // Marcar como devuelto
                    Date fechaDevolucion = Date.valueOf(LocalDate.now());
                    
                    if (prestamoDAO.marcarComoDevuelto(idPrestamo, fechaDevolucion)) {
                        // Marcar el libro como disponible
                        libroDAO.cambiarDisponibilidad(prestamo.getIdLibro(), true);
                        
                        request.setAttribute("mensaje", "Libro devuelto exitosamente");
                    } else {
                        request.setAttribute("error", "Error al procesar la devolución");
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
            request.setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Elimina un préstamo (solo admin)
     */
    private void eliminarPrestamo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            // Obtener información del préstamo antes de eliminarlo
            PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(id);
            
            if (prestamo != null && !prestamo.isDevuelto()) {
                // Si el préstamo no estaba devuelto, hacer el libro disponible
                libroDAO.cambiarDisponibilidad(prestamo.getIdLibro(), true);
            }
            
            if (prestamoDAO.eliminarPrestamo(id)) {
                request.setAttribute("mensaje", "Préstamo eliminado exitosamente");
            } else {
                request.setAttribute("error", "Error al eliminar el préstamo");
            }
            
            response.sendRedirect(request.getContextPath() + "/prestamos");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Genera comprobante de préstamo en PDF
     */
    private void generarComprobantePDF(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            int idPrestamo = Integer.parseInt(request.getParameter("id"));
            PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(idPrestamo);
            
            if (prestamo == null) {
                request.setAttribute("error", "Préstamo no encontrado");
                response.sendRedirect(request.getContextPath() + "/prestamos");
                return;
            }
            
            // Verificar permisos
            UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
            if (!LoginServlet.usuarioEsAdmin(request) && 
                prestamo.getIdUsuario() != usuarioLogueado.getId()) {
                request.setAttribute("error", "No tienes permisos para generar este comprobante");
                response.sendRedirect(request.getContextPath() + "/prestamos");
                return;
            }
            
            // Configurar respuesta para PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=comprobante_prestamo_" + idPrestamo + ".pdf");
            
            // TODO: Implementar generación de PDF con iText
            // Por ahora, enviamos un mensaje de que la funcionalidad está en desarrollo
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("<!DOCTYPE html>");
            response.getWriter().write("<html><head><title>Comprobante de Préstamo</title>");
            response.getWriter().write("<style>body{font-family:Arial,sans-serif;margin:40px;}</style></head><body>");
            response.getWriter().write("<h2>📄 Comprobante de Préstamo</h2>");
            response.getWriter().write("<div style='border:1px solid #ccc;padding:20px;border-radius:5px;'>");
            response.getWriter().write("<p><strong>ID Préstamo:</strong> " + idPrestamo + "</p>");
            response.getWriter().write("<p><strong>Libro:</strong> " + prestamo.getTituloLibro() + "</p>");
            response.getWriter().write("<p><strong>Autor:</strong> " + prestamo.getAutorLibro() + "</p>");
            response.getWriter().write("<p><strong>Usuario:</strong> " + prestamo.getNombreUsuario() + "</p>");
            response.getWriter().write("<p><strong>Fecha Préstamo:</strong> " + prestamo.getFechaPrestamo() + "</p>");
            response.getWriter().write("<p><strong>Fecha Devolución:</strong> " + prestamo.getFechaDevolucion() + "</p>");
            response.getWriter().write("<p><strong>Estado:</strong> " + (prestamo.isDevuelto() ? "Devuelto" : "Activo") + "</p>");
            response.getWriter().write("</div>");
            response.getWriter().write("<br><p style='color:#666;font-size:12px;'>Sistema de Biblioteca ADSO - " + new java.util.Date() + "</p>");
            response.getWriter().write("<button onclick='window.print()'>🖨️ Imprimir</button> ");
            response.getWriter().write("<button onclick='history.back()'>← Volver</button>");
            response.getWriter().write("</body></html>");
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de préstamo inválido");
            response.sendRedirect(request.getContextPath() + "/prestamos");
        }
    }
    
    /**
     * Genera reporte de inventario en Excel
     */
    private void generarReporteExcel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Configurar respuesta para Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_inventario.xlsx");
        
        // TODO: Implementar generación de Excel con Apache POI
        // Por ahora, enviamos un mensaje de que la funcionalidad está en desarrollo
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("<!DOCTYPE html>");
        response.getWriter().write("<html><head><title>Reporte Excel</title>");
        response.getWriter().write("<style>body{font-family:Arial,sans-serif;margin:40px;text-align:center;}</style></head><body>");
        response.getWriter().write("<h2>📊 Generación de Reporte Excel</h2>");
        response.getWriter().write("<div style='border:1px solid #28a745;padding:30px;border-radius:10px;background:#f8f9fa;'>");
        response.getWriter().write("<p>🚧 <strong>Funcionalidad en desarrollo</strong></p>");
        response.getWriter().write("<p>El reporte de inventario en Excel estará disponible próximamente.</p>");
        response.getWriter().write("<p>Incluirá:</p>");
        response.getWriter().write("<ul style='text-align:left;max-width:400px;margin:20px auto;'>");
        response.getWriter().write("<li>📚 Lista completa de libros</li>");
        response.getWriter().write("<li>📊 Estadísticas de préstamos</li>");
        response.getWriter().write("<li>👥 Información de usuarios</li>");
        response.getWriter().write("<li>📈 Gráficos y análisis</li>");
        response.getWriter().write("</ul>");
        response.getWriter().write("</div>");
        response.getWriter().write("<br><button onclick='history.back()' style='padding:10px 20px;background:#007bff;color:white;border:none;border-radius:5px;cursor:pointer;'>← Volver al Dashboard</button>");
        response.getWriter().write("</body></html>");
    }
}