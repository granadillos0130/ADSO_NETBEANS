<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios - Sistema de Biblioteca ADSO</title>
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
        .stats-card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s;
        }
        .stats-card:hover {
            transform: translateY(-5px);
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
        .badge-admin {
            background-color: #dc3545;
        }
        .badge-usuario {
            background-color: #28a745;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/usuarios">
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
                <div class="col-md-6">
                    <h1 class="mb-0">
                        <i class="fas fa-users me-3"></i>Gestión de Usuarios
                    </h1>
                    <p class="mb-0 mt-2">Administre los usuarios del sistema</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <a href="${pageContext.request.contextPath}/usuario-nuevo" class="btn btn-light btn-lg">
                        <i class="fas fa-user-plus me-2"></i>Nuevo Usuario
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Estadísticas -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card stats-card text-center">
                    <div class="card-body">
                        <i class="fas fa-users fa-2x text-primary mb-3"></i>
                        <h3 class="card-title">${totalUsuarios}</h3>
                        <p class="card-text text-muted">Total de Usuarios</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card stats-card text-center">
                    <div class="card-body">
                        <i class="fas fa-user-shield fa-2x text-danger mb-3"></i>
                        <h3 class="card-title">
                            <c:set var="admins" value="0"/>
                            <c:forEach var="usuario" items="${usuarios}">
                                <c:if test="${usuario.rol == 'admin'}">
                                    <c:set var="admins" value="${admins + 1}"/>
                                </c:if>
                            </c:forEach>
                            ${admins}
                        </h3>
                        <p class="card-text text-muted">Administradores</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card stats-card text-center">
                    <div class="card-body">
                        <i class="fas fa-user fa-2x text-success mb-3"></i>
                        <h3 class="card-title">
                            <c:set var="lectores" value="0"/>
                            <c:forEach var="usuario" items="${usuarios}">
                                <c:if test="${usuario.rol == 'usuario'}">
                                    <c:set var="lectores" value="${lectores + 1}"/>
                                </c:if>
                            </c:forEach>
                            ${lectores}
                        </h3>
                        <p class="card-text text-muted">Lectores</p>
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

        <!-- Tabla de usuarios -->
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="fas fa-list me-2"></i>Lista de Usuarios
                    <span class="badge bg-light text-dark ms-2">${usuarios.size()} registros</span>
                </h5>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Documento</th>
                                <th>Correo</th>
                                <th>Teléfono</th>
                                <th>Rol</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="usuario" items="${usuarios}">
                                <tr>
                                    <td><strong>#${usuario.id}</strong></td>
                                    <td>
                                        <strong>${usuario.nombre}</strong>
                                    </td>
                                    <td>${usuario.documento}</td>
                                    <td>${usuario.correo}</td>
                                    <td>${usuario.telefono}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${usuario.rol == 'admin'}">
                                                <span class="badge badge-admin">
                                                    <i class="fas fa-user-shield me-1"></i>Administrador
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-usuario">
                                                    <i class="fas fa-user me-1"></i>Usuario
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/usuario-editar?id=${usuario.id}" 
                                               class="btn btn-sm btn-outline-primary btn-action" 
                                               title="Editar usuario">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <c:if test="${usuario.id != sessionScope.idUsuario}">
                                                <button type="button" 
                                                        class="btn btn-sm btn-outline-danger btn-action" 
                                                        onclick="confirmarEliminacion(${usuario.id}, '${usuario.nombre}')"
                                                        title="Eliminar usuario">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <c:if test="${empty usuarios}">
            <div class="text-center mt-4">
                <i class="fas fa-users fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">No hay usuarios registrados</h4>
                <p class="text-muted">Comience agregando el primer usuario</p>
                <a href="${pageContext.request.contextPath}/usuario-nuevo" class="btn btn-primary">
                    <i class="fas fa-user-plus me-2"></i>Agregar Primer Usuario
                </a>
            </div>
        </c:if>
    </div>

    <!-- Modal de confirmación -->
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
                    <p>¿Está seguro de que desea eliminar al usuario <strong id="usuarioNombre"></strong>?</p>
                    <p class="text-danger">
                        <i class="fas fa-warning me-2"></i>
                        Esta acción no se puede deshacer y eliminará todos los datos asociados.
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>Cancelar
                    </button>
                    <a id="eliminarLink" href="#" class="btn btn-danger">
                        <i class="fas fa-trash me-2"></i>Eliminar Usuario
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Función para confirmar eliminación
        function confirmarEliminacion(id, nombre) {
            document.getElementById('usuarioNombre').textContent = nombre;
            document.getElementById('eliminarLink').href = 
                '${pageContext.request.contextPath}/usuario-eliminar?id=' + id;
            
            new bootstrap.Modal(document.getElementById('confirmModal')).show();
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

            // Animación de entrada para las filas
            const rows = document.querySelectorAll('tbody tr');
            rows.forEach((row, index) => {
                row.style.opacity = '0';
                row.style.transform = 'translateX(-20px)';
                row.style.transition = 'all 0.3s ease';
                
                setTimeout(() => {
                    row.style.opacity = '1';
                    row.style.transform = 'translateX(0)';
                }, index * 50);
            });
        });
    </script>
</body>
</html>