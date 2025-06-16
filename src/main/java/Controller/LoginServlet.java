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
 * Servlet para manejar el login con debugging
 * @author HAWLETH
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout", "/captcha"})
public class LoginServlet extends HttpServlet {
    
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() throws ServletException {
        System.out.println("🔥 LoginServlet.init() - Inicializando servlet");
        usuarioDAO = new UsuarioDAO();
        System.out.println("✅ LoginServlet.init() - UsuarioDAO inicializado");
    }
    
    /**
     * Maneja las peticiones GET para mostrar el formulario de login y generar CAPTCHA
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        System.out.println("🔍 LoginServlet.doGet() - Action: " + action);
        System.out.println("🔍 LoginServlet.doGet() - RequestURI: " + request.getRequestURI());
        System.out.println("🔍 LoginServlet.doGet() - ContextPath: " + request.getContextPath());
        
        switch (action) {
            case "/logout":
                System.out.println("🚪 Procesando logout");
                logout(request, response);
                break;
            case "/captcha":
                System.out.println("🖼️ Generando nuevo CAPTCHA");
                generarNuevoCaptcha(request, response);
                break;
            default:
                System.out.println("📝 Mostrando formulario de login");
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
        
        System.out.println("🔐 LoginServlet.doPost() - Procesando intento de login");
        procesarLogin(request, response);
    }
    
    /**
     * Muestra el formulario de login con CAPTCHA
     */
    private void mostrarFormularioLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📋 mostrarFormularioLogin() - Iniciando");
        
        try {
            // Generar nuevo CAPTCHA
            String[] captchaData = CaptchaGenerator.generarCaptchaCompleto();
            String textoCaptcha = captchaData[0];
            String imagenCaptcha = captchaData[1];
            
            System.out.println("🖼️ CAPTCHA generado: " + textoCaptcha);
            
            // Guardar el texto del CAPTCHA en la sesión
            HttpSession session = request.getSession();
            session.setAttribute("captchaTexto", textoCaptcha);
            
            // Enviar la imagen del CAPTCHA a la vista
            request.setAttribute("captchaImagen", imagenCaptcha);
            
            System.out.println("➡️ Redirigiendo a login.jsp");
            // Redirigir al JSP de login
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarFormularioLogin(): " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("Error al cargar la página de login: " + e.getMessage());
        }
    }
    
    /**
     * Genera un nuevo CAPTCHA (AJAX)
     */
    private void generarNuevoCaptcha(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🔄 generarNuevoCaptcha() - Iniciando");
        
        try {
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
            
            System.out.println("✅ Nuevo CAPTCHA enviado: " + textoCaptcha);
            
        } catch (Exception e) {
            System.err.println("❌ Error en generarNuevoCaptcha(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Procesa el intento de login
     */
    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🔐 procesarLogin() - Iniciando proceso de autenticación");
        
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String captchaIngresado = request.getParameter("captcha");
        
        System.out.println("📧 Correo ingresado: " + correo);
        System.out.println("🔒 Contraseña ingresada: " + (contrasena != null ? "***" + contrasena.length() + " chars***" : "null"));
        System.out.println("🖼️ CAPTCHA ingresado: " + captchaIngresado);
        
        HttpSession session = request.getSession();
        String captchaReal = (String) session.getAttribute("captchaTexto");
        System.out.println("🖼️ CAPTCHA esperado: " + captchaReal);
        
        // Validar que todos los campos estén presentes
        if (correo == null || contrasena == null || captchaIngresado == null ||
            correo.trim().isEmpty() || contrasena.trim().isEmpty() || captchaIngresado.trim().isEmpty()) {
            
            System.out.println("❌ Campos incompletos");
            enviarErrorYRegenerarCaptcha(request, response, "Por favor, complete todos los campos.");
            return;
        }
        
        // Validar CAPTCHA primero
        if (!CaptchaGenerator.validarCaptcha(captchaIngresado, captchaReal)) {
            System.out.println("❌ CAPTCHA incorrecto");
            enviarErrorYRegenerarCaptcha(request, response, "El código CAPTCHA ingresado es incorrecto.");
            return;
        }
        
        System.out.println("✅ CAPTCHA validado correctamente");
        
        // Validar credenciales
        System.out.println("🔍 Validando credenciales en base de datos...");
        UsuarioDTO usuario = null;
        
        try {
            usuario = usuarioDAO.validarCredenciales(correo.trim(), contrasena);
            System.out.println("🔍 Resultado validación: " + (usuario != null ? "Usuario encontrado" : "Usuario no encontrado"));
            
            if (usuario != null) {
                System.out.println("👤 Usuario válido: " + usuario.getNombre() + " - Rol: " + usuario.getRol());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al validar credenciales: " + e.getMessage());
            e.printStackTrace();
            enviarErrorYRegenerarCaptcha(request, response, "Error en el sistema. Intente más tarde.");
            return;
        }
        
        if (usuario != null) {
            // Login exitoso
            System.out.println("✅ Login exitoso para: " + usuario.getNombre());
            
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("rolUsuario", usuario.getRol());
            session.setAttribute("idUsuario", usuario.getId());
            
            System.out.println("💾 Datos guardados en sesión:");
            System.out.println("   - nombreUsuario: " + usuario.getNombre());
            System.out.println("   - rolUsuario: " + usuario.getRol());
            System.out.println("   - idUsuario: " + usuario.getId());
            
            // Limpiar CAPTCHA de la sesión
            session.removeAttribute("captchaTexto");
            
            // Redirigir según el rol
            String redirectURL = "";
            if ("admin".equals(usuario.getRol())) {
                redirectURL = request.getContextPath() + "/admin-dashboard";
                System.out.println("🔄 Redirigiendo a dashboard admin: " + redirectURL);
            } else {
                redirectURL = request.getContextPath() + "/user-dashboard";
                System.out.println("🔄 Redirigiendo a dashboard usuario: " + redirectURL);
            }
            
            System.out.println("🌐 URL de redirección completa: " + redirectURL);
            response.sendRedirect(redirectURL);
            
        } else {
            // Credenciales incorrectas
            System.out.println("❌ Credenciales incorrectas para: " + correo);
            enviarErrorYRegenerarCaptcha(request, response, "Correo o contraseña incorrectos.");
        }
    }
    
    /**
     * Maneja el logout del usuario
     */
    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🚪 logout() - Cerrando sesión");
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("🗑️ Invalidando sesión existente");
            session.invalidate();
        }
        
        String redirectURL = request.getContextPath() + "/login";
        System.out.println("🔄 Redirigiendo después de logout: " + redirectURL);
        response.sendRedirect(redirectURL);
    }
    
    /**
     * Envía un mensaje de error y regenera el CAPTCHA
     */
    private void enviarErrorYRegenerarCaptcha(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        
        System.out.println("❌ enviarErrorYRegenerarCaptcha() - Mensaje: " + mensaje);
        
        try {
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
            
            System.out.println("➡️ Reenviando a login.jsp con error");
            // Volver al formulario de login
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en enviarErrorYRegenerarCaptcha(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si el usuario está logueado
     */
    public static boolean usuarioEstaLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean logueado = session != null && session.getAttribute("usuarioLogueado") != null;
        System.out.println("🔍 usuarioEstaLogueado(): " + logueado);
        return logueado;
    }
    
    /**
     * Obtiene el usuario logueado de la sesión
     */
    public static UsuarioDTO obtenerUsuarioLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioLogueado");
            System.out.println("🔍 obtenerUsuarioLogueado(): " + (usuario != null ? usuario.getNombre() : "null"));
            return usuario;
        }
        System.out.println("🔍 obtenerUsuarioLogueado(): sesión null");
        return null;
    }
    
    /**
     * Verifica si el usuario es administrador
     */
    public static boolean usuarioEsAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String rol = (String) session.getAttribute("rolUsuario");
            boolean esAdmin = "admin".equals(rol);
            System.out.println("🔍 usuarioEsAdmin(): " + esAdmin + " (rol: " + rol + ")");
            return esAdmin;
        }
        System.out.println("🔍 usuarioEsAdmin(): false (no hay sesión)");
        return false;
    }
}