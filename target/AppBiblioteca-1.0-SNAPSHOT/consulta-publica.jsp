<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consulta Pública - Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 4rem 0;
            margin-bottom: 3rem;
        }
        .search-card {
            background: white;
            border-radius: 20px;
            padding: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            margin-top: -2rem;
            position: relative;
        }
        .book-card {
            background: white;
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s;
            margin-bottom: 1.5rem;
        }
        .book-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }
        .book-icon {
            font-size: 3rem;
            color: #667eea;
            margin-bottom: 1rem;
        }
        .badge-disponible {
            background-color: #28a745;
        }
        .badge-no-disponible {
            background-color: #dc3545;
        }
        .navbar-brand {
            font-weight: 600;
        }
        .stats-banner {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        .category-filter {
            background: white;
            border-radius: 10px;
            padding: 1rem;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 1.5rem;
        }
        .login-prompt {
            background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
            color: white;
            border-radius: 15px;
            padding: 1.5rem;
            text-align: center;
            margin-bottom: 2rem;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/consulta-publica">
                <i class="fas fa-book-open me-2"></i>
                Biblioteca ADSO - Consulta Pública
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/login">
                    <i class="fas fa-sign-in-alt me-2"></i>Iniciar Sesión
                </a>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container text-center">
            <h1 class="display-4 fw-bold mb-4">
                <i class="fas fa-search me-3"></i>
                Catálogo Público de Libros
            </h1>
            <p class="lead">
                Explore nuestro catálogo de libros y consulte la disponibilidad
            </p>
            <p class="mb-0">
                <i class="fas fa-info-circle me-2"></i>
                No necesita iniciar sesión para consultar nuestro catálogo
            </p>
        </div>
    </div>

    <div class="container">
        <!-- Tarjeta de búsqueda -->
        <div class="search-card">
            <h4 class="mb-4 text-center">
                <i class="fas fa-search me-2 text-primary"></i>
                Buscar Libros
            </h4>
            
            <form method="post" action="${pageContext.request.contextPath}/consulta-publica">
                <div class="row">
                    <div class="col-md-5">
                        <div class="form-group">
                            <label for="termino" class="form-label">Término de búsqueda</label>
                            <input type="text" class="form-control form-control-lg" 
                                   id="termino" name="termino" 
                                   placeholder="Título, autor o categoría..." 
                                   value="${terminoBusqueda}">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="tipo" class="form-label">Buscar por</label>
                            <select class="form-select form-select-lg" name="tipo">
                                <option value="general" ${tipoBusqueda == 'general' ? 'selected' : ''}>Todo</option>
                                <option value="titulo" ${tipoBusqueda == 'titulo' ? 'selected' : ''}>Título</option>
                                <option value="autor" ${tipoBusqueda == 'autor' ? 'selected' : ''}>Autor</option>
                                <option value="categoria" ${tipoBusqueda == 'categoria' ? 'selected' : ''}>Categoría</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">&nbsp;</label>
                        <div class="d-grid gap-2 d-md-flex">
                            <button type="submit" class="btn btn-primary btn-lg flex-grow-1">
                                <i class="fas fa-search me-2"></i>Buscar
                            </button>
                            <a href="${pageContext.request.contextPath}/consulta-publica" 
                               class="btn btn-outline-secondary btn-lg">
                                <i class="fas fa-times"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <!-- Banner de estadísticas -->
        <div class="stats-banner">
            <div class="row text-center">
                <div class="col-md-4">
                    <h3><i class="fas fa-book me-2"></i>${totalLibros}</h3>
                    <p class="mb-0">Libros en el catálogo</p>
                </div>
                <div class="col-md-4">
                    <h3>
                        <i class="fas fa-check-circle me-2"></i>
                        <c:set var="disponibles" value="0"/>
                        <c:forEach var="libro" items="${libros}">
                            <c:if test="${libro.disponible}">
                                <c:set var="disponibles" value="${disponibles + 1}"/>
                            </c:if>
                        </c:forEach>
                        ${disponibles}
                    </h3>
                    <p class="mb-0">Libros disponibles</p>
                </div>
                <div class="col-md-4">
                    <h3><i class="fas fa-tags me-2"></i>${categorias.size()}</h3>
                    <p class="mb-0">Categorías diferentes</p>
                </div>
            </div>
        </div>

        <!-- Filtro por categorías -->
        <c:if test="${not empty categorias}">
            <div class="category-filter">
                <h6 class="mb-3">
                    <i class="fas fa-filter me-2"></i>Filtrar por categoría:
                </h6>
                <div class="d-flex flex-wrap gap-2">
                    <a href="${pageContext.request.contextPath}/consulta-publica" 
                       class="btn btn-outline-primary btn-sm ${empty tipoBusqueda || tipoBusqueda != 'categoria' ? 'active' : ''}">
                        Todas las categorías
                    </a>
                    <c:forEach var="categoria" items="${categorias}">
                        <form method="post" action="${pageContext.request.contextPath}/consulta-publica" 
                              style="display: inline;">
                            <input type="hidden" name="termino" value="${categoria}">
                            <input type="hidden" name="tipo" value="categoria">
                            <button type="submit" 
                                    class="btn btn-outline-info btn-sm ${terminoBusqueda == categoria ? 'active' : ''}">
                                ${categoria}
                            </button>
                        </form>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <!-- Prompt para iniciar sesión -->
        <div class="login-prompt">
            <h5><i class="fas fa-user-plus me-2"></i>¿Desea tomar prestado un libro?</h5>
            <p class="mb-3">Inicie sesión para acceder a todas las funcionalidades del sistema</p>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-light btn-lg">
                <i class="fas fa-sign-in-alt me-2"></i>Iniciar Sesión
            </a>
        </div>

        <!-- Resultados de búsqueda -->
        <c:if test="${not empty terminoBusqueda}">
            <div class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i>
                Se encontraron <strong>${libros.size()}</strong> resultados para 
                "<strong>${terminoBusqueda}</strong>" en <strong>${tipoBusqueda}</strong>
            </div>
        </c:if>

        <!-- Lista de libros -->
        <div class="row">
            <c:forEach var="libro" items="${libros}">
                <div class="col-md-6 col-lg-4">
                    <div class="book-card card h-100">
                        <div class="card-body text-center">
                            <div class="book-icon">
                                <i class="fas fa-book"></i>
                            </div>
                            <h5 class="card-title">${libro.titulo}</h5>
                            <p class="card-text">
                                <strong>Autor:</strong> ${libro.autor}<br>
                                <strong>Editorial:</strong> ${libro.editorial}<br>
                                <strong>Año:</strong> ${libro.anio}
                            </p>
                            <div class="mb-3">
                                <span class="badge bg-info">${libro.categoria}</span>
                            </div>
                            <div class="mb-3">
                                <c:choose>
                                    <c:when test="${libro.disponible}">
                                        <span class="badge badge-disponible fs-6">
                                            <i class="fas fa-check me-1"></i>Disponible
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-no-disponible fs-6">
                                            <i class="fas fa-times me-1"></i>No Disponible
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <c:if test="${libro.disponible}">
                                <div class="d-grid">
                                    <a href="${pageContext.request.contextPath}/login" 
                                       class="btn btn-primary">
                                        <i class="fas fa-hand-holding me-2"></i>
                                        Solicitar Préstamo
                                    </a>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Mensaje cuando no hay libros -->
        <c:if test="${empty libros}">
            <div class="text-center py-5">
                <i class="fas fa-book fa-4x text-muted mb-4"></i>
                <h4 class="text-muted">No se encontraron libros</h4>
                <c:choose>
                    <c:when test="${not empty terminoBusqueda}">
                        <p class="text-muted">
                            No hay libros que coincidan con su búsqueda "<strong>${terminoBusqueda}</strong>"
                        </p>
                        <a href="${pageContext.request.contextPath}/consulta-publica" 
                           class="btn btn-primary">
                            <i class="fas fa-eye me-2"></i>Ver todos los libros
                        </a>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">
                            Actualmente no hay libros disponibles en el catálogo
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <!-- Información adicional -->
        <div class="row mt-5">
            <div class="col-md-4">
                <div class="card text-center h-100">
                    <div class="card-body">
                        <i class="fas fa-clock fa-2x text-primary mb-3"></i>
                        <h5>Horarios de Atención</h5>
                        <p class="card-text">
                            Lunes a Viernes: 8:00 AM - 6:00 PM<br>
                            Sábados: 9:00 AM - 2:00 PM
                        </p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center h-100">
                    <div class="card-body">
                        <i class="fas fa-calendar-alt fa-2x text-success mb-3"></i>
                        <h5>Tiempo de Préstamo</h5>
                        <p class="card-text">
                            Libros generales: 15 días<br>
                            Libros de referencia: 7 días
                        </p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center h-100">
                    <div class="card-body">
                        <i class="fas fa-phone fa-2x text-info mb-3"></i>
                        <h5>Contacto</h5>
                        <p class="card-text">
                            Teléfono: (123) 456-7890<br>
                            Email: biblioteca@adso.edu.co
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white mt-5 py-4">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>
                        <i class="fas fa-book-open me-2"></i>
                        Sistema de Biblioteca ADSO
                    </h5>
                    <p class="mb-0">Tecnología en Análisis y Desarrollo de Software</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p class="mb-0">
                        <a href="${pageContext.request.contextPath}/login" 
                           class="text-white text-decoration-none">
                            <i class="fas fa-sign-in-alt me-2"></i>Acceso para usuarios registrados
                        </a>
                    </p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Enfocar campo de búsqueda al cargar
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('termino').focus();
        });

        // Efecto de scroll suave para enlaces
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                document.querySelector(this.getAttribute('href')).scrollIntoView({
                    behavior: 'smooth'
                });
            });
        });

        // Animación de cards al hacer scroll
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };

        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, observerOptions);

        // Aplicar animación a las cards de libros
        document.querySelectorAll('.book-card').forEach(card => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            observer.observe(card);
        });
    </script>
</body>
</html>