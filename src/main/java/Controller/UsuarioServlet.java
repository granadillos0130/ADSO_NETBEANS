package Controller;

import DAO.UsuarioDAO;
import DTO.UsuarioDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet para manejar operaciones con usuarios
 * @author HAWLETH
 */
@WebServlet(name = "UsuarioServlet", urlPatterns = {
    "/usuarios", "/usuario-nuevo", "/usuario-editar", "/usuario-eliminar", "/perfil"
})
public class UsuarioServlet extends HttpServlet {
    
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() throws ServletException {
        System.out.println("🔥 UsuarioServlet.init() - Inicializando servlet");
        usuarioDAO = new UsuarioDAO();
        System.out.println("✅ UsuarioServlet.init() - UsuarioDAO inicializado");
    }
    
    /**
     * Maneja las peticiones GET
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        System.out.println("🔍 UsuarioServlet.doGet() - Action: " + action);
        
        switch (action) {
            case "/usuarios":
                mostrarListaUsuarios(request, response);
                break;
            case "/usuario-nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "/usuario-editar":
                mostrarFormularioEditar(request, response);
                break;
            case "/usuario-eliminar":
                eliminarUsuario(request, response);
                break;
            case "/perfil":
                mostrarPerfil(request, response);
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
        System.out.println("📝 UsuarioServlet.doPost() - Action: " + action);
        
        switch (action) {
            case "/usuario-nuevo":
                crearUsuario(request, response);
                break;
            case "/usuario-editar":
                actualizarUsuario(request, response);
                break;
            case "/perfil":
                actualizarPerfil(request, response);
                break;
            default:
                System.out.println("❌ Acción POST no reconocida: " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Muestra la lista de todos los usuarios (solo para admin)
     */
    private void mostrarListaUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("👥 mostrarListaUsuarios() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para ver usuarios");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            List<UsuarioDTO> usuarios = usuarioDAO.obtenerTodosUsuarios();
            
            System.out.println("📊 Total usuarios cargados: " + usuarios.size());
            
            request.setAttribute("usuarios", usuarios);
            request.setAttribute("totalUsuarios", usuarios.size());
            
            request.getRequestDispatcher("/usuarios.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarListaUsuarios(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar la lista de usuarios");
            request.getRequestDispatcher("/usuarios.jsp").forward(request, response);
        }
    }
    
    /**
     * Muestra el formulario para crear un nuevo usuario
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("➕ mostrarFormularioNuevo() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para crear usuarios");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        request.setAttribute("accion", "nuevo");
        System.out.println("📝 Mostrando formulario nuevo usuario");
        request.getRequestDispatcher("/usuario-form.jsp").forward(request, response);
    }
    
    /**
     * Muestra el formulario para editar un usuario existente
     */
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("✏️ mostrarFormularioEditar() - Iniciando");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para editar usuarios");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de usuario a editar: " + idStr);
            
            int id = Integer.parseInt(idStr);
            UsuarioDTO usuario = usuarioDAO.obtenerUsuarioPorId(id);
            
            if (usuario != null) {
                request.setAttribute("usuario", usuario);
                request.setAttribute("accion", "editar");
                
                System.out.println("📝 Mostrando formulario editar usuario: " + usuario.getNombre());
                request.getRequestDispatcher("/usuario-form.jsp").forward(request, response);
            } else {
                System.out.println("❌ Usuario no encontrado con ID: " + id);
                request.getSession().setAttribute("error", "Usuario no encontrado");
                response.sendRedirect(request.getContextPath() + "/usuarios");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de usuario inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de usuario inválido");
            response.sendRedirect(request.getContextPath() + "/usuarios");
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarFormularioEditar(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error al cargar el formulario de edición");
            response.sendRedirect(request.getContextPath() + "/usuarios");
        }
    }
    
    /**
     * Muestra el perfil del usuario logueado
     */
    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("👤 mostrarPerfil() - Iniciando");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        UsuarioDTO usuario = LoginServlet.obtenerUsuarioLogueado(request);
        request.setAttribute("usuario", usuario);
        request.setAttribute("accion", "perfil");
        
        System.out.println("📝 Mostrando perfil de: " + usuario.getNombre());
        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }
    
    /**
     * Crea un nuevo usuario
     */
    private void crearUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("👤 crearUsuario() - Iniciando creación de usuario");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para crear usuarios");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String documento = request.getParameter("documento");
            String correo = request.getParameter("correo");
            String telefono = request.getParameter("telefono");
            String contrasena = request.getParameter("contrasena");
            String rol = request.getParameter("rol");
            
            System.out.println("📝 Datos recibidos:");
            System.out.println("   - Nombre: " + nombre);
            System.out.println("   - Documento: " + documento);
            System.out.println("   - Correo: " + correo);
            System.out.println("   - Teléfono: " + telefono);
            System.out.println("   - Rol: " + rol);
            
            // Validar datos básicos
            if (nombre == null || nombre.trim().isEmpty() || 
                documento == null || documento.trim().isEmpty() ||
                correo == null || correo.trim().isEmpty() ||
                contrasena == null || contrasena.trim().isEmpty()) {
                
                System.out.println("❌ Datos incompletos");
                request.setAttribute("error", "Los campos nombre, documento, correo y contraseña son obligatorios");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Validar formato de documento (solo números)
            if (!documento.trim().matches("\\d+")) {
                System.out.println("❌ Documento inválido: " + documento);
                request.setAttribute("error", "El documento debe contener solo números");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Validar formato de correo
            if (!correo.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                System.out.println("❌ Correo inválido: " + correo);
                request.setAttribute("error", "El formato del correo electrónico es inválido");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Validar longitud de contraseña
            if (contrasena.length() < 6) {
                System.out.println("❌ Contraseña muy corta");
                request.setAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Verificar si el correo ya existe
            if (usuarioDAO.existeCorreo(correo.trim())) {
                System.out.println("❌ Correo ya existe: " + correo);
                request.setAttribute("error", "El correo electrónico ya está registrado");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Verificar si el documento ya existe
            if (usuarioDAO.existeDocumento(documento.trim())) {
                System.out.println("❌ Documento ya existe: " + documento);
                request.setAttribute("error", "El documento ya está registrado");
                mostrarFormularioNuevo(request, response);
                return;
            }
            
            // Crear objeto UsuarioDTO
            UsuarioDTO usuario = new UsuarioDTO(
                nombre.trim(), 
                documento.trim(), 
                correo.trim(), 
                telefono != null ? telefono.trim() : "", 
                contrasena, 
                rol != null && !rol.trim().isEmpty() ? rol : "usuario"
            );
            
            System.out.println("💾 Guardando usuario en base de datos...");
            
            // Guardar en la base de datos
            if (usuarioDAO.crearUsuario(usuario)) {
                System.out.println("✅ Usuario creado exitosamente");
                request.getSession().setAttribute("mensaje", "Usuario '" + nombre.trim() + "' creado exitosamente");
                response.sendRedirect(request.getContextPath() + "/usuarios");
            } else {
                System.out.println("❌ Error al guardar usuario en BD");
                request.setAttribute("error", "Error al crear el usuario en la base de datos");
                mostrarFormularioNuevo(request, response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en crearUsuario(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioNuevo(request, response);
        }
    }
    
    /**
     * Actualiza un usuario existente
     */
    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("✏️ actualizarUsuario() - Iniciando actualización de usuario");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para editar usuarios");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Obtener datos del formulario
            String idStr = request.getParameter("id");
            String nombre = request.getParameter("nombre");
            String documento = request.getParameter("documento");
            String correo = request.getParameter("correo");
            String telefono = request.getParameter("telefono");
            String rol = request.getParameter("rol");
            String nuevaContrasena = request.getParameter("nuevaContrasena");
            
            System.out.println("📝 Datos recibidos para actualización:");
            System.out.println("   - ID: " + idStr);
            System.out.println("   - Nombre: " + nombre);
            System.out.println("   - Rol: " + rol);
            System.out.println("   - Nueva contraseña: " + (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty() ? "Sí" : "No"));
            
            // Validar ID
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ ID inválido: " + idStr);
                request.setAttribute("error", "ID de usuario inválido");
                mostrarListaUsuarios(request, response);
                return;
            }
            
            // Validar datos básicos
            if (nombre == null || nombre.trim().isEmpty() || 
                documento == null || documento.trim().isEmpty() ||
                correo == null || correo.trim().isEmpty()) {
                
                System.out.println("❌ Datos incompletos");
                request.setAttribute("error", "Los campos nombre, documento y correo son obligatorios");
                request.setAttribute("id", id);
                mostrarFormularioEditar(request, response);
                return;
            }
            
            // Validar formato de documento (solo números)
            if (!documento.trim().matches("\\d+")) {
                System.out.println("❌ Documento inválido: " + documento);
                request.setAttribute("error", "El documento debe contener solo números");
                request.setAttribute("id", id);
                mostrarFormularioEditar(request, response);
                return;
            }
            
            // Validar formato de correo
            if (!correo.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                System.out.println("❌ Correo inválido: " + correo);
                request.setAttribute("error", "El formato del correo electrónico es inválido");
                request.setAttribute("id", id);
                mostrarFormularioEditar(request, response);
                return;
            }
            
            // Validar nueva contraseña si se proporcionó
            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty() && nuevaContrasena.length() < 6) {
                System.out.println("❌ Nueva contraseña muy corta");
                request.setAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres");
                request.setAttribute("id", id);
                mostrarFormularioEditar(request, response);
                return;
            }
            
            // Crear objeto UsuarioDTO
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setId(id);
            usuario.setNombre(nombre.trim());
            usuario.setDocumento(documento.trim());
            usuario.setCorreo(correo.trim());
            usuario.setTelefono(telefono != null ? telefono.trim() : "");
            usuario.setRol(rol != null && !rol.trim().isEmpty() ? rol : "usuario");
            
            System.out.println("💾 Actualizando usuario en base de datos...");
            
            // Actualizar en la base de datos
            boolean actualizado = usuarioDAO.actualizarUsuario(usuario);
            
            // Cambiar contraseña si se proporcionó una nueva
            if (actualizado && nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                System.out.println("🔑 Actualizando contraseña...");
                actualizado = usuarioDAO.cambiarContrasena(id, nuevaContrasena);
            }
            
            if (actualizado) {
                System.out.println("✅ Usuario actualizado exitosamente");
                request.getSession().setAttribute("mensaje", "Usuario '" + nombre.trim() + "' actualizado exitosamente");
                response.sendRedirect(request.getContextPath() + "/usuarios");
            } else {
                System.out.println("❌ Error al actualizar usuario en BD");
                request.setAttribute("error", "Error al actualizar el usuario en la base de datos");
                request.setAttribute("id", id);
                mostrarFormularioEditar(request, response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en actualizarUsuario(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioEditar(request, response);
        }
    }
    
    /**
     * Actualiza el perfil del usuario logueado
     */
    private void actualizarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("👤 actualizarPerfil() - Iniciando actualización de perfil");
        
        // Verificar autenticación
        if (!LoginServlet.usuarioEstaLogueado(request)) {
            System.out.println("❌ Usuario no autenticado");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
            System.out.println("👤 Actualizando perfil de: " + usuarioLogueado.getNombre());
            
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String telefono = request.getParameter("telefono");
            String nuevaContrasena = request.getParameter("nuevaContrasena");
            
            // Validar datos básicos
            if (nombre == null || nombre.trim().isEmpty()) {
                System.out.println("❌ Nombre vacío");
                request.setAttribute("error", "El nombre es obligatorio");
                mostrarPerfil(request, response);
                return;
            }
            
            // Validar nueva contraseña si se proporcionó
            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty() && nuevaContrasena.length() < 6) {
                System.out.println("❌ Nueva contraseña muy corta");
                request.setAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres");
                mostrarPerfil(request, response);
                return;
            }
            
            // Actualizar datos básicos
            usuarioLogueado.setNombre(nombre.trim());
            usuarioLogueado.setTelefono(telefono != null ? telefono.trim() : "");
            
            boolean actualizado = usuarioDAO.actualizarUsuario(usuarioLogueado);
            
            // Cambiar contraseña si se proporcionó una nueva
            if (actualizado && nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                System.out.println("🔑 Actualizando contraseña del perfil...");
                actualizado = usuarioDAO.cambiarContrasena(usuarioLogueado.getId(), nuevaContrasena);
            }
            
            if (actualizado) {
                System.out.println("✅ Perfil actualizado exitosamente");
                // Actualizar la sesión
                request.getSession().setAttribute("usuarioLogueado", usuarioLogueado);
                request.getSession().setAttribute("nombreUsuario", usuarioLogueado.getNombre());
                
                request.setAttribute("mensaje", "Perfil actualizado exitosamente");
            } else {
                System.out.println("❌ Error al actualizar perfil en BD");
                request.setAttribute("error", "Error al actualizar el perfil");
            }
            
            mostrarPerfil(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en actualizarPerfil(): " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarPerfil(request, response);
        }
    }
    
    /**
     * Elimina un usuario
     */
    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🗑️ eliminarUsuario() - Iniciando eliminación de usuario");
        
        // Verificar autenticación y permisos de admin
        if (!LoginServlet.usuarioEstaLogueado(request) || !LoginServlet.usuarioEsAdmin(request)) {
            System.out.println("❌ Usuario no autorizado para eliminar usuarios");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("🔍 ID de usuario a eliminar: " + idStr);
            
            int id = Integer.parseInt(idStr);
            
            // No permitir que el admin se elimine a sí mismo
            UsuarioDTO usuarioLogueado = LoginServlet.obtenerUsuarioLogueado(request);
            if (usuarioLogueado.getId() == id) {
                System.out.println("❌ Intento de auto-eliminación bloqueado");
                request.getSession().setAttribute("error", "No puede eliminar su propio usuario");
                response.sendRedirect(request.getContextPath() + "/usuarios");
                return;
            }
            
            // Obtener información del usuario antes de eliminarlo
            UsuarioDTO usuario = usuarioDAO.obtenerUsuarioPorId(id);
            if (usuario == null) {
                System.out.println("❌ Usuario no encontrado con ID: " + id);
                request.getSession().setAttribute("error", "Usuario no encontrado");
                response.sendRedirect(request.getContextPath() + "/usuarios");
                return;
            }
            
            System.out.println("👤 Eliminando usuario: " + usuario.getNombre());
            
            if (usuarioDAO.eliminarUsuario(id)) {
                System.out.println("✅ Usuario eliminado exitosamente");
                request.getSession().setAttribute("mensaje", "Usuario '" + usuario.getNombre() + "' eliminado exitosamente");
            } else {
                System.out.println("❌ Error al eliminar usuario de BD");
                request.getSession().setAttribute("error", "Error al eliminar el usuario. Puede que tenga préstamos asociados.");
            }
            
            response.sendRedirect(request.getContextPath() + "/usuarios");
            
        } catch (NumberFormatException e) {
            System.err.println("❌ ID de usuario inválido: " + request.getParameter("id"));
            request.getSession().setAttribute("error", "ID de usuario inválido");
            response.sendRedirect(request.getContextPath() + "/usuarios");
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en eliminarUsuario(): " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error inesperado al eliminar el usuario");
            response.sendRedirect(request.getContextPath() + "/usuarios");
        }
    }
}