package Controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtro para verificar autenticación en rutas protegidas
 * @author HAWLETH
 */
@WebFilter(filterName = "AuthenticationFilter")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Verificar si el usuario está logueado
        boolean isLoggedIn = (session != null && session.getAttribute("usuarioLogueado") != null);
        
        // Verificar si es una ruta que requiere autenticación
        boolean isProtectedRoute = isProtectedRoute(requestURI, contextPath);
        
        if (isProtectedRoute && !isLoggedIn) {
            // Redirigir al login si no está autenticado
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        // Verificar permisos de administrador para rutas específicas
        if (isLoggedIn && requiresAdminRole(requestURI, contextPath)) {
            String userRole = (String) session.getAttribute("rolUsuario");
            
            if (!"admin".equals(userRole)) {
                // Redirigir al dashboard de usuario si no es admin
                httpResponse.sendRedirect(contextPath + "/user-dashboard");
                return;
            }
        }
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpieza del filtro
    }
    
    /**
     * Verifica si la ruta requiere autenticación
     */
    private boolean isProtectedRoute(String requestURI, String contextPath) {
        String path = requestURI.substring(contextPath.length());
        
        // Rutas protegidas que requieren autenticación
        String[] protectedPaths = {
            "/admin-dashboard",
            "/user-dashboard",
            "/libros",
            "/libro-",
            "/usuarios",
            "/usuario-",
            "/prestamos",
            "/prestamo-",
            "/mis-prestamos",
            "/prestamos-vencidos",
            "/perfil",
            "/generar-"
        };
        
        for (String protectedPath : protectedPaths) {
            if (path.startsWith(protectedPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si la ruta requiere rol de administrador
     */
    private boolean requiresAdminRole(String requestURI, String contextPath) {
        String path = requestURI.substring(contextPath.length());
        
        // Rutas que requieren rol de administrador
        String[] adminPaths = {
            "/admin-dashboard",
            "/usuarios",
            "/usuario-",
            "/prestamos-vencidos",
            "/generar-excel"
        };
        
        for (String adminPath : adminPaths) {
            if (path.startsWith(adminPath)) {
                return true;
            }
        }
        
        // También verificar operaciones de edición/eliminación de libros
        if (path.startsWith("/libro-") && !path.equals("/libros-buscar")) {
            return true;
        }
        
        // Operaciones de eliminación de préstamos
        if (path.equals("/prestamo-eliminar")) {
            return true;
        }
        
        return false;
    }
}