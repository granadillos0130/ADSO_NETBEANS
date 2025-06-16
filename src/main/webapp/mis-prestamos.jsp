<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Préstamos - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .navbar-brand {
            font-weight: 600;
        }
        .content-header {
            background: linear-gradient(135deg, #17a2b8 0%, #20c997 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .prestamo-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s;
            margin-bottom: 1.5rem;
        }
        .prestamo-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }
        .badge-activo {
            background-color: #fd7e14;
        }
        .badge-devuelto {
            background-color: #28a745;
        }
        .badge-vencido {
            background-color: #dc3545;
        }
        .text-vencido {
            color: #dc3545 !important;
            font-weight: bold;
        }
        .book-icon {
            font-size: 3rem;
            color: #17a2b8;
            margin-bottom: 1rem;
        }
        .stats-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s;
        }
        .stats-card:hover {
            transform: translateY(-5px);
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/user-dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>Mi Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/mis-prestamos">
                            <i class="fas fa-book-reader me-1"></i>Mis Préstamos
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/libros-buscar">
                            <i class="fas fa-search me-1"></i>Buscar Libros
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/consulta-publica">
                            <i class="fas fa-globe me-1"></i>Catálogo Público
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
                        <i class="fas fa-book-reader me-3"></i>Mis Préstamos
                    </h1>
                    <p class="mb-0 mt-2">Revisa el estado de tus libros prestados</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="${pageContext.request.contextPath}/prestamo-nuevo" class="btn btn-light btn-lg">
                        <i class="fas fa-plus me-2"></i>Solicitar Préstamo
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Estadísticas del usuario -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card stats-card text-center">
                    <div class="card-body">
                        <i class="fas fa-exchange-alt fa-2x text-primary mb-3"></i>
                        <h3 class="card-title">${prestamos.size()}</h3>
                        <p class="card-text text-muted">Total Préstamos</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card stats-card text-center">
                    <div class="card-body">
                        <i class="fas fa-clock fa-2x text-warning mb-3"></i>
                        <h3 class="card-title">
                            <c:set var="activos" value="0"/>
                            <c:forEach var="prestamo" items="${prestamos}">
                                <c:if test="${not prestamo.devuelto}">
                                    <c:set var="activos" value="${activos + 1}"/>
                                </c:if>
                            </c:forEach>
                            ${activos}
                        </h3>
                        <p class="card-text text-muted">Activos</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card stats-card text-center">
                    <div class="card-body">
                        <i class="fas fa-check-circle fa-2x text-success mb-3"></i>
                        <h3 class="card-title">
                            <c:set var="devueltos" value="0"/>
                            <c:forEach var="prestamo" items="${prestamos}">
                                <c:if test="${prestamo.devuelto}">
                                    <c:set var="devueltos" value="${devueltos + 1}"/>
                                </c:if>
                            </c:forEach>
                            ${devueltos}
                        </h3>
                        <p class="card-text text-muted">Devueltos</p>
                    </div>
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

        <!-- Lista de préstamos en formato de cards -->
        <div class="row">
            <c:forEach var="prestamo" items="${prestamos}">
                <div class="col-md-6 col-lg-4">
                    <div class="prestamo-card card h-100">
                        <div class="card-body text-center">
                            <div class="book-icon">
                                <i class="fas fa-book"></i>
                            </div>
                            <h5 class="card-title">${prestamo.tituloLibro}</h5>
                            <p class="card-text">
                                <strong>Autor:</strong> ${prestamo.autorLibro}<br>
                                <strong>ID Préstamo:</strong> #${prestamo.id}
                            </p>
                            <div class="mb-3">
                                <strong>Fecha Préstamo:</strong><br>
                                <fmt:formatDate value="${prestamo.fechaPrestamo}" pattern="dd/MM/yyyy"/>
                            </div>
                            <div class="mb-3">
                                <strong>Fecha Devolución:</strong><br>
                                <c:choose>
                                    <c:when test="${prestamo.devuelto}">
                                        <fmt:formatDate value="${prestamo.fechaDevolucion}" pattern="dd/MM/yyyy"/>
                                    </c:when>
                                    <c:otherwise>
                                        <jsp:useBean id="hoy" class="java.util.Date"/>
                                        <c:choose>
                                            <c:when test="${prestamo.fechaDevolucion.before(hoy)}">
                                                <span class="text-vencido">
                                                    <fmt:formatDate value="${prestamo.fechaDevolucion}" pattern="dd/MM/yyyy"/>
                                                    <br><small>¡VENCIDO!</small>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:formatDate value="${prestamo.fechaDevolucion}" pattern="dd/MM/yyyy"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="mb-3">
                                <c:choose>
                                    <c:when test="${prestamo.devuelto}">
                                        <span class="badge badge-devuelto fs-6">
                                            <i class="fas fa-check me-1"></i>Devuelto
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <jsp:useBean id="hoy2" class="java.util.Date"/>
                                        <c:choose>
                                            <c:when test="${prestamo.fechaDevolucion.before(hoy2)}">
                                                <span class="badge badge-vencido fs-6">
                                                    <i class="fas fa-exclamation-triangle me-1"></i>Vencido
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-activo fs-6">
                                                    <i class="fas fa-clock me-1"></i>Activo
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="card-footer bg-transparent">
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/generar-pdf?id=${prestamo.id}" 
                                   class="btn btn-outline-primary" target="_blank">
                                    <i class="fas fa-file-pdf me-2"></i>Generar Comprobante
                                </a>
                                <c:if test="${not prestamo.devuelto}">
                                    <button type="button" 
                                            class="btn btn-outline-success" 
                                            onclick="confirmarDevolucion(${prestamo.id}, '${prestamo.tituloLibro}')">
                                        <i class="fas fa-undo me-2"></i>Marcar como Devuelto
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Mensaje cuando no hay préstamos -->
        <c:if test="${empty prestamos}">
            <div class="text-center py-5">
                <i class="fas fa-book-reader fa-4x text-muted mb-4"></i>
                <h4 class="text-muted">No tienes préstamos registrados</h4>
                <p class="text-muted">
                    Explora nuestro catálogo y solicita tu primer libro
                </p>
                <div class="d-flex gap-3 justify-content-center">
                    <a href="${pageContext.request.contextPath}/libros-buscar" 
                       class="btn btn-primary">
                        <i class="fas fa-search me-2"></i>Buscar Libros
                    </a>
                    <a href="${pageContext.request.contextPath}/consulta-publica" 
                       class="btn btn-outline-primary">
                        <i class="fas fa-eye me-2"></i>Ver Catálogo Público
                    </a>
                </div>
            </div>
        </c:if>

        <!-- Información adicional -->
        <c:if test="${not empty prestamos}">
            <div class="row mt-5">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header bg-info text-white">
                            <h5 class="mb-0">
                                <i class="fas fa-info-circle me-2"></i>Información Importante
                            </h5>
                        </div>
                        <div class="card-body">
                            <p><i class="fas fa-clock me-2 text-primary"></i><strong>Tiempo de préstamo:</strong> 15 días para libros generales</p>
                            <p><i class="fas fa-calendar-alt me-2 text-success"></i><strong>Renovaciones:</strong> Disponibles si no hay reservas</p>
                            <p><i class="fas fa-exclamation-triangle me-2 text-warning"></i><strong>Multas:</strong> $1,000 por día de retraso</p>
                            <p class="mb-0"><i class="fas fa-phone me-2 text-info"></i><strong>Contacto:</strong> biblioteca@adso.edu.co</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header bg-success text-white">
                            <h5 class="mb-0">
                                <i class="fas fa-lightbulb me-2"></i>Consejos
                            </h5>
                        </div>
                        <div class="card-body">
                            <p><i class="fas fa-bookmark me-2 text-primary"></i>Marca tus libros favoritos para encontrarlos fácilmente</p>
                            <p><i class="fas fa-bell me-2 text-warning"></i>Revisa regularmente las fechas de vencimiento</p>
                            <p><i class="fas fa-star me-2 text-success"></i>Explora diferentes categorías para descubrir nuevos temas</p>
                            <p class="mb-0"><i class="fas fa-heart me-2 text-danger"></i>Cuida los libros para que otros también puedan disfrutarlos</p>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
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
                    <p>¿Confirma que ha devuelto el libro <strong id="libroDevolucion"></strong>?</p>
                    <p class="text-info">
                        <i class="fas fa-info-circle me-2"></i>
                        Asegúrese de haber entregado físicamente el libro en la biblioteca.
                    </p>
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Función para confirmar devolución
        function confirmarDevolucion(id, titulo) {
            document.getElementById('libroDevolucion').textContent = titulo;
            document.getElementById('devolucionLink').href = 
                '${pageContext.request.contextPath}/prestamo-devolver?id=' + id;
            
            new bootstrap.Modal(document.getElementById('devolucionModal')).show();
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

            // Animación de entrada para las cards
            const cards = document.querySelectorAll('.prestamo-card');
            cards.forEach((card, index) => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                card.style.transition = 'all 0.5s ease';
                
                setTimeout(() => {
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, index * 100);
            });
        });

        // Calcular días restantes para vencimiento
        function calcularDiasRestantes() {
            const elementos = document.querySelectorAll('[data-fecha-vencimiento]');
            elementos.forEach(elemento => {
                const fechaVencimiento = new Date(elemento.getAttribute('data-fecha-vencimiento'));
                const hoy = new Date();
                const diferencia = Math.ceil((fechaVencimiento - hoy) / (1000 * 60 * 60 * 24));
                
                if (diferencia > 0) {
                    elemento.innerHTML += `<br><small class="text-info">(${diferencia} días restantes)</small>`;
                } else if (diferencia === 0) {
                    elemento.innerHTML += `<br><small class="text-warning">(Vence hoy)</small>`;
                }
            });
        }

        // Ejecutar al cargar la página
        document.addEventListener('DOMContentLoaded', calcularDiasRestantes);
    </script>
</body>
</html>