<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${accion == 'nuevo' ? 'Nuevo' : 'Editar'} Usuario - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .navbar-brand {
            font-weight: 600;
        }
        .content-header {
            background: linear-gradient(135deg, #6f42c1 0%, #6610f2 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .form-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }
        .btn-custom {
            border-radius: 25px;
            padding: 10px 30px;
            font-weight: 600;
        }
        .password-strength {
            margin-top: 5px;
        }
        .strength-weak { color: #dc3545; }
        .strength-medium { color: #ffc107; }
        .strength-strong { color: #28a745; }
        .role-option {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 10px;
            cursor: pointer;
            transition: all 0.3s;
        }
        .role-option:hover {
            border-color: #007bff;
            background-color: #f8f9fa;
        }
        .role-option.selected {
            border-color: #007bff;
            background-color: #e7f3ff;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-book-open me-2"></i>
                Sistema de Biblioteca ADSO
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="fas fa-user-shield me-1"></i>${sessionScope.nombreUsuario}
                </span>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt"></i> Salir
                </a>
            </div>
        </div>
    </nav>

    <!-- Header -->
    <div class="content-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="mb-0">
                        <i class="fas ${accion == 'nuevo' ? 'fa-user-plus' : 'fa-user-edit'} me-3"></i>
                        ${accion == 'nuevo' ? 'Nuevo' : 'Editar'} Usuario
                    </h1>
                    <p class="mb-0 mt-2">
                        ${accion == 'nuevo' ? 'Registre un nuevo usuario en el sistema' : 'Modifique la información del usuario'}
                    </p>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="${pageContext.request.contextPath}/usuarios" class="btn btn-light btn-lg">
                        <i class="fas fa-arrow-left me-2"></i>Volver a Usuarios
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <!-- Mensajes -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${not empty mensaje}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>
                        ${mensaje}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Formulario -->
                <div class="card form-card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-user me-2"></i>
                            Información del Usuario
                        </h5>
                    </div>
                    <div class="card-body p-4">
                        <form method="post" action="${pageContext.request.contextPath}/${accion == 'nuevo' ? 'usuario-nuevo' : 'usuario-editar'}" id="usuarioForm">
                            <c:if test="${accion == 'editar'}">
                                <input type="hidden" name="id" value="${usuario.id}">
                            </c:if>

                            <!-- Información Personal -->
                            <div class="row">
                                <div class="col-12">
                                    <h6 class="text-primary mb-3">
                                        <i class="fas fa-id-card me-2"></i>Información Personal
                                    </h6>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="nombre" class="form-label">
                                            <i class="fas fa-user me-1 text-primary"></i>
                                            Nombre Completo <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" class="form-control form-control-lg" 
                                               id="nombre" name="nombre" 
                                               value="${usuario.nombre}" 
                                               required maxlength="255"
                                               placeholder="Ingrese el nombre completo">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="documento" class="form-label">
                                            <i class="fas fa-id-badge me-1 text-success"></i>
                                            Documento de Identidad <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" class="form-control form-control-lg" 
                                               id="documento" name="documento" 
                                               value="${usuario.documento}" 
                                               required maxlength="20" pattern="[0-9]+"
                                               placeholder="Ej: 1234567890"
                                               ${accion == 'editar' ? 'readonly' : ''}>
                                        <small class="form-text text-muted">Solo números, sin puntos ni espacios</small>
                                    </div>
                                </div>
                            </div>

                            <!-- Información de Contacto -->
                            <div class="row">
                                <div class="col-12">
                                    <h6 class="text-success mb-3 mt-3">
                                        <i class="fas fa-address-book me-2"></i>Información de Contacto
                                    </h6>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="correo" class="form-label">
                                            <i class="fas fa-envelope me-1 text-info"></i>
                                            Correo Electrónico <span class="text-danger">*</span>
                                        </label>
                                        <input type="email" class="form-control form-control-lg" 
                                               id="correo" name="correo" 
                                               value="${usuario.correo}" 
                                               required maxlength="255"
                                               placeholder="ejemplo@correo.com"
                                               ${accion == 'editar' ? 'readonly' : ''}>
                                        <c:if test="${accion == 'editar'}">
                                            <small class="form-text text-muted">El correo no se puede modificar</small>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="telefono" class="form-label">
                                            <i class="fas fa-phone me-1 text-warning"></i>
                                            Teléfono
                                        </label>
                                        <input type="tel" class="form-control form-control-lg" 
                                               id="telefono" name="telefono" 
                                               value="${usuario.telefono}" 
                                               maxlength="15" pattern="[0-9\-\+\(\)\s]+"
                                               placeholder="Ej: 300-123-4567">
                                    </div>
                                </div>
                            </div>

                            <!-- Información del Sistema -->
                            <div class="row">
                                <div class="col-12">
                                    <h6 class="text-warning mb-3 mt-3">
                                        <i class="fas fa-cog me-2"></i>Configuración del Sistema
                                    </h6>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label">
                                            <i class="fas fa-user-tag me-1 text-secondary"></i>
                                            Rol del Usuario <span class="text-danger">*</span>
                                        </label>
                                        
                                        <!-- Opciones de rol mejoradas -->
                                        <div class="role-options">
                                            <div class="role-option" onclick="selectRole('lector')" id="role-lector">
                                                <input type="radio" name="rol" value="lector" id="rol-lector" 
                                                       ${usuario.rol == 'lector' || empty usuario.rol ? 'checked' : ''} style="display: none;">
                                                <div class="d-flex align-items-center">
                                                    <div class="me-3">
                                                        <i class="fas fa-user fa-2x text-success"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="mb-1">👤 Lector</h6>
                                                        <small class="text-muted">Puede buscar libros, solicitar préstamos y gestionar sus devoluciones</small>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                            <div class="role-option" onclick="selectRole('admin')" id="role-admin">
                                                <input type="radio" name="rol" value="admin" id="rol-admin" 
                                                       ${usuario.rol == 'admin' ? 'checked' : ''} style="display: none;">
                                                <div class="d-flex align-items-center">
                                                    <div class="me-3">
                                                        <i class="fas fa-user-shield fa-2x text-danger"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="mb-1">🛡️ Administrador</h6>
                                                        <small class="text-muted">Acceso completo: gestión de libros, usuarios, préstamos y reportes</small>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <c:if test="${accion == 'nuevo'}">
                                        <div class="mb-3">
                                            <label for="contrasena" class="form-label">
                                                <i class="fas fa-lock me-1 text-danger"></i>
                                                Contraseña <span class="text-danger">*</span>
                                            </label>
                                            <div class="input-group">
                                                <input type="password" class="form-control form-control-lg" 
                                                       id="contrasena" name="contrasena" 
                                                       required minlength="6" maxlength="50"
                                                       placeholder="Mínimo 6 caracteres">
                                                <button class="btn btn-outline-secondary" type="button" 
                                                        onclick="togglePassword('contrasena')">
                                                    <i class="fas fa-eye" id="contrasena-icon"></i>
                                                </button>
                                            </div>
                                            <div class="password-strength" id="password-strength"></div>
                                        </div>
                                    </c:if>
                                    <c:if test="${accion == 'editar'}">
                                        <div class="mb-3">
                                            <label for="nuevaContrasena" class="form-label">
                                                <i class="fas fa-key me-1 text-danger"></i>
                                                Nueva Contraseña (Opcional)
                                            </label>
                                            <div class="input-group">
                                                <input type="password" class="form-control form-control-lg" 
                                                       id="nuevaContrasena" name="nuevaContrasena" 
                                                       minlength="6" maxlength="50"
                                                       placeholder="Dejar vacío para mantener actual">
                                                <button class="btn btn-outline-secondary" type="button" 
                                                        onclick="togglePassword('nuevaContrasena')">
                                                    <i class="fas fa-eye" id="nuevaContrasena-icon"></i>
                                                </button>
                                            </div>
                                            <small class="form-text text-muted">
                                                Solo complete si desea cambiar la contraseña
                                            </small>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <!-- Botones -->
                            <div class="row">
                                <div class="col-12">
                                    <div class="d-flex gap-3 justify-content-center">
                                        <button type="submit" class="btn btn-primary btn-custom btn-lg">
                                            <i class="fas ${accion == 'nuevo' ? 'fa-user-plus' : 'fa-save'} me-2"></i>
                                            ${accion == 'nuevo' ? 'Crear Usuario' : 'Guardar Cambios'}
                                        </button>
                                        <a href="${pageContext.request.contextPath}/usuarios" 
                                           class="btn btn-secondary btn-custom btn-lg">
                                            <i class="fas fa-times me-2"></i>Cancelar
                                        </a>
                                        <c:if test="${accion == 'editar'}">
                                            <button type="button" class="btn btn-info btn-custom btn-lg" onclick="resetForm()">
                                                <i class="fas fa-undo me-2"></i>Restaurar
                                            </button>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Información adicional -->
                <div class="row mt-4">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header bg-info text-white">
                                <h6 class="mb-0">
                                    <i class="fas fa-info-circle me-2"></i>Información
                                </h6>
                            </div>
                            <div class="card-body">
                                <p class="mb-1"><i class="fas fa-asterisk me-2 text-danger"></i>Los campos marcados con * son obligatorios</p>
                                <p class="mb-1"><i class="fas fa-envelope me-2 text-info"></i>El correo debe ser único en el sistema</p>
                                <p class="mb-1"><i class="fas fa-id-badge me-2 text-success"></i>El documento debe ser único</p>
                                <p class="mb-0"><i class="fas fa-lock me-2 text-warning"></i>La contraseña debe tener mínimo 6 caracteres</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header bg-warning text-dark">
                                <h6 class="mb-0">
                                    <i class="fas fa-user-cog me-2"></i>Roles de Usuario
                                </h6>
                            </div>
                            <div class="card-body">
                                <p class="mb-1"><i class="fas fa-user me-2 text-success"></i><strong>Lector:</strong> Puede buscar libros y gestionar sus préstamos</p>
                                <p class="mb-0"><i class="fas fa-user-shield me-2 text-danger"></i><strong>Admin:</strong> Acceso completo al sistema, gestión de libros y usuarios</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Función para seleccionar rol
        function selectRole(role) {
            // Limpiar selecciones anteriores
            document.querySelectorAll('.role-option').forEach(option => {
                option.classList.remove('selected');
            });
            
            // Limpiar radio buttons
            document.querySelectorAll('input[name="rol"]').forEach(radio => {
                radio.checked = false;
            });
            
            // Seleccionar el rol clickeado
            document.getElementById('role-' + role).classList.add('selected');
            document.getElementById('rol-' + role).checked = true;
            
            console.log('Rol seleccionado:', role);
        }

        // Inicializar la selección visual al cargar la página
        document.addEventListener('DOMContentLoaded', function() {
            const rolSeleccionado = document.querySelector('input[name="rol"]:checked');
            if (rolSeleccionado) {
                selectRole(rolSeleccionado.value);
            } else {
                // Seleccionar "lector" por defecto
                selectRole('lector');
            }
        });

        // Función para mostrar/ocultar contraseña
        function togglePassword(fieldId) {
            const field = document.getElementById(fieldId);
            const icon = document.getElementById(fieldId + '-icon');
            
            if (field.type === 'password') {
                field.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                field.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }

        // Validador de fuerza de contraseña
        function checkPasswordStrength(password) {
            let strength = 0;
            let feedback = [];

            if (password.length >= 6) strength++;
            else feedback.push('Mínimo 6 caracteres');

            if (password.match(/[a-z]/)) strength++;
            else feedback.push('Una letra minúscula');

            if (password.match(/[A-Z]/)) strength++;
            else feedback.push('Una letra mayúscula');

            if (password.match(/[0-9]/)) strength++;
            else feedback.push('Un número');

            if (password.match(/[^a-zA-Z0-9]/)) strength++;
            else feedback.push('Un carácter especial');

            return { strength, feedback };
        }

        // Listener para validación de contraseña en tiempo real
        document.addEventListener('DOMContentLoaded', function() {
            const passwordField = document.getElementById('contrasena') || document.getElementById('nuevaContrasena');
            const strengthDiv = document.getElementById('password-strength');
            
            if (passwordField && strengthDiv) {
                passwordField.addEventListener('input', function() {
                    const password = this.value;
                    if (password.length === 0) {
                        strengthDiv.innerHTML = '';
                        return;
                    }

                    const result = checkPasswordStrength(password);
                    let strengthText = '';
                    let strengthClass = '';

                    if (result.strength <= 2) {
                        strengthText = 'Débil';
                        strengthClass = 'strength-weak';
                    } else if (result.strength <= 3) {
                        strengthText = 'Media';
                        strengthClass = 'strength-medium';
                    } else {
                        strengthText = 'Fuerte';
                        strengthClass = 'strength-strong';
                    }

                    strengthDiv.innerHTML = `<small class="${strengthClass}">Fortaleza: ${strengthText}</small>`;
                    
                    if (result.feedback.length > 0) {
                        strengthDiv.innerHTML += `<br><small class="text-muted">Agregar: ${result.feedback.join(', ')}</small>`;
                    }
                });
            }
        });

        // Validación del formulario
        document.getElementById('usuarioForm').addEventListener('submit', function(e) {
            const documento = document.getElementById('documento').value;
            const correo = document.getElementById('correo').value;
            const rolSeleccionado = document.querySelector('input[name="rol"]:checked');
            
            // Validar que se haya seleccionado un rol
            if (!rolSeleccionado) {
                e.preventDefault();
                alert('Por favor, seleccione un rol para el usuario.');
                return false;
            }
            
            console.log('Rol enviado en formulario:', rolSeleccionado.value);
            
            // Validar documento (solo números)
            if (!/^\d+$/.test(documento)) {
                e.preventDefault();
                alert('El documento debe contener solo números.');
                document.getElementById('documento').focus();
                return false;
            }
            
            // Validar correo
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(correo)) {
                e.preventDefault();
                alert('Por favor, ingrese un correo electrónico válido.');
                document.getElementById('correo').focus();
                return false;
            }
        });

        // Función para restaurar formulario
        function resetForm() {
            if (confirm('¿Está seguro de que desea restaurar todos los campos a sus valores originales?')) {
                document.getElementById('usuarioForm').reset();
                // Restaurar valores originales si es edición
                <c:if test="${accion == 'editar'}">
                    document.getElementById('nombre').value = '${usuario.nombre}';
                    document.getElementById('documento').value = '${usuario.documento}';
                    document.getElementById('correo').value = '${usuario.correo}';
                    document.getElementById('telefono').value = '${usuario.telefono}';
                    selectRole('${usuario.rol}');
                </c:if>
            }
        }

        // Auto-ocultar alertas
        document.addEventListener('DOMContentLoaded', function() {
            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert-dismissible');
                alerts.forEach(function(alert) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);

            // Enfocar primer campo
            document.getElementById('nombre').focus();
        });

        // Prevenir envío duplicado
        let formSubmitted = false;
        document.getElementById('usuarioForm').addEventListener('submit', function(e) {
            if (formSubmitted) {
                e.preventDefault();
                return false;
            }
            formSubmitted = true;
            // Deshabilitar botón de envío
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Guardando...';
        });

        // Formatear teléfono automáticamente
        document.getElementById('telefono').addEventListener('input', function() {
            let value = this.value.replace(/\D/g, '');
            if (value.length >= 10) {
                value = value.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
            }
            this.value = value;
        });
    </script>
</body>
</html>