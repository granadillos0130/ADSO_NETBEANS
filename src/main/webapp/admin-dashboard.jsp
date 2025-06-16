<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Administrador - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .navbar-brand {
            font-weight: 600;
        }
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 3rem 0;
            margin-bottom: 2rem;
        }
        .stats-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s;
            cursor: pointer;
        }
        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }
        .quick-action-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s;
            height: 100%;
        }
        .quick-action-card:hover {
            transform: translateY(-3px);
        }
        .action-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        .welcome-card {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            border-radius: 15px;
            padding: 2rem;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin-dashboard">
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
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user-shield me-1"></i>${sessionScope.nombreUsuario}
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

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="display-5 fw-bold mb-3">
                        <i class="fas fa-tachometer-alt me-3"></i>
                        Panel de Administración
                    </h1>
                    <p class="lead mb-0">
                        Bienvenido ${sessionScope.nombreUsuario}, gestione el sistema de biblioteca
                    </p>
                </div>
                <div class="col-md-4 text-center">
                    <i class="fas fa-user-shield fa-5x opacity-25"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Tarjeta de bienvenida -->
        <div class="welcome-card">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h4><i class="fas fa-info-circle me-2"></i>Estado del Sistema</h4>
                    <p class="mb-0">
                        Sistema funcionando correctamente. Última actualización: 
                        <strong><script>document.write(new Date().toLocaleDateString());</script></strong>
                    </p>
                </div>
                <div class="col-md-4 text-center">
                    <i class="fas fa-check-circle fa-3x"></i>
                </div>
            </div>
        </div>

        <!-- Estadísticas rápidas -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stats-card card text-center" onclick="location.href='${pageContext.request.contextPath}/libros'">
                    <div class="card-body">
                        <i class="fas fa-book fa-3x text-primary mb-3"></i>
                        <h3 class="card-title">${totalUsuarios > 0 ? '25' : '3'}</h3>
                        <p class="card-text text-muted">Total Libros</p>
                        <small class="text-success">
                            <i class="fas fa-arrow-up me-1"></i>+5 este mes
                        </small>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card card text-center" onclick="location.href='${pageContext.request.contextPath}/usuarios'">
                    <div class="card-body">
                        <i class="fas fa-users fa-3x text-success mb-3"></i>
                        <h3 class="card-title">${totalUsuarios}</h3>
                        <p class="card-text text-muted">Usuarios</p>
                        <small class="text-info">
                            <i class="fas fa-user-plus me-1"></i>2 nuevos
                        </small>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card card text-center" onclick="location.href='${pageContext.request.contextPath}/prestamos'">
                    <div class="card-body">
                        <i class="fas fa-exchange-alt fa-3x text-warning mb-3"></i>
                        <h3 class="card-title">12</h3>
                        <p class="card-text text-muted">Préstamos Activos</p>
                        <small class="text-warning">
                            <i class="fas fa-clock me-1"></i>3 por vencer
                        </small>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card card text-center" onclick="location.href='${pageContext.request.contextPath}/prestamos-vencidos'">
                    <div class="card-body">
                        <i class="fas fa-exclamation-triangle fa-3x text-danger mb-3"></i>
                        <h3 class="card-title">2</h3>
                        <p class="card-text text-muted">Vencidos</p>
                        <small class="text-danger">
                            <i class="fas fa-bell me-1"></i>Atención requerida
                        </small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Acciones rápidas -->
        <div class="row mb-4">
            <div class="col-md-12">
                <h4 class="mb-3">
                    <i class="fas fa-bolt me-2 text-warning"></i>
                    Acciones Rápidas
                </h4>
            </div>
            <div class="col-md-3">
                <div class="quick-action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-primary">
                            <i class="fas fa-plus-circle"></i>
                        </div>
                        <h5>Nuevo Libro</h5>
                        <p class="card-text">Agregar libro al catálogo</p>
                        <a href="${pageContext.request.contextPath}/libro-nuevo" class="btn btn-primary">
                            <i class="fas fa-plus me-2"></i>Crear
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="quick-action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-success">
                            <i class="fas fa-user-plus"></i>
                        </div>
                        <h5>Nuevo Usuario</h5>
                        <p class="card-text">Registrar nuevo usuario</p>
                        <a href="${pageContext.request.contextPath}/usuario-nuevo" class="btn btn-success">
                            <i class="fas fa-user-plus me-2"></i>Crear
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="quick-action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-warning">
                            <i class="fas fa-handshake"></i>
                        </div>
                        <h5>Nuevo Préstamo</h5>
                        <p class="card-text">Registrar préstamo de libro</p>
                        <a href="${pageContext.request.contextPath}/prestamo-nuevo" class="btn btn-warning">
                            <i class="fas fa-handshake me-2"></i>Crear
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="quick-action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-info">
                            <i class="fas fa-file-excel"></i>
                        </div>
                        <h5>Reporte Excel</h5>
                        <p class="card-text">Generar reporte de inventario</p>
                        <a href="${pageContext.request.contextPath}/generar-excel" class="btn btn-info">
                            <i class="fas fa-download me-2"></i>Generar
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Información reciente -->
        <div class="row">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-clock me-2"></i>Actividad Reciente
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="list-group list-group-flush">
                            <div class="list-group-item d-flex align-items-center">
                                <i class="fas fa-book text-primary me-3"></i>
                                <div>
                                    <strong>Nuevo libro agregado</strong><br>
                                    <small class="text-muted">"Cien Años de Soledad" - Hace 2 horas</small>
                                </div>
                            </div>
                            <div class="list-group-item d-flex align-items-center">
                                <i class="fas fa-user-plus text-success me-3"></i>
                                <div>
                                    <strong>Usuario registrado</strong><br>
                                    <small class="text-muted">Carlos Pérez - Hace 4 horas</small>
                                </div>
                            </div>
                            <div class="list-group-item d-flex align-items-center">
                                <i class="fas fa-handshake text-warning me-3"></i>
                                <div>
                                    <strong>Préstamo realizado</strong><br>
                                    <small class="text-muted">"El Principito" - Hace 6 horas</small>
                                </div>
                            </div>
                            <div class="list-group-item d-flex align-items-center">
                                <i class="fas fa-undo text-info me-3"></i>
                                <div>
                                    <strong>Libro devuelto</strong><br>
                                    <small class="text-muted">"Java Programming" - Ayer</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-footer text-center">
                        <a href="${pageContext.request.contextPath}/prestamos" class="btn btn-outline-primary">
                            Ver todos los préstamos
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-warning text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-exclamation-triangle me-2"></i>Alertas Importantes
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-warning">
                            <i class="fas fa-clock me-2"></i>
                            <strong>2 préstamos vencidos</strong><br>
                            Requieren atención inmediata.
                            <a href="${pageContext.request.contextPath}/prestamos-vencidos" class="alert-link">Ver detalles</a>
                        </div>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            <strong>3 préstamos por vencer</strong><br>
                            Vencen en los próximos 3 días.
                        </div>
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle me-2"></i>
                            <strong>Sistema funcionando correctamente</strong><br>
                            Todas las funciones operativas.
                        </div>
                    </div>
                </div>

                <!-- Panel de navegación rápida -->
                <div class="card mt-3">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-compass me-2"></i>Navegación Rápida
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-6 mb-2">
                                <a href="${pageContext.request.contextPath}/libros" class="btn btn-outline-primary w-100">
                                    <i class="fas fa-book me-2"></i>Libros
                                </a>
                            </div>
                            <div class="col-6 mb-2">
                                <a href="${pageContext.request.contextPath}/usuarios" class="btn btn-outline-success w-100">
                                    <i class="fas fa-users me-2"></i>Usuarios
                                </a>
                            </div>
                            <div class="col-6 mb-2">
                                <a href="${pageContext.request.contextPath}/prestamos" class="btn btn-outline-warning w-100">
                                    <i class="fas fa-exchange-alt me-2"></i>Préstamos
                                </a>
                            </div>
                            <div class="col-6 mb-2">
                                <a href="${pageContext.request.contextPath}/consulta-publica" class="btn btn-outline-info w-100">
                                    <i class="fas fa-eye me-2"></i>Vista Pública
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Actualizar hora cada segundo
        function actualizarHora() {
            const ahora = new Date();
            const hora = ahora.toLocaleTimeString('es-CO');
            const fecha = ahora.toLocaleDateString('es-CO');
            document.title = `Admin Dashboard - ${hora}`;
        }
        
        setInterval(actualizarHora, 1000);

        // Animación de entrada para las cards
        document.addEventListener('DOMContentLoaded', function() {
            const cards = document.querySelectorAll('.stats-card, .quick-action-card');
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

        // Hover effect para stats cards
        document.querySelectorAll('.stats-card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.cursor = 'pointer';
            });
        });
    </script>
</body>
</html>