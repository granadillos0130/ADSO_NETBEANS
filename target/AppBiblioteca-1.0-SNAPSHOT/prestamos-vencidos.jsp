<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Préstamos Vencidos - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <style>
        .navbar-brand {
            font-weight: 600;
        }
        .content-header {
            background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .alert-danger-custom {
            background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
            border: 1px solid #f5c6cb;
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .vencido-card {
            border-left: 5px solid #dc3545;
            transition: all 0.3s;
        }
        .vencido-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(220, 53, 69, 0.2);
        }
        .dias-vencido {
            background: #dc3545;
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-weight: bold;
        }
        .btn-action {
            margin: 0 2px;
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 0.875rem;
        }
        .table-responsive {
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        .urgente {
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0% { background-color: #fff5f5; }
            50% { background-color: #fed7d7; }
            100% { background-color: #fff5f5; }
        }
        .stats-danger {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            color: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 2rem;
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
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin-dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/libros">
                            <i class="fas fa-book me-1"></i>Libros
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/usuarios">
                            <i class="fas fa-users me-1"></i>Usuarios
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/prestamos">
                            <i class="fas fa-exchange-alt me-1"></i>Préstamos
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/prestamos-vencidos">
                            <i class="fas fa-exclamation-triangle me-1"></i>Vencidos
                        </a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user me-1"></i>${sessionScope.nombreUsuario}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/perfil">
                                <i class="fas fa-user-edit me-2"></i>Mi Perfil
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Header -->
    <div class="content-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="mb-0">
                        <i class="fas fa-exclamation-triangle me-3"></i>Préstamos Vencidos
                    </h1>
                    <p class="mb-0 mt-2">Gestione los préstamos que han superado su fecha de devolución</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <div class="btn-group">
                        <a href="${pageContext.request.contextPath}/prestamos" class="btn btn-light btn-lg">
                            <i class="fas fa-arrow-left me-2"></i>Todos los Préstamos
                        </a>
                        <a href="${pageContext.request.contextPath}/prestamo-nuevo" class="btn btn-warning btn-lg">
                            <i class="fas fa-plus me-2"></i>Nuevo Préstamo
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Alerta importante -->
        <div class="stats-danger">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h4><i class="fas fa-bell me-2"></i>Atención Requerida</h4>
                    <p class="mb-1"><strong>${prestamos.size()}</strong> préstamos vencidos requieren seguimiento</p>
                    <p class="mb-0">Es importante contactar a los usuarios para coordinar la devolución</p>
                </div>
                <div class="col-md-4 text-center">
                    <i class="fas fa-exclamation-triangle fa-4x opacity-50"></i>
                </div>
            </div>
        </div>

        <!-- Mensajes -->
        <c:if test="${not empty mensaje}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Lista de préstamos vencidos -->
        <c:choose>
            <c:when test="${not empty prestamos}">
                <!-- Tabla de préstamos vencidos -->
                <div class="card">
                    <div class="card-header bg-danger text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-list me-2"></i>Préstamos Vencidos
                            <span class="badge bg-light text-danger ms-2">${prestamos.size()} registros</span>
                        </h5>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0" id="vencidosTable">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Usuario</th>
                                        <th>Contacto</th>
                                        <th>Libro</th>
                                        <th>Fecha Vencimiento</th>
                                        <th>Días Vencido</th>
                                        <th>Multa Estimada</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <jsp:useBean id="hoy" class="java.util.Date"/>
                                    <c:forEach var="prestamo" items="${prestamos}">
                                        <c:set var="diasVencido" value="${(hoy.time - prestamo.fechaDevolucion.time) / (1000 * 60 * 60 * 24)}"/>
                                        <c:set var="diasVencidoInt" value="${diasVencido - (diasVencido % 1)}"/>
                                        <c:set var="multa" value="${diasVencidoInt * 1000}"/>
                                        
                                        <tr class="${diasVencidoInt > 7 ? 'urgente' : ''}">
                                            <td><strong>#${prestamo.id}</strong></td>
                                            <td>
                                                <i class="fas fa-user me-2 text-muted"></i>
                                                <strong>${prestamo.nombreUsuario}</strong>
                                            </td>
                                            <td>
                                                <small class="text-muted">
                                                    <i class="fas fa-envelope me-1"></i>Contactar<br>
                                                    <i class="fas fa-phone me-1"></i>Llamar
                                                </small>
                                            </td>
                                            <td>
                                                <strong>${prestamo.tituloLibro}</strong><br>
                                                <small class="text-muted">${prestamo.autorLibro}</small>
                                            </td>
                                            <td>
                                                <span class="text-danger">
                                                    <i class="fas fa-calendar-times me-1"></i>
                                                    <fmt:formatDate value="${prestamo.fechaDevolucion}" pattern="dd/MM/yyyy"/>
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${diasVencidoInt > 30}">
                                                        <span class="badge bg-dark">${diasVencidoInt} días</span>
                                                    </c:when>
                                                    <c:when test="${diasVencidoInt > 7}">
                                                        <span class="badge bg-danger">${diasVencidoInt} días</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning text-dark">${diasVencidoInt} días</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="text-danger fw-bold">
                                                    $<fmt:formatNumber value="${multa}" pattern="#,###"/>
                                                </span>
                                            </td>
                                            <td>
                                                <div class="btn-group" role="group">
                                                    <button type="button" 
                                                            class="btn btn-sm btn-outline-success btn-action" 
                                                            onclick="confirmarDevolucion(${prestamo.id}, '${prestamo.tituloLibro}')"
                                                            title="Marcar como devuelto">
                                                        <i class="fas fa-undo"></i>
                                                    </button>
                                                    <button type="button" 
                                                            class="btn btn-sm btn-outline-info btn-action" 
                                                            onclick="contactarUsuario('${prestamo.nombreUsuario}', '${prestamo.tituloLibro}', ${diasVencidoInt})"
                                                            title="Contactar usuario">
                                                        <i class="fas fa-envelope"></i>
                                                    </button>
                                                    <a href="${pageContext.request.contextPath}/generar-pdf?id=${prestamo.id}" 
                                                       class="btn btn-sm btn-outline-primary btn-action" 
                                                       title="Generar comprobante" target="_blank">
                                                        <i class="fas fa-file-pdf"></i>
                                                    </a>
                                                    <button type="button" 
                                                            class="btn btn-sm btn-outline-danger btn-action" 
                                                            onclick="confirmarEliminacion(${prestamo.id}, '${prestamo.tituloLibro}')"
                                                            title="Eliminar préstamo">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Resumen de multas -->
                <div class="row mt-4">
                    <div class="col-md-4">
                        <div class="card text-center">
                            <div class="card-body">
                                <i class="fas fa-money-bill-wave fa-2x text-danger mb-3"></i>
                                <h4 class="text-danger">
                                    <c:set var="totalMulta" value="0"/>
                                    <jsp:useBean id="hoy2" class="java.util.Date"/>
                                    <c:forEach var="prestamo" items="${prestamos}">
                                        <c:set var="dias" value="${(hoy2.time - prestamo.fechaDevolucion.time) / (1000 * 60 * 60 * 24)}"/>
                                        <c:set var="diasInt" value="${dias - (dias % 1)}"/>
                                        <c:set var="totalMulta" value="${totalMulta + (diasInt * 1000)}"/>
                                    </c:forEach>
                                    $<fmt:formatNumber value="${totalMulta}" pattern="#,###"/>
                                </h4>
                                <p class="text-muted">Total Multas Estimadas</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-center">
                            <div class="card-body">
                                <i class="fas fa-clock fa-2x text-warning mb-3"></i>
                                <h4 class="text-warning">
                                    <c:set var="totalDias" value="0"/>
                                    <jsp:useBean id="hoy3" class="java.util.Date"/>
                                    <c:forEach var="prestamo" items="${prestamos}">
                                        <c:set var="dias" value="${(hoy3.time - prestamo.fechaDevolucion.time) / (1000 * 60 * 60 * 24)}"/>
                                        <c:set var="totalDias" value="${totalDias + (dias - (dias % 1))}"/>
                                    </c:forEach>
                                    ${totalDias}
                                </h4>
                                <p class="text-muted">Total Días Vencidos</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-center">
                            <div class="card-body">
                                <i class="fas fa-user-clock fa-2x text-info mb-3"></i>
                                <h4 class="text-info">
                                    <%-- Contar usuarios únicos con Java --%>
                                    <%
                                        java.util.Set<String> usuariosUnicos = new java.util.HashSet<>();
                                        java.util.List<DTO.PrestamoDTO> prestamos = (java.util.List<DTO.PrestamoDTO>) request.getAttribute("prestamos");
                                        if (prestamos != null) {
                                            for (DTO.PrestamoDTO p : prestamos) {
                                                usuariosUnicos.add(p.getNombreUsuario());
                                            }
                                        }
                                        out.print(usuariosUnicos.size());
                                    %>
                                </h4>
                                <p class="text-muted">Usuarios Afectados</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Acciones masivas -->
                <div class="row mt-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header bg-warning text-dark">
                                <h5 class="mb-0">
                                    <i class="fas fa-tools me-2"></i>Acciones Masivas
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-4">
                                        <button type="button" class="btn btn-outline-primary w-100" onclick="enviarRecordatorios()">
                                            <i class="fas fa-paper-plane me-2"></i>
                                            Enviar Recordatorios
                                        </button>
                                        <small class="text-muted">Envía emails a todos los usuarios</small>
                                    </div>
                                    <div class="col-md-4">
                                        <button type="button" class="btn btn-outline-success w-100" onclick="generarReporteVencidos()">
                                            <i class="fas fa-file-excel me-2"></i>
                                            Generar Reporte
                                        </button>
                                        <small class="text-muted">Exporta lista de vencidos</small>
                                    </div>
                                    <div class="col-md-4">
                                        <button type="button" class="btn btn-outline-info w-100" onclick="programarSeguimiento()">
                                            <i class="fas fa-calendar-plus me-2"></i>
                                            Programar Seguimiento
                                        </button>
                                        <small class="text-muted">Agenda llamadas de seguimiento</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </c:when>
            <c:otherwise>
                <!-- No hay préstamos vencidos -->
                <div class="text-center py-5">
                    <i class="fas fa-check-circle fa-5x text-success mb-4"></i>
                    <h3 class="text-success">¡Excelente!</h3>
                    <h4 class="text-muted">No hay préstamos vencidos</h4>
                    <p class="text-muted">
                        Todos los libros están siendo devueltos a tiempo.
                        <br>Mantenga el buen trabajo fomentando la responsabilidad en los usuarios.
                    </p>
                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/prestamos" class="btn btn-primary">
                            <i class="fas fa-list me-2"></i>Ver Todos los Préstamos
                        </a>
                        <a href="${pageContext.request.contextPath}/admin-dashboard" class="btn btn-outline-primary">
                            <i class="fas fa-tachometer-alt me-2"></i>Ir al Dashboard
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Modal de confirmación de devolución -->
    <div class="modal fade" id="devolucionModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-undo text-success me-2"></i>
                        Confirmar Devolución
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>¿Confirma la devolución del libro <strong id="libroDevolucion"></strong>?</p>
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        <strong>Nota:</strong> El libro estaba vencido. Asegúrese de aplicar las multas correspondientes según las políticas de la biblioteca.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>Cancelar
                    </button>
                    <a id="devolucionLink" href="#" class="btn btn-success">
                        <i class="fas fa-undo me-2"></i>Confirmar Devolución
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal de confirmación de eliminación -->
    <div class="modal fade" id="confirmModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-exclamation-triangle text-warning me-2"></i>
                        Confirmar Eliminación
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>¿Está seguro de que desea eliminar el préstamo del libro <strong id="libroTitulo"></strong>?</p>
                    <div class="alert alert-danger">
                        <i class="fas fa-warning me-2"></i>
                        <strong>Atención:</strong> Esta acción no se puede deshacer y el libro quedará disponible para nuevos préstamos.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>Cancelar
                    </button>
                    <a id="eliminarLink" href="#" class="btn btn-danger">
                        <i class="fas fa-trash me-2"></i>Eliminar
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal de contacto -->
    <div class="modal fade" id="contactoModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-envelope text-info me-2"></i>
                        Contactar Usuario
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-info">
                        <h6><i class="fas fa-info-circle me-2"></i>Información del Préstamo Vencido</h6>
                        <p class="mb-1"><strong>Usuario:</strong> <span id="contactoUsuario"></span></p>
                        <p class="mb-1"><strong>Libro:</strong> <span id="contactoLibro"></span></p>
                        <p class="mb-0"><strong>Días vencidos:</strong> <span id="contactoDias"></span> días</p>
                    </div>
                    
                    <h6>Plantillas de Mensaje:</h6>
                    <div class="mb-3">
                        <label class="form-label">Seleccione una plantilla:</label>
                        <select class="form-select" id="plantillaMensaje" onchange="aplicarPlantilla()">
                            <option value="">Seleccione una plantilla...</option>
                            <option value="recordatorio">Recordatorio Amable</option>
                            <option value="urgente">Solicitud Urgente</option>
                            <option value="multa">Notificación de Multa</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label for="mensajeTexto" class="form-label">Mensaje:</label>
                        <textarea class="form-control" id="mensajeTexto" rows="5" placeholder="Escriba su mensaje aquí..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>Cancelar
                    </button>
                    <button type="button" class="btn btn-info" onclick="enviarMensaje()">
                        <i class="fas fa-paper-plane me-2"></i>Enviar Mensaje
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>

    <script>
        // Inicializar DataTable
        $(document).ready(function() {
            $('#vencidosTable').DataTable({
                language: {
                    url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/es-ES.json'
                },
                pageLength: 10,
                order: [[5, 'desc']], // Ordenar por días vencidos descendente
                columnDefs: [
                    { orderable: false, targets: [7] } // Deshabilitar orden en columna de acciones
                ]
            });
        });

        // Función para confirmar devolución
        function confirmarDevolucion(id, titulo) {
            document.getElementById('libroDevolucion').textContent = titulo;
            document.getElementById('devolucionLink').href = 
                '${pageContext.request.contextPath}/prestamo-devolver?id=' + id;
            
            new bootstrap.Modal(document.getElementById('devolucionModal')).show();
        }

        // Función para confirmar eliminación
        function confirmarEliminacion(id, titulo) {
            document.getElementById('libroTitulo').textContent = titulo;
            document.getElementById('eliminarLink').href = 
                '${pageContext.request.contextPath}/prestamo-eliminar?id=' + id;
            
            new bootstrap.Modal(document.getElementById('confirmModal')).show();
        }

        // Función para contactar usuario
        function contactarUsuario(usuario, libro, dias) {
            document.getElementById('contactoUsuario').textContent = usuario;
            document.getElementById('contactoLibro').textContent = libro;
            document.getElementById('contactoDias').textContent = dias;
            document.getElementById('mensajeTexto').value = '';
            document.getElementById('plantillaMensaje').value = '';
            
            new bootstrap.Modal(document.getElementById('contactoModal')).show();
        }

        // Aplicar plantilla de mensaje
        function aplicarPlantilla() {
            const plantilla = document.getElementById('plantillaMensaje').value;
            const usuario = document.getElementById('contactoUsuario').textContent;
            const libro = document.getElementById('contactoLibro').textContent;
            const dias = document.getElementById('contactoDias').textContent;
            let mensaje = '';
            
            switch(plantilla) {
                case 'recordatorio':
                    mensaje = `Estimado/a ${usuario},\n\nEsperamos que se encuentre bien. Le escribimos para recordarle que el libro "${libro}" tiene ${dias} días de retraso en su devolución.\n\nLe agradeceríamos que pueda acercarse a la biblioteca a la brevedad posible para hacer la devolución correspondiente.\n\nGracias por su comprensión.\n\nBiblioteca ADSO`;
                    break;
                case 'urgente':
                    mensaje = `Estimado/a ${usuario},\n\nEste es un recordatorio URGENTE sobre la devolución del libro "${libro}" que está vencido hace ${dias} días.\n\nEs necesario que proceda con la devolución INMEDIATAMENTE para evitar sanciones adicionales.\n\nPor favor contacte con la biblioteca lo antes posible.\n\nBiblioteca ADSO\nTeléfono: (123) 456-7890`;
                    break;
                case 'multa':
                    const multa = dias * 1000;
                    mensaje = `Estimado/a ${usuario},\n\nLe informamos que el libro "${libro}" está vencido hace ${dias} días, generando una multa de ${multa.toLocaleString()}.\n\nPara regularizar su situación:\n1. Devuelva el libro\n2. Cancele la multa correspondiente\n3. Presente este mensaje en la biblioteca\n\nMulta diaria: $1,000\nTotal adeudado: ${multa.toLocaleString()}\n\nBiblioteca ADSO`;
                    break;
            }
            
            document.getElementById('mensajeTexto').value = mensaje;
        }

        // Enviar mensaje (simulado)
        function enviarMensaje() {
            const mensaje = document.getElementById('mensajeTexto').value;
            if (mensaje.trim() === '') {
                alert('Por favor escriba un mensaje antes de enviar.');
                return;
            }
            
            // Simular envío de mensaje
            alert('Mensaje enviado exitosamente al usuario.');
            bootstrap.Modal.getInstance(document.getElementById('contactoModal')).hide();
        }

        // Acciones masivas
        function enviarRecordatorios() {
            if (confirm('¿Está seguro de que desea enviar recordatorios a todos los usuarios con préstamos vencidos?')) {
                // Simular envío masivo
                const totalPrestamos = ${prestamos.size()};
                alert(`Se enviaron recordatorios a ${totalPrestamos} usuarios con préstamos vencidos.`);
            }
        }

        function generarReporteVencidos() {
            // Redirigir para generar reporte
            window.open('${pageContext.request.contextPath}/generar-excel?tipo=vencidos', '_blank');
        }

        function programarSeguimiento() {
            alert('Funcionalidad de programación de seguimiento en desarrollo.\n\nSe integrará con el calendario del sistema para agendar llamadas y recordatorios automáticos.');
        }

        // Auto-ocultar alertas después de 5 segundos
        document.addEventListener('DOMContentLoaded', function() {
            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert-dismissible');
                alerts.forEach(function(alert) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);

            // Destacar préstamos muy vencidos
            const filasUrgentes = document.querySelectorAll('.urgente');
            filasUrgentes.forEach(function(fila) {
                // Agregar tooltip
                fila.setAttribute('title', 'Préstamo con más de 7 días de retraso - Requiere atención urgente');
            });
        });

        // Función para actualizar contadores en tiempo real
        function actualizarContadores() {
            // Esta función se puede usar para actualizar los contadores sin recargar la página
            // Útil si se implementa WebSocket o polling
        }

        // Tooltip para botones de acción
        document.addEventListener('DOMContentLoaded', function() {
            const tooltipTriggerList = [].slice.call(document.querySelectorAll('[title]'));
            const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
        });

        // Función para exportar lista específica
        function exportarLista(formato) {
            const url = `${pageContext.request.contextPath}/generar-${formato}?tipo=vencidos`;
            window.open(url, '_blank');
        }

        // Función para filtrar por días vencidos
        function filtrarPorDias(minDias) {
            const table = $('#vencidosTable').DataTable();
            
            // Limpiar filtros anteriores
            table.search('').columns().search('').draw();
            
            // Aplicar filtro personalizado
            if (minDias > 0) {
                table.column(5).search(minDias + '|[0-9]{2,}', true, false).draw();
            }
        }

        // Shortcuts de teclado
        document.addEventListener('keydown', function(e) {
            // Ctrl + R para recargar datos
            if (e.ctrlKey && e.key === 'r') {
                e.preventDefault();
                location.reload();
            }
            
            // Escape para cerrar modales
            if (e.key === 'Escape') {
                const modales = document.querySelectorAll('.modal.show');
                modales.forEach(modal => {
                    bootstrap.Modal.getInstance(modal)?.hide();
                });
            }
        });

        // Función para imprimir tabla
        function imprimirTabla() {
            const contenido = document.querySelector('.table-responsive').outerHTML;
            const ventana = window.open('', '_blank');
            ventana.document.write(`
                <html>
                <head>
                    <title>Préstamos Vencidos - <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()) %></title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        table { width: 100%; border-collapse: collapse; }
                        th, td { padding: 8px; border: 1px solid #ddd; text-align: left; }
                        th { background: #f5f5f5; }
                        .badge { padding: 2px 8px; border-radius: 10px; }
                        .bg-danger { background: #dc3545 !important; color: white; }
                        .bg-warning { background: #ffc107 !important; color: black; }
                        .bg-dark { background: #6c757d !important; color: white; }
                    </style>
                </head>
                <body>
                    <h2>Préstamos Vencidos - Sistema de Biblioteca ADSO</h2>
                    <p>Fecha de reporte: <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()) %></p>
                    ${contenido}
                    <p style="margin-top: 20px; font-size: 12px; color: #666;">
                        Total de registros: ${prestamos.size()} | 
                        Generado por: ${sessionScope.nombreUsuario} | 
                        Fecha: <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) %>
                    </p>
                </body>
                </html>
            `);
            ventana.document.close();
            ventana.print();
        }
    </script>
</body>
</html>