<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuevo Préstamo - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .navbar-brand {
            font-weight: 600;
        }
        .content-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
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
        .info-card {
            background: linear-gradient(135deg, #17a2b8 0%, #20c997 100%);
            color: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 2rem;
        }
        .libro-preview {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 15px;
            margin-top: 10px;
            display: none;
        }
        .usuario-preview {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 15px;
            margin-top: 10px;
            display: none;
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
                    <c:choose>
                        <c:when test="${sessionScope.rolUsuario == 'admin'}">
                            <i class="fas fa-user-shield me-1"></i>${sessionScope.nombreUsuario}
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-user me-1"></i>${sessionScope.nombreUsuario}
                        </c:otherwise>
                    </c:choose>
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
                        <i class="fas fa-handshake me-3"></i>Solicitar Préstamo
                    </h1>
                    <p class="mb-0 mt-2">
                        Complete el formulario para solicitar un préstamo de libro
                    </p>
                </div>
                <div class="col-md-4 text-md-end">
                    <c:choose>
                        <c:when test="${sessionScope.rolUsuario == 'admin'}">
                            <a href="${pageContext.request.contextPath}/prestamos" class="btn btn-light btn-lg">
                                <i class="fas fa-arrow-left me-2"></i>Volver a Préstamos
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/mis-prestamos" class="btn btn-light btn-lg">
                                <i class="fas fa-arrow-left me-2"></i>Mis Préstamos
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <!-- Información importante -->
                <div class="info-card">
                    <div class="row align-items-center">
                        <div class="col-md-8">
                            <h5><i class="fas fa-info-circle me-2"></i>Información del Préstamo</h5>
                            <p class="mb-1"><strong>Duración:</strong> 15 días para libros generales</p>
                            <p class="mb-1"><strong>Fecha de hoy:</strong> <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy"/></p>
                            <p class="mb-0"><strong>Fecha máxima devolución:</strong> <fmt:formatDate value="<%= new java.util.Date(System.currentTimeMillis() + 15L*24*60*60*1000) %>" pattern="dd/MM/yyyy"/></p>
                        </div>
                        <div class="col-md-4 text-center">
                            <i class="fas fa-calendar-alt fa-3x"></i>
                        </div>
                    </div>
                </div>

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
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-plus-circle me-2"></i>
                            Datos del Préstamo
                        </h5>
                    </div>
                    <div class="card-body p-4">
                        <form method="post" action="${pageContext.request.contextPath}/prestamo-nuevo" id="prestamoForm">
                            <div class="row">
                                <!-- Selección de libro -->
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="idLibro" class="form-label">
                                            <i class="fas fa-book me-1 text-primary"></i>
                                            Libro <span class="text-danger">*</span>
                                        </label>
                                        <select class="form-select form-select-lg" id="idLibro" name="idLibro" required onchange="mostrarInfoLibro()">
                                            <option value="">Seleccione un libro</option>
                                            <c:forEach var="libro" items="${librosDisponibles}">
                                                <option value="${libro.id}" 
                                                        data-titulo="${libro.titulo}"
                                                        data-autor="${libro.autor}"
                                                        data-editorial="${libro.editorial}"
                                                        data-categoria="${libro.categoria}">
                                                    ${libro.titulo} - ${libro.autor}
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <!-- Preview del libro seleccionado -->
                                        <div id="libroPreview" class="libro-preview">
                                            <h6><i class="fas fa-book me-2 text-primary"></i>Información del Libro</h6>
                                            <p class="mb-1"><strong>Título:</strong> <span id="libroTitulo"></span></p>
                                            <p class="mb-1"><strong>Autor:</strong> <span id="libroAutor"></span></p>
                                            <p class="mb-1"><strong>Editorial:</strong> <span id="libroEditorial"></span></p>
                                            <p class="mb-0"><strong>Categoría:</strong> <span id="libroCategoria"></span></p>
                                        </div>
                                    </div>
                                </div>

                                <!-- Selección de usuario (solo para admin) -->
                                <div class="col-md-6">
                                    <c:if test="${sessionScope.rolUsuario == 'admin' && not empty usuarios}">
                                        <div class="mb-3">
                                            <label for="idUsuario" class="form-label">
                                                <i class="fas fa-user me-1 text-success"></i>
                                                Usuario <span class="text-danger">*</span>
                                            </label>
                                            <select class="form-select form-select-lg" id="idUsuario" name="idUsuario" required onchange="mostrarInfoUsuario()">
                                                <option value="">Seleccione un usuario</option>
                                                <c:forEach var="usuario" items="${usuarios}">
                                                    <option value="${usuario.id}"
                                                            data-nombre="${usuario.nombre}"
                                                            data-documento="${usuario.documento}"
                                                            data-correo="${usuario.correo}"
                                                            data-rol="${usuario.rol}">
                                                        ${usuario.nombre} - ${usuario.documento}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <!-- Preview del usuario seleccionado -->
                                            <div id="usuarioPreview" class="usuario-preview">
                                                <h6><i class="fas fa-user me-2 text-success"></i>Información del Usuario</h6>
                                                <p class="mb-1"><strong>Nombre:</strong> <span id="usuarioNombre"></span></p>
                                                <p class="mb-1"><strong>Documento:</strong> <span id="usuarioDocumento"></span></p>
                                                <p class="mb-1"><strong>Correo:</strong> <span id="usuarioCorreo"></span></p>
                                                <p class="mb-0"><strong>Rol:</strong> <span id="usuarioRol"></span></p>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${sessionScope.rolUsuario != 'admin'}">
                                        <!-- Para usuarios normales, mostrar su información -->
                                        <div class="mb-3">
                                            <label class="form-label">
                                                <i class="fas fa-user me-1 text-success"></i>
                                                Solicitante
                                            </label>
                                            <div class="usuario-preview" style="display: block;">
                                                <h6><i class="fas fa-user me-2 text-success"></i>Tu Información</h6>
                                                <p class="mb-1"><strong>Nombre:</strong> ${sessionScope.nombreUsuario}</p>
                                                <p class="mb-1"><strong>Rol:</strong> Usuario</p>
                                                <p class="mb-0"><small class="text-muted">El préstamo se registrará a tu nombre</small></p>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="row">
                                <!-- Fecha de devolución -->
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="fechaDevolucion" class="form-label">
                                            <i class="fas fa-calendar-alt me-1 text-warning"></i>
                                            Fecha de Devolución <span class="text-danger">*</span>
                                        </label>
                                        <input type="date" class="form-control form-control-lg" 
                                               id="fechaDevolucion" name="fechaDevolucion" 
                                               required min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 24*60*60*1000)) %>"
                                               max="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 30L*24*60*60*1000)) %>"
                                               value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 15L*24*60*60*1000)) %>">
                                        <small class="form-text text-muted">
                                            Máximo 30 días desde hoy. Por defecto: 15 días.
                                        </small>
                                    </div>
                                </div>

                                <!-- Información adicional -->
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label">
                                            <i class="fas fa-info-circle me-1 text-info"></i>
                                            Resumen del Préstamo
                                        </label>
                                        <div class="alert alert-info">
                                            <p class="mb-1"><i class="fas fa-calendar me-2"></i><strong>Fecha de préstamo:</strong> Hoy</p>
                                            <p class="mb-1"><i class="fas fa-clock me-2"></i><strong>Duración:</strong> <span id="duracionPrestamo">15 días</span></p>
                                            <p class="mb-0"><i class="fas fa-exclamation-triangle me-2"></i><strong>Recuerde:</strong> Devolver a tiempo</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Términos y condiciones -->
                            <div class="row">
                                <div class="col-12">
                                    <div class="mb-4">
                                        <div class="form-check form-check-lg">
                                            <input class="form-check-input" type="checkbox" id="aceptarTerminos" required>
                                            <label class="form-check-label" for="aceptarTerminos">
                                                <strong>Acepto los términos y condiciones del préstamo</strong>
                                            </label>
                                        </div>
                                        <small class="form-text text-muted">
                                            Al marcar esta casilla, confirmo que me comprometo a devolver el libro en la fecha acordada y en buen estado.
                                        </small>
                                    </div>
                                </div>
                            </div>

                            <!-- Botones -->
                            <div class="row">
                                <div class="col-12">
                                    <div class="d-flex gap-3 justify-content-center">
                                        <button type="submit" class="btn btn-success btn-custom btn-lg">
                                            <i class="fas fa-handshake me-2"></i>
                                            Solicitar Préstamo
                                        </button>
                                        <c:choose>
                                            <c:when test="${sessionScope.rolUsuario == 'admin'}">
                                                <a href="${pageContext.request.contextPath}/prestamos" 
                                                   class="btn btn-secondary btn-custom btn-lg">
                                                    <i class="fas fa-times me-2"></i>Cancelar
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/user-dashboard" 
                                                   class="btn btn-secondary btn-custom btn-lg">
                                                    <i class="fas fa-times me-2"></i>Cancelar
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                        <button type="button" class="btn btn-info btn-custom btn-lg" onclick="resetForm()">
                                            <i class="fas fa-undo me-2"></i>Limpiar
                                        </button>
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
                                    <i class="fas fa-info-circle me-2"></i>Información Importante
                                </h6>
                            </div>
                            <div class="card-body">
                                <p class="mb-1"><i class="fas fa-calendar me-2 text-primary"></i>Duración estándar: 15 días</p>
                                <p class="mb-1"><i class="fas fa-money-bill me-2 text-warning"></i>Multa por retraso: $1,000/día</p>
                                <p class="mb-1"><i class="fas fa-book me-2 text-success"></i>Solo libros disponibles aparecen en la lista</p>
                                <p class="mb-0"><i class="fas fa-phone me-2 text-info"></i>Soporte: biblioteca@adso.edu.co</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header bg-warning text-dark">
                                <h6 class="mb-0">
                                    <i class="fas fa-exclamation-triangle me-2"></i>Responsabilidades
                                </h6>
                            </div>
                            <div class="card-body">
                                <p class="mb-1"><i class="fas fa-shield-alt me-2 text-success"></i>Cuidar el libro prestado</p>
                                <p class="mb-1"><i class="fas fa-clock me-2 text-primary"></i>Devolver en la fecha acordada</p>
                                <p class="mb-1"><i class="fas fa-ban me-2 text-danger"></i>No prestar a terceros</p>
                                <p class="mb-0"><i class="fas fa-tools me-2 text-warning"></i>Reportar daños inmediatamente</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Estado de libros disponibles -->
                <c:if test="${empty librosDisponibles}">
                    <div class="alert alert-warning mt-4">
                        <h5><i class="fas fa-exclamation-triangle me-2"></i>No hay libros disponibles</h5>
                        <p class="mb-0">Actualmente no hay libros disponibles para préstamo. Inténtelo más tarde o contacte al administrador.</p>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Función para mostrar información del libro seleccionado
        function mostrarInfoLibro() {
            const select = document.getElementById('idLibro');
            const preview = document.getElementById('libroPreview');
            
            if (select.value) {
                const option = select.options[select.selectedIndex];
                document.getElementById('libroTitulo').textContent = option.getAttribute('data-titulo');
                document.getElementById('libroAutor').textContent = option.getAttribute('data-autor');
                document.getElementById('libroEditorial').textContent = option.getAttribute('data-editorial') || 'No especificada';
                document.getElementById('libroCategoria').textContent = option.getAttribute('data-categoria') || 'Sin categoría';
                preview.style.display = 'block';
            } else {
                preview.style.display = 'none';
            }
        }

        // Función para mostrar información del usuario seleccionado (solo admin)
        function mostrarInfoUsuario() {
            const select = document.getElementById('idUsuario');
            const preview = document.getElementById('usuarioPreview');
            
            if (select && select.value) {
                const option = select.options[select.selectedIndex];
                document.getElementById('usuarioNombre').textContent = option.getAttribute('data-nombre');
                document.getElementById('usuarioDocumento').textContent = option.getAttribute('data-documento');
                document.getElementById('usuarioCorreo').textContent = option.getAttribute('data-correo');
                
                const rol = option.getAttribute('data-rol');
                document.getElementById('usuarioRol').textContent = rol === 'admin' ? 'Administrador' : 'Usuario';
                
                preview.style.display = 'block';
            } else if (preview) {
                preview.style.display = 'none';
            }
        }

        // Calcular duración del préstamo
        function calcularDuracion() {
            const fechaDevolucion = document.getElementById('fechaDevolucion').value;
            if (fechaDevolucion) {
                const hoy = new Date();
                const fechaDev = new Date(fechaDevolucion);
                const diferencia = Math.ceil((fechaDev - hoy) / (1000 * 60 * 60 * 24));
                document.getElementById('duracionPrestamo').textContent = diferencia + ' días';
            }
        }

        // Event listener para cambio de fecha
        document.getElementById('fechaDevolucion').addEventListener('change', calcularDuracion);

        // Validación del formulario
        document.getElementById('prestamoForm').addEventListener('submit', function(e) {
            const libro = document.getElementById('idLibro').value;
            const fechaDevolucion = document.getElementById('fechaDevolucion').value;
            const terminos = document.getElementById('aceptarTerminos').checked;
            
            // Validar campos requeridos
            if (!libro) {
                e.preventDefault();
                alert('Por favor, seleccione un libro.');
                document.getElementById('idLibro').focus();
                return false;
            }
            
            // Validar usuario si es admin
            const usuarioSelect = document.getElementById('idUsuario');
            if (usuarioSelect && !usuarioSelect.value) {
                e.preventDefault();
                alert('Por favor, seleccione un usuario.');
                usuarioSelect.focus();
                return false;
            }
            
            if (!fechaDevolucion) {
                e.preventDefault();
                alert('Por favor, seleccione una fecha de devolución.');
                document.getElementById('fechaDevolucion').focus();
                return false;
            }
            
            if (!terminos) {
                e.preventDefault();
                alert('Debe aceptar los términos y condiciones del préstamo.');
                document.getElementById('aceptarTerminos').focus();
                return false;
            }
            
            // Validar fecha no sea en el pasado
            const hoy = new Date();
            const fechaDev = new Date(fechaDevolucion);
            if (fechaDev <= hoy) {
                e.preventDefault();
                alert('La fecha de devolución debe ser posterior a hoy.');
                document.getElementById('fechaDevolucion').focus();
                return false;
            }
        });

        // Función para limpiar formulario
        function resetForm() {
            if (confirm('¿Está seguro de que desea limpiar el formulario?')) {
                document.getElementById('prestamoForm').reset();
                document.getElementById('libroPreview').style.display = 'none';
                const usuarioPreview = document.getElementById('usuarioPreview');
                if (usuarioPreview && usuarioPreview.classList.contains('usuario-preview')) {
                    usuarioPreview.style.display = 'none';
                }
                document.getElementById('duracionPrestamo').textContent = '15 días';
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

            // Calcular duración inicial
            calcularDuracion();
        });

        // Prevenir envío duplicado
        let formSubmitted = false;
        document.getElementById('prestamoForm').addEventListener('submit', function(e) {
            if (formSubmitted) {
                e.preventDefault();
                return false;
            }
            formSubmitted = true;
            // Deshabilitar botón de envío
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Procesando...';
        });
    </script>
</body>
</html>