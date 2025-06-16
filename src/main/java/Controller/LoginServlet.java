package Controller;

import DAO.UsuarioDAO;
import DTO.UsuarioDTO;
import Utils.CaptchaGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet para manejar el login con autenticación CAPTCHA
 * @author HAWLETH
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout", "/captcha"})
public class LoginServlet extends HttpServlet {
    
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Maneja las peticiones GET para mostrar el formulario de login y generar CAPTCHA
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        
        switch (action) {
            case "/logout":
                logout(request, response);
                break;
            case "/captcha":
                generarNuevoCaptcha(request, response);
                break;
            default:
                mostrarFormularioLogin(request, response);
                break;
        }
    }
    
    /**
     * Maneja las peticiones POST para procesar el login
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        procesarLogin(request, response);
    }
    
    /**
     * Muestra el formulario de login con CAPTCHA
     */
    private void mostrarFormularioLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Generar nuevo CAPTCHA
        String[] captchaData = CaptchaGenerator.generarCaptchaCompleto();
        String textoCaptcha = captchaData[0];
        String imagenCaptcha = captchaData[1];
        
        // Guardar el texto del CAPTCHA en la sesión
        HttpSession session = request.getSession();
        session.setAttribute("captchaTexto", textoCaptcha);
        
        // Enviar la imagen del CAPTCHA a la vista
        request.setAttribute("captchaImagen", imagenCaptcha);
        
        // Redirigir al JSP de login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    /**
     * Genera un nuevo CAPTCHA (AJAX)
     */
    private void generarNuevoCaptcha(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Generar nuevo CAPTCHA
        String[] captchaData = CaptchaGenerator.generarCaptchaCompleto();
        String textoCaptcha = captchaData[0];
        String imagenCaptcha = captchaData[1];
        
        // Guardar el texto del CAPTCHA en la sesión
        HttpSession session = request.getSession();
        session.setAttribute("captchaTexto", textoCaptcha);
        
        // Responder con la nueva imagen en formato JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"imagen\": \"" + imagenCaptcha + "\"}");
    }
    
    /**
     * Procesa el intento de login
     */
    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String captchaIngresado = request.getParameter("captcha");
        
        HttpSession session = request.getSession();
        String captchaReal = (String) session.getAttribute("captchaTexto");
        
        // Validar que todos los campos estén presentes
        if (correo == null || contrasena == null || captchaIngresado == null ||
            correo.trim().isEmpty() || contrasena.trim().isEmpty() || captchaIngresado.trim().isEmpty()) {
            
            enviarErrorYRegenerarCaptcha(request, response, "Por favor, complete todos los campos.");
            return;
        }
        
        // Validar CAPTCHA primero
        if (!CaptchaGenerator.validarCaptcha(captchaIngresado, captchaReal)) {
            enviarErrorYRegenerarCaptcha(request, response, "El código CAPTCHA ingresado es incorrecto.");
            return;
        }
        
        // Validar credenciales
        UsuarioDTO usuario = usuarioDAO.validarCredenciales(correo.trim(), contrasena);
        
        if (usuario != null) {
            // Login exitoso
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("rolUsuario", usuario.getRol());
            session.setAttribute("idUsuario", usuario.getId());
            
            // Limpiar CAPTCHA de la sesión
            session.removeAttribute("captchaTexto");
            
            // Redirigir según el rol
            if ("admin".equals(usuario.getRol())) {
                response.sendRedirect(request.getContextPath() + "/admin-dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/user-dashboard");
            }
            
        } else {
            // Credenciales incorrectas
            enviarErrorYRegenerarCaptcha(request, response, "Correo o contraseña incorrectos.");
        }
    }
    
    /**
     * Maneja el logout del usuario
     */
    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/login");
    }
    
    /**
     * Envía un mensaje de error y regenera el CAPTCHA
     */
    private void enviarErrorYRegenerarCaptcha(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        
        // Generar nuevo CAPTCHA
        String[] captchaData = CaptchaGenerator.generarCaptchaCompleto();
        String textoCaptcha = captchaData[0];
        String imagenCaptcha = captchaData[1];
        
        // Guardar el nuevo CAPTCHA en la sesión
        HttpSession session = request.getSession();
        session.setAttribute("captchaTexto", textoCaptcha);
        
        // Enviar error y nueva imagen a la vista
        request.setAttribute("error", mensaje);
        request.setAttribute("captchaImagen", imagenCaptcha);
        
        // Mantener el correo ingresado (para UX)
        request.setAttribute("correoAnterior", request.getParameter("correo"));
        
        // Volver al formulario de login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    /**
     * Verifica si el usuario está logueado
     * @param request HttpServletRequest
     * @return true si está logueado, false si no
     */
    public static boolean usuarioEstaLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("usuarioLogueado") != null;
    }
    
    /**
     * Obtiene el usuario logueado de la sesión
     * @param request HttpServletRequest
     * @return UsuarioDTO usuario logueado o null
     */
    public static UsuarioDTO obtenerUsuarioLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (UsuarioDTO) session.getAttribute("usuarioLogueado");
        }
        return null;
    }
    
    /**
     * Verifica si el usuario es administrador
     * @param request HttpServletRequest
     * @return true si es admin, false si no
     */
    public static boolean usuarioEsAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String rol = (String) session.getAttribute("rolUsuario");
            return "admin".equals(rol);
        }
        return false;
    }
}