<%-- 
    Document   : user-dashboard
    Created on : 16/06/2025, 11:55:31 a. m.
    Author     : HAWLETH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Dashboard - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .navbar-brand {
            font-weight: 600;
        }
        .hero-section {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
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
        .action-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s;
            height: 100%;
        }
        .action-card:hover {
            transform: translateY(-3px);
        }
        .action-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        .welcome-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        .book-preview {
            max-height: 400px;
            overflow-y: auto;
        }
        .book-item {
            border-radius: 10px;
            margin-bottom: 10px;
            padding: 15px;
            background: #f8f9fa;
            transition: all 0.3s;
        }
        .book-item:hover {
            background: #e9ecef;
            transform: translateX(5px);
        }
        .badge-disponible {
            background-color: #28a745;
        }
        .badge-prestado {
            background-color: #ffc107;
            color: #000;
        }
        .badge-vencido {
            background-color: #dc3545;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/user-dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>Mi Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/mis-prestamos">
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

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="display-5 fw-bold mb-3">
                        <i class="fas fa-user-circle me-3"></i>
                        ¡Bienvenido, ${sessionScope.nombreUsuario}!
                    </h1>
                    <p class="lead mb-0">
                        Gestiona tus préstamos y explora nuestro catálogo de libros
                    </p>
                </div>
                <div class="col-md-4 text-center">
                    <i class="fas fa-book-reader fa-5x opacity-25"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Tarjeta de bienvenida -->
        <div class="welcome-card">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h4><i class="fas fa-sparkles me-2"></i>¿Qué quieres hacer hoy?</h4>
                    <p class="mb-0">
                        Explora nuestro catálogo, revisa tus préstamos actuales o solicita nuevos libros.
                        <br><strong>Fecha actual:</strong> <span id="fechaActual"></span>
                    </p>
                </div>
                <div class="col-md-4 text-center">
                    <i class="fas fa-calendar-alt fa-3x"></i>
                </div>
            </div>
        </div>

        <!-- Estadísticas del usuario -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="stats-card card text-center" onclick="location.href='${pageContext.request.contextPath}/mis-prestamos'">
                    <div class="card-body">
                        <i class="fas fa-book-open fa-3x text-primary mb-3"></i>
                        <h3 class="card-title">3</h3>
                        <p class="card-text text-muted">Préstamos Activos</p>
                        <small class="text-info">
                            <i class="fas fa-clock me-1"></i>1 por vencer pronto
                        </small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stats-card card text-center" onclick="location.href='${pageContext.request.contextPath}/mis-prestamos'">
                    <div class="card-body">
                        <i class="fas fa-history fa-3x text-success mb-3"></i>
                        <h3 class="card-title">8</h3>
                        <p class="card-text text-muted">Libros Devueltos</p>
                        <small class="text-success">
                            <i class="fas fa-check me-1"></i>Historial completo
                        </small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stats-card card text-center" onclick="location.href='${pageContext.request.contextPath}/libros-buscar'">
                    <div class="card-body">
                        <i class="fas fa-star fa-3x text-warning mb-3"></i>
                        <h3 class="card-title">25</h3>
                        <p class="card-text text-muted">Libros Disponibles</p>
                        <small class="text-warning">
                            <i class="fas fa-plus me-1"></i>5 nuevos esta semana
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
                <div class="action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-primary">
                            <i class="fas fa-search"></i>
                        </div>
                        <h5>Buscar Libros</h5>
                        <p class="card-text">Encuentra tu próximo libro favorito</p>
                        <a href="${pageContext.request.contextPath}/libros-buscar" class="btn btn-primary">
                            <i class="fas fa-search me-2"></i>Buscar
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-success">
                            <i class="fas fa-book-reader"></i>
                        </div>
                        <h5>Mis Préstamos</h5>
                        <p class="card-text">Revisa tus libros actuales</p>
                        <a href="${pageContext.request.contextPath}/mis-prestamos" class="btn btn-success">
                            <i class="fas fa-list me-2"></i>Ver Todos
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-warning">
                            <i class="fas fa-handshake"></i>
                        </div>
                        <h5>Solicitar Préstamo</h5>
                        <p class="card-text">Pide un libro prestado</p>
                        <a href="${pageContext.request.contextPath}/prestamo-nuevo" class="btn btn-warning">
                            <i class="fas fa-plus me-2"></i>Solicitar
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="action-card card text-center">
                    <div class="card-body">
                        <div class="action-icon text-info">
                            <i class="fas fa-user-edit"></i>
                        </div>
                        <h5>Mi Perfil</h5>
                        <p class="card-text">Actualiza tu información</p>
                        <a href="${pageContext.request.contextPath}/perfil" class="btn btn-info">
                            <i class="fas fa-edit me-2"></i>Editar
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Información del usuario -->
        <div class="row">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-clock me-2"></i>Mis Préstamos Activos
                        </h5>
                    </div>
                    <div class="card-body book-preview">
                        <!-- Ejemplo de préstamos activos -->
                        <div class="book-item">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">Cien Años de Soledad</h6>
                                    <p class="mb-1 text-muted">Gabriel García Márquez</p>
                                    <small>Prestado: 10/06/2025 | Vence: 25/06/2025</small>
                                </div>
                                <span class="badge badge-prestado">
                                    <i class="fas fa-clock me-1"></i>Activo
                                </span>
                            </div>
                        </div>
                        <div class="book-item">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">El Principito</h6>
                                    <p class="mb-1 text-muted">Antoine de Saint-Exupéry</p>
                                    <small>Prestado: 05/06/2025 | Vence: 20/06/2025</small>
                                </div>
                                <span class="badge badge-vencido">
                                    <i class="fas fa-exclamation-triangle me-1"></i>Por vencer
                                </span>
                            </div>
                        </div>
                        <div class="book-item">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">Java: A Beginner's Guide</h6>
                                    <p class="mb-1 text-muted">Herbert Schildt</p>
                                    <small>Prestado: 15/06/2025 | Vence: 30/06/2025</small>
                                </div>
                                <span class="badge badge-prestado">
                                    <i class="fas fa-clock me-1"></i>Activo
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="card-footer text-center">
                        <a href="${pageContext.request.contextPath}/mis-prestamos" class="btn btn-outline-primary">
                            <i class="fas fa-eye me-2"></i>Ver todos mis préstamos
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-star me-2"></i>Libros Recomendados
                        </h5>
                    </div>
                    <div class="card-body book-preview">
                        <!-- Ejemplo de libros recomendados -->
                        <div class="book-item">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">Clean Code</h6>
                                    <p class="mb-1 text-muted">Robert C. Martin</p>
                                    <small>Categoría: Tecnología</small>
                                </div>
                                <span class="badge badge-disponible">
                                    <i class="fas fa-check me-1"></i>Disponible
                                </span>
                            </div>
                        </div>
                        <div class="book-item">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">Rayuela</h6>
                                    <p class="mb-1 text-muted">Julio Cortázar</p>
                                    <small>Categoría: Literatura</small>
                                </div>
                                <span class="badge badge-disponible">
                                    <i class="fas fa-check me-1"></i>Disponible
                                </span>
                            </div>
                        </div>
                        <div class="book-item">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">Algoritmos y Programación</h6>
                                    <p class="mb-1 text-muted">Varios Autores</p>
                                    <small>Categoría: Tecnología</small>
                                </div>
                                <span class="badge badge-disponible">
                                    <i class="fas fa-check me-1"></i>Disponible
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="card-footer text-center">
                        <a href="${pageContext.request.contextPath}/libros-buscar" class="btn btn-outline-success">
                            <i class="fas fa-search me-2"></i>Explorar catálogo completo
                        </a>
                    </div>
                </div>

                <!-- Panel de información -->
                <div class="card mt-3">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-info-circle me-2"></i>Información Importante
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-warning">
                            <i class="fas fa-bell me-2"></i>
                            <strong>Recordatorio:</strong> Tienes 1 libro que vence en 4 días.
                        </div>
                        <div class="alert alert-info">
                            <i class="fas fa-clock me-2"></i>
                            <strong>Horarios:</strong> Lun-Vie: 8:00-18:00, Sáb: 9:00-14:00
                        </div>
                        <div class="alert alert-success">
                            <i class="fas fa-gift me-2"></i>
                            <strong>¡Nuevos libros!</strong> Se agregaron 5 títulos esta semana.
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Mostrar fecha actual
        function actualizarFecha() {
            const ahora = new Date();
            const opciones = { 
                weekday: 'long', 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
            };
            const fechaFormateada = ahora.toLocaleDateString('es-CO', opciones);
            document.getElementById('fechaActual').textContent = fechaFormateada;
        }
        
        // Actualizar cada minuto
        actualizarFecha();
        setInterval(actualizarFecha, 60000);

        // Animación de entrada para las cards
        document.addEventListener('DOMContentLoaded', function() {
            const cards = document.querySelectorAll('.stats-card, .action-card');
            cards.forEach((card, index) => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                card.style.transition = 'all 0.5s ease';
                
                setTimeout(() => {
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, index * 100);
            });

            // Animación para book-items
            const bookItems = document.querySelectorAll('.book-item');
            bookItems.forEach((item, index) => {
                item.style.opacity = '0';
                item.style.transform = 'translateX(-20px)';
                item.style.transition = 'all 0.5s ease';
                
                setTimeout(() => {
                    item.style.opacity = '1';
                    item.style.transform = 'translateX(0)';
                }, 500 + (index * 100));
            });
        });

        // Hover effect para stats cards
        document.querySelectorAll('.stats-card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.cursor = 'pointer';
            });
        });

        // Saludo dinámico basado en la hora
        document.addEventListener('DOMContentLoaded', function() {
            const hora = new Date().getHours();
            let saludo = '';
            
            if (hora < 12) {
                saludo = '🌅 ¡Buenos días!';
            } else if (hora < 18) {
                saludo = '☀️ ¡Buenas tardes!';
            } else {
                saludo = '🌙 ¡Buenas noches!';
            }
            
            // Agregar saludo si existe un elemento para ello
            const welcomeCard = document.querySelector('.welcome-card h4');
            if (welcomeCard) {
                welcomeCard.innerHTML = `<i class="fas fa-sparkles me-2"></i>${saludo} ¿Qué quieres hacer hoy?`;
            }
        });
    </script>
</body>
</html>