<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .login-container {
            max-width: 450px;
            margin: 0 auto;
        }
        .login-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            padding: 2rem;
        }
        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        .login-header i {
            font-size: 3rem;
            color: #667eea;
            margin-bottom: 1rem;
        }
        .captcha-container {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 1rem;
        }
        .captcha-image {
            border: 1px solid #ddd;
            border-radius: 5px;
            cursor: pointer;
        }
        .btn-refresh {
            border: none;
            background: #f8f9fa;
            color: #667eea;
            border-radius: 5px;
            padding: 8px 12px;
            cursor: pointer;
            transition: all 0.3s;
        }
        .btn-refresh:hover {
            background: #667eea;
            color: white;
        }
        .alert {
            border-radius: 10px;
            border: none;
        }
        .form-control {
            border-radius: 10px;
            border: 1px solid #ddd;
            padding: 12px 15px;
            transition: all 0.3s;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
            transition: all 0.3s;
        }
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .public-link {
            text-align: center;
            margin-top: 1.5rem;
        }
        .public-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
        }
        .public-link a:hover {
            color: #764ba2;
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="login-container">
            <div class="login-card">
                <div class="login-header">
                    <i class="fas fa-book-open"></i>
                    <h2 class="mb-0">Sistema de Biblioteca</h2>
                    <p class="text-muted">ADSO - Ingrese sus credenciales</p>
                </div>

                <!-- Mostrar mensajes de error -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        ${error}
                    </div>
                </c:if>

                <!-- Formulario de login -->
                <form method="post" action="${pageContext.request.contextPath}/login">
                    <div class="mb-3">
                        <label for="correo" class="form-label">
                            <i class="fas fa-envelope me-2"></i>Correo Electrónico
                        </label>
                        <input type="email" class="form-control" id="correo" name="correo" 
                               value="${correoAnterior}" required 
                               placeholder="Ingrese su correo electrónico">
                    </div>

                    <div class="mb-3">
                        <label for="contrasena" class="form-label">
                            <i class="fas fa-lock me-2"></i>Contraseña
                        </label>
                        <input type="password" class="form-control" id="contrasena" name="contrasena" 
                               required placeholder="Ingrese su contraseña">
                    </div>

                    <!-- CAPTCHA -->
                    <div class="mb-3">
                        <label for="captcha" class="form-label">
                            <i class="fas fa-shield-alt me-2"></i>Código de Verificación
                        </label>
                        <div class="captcha-container">
                            <img id="captchaImage" src="${captchaImagen}" alt="CAPTCHA" 
                                 class="captcha-image" title="Haga clic para generar nuevo código">
                            <button type="button" class="btn-refresh" onclick="regenerarCaptcha()" 
                                    title="Generar nuevo código">
                                <i class="fas fa-sync-alt"></i>
                            </button>
                        </div>
                        <input type="text" class="form-control mt-2" id="captcha" name="captcha" 
                               required placeholder="Ingrese el código mostrado en la imagen" 
                               autocomplete="off">
                        <small class="form-text text-muted">
                            Ingrese exactamente los caracteres que ve en la imagen
                        </small>
                    </div>

                    <button type="submit" class="btn btn-primary btn-login w-100">
                        <i class="fas fa-sign-in-alt me-2"></i>Iniciar Sesión
                    </button>
                </form>

                <!-- Enlaces adicionales -->
                <div class="public-link">
                    <a href="${pageContext.request.contextPath}/consulta-publica">
                        <i class="fas fa-search me-2"></i>Consultar catálogo público
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Función para regenerar CAPTCHA
        function regenerarCaptcha() {
            fetch('${pageContext.request.contextPath}/captcha')
                .then(response => response.json())
                .then(data => {
                    document.getElementById('captchaImage').src = data.imagen;
                    document.getElementById('captcha').value = '';
                    document.getElementById('captcha').focus();
                })
                .catch(error => {
                    console.error('Error al regenerar CAPTCHA:', error);
                    alert('Error al generar nuevo código. Intente recargar la página.');
                });
        }

        // Regenerar CAPTCHA al hacer clic en la imagen
        document.getElementById('captchaImage').addEventListener('click', regenerarCaptcha);

        // Enfocar primer campo al cargar
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('correo').focus();
        });

        // Validación básica del formulario
        document.querySelector('form').addEventListener('submit', function(e) {
            const correo = document.getElementById('correo').value.trim();
            const contrasena = document.getElementById('contrasena').value.trim();
            const captcha = document.getElementById('captcha').value.trim();

            if (!correo || !contrasena || !captcha) {
                e.preventDefault();
                alert('Por favor, complete todos los campos.');
                return false;
            }

            if (captcha.length !== 6) {
                e.preventDefault();
                alert('El código de verificación debe tener 6 caracteres.');
                document.getElementById('captcha').focus();
                return false;
            }
        });
    </script>
</body>
</html>