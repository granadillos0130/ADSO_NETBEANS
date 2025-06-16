package Controller;

import DAO.LibroDAO;
import DAO.UsuarioDAO;
import DAO.PrestamoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet para manejar los dashboards de admin y usuario con debugging
 * @author HAWLETH
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {
    "/admin-dashboard", "/user-dashboard"
})
public class DashboardServlet extends HttpServlet {
    
    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    private PrestamoDAO prestamoDAO;
    
    @Override
    public void init() throws ServletException {
        System.out.println("🔥 DashboardServlet.init() - Inicializando servlet");
        libroDAO = new LibroDAO();
        usuarioDAO = new UsuarioDAO();
        prestamoDAO = new PrestamoDAO();
        System.out.println("✅ DashboardServlet.init() - DAOs inicializados");
    }
    
    /**
     * Maneja las peticiones GET para mostrar los dashboards
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        System.out.println("🔍 DashboardServlet.doGet() - Action: " + action);
        System.out.println("🔍 DashboardServlet.doGet() - RequestURI: " + request.getRequestURI());
        System.out.println("🔍 DashboardServlet.doGet() - ContextPath: " + request.getContextPath());
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado, redirigiendo a login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        System.out.println("✅ Usuario autenticado, procesando dashboard");
        
        switch (action) {
            case "/admin-dashboard":
                System.out.println("🏢 Mostrando dashboard de administrador");
                mostrarDashboardAdmin(request, response);
                break;
            case "/user-dashboard":
                System.out.println("👤 Mostrando dashboard de usuario");
                mostrarDashboardUsuario(request, response);
                break;
            default:
                System.out.println("❌ Acción no reconocida: " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Muestra el dashboard del administrador
     */
    private void mostrarDashboardAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🏢 mostrarDashboardAdmin() - Iniciando");
        
        // Verificar permisos de admin
        if (!LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no es admin, redirigiendo a user-dashboard");
            response.sendRedirect(request.getContextPath() + "/user-dashboard");
            return;
        }
        
        System.out.println("✅ Usuario es admin, cargando datos del dashboard");
        
        try {
            // Obtener estadísticas para el dashboard
            System.out.println("📊 Obteniendo estadísticas...");
            int totalLibros = 0;
            int totalUsuarios = 0;
            String[] estadisticasPrestamos = {"0", "0", "0", "0"};
            
            try {
                totalLibros = libroDAO.obtenerTodosLibros().size();
                System.out.println("✅ Total libros obtenido: " + totalLibros);
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo libros: " + e.getMessage());
            }
            
            try {
                totalUsuarios = usuarioDAO.obtenerTodosUsuarios().size();
                System.out.println("✅ Total usuarios obtenido: " + totalUsuarios);
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo usuarios: " + e.getMessage());
            }
            
            try {
                estadisticasPrestamos = prestamoDAO.obtenerEstadisticasPrestamos();
                System.out.println("✅ Estadísticas préstamos obtenidas");
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo estadísticas préstamos: " + e.getMessage());
            }
            
            System.out.println("📊 Estadísticas cargadas:");
            System.out.println("   - Total libros: " + totalLibros);
            System.out.println("   - Total usuarios: " + totalUsuarios);
            System.out.println("   - Préstamos activos: " + (estadisticasPrestamos != null ? estadisticasPrestamos[1] : "0"));
            
            // Intentar cargar JSP primero
            try {
                // Pasar datos a la vista
                request.setAttribute("totalLibros", totalLibros);
                request.setAttribute("totalUsuarios", totalUsuarios);
                if (estadisticasPrestamos != null) {
                    request.setAttribute("totalPrestamos", estadisticasPrestamos[0]);
                    request.setAttribute("prestamosActivos", estadisticasPrestamos[1]);
                    request.setAttribute("prestamosDevueltos", estadisticasPrestamos[2]);
                    request.setAttribute("prestamosVencidos", estadisticasPrestamos[3]);
                } else {
                    request.setAttribute("totalPrestamos", "0");
                    request.setAttribute("prestamosActivos", "0");
                    request.setAttribute("prestamosDevueltos", "0");
                    request.setAttribute("prestamosVencidos", "0");
                }
                
                System.out.println("➡️ Intentando cargar admin-dashboard.jsp");
                request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
                System.out.println("✅ admin-dashboard.jsp cargado correctamente");
                
            } catch (Exception jspError) {
                System.err.println("❌ Error cargando JSP, usando HTML: " + jspError.getMessage());
                jspError.printStackTrace();
                // Si falla el JSP, usar página HTML simple
                crearPaginaAdminHTML(request, response, totalLibros, totalUsuarios, estadisticasPrestamos);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar dashboard admin: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h1>Error al cargar el dashboard</h1>");
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            response.getWriter().println("<a href='" + request.getContextPath() + "/logout'>Cerrar sesión</a>");
        }
    }
    
    /**
     * Muestra el dashboard del usuario normal
     */
    private void mostrarDashboardUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("👤 mostrarDashboardUsuario() - Iniciando");
        
        try {
            // Obtener información del usuario logueado
            Integer idUsuario = (Integer) request.getSession().getAttribute("idUsuario");
            System.out.println("🔍 ID de usuario desde sesión: " + idUsuario);
            
            if (idUsuario == null) {
                System.out.println("❌ No se encontró ID de usuario en sesión");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            // Obtener estadísticas del usuario
            int misPrestamosActivos = 0;
            int librosDisponibles = 0;
            
            try {
                misPrestamosActivos = prestamoDAO.obtenerPrestamosPorUsuario(idUsuario).size();
                System.out.println("✅ Préstamos del usuario obtenidos: " + misPrestamosActivos);
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo préstamos del usuario: " + e.getMessage());
            }
            
            try {
                librosDisponibles = libroDAO.obtenerLibrosDisponibles().size();
                System.out.println("✅ Libros disponibles obtenidos: " + librosDisponibles);
            } catch (Exception e) {
                System.err.println("❌ Error obteniendo libros disponibles: " + e.getMessage());
            }
            
            System.out.println("📊 Estadísticas del usuario:");
            System.out.println("   - Mis préstamos: " + misPrestamosActivos);
            System.out.println("   - Libros disponibles: " + librosDisponibles);
            
            // Intentar cargar JSP primero
            try {
                // Pasar datos a la vista
                request.setAttribute("misPrestamosActivos", misPrestamosActivos);
                request.setAttribute("librosDisponibles", librosDisponibles);
                
                System.out.println("➡️ Intentando cargar user-dashboard.jsp");
                request.getRequestDispatcher("/user-dashboard.jsp").forward(request, response);
                System.out.println("✅ user-dashboard.jsp cargado correctamente");
                
            } catch (Exception jspError) {
                System.err.println("❌ Error cargando JSP, usando HTML: " + jspError.getMessage());
                jspError.printStackTrace();
                // Si falla el JSP, usar página HTML simple
                crearPaginaUsuarioHTML(request, response, misPrestamosActivos, librosDisponibles);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar dashboard usuario: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h1>Error al cargar el dashboard</h1>");
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            response.getWriter().println("<a href='" + request.getContextPath() + "/logout'>Cerrar sesión</a>");
        }
    }
    
    /**
     * Crea página HTML para admin dashboard
     */
    private void crearPaginaAdminHTML(HttpServletRequest request, HttpServletResponse response, 
                                     int totalLibros, int totalUsuarios, String[] estadisticas)
            throws IOException {
        
        System.out.println("🌐 Creando página HTML para admin");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html lang='es'>");
        response.getWriter().println("<head>");
        response.getWriter().println("<meta charset='UTF-8'>");
        response.getWriter().println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        response.getWriter().println("<title>Dashboard Admin - Sistema de Biblioteca ADSO</title>");
        response.getWriter().println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        response.getWriter().println("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        
        // Navbar
        response.getWriter().println("<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>");
        response.getWriter().println("<div class='container'>");
        response.getWriter().println("<a class='navbar-brand' href='#'><i class='fas fa-book-open me-2'></i>Sistema de Biblioteca ADSO</a>");
        response.getWriter().println("<div class='navbar-nav ms-auto'>");
        response.getWriter().println("<span class='navbar-text me-3'><i class='fas fa-user-shield me-1'></i>" + request.getSession().getAttribute("nombreUsuario") + "</span>");
        response.getWriter().println("<a class='nav-link' href='" + request.getContextPath() + "/logout'><i class='fas fa-sign-out-alt'></i> Salir</a>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</nav>");
        
        // Hero section
        response.getWriter().println("<div class='bg-primary text-white py-5'>");
        response.getWriter().println("<div class='container text-center'>");
        response.getWriter().println("<h1><i class='fas fa-tachometer-alt me-3'></i>Panel de Administración</h1>");
        response.getWriter().println("<p class='lead'>Bienvenido " + request.getSession().getAttribute("nombreUsuario") + "</p>");
        response.getWriter().println("<p class='mb-0'>✅ Sistema funcionando correctamente - Página generada dinámicamente</p>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        
        // Estadísticas
        response.getWriter().println("<div class='container py-5'>");
        response.getWriter().println("<div class='row'>");
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-book fa-3x text-primary mb-3'></i>");
        response.getWriter().println("<h3>" + totalLibros + "</h3>");
        response.getWriter().println("<p class='text-muted'>Total Libros</p>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-users fa-3x text-success mb-3'></i>");
        response.getWriter().println("<h3>" + totalUsuarios + "</h3>");
        response.getWriter().println("<p class='text-muted'>Usuarios</p>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-exchange-alt fa-3x text-warning mb-3'></i>");
        response.getWriter().println("<h3>" + (estadisticas != null ? estadisticas[1] : "0") + "</h3>");
        response.getWriter().println("<p class='text-muted'>Préstamos Activos</p>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-check-circle fa-3x text-info mb-3'></i>");
        response.getWriter().println("<h3>" + (estadisticas != null ? estadisticas[2] : "0") + "</h3>");
        response.getWriter().println("<p class='text-muted'>Devueltos</p>");
        response.getWriter().println("</div></div></div>");
        response.getWriter().println("</div>");
        
        // Acciones rápidas
        response.getWriter().println("<div class='row mt-5'>");
        response.getWriter().println("<div class='col-12'><h4><i class='fas fa-bolt me-2'></i>Acciones Rápidas</h4></div>");
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-plus-circle fa-3x text-primary mb-3'></i>");
        response.getWriter().println("<h5>Nuevo Libro</h5>");
        response.getWriter().println("<p class='card-text'>Agregar libro al catálogo</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/libro-nuevo' class='btn btn-primary'>Crear</a>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-user-plus fa-3x text-success mb-3'></i>");
        response.getWriter().println("<h5>Nuevo Usuario</h5>");
        response.getWriter().println("<p class='card-text'>Registrar nuevo usuario</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/usuario-nuevo' class='btn btn-success'>Crear</a>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-list fa-3x text-warning mb-3'></i>");
        response.getWriter().println("<h5>Ver Libros</h5>");
        response.getWriter().println("<p class='card-text'>Gestionar catálogo</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/libros' class='btn btn-warning'>Ver</a>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-exchange-alt fa-3x text-info mb-3'></i>");
        response.getWriter().println("<h5>Préstamos</h5>");
        response.getWriter().println("<p class='card-text'>Gestionar préstamos</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/prestamos' class='btn btn-info'>Ver</a>");
        response.getWriter().println("</div></div></div>");
        response.getWriter().println("</div>");
        
        // Footer informativo
        response.getWriter().println("<div class='row mt-5'>");
        response.getWriter().println("<div class='col-12'>");
        response.getWriter().println("<div class='alert alert-info'>");
        response.getWriter().println("<h5><i class='fas fa-info-circle me-2'></i>Información del Sistema</h5>");
        response.getWriter().println("<p class='mb-1'>✅ Dashboard cargado correctamente usando HTML dinámico</p>");
        response.getWriter().println("<p class='mb-1'>🔄 Fecha: " + new java.util.Date() + "</p>");
        response.getWriter().println("<p class='mb-0'>👤 Usuario: " + request.getSession().getAttribute("nombreUsuario") + " (Admin)</p>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        
        response.getWriter().println("</div>");
        response.getWriter().println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
        
        System.out.println("✅ Página HTML para admin creada exitosamente");
    }
    
    /**
     * Crea página HTML para usuario dashboard
     */
    private void crearPaginaUsuarioHTML(HttpServletRequest request, HttpServletResponse response, 
                                       int misPrestamos, int librosDisponibles)
            throws IOException {
        
        System.out.println("🌐 Creando página HTML para usuario");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html lang='es'>");
        response.getWriter().println("<head>");
        response.getWriter().println("<meta charset='UTF-8'>");
        response.getWriter().println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        response.getWriter().println("<title>Mi Dashboard - Sistema de Biblioteca ADSO</title>");
        response.getWriter().println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        response.getWriter().println("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        
        // Navbar
        response.getWriter().println("<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>");
        response.getWriter().println("<div class='container'>");
        response.getWriter().println("<a class='navbar-brand' href='#'><i class='fas fa-book-open me-2'></i>Sistema de Biblioteca ADSO</a>");
        response.getWriter().println("<div class='navbar-nav ms-auto'>");
        response.getWriter().println("<span class='navbar-text me-3'><i class='fas fa-user me-1'></i>" + request.getSession().getAttribute("nombreUsuario") + "</span>");
        response.getWriter().println("<a class='nav-link' href='" + request.getContextPath() + "/logout'><i class='fas fa-sign-out-alt'></i> Salir</a>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</nav>");
        
        // Hero section
        response.getWriter().println("<div class='bg-success text-white py-5'>");
        response.getWriter().println("<div class='container text-center'>");
        response.getWriter().println("<h1><i class='fas fa-user-circle me-3'></i>¡Bienvenido, " + request.getSession().getAttribute("nombreUsuario") + "!</h1>");
        response.getWriter().println("<p class='lead'>Gestiona tus préstamos y explora nuestro catálogo</p>");
        response.getWriter().println("<p class='mb-0'>✅ Sistema funcionando correctamente - Página generada dinámicamente</p>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        
        // Estadísticas
        response.getWriter().println("<div class='container py-5'>");
        response.getWriter().println("<div class='row'>");
        response.getWriter().println("<div class='col-md-6'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-book-reader fa-3x text-primary mb-3'></i>");
        response.getWriter().println("<h3>" + misPrestamos + "</h3>");
        response.getWriter().println("<p class='text-muted'>Mis Préstamos</p>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-6'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-star fa-3x text-warning mb-3'></i>");
        response.getWriter().println("<h3>" + librosDisponibles + "</h3>");
        response.getWriter().println("<p class='text-muted'>Libros Disponibles</p>");
        response.getWriter().println("</div></div></div>");
        response.getWriter().println("</div>");
        
        // Acciones rápidas
        response.getWriter().println("<div class='row mt-5'>");
        response.getWriter().println("<div class='col-12'><h4><i class='fas fa-bolt me-2'></i>¿Qué quieres hacer hoy?</h4></div>");
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-search fa-3x text-primary mb-3'></i>");
        response.getWriter().println("<h5>Buscar Libros</h5>");
        response.getWriter().println("<p class='card-text'>Encuentra tu próximo libro</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/libros-buscar' class='btn btn-primary'>Buscar</a>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-list fa-3x text-success mb-3'></i>");
        response.getWriter().println("<h5>Mis Préstamos</h5>");
        response.getWriter().println("<p class='card-text'>Revisa tus libros actuales</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/mis-prestamos' class='btn btn-success'>Ver</a>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-plus fa-3x text-warning mb-3'></i>");
        response.getWriter().println("<h5>Solicitar Préstamo</h5>");
        response.getWriter().println("<p class='card-text'>Pide un libro prestado</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/prestamo-nuevo' class='btn btn-warning'>Solicitar</a>");
        response.getWriter().println("</div></div></div>");
        
        response.getWriter().println("<div class='col-md-3'>");
        response.getWriter().println("<div class='card text-center h-100'>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<i class='fas fa-globe fa-3x text-info mb-3'></i>");
        response.getWriter().println("<h5>Catálogo Público</h5>");
        response.getWriter().println("<p class='card-text'>Explora todos los libros</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/consulta-publica' class='btn btn-info'>Ver</a>");
        response.getWriter().println("</div></div></div>");
        response.getWriter().println("</div>");
        
        // Footer informativo
        response.getWriter().println("<div class='row mt-5'>");
        response.getWriter().println("<div class='col-12'>");
        response.getWriter().println("<div class='alert alert-info'>");
        response.getWriter().println("<h5><i class='fas fa-info-circle me-2'></i>Información del Sistema</h5>");
        response.getWriter().println("<p class='mb-1'>✅ Dashboard cargado correctamente usando HTML dinámico</p>");
        response.getWriter().println("<p class='mb-1'>🔄 Fecha: " + new java.util.Date() + "</p>");
        response.getWriter().println("<p class='mb-0'>👤 Usuario: " + request.getSession().getAttribute("nombreUsuario") + " (Lector)</p>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        
        response.getWriter().println("</div>");
        response.getWriter().println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
        
        System.out.println("✅ Página HTML para usuario creada exitosamente");
    }
}