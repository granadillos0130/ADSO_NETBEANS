<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${accion == 'nuevo' ? 'Nuevo' : 'Editar'} Libro - Sistema de Biblioteca ADSO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .navbar-brand {
            font-weight: 600;
        }
        .content-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
                        <i class="fas ${accion == 'nuevo' ? 'fa-plus-circle' : 'fa-edit'} me-3"></i>
                        ${accion == 'nuevo' ? 'Nuevo' : 'Editar'} Libro
                    </h1>
                    <p class="mb-0 mt-2">
                        ${accion == 'nuevo' ? 'Agregue un nuevo libro al catálogo' : 'Modifique la información del libro'}
                    </p>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="${pageContext.request.contextPath}/libros" class="btn btn-light btn-lg">
                        <i class="fas fa-arrow-left me-2"></i>Volver a Libros
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
                            <i class="fas fa-book me-2"></i>
                            Información del Libro
                        </h5>
                    </div>
                    <div class="card-body p-4">
                        <form method="post" action="${pageContext.request.contextPath}/${accion == 'nuevo' ? 'libro-nuevo' : 'libro-editar'}" id="libroForm">
                            <c:if test="${accion == 'editar'}">
                                <input type="hidden" name="id" value="${libro.id}">
                            </c:if>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="titulo" class="form-label">
                                            <i class="fas fa-book me-1 text-primary"></i>
                                            Título <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" class="form-control form-control-lg" 
                                               id="titulo" name="titulo" 
                                               value="${libro.titulo}" 
                                               required maxlength="255"
                                               placeholder="Ingrese el título del libro">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="autor" class="form-label">
                                            <i class="fas fa-user-edit me-1 text-success"></i>
                                            Autor <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" class="form-control form-control-lg" 
                                               id="autor" name="autor" 
                                               value="${libro.autor}" 
                                               required maxlength="255"
                                               placeholder="Ingrese el nombre del autor">
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editorial" class="form-label">
                                            <i class="fas fa-building me-1 text-info"></i>
                                            Editorial
                                        </label>
                                        <input type="text" class="form-control form-control-lg" 
                                               id="editorial" name="editorial" 
                                               value="${libro.editorial}" 
                                               maxlength="255"
                                               placeholder="Ingrese la editorial">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="anio" class="form-label">
                                            <i class="fas fa-calendar me-1 text-warning"></i>
                                            Año de Publicación
                                        </label>
                                        <input type="number" class="form-control form-control-lg" 
                                               id="anio" name="anio" 
                                               value="${libro.anio}" 
                                               min="1000" max="2030"
                                               placeholder="Ej: 2023">
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="categoria" class="form-label">
                                            <i class="fas fa-tags me-1 text-secondary"></i>
                                            Categoría
                                        </label>
                                        <select class="form-select form-select-lg" id="categoria" name="categoria">
                                            <option value="">Seleccione una categoría</option>
                                            <c:forEach var="cat" items="${categorias}">
                                                <option value="${cat}" ${libro.categoria == cat ? 'selected' : ''}>${cat}</option>
                                            </c:forEach>
                                            <option value="Literatura" ${libro.categoria == 'Literatura' ? 'selected' : ''}>Literatura</option>
                                            <option value="Ciencia" ${libro.categoria == 'Ciencia' ? 'selected' : ''}>Ciencia</option>
                                            <option value="Tecnología" ${libro.categoria == 'Tecnología' ? 'selected' : ''}>Tecnología</option>
                                            <option value="Historia" ${libro.categoria == 'Historia' ? 'selected' : ''}>Historia</option>
                                            <option value="Arte" ${libro.categoria == 'Arte' ? 'selected' : ''}>Arte</option>
                                            <option value="Filosofía" ${libro.categoria == 'Filosofía' ? 'selected' : ''}>Filosofía</option>
                                            <option value="Educación" ${libro.categoria == 'Educación' ? 'selected' : ''}>Educación</option>
                                            <option value="Ficción" ${libro.categoria == 'Ficción' ? 'selected' : ''}>Ficción</option>
                                            <option value="No Ficción" ${libro.categoria == 'No Ficción' ? 'selected' : ''}>No Ficción</option>
                                            <option value="Biografía" ${libro.categoria == 'Biografía' ? 'selected' : ''}>Biografía</option>
                                            <option value="Otro">Otro</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="categoriaPersonalizada" class="form-label">
                                            <i class="fas fa-pen me-1 text-secondary"></i>
                                            Categoría Personalizada
                                        </label>
                                        <input type="text" class="form-control form-control-lg" 
                                               id="categoriaPersonalizada" name="categoriaPersonalizada" 
                                               placeholder="Solo si seleccionó 'Otro'"
                                               maxlength="100">
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-12">
                                    <div class="mb-4">
                                        <div class="form-check form-switch form-check-lg">
                                            <input class="form-check-input" type="checkbox" 
                                                   id="disponible" name="disponible" 
                                                   ${libro.disponible || accion == 'nuevo' ? 'checked' : ''}>
                                            <label class="form-check-label fs-5" for="disponible">
                                                <i class="fas fa-check-circle me-2 text-success"></i>
                                                Libro disponible para préstamo
                                            </label>
                                        </div>
                                        <small class="form-text text-muted">
                                            Marque esta opción si el libro está disponible para ser prestado
                                        </small>
                                    </div>
                                </div>
                            </div>

                            <!-- Botones -->
                            <div class="row">
                                <div class="col-12">
                                    <div class="d-flex gap-3 justify-content-center">
                                        <button type="submit" class="btn btn-primary btn-custom btn-lg">
                                            <i class="fas ${accion == 'nuevo' ? 'fa-plus' : 'fa-save'} me-2"></i>
                                            ${accion == 'nuevo' ? 'Crear Libro' : 'Guardar Cambios'}
                                        </button>
                                        <a href="${pageContext.request.contextPath}/libros" 
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
                                <p class="mb-1"><i class="fas fa-book me-2 text-primary"></i>El título debe ser único en el sistema</p>
                                <p class="mb-0"><i class="fas fa-calendar me-2 text-warning"></i>El año debe estar entre 1000 y 2030</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header bg-success text-white">
                                <h6 class="mb-0">
                                    <i class="fas fa-lightbulb me-2"></i>Consejos
                                </h6>
                            </div>
                            <div class="card-body">
                                <p class="mb-1"><i class="fas fa-check me-2 text-success"></i>Verifique la ortografía antes de guardar</p>
                                <p class="mb-1"><i class="fas fa-tags me-2 text-info"></i>Use categorías existentes cuando sea posible</p>
                                <p class="mb-0"><i class="fas fa-save me-2 text-primary"></i>Los cambios se guardan inmediatamente</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Manejar categoría personalizada
        document.getElementById('categoria').addEventListener('change', function() {
            const categoriaPersonalizada = document.getElementById('categoriaPersonalizada');
            if (this.value === 'Otro') {
                categoriaPersonalizada.style.display = 'block';
                categoriaPersonalizada.required = true;
                categoriaPersonalizada.focus();
            } else {
                categoriaPersonalizada.style.display = 'none';
                categoriaPersonalizada.required = false;
                categoriaPersonalizada.value = '';
            }
        });

        // Validación del formulario
        document.getElementById('libroForm').addEventListener('submit', function(e) {
            const categoria = document.getElementById('categoria').value;
            const categoriaPersonalizada = document.getElementById('categoriaPersonalizada').value;
            
            if (categoria === 'Otro' && !categoriaPersonalizada.trim()) {
                e.preventDefault();
                alert('Por favor, ingrese una categoría personalizada.');
                document.getElementById('categoriaPersonalizada').focus();
                return false;
            }
            
            // Si hay categoría personalizada, usarla en lugar de la seleccionada
            if (categoria === 'Otro' && categoriaPersonalizada.trim()) {
                document.getElementById('categoria').value = categoriaPersonalizada.trim();
            }
        });

        // Función para restaurar formulario
        function resetForm() {
            if (confirm('¿Está seguro de que desea restaurar todos los campos a sus valores originales?')) {
                document.getElementById('libroForm').reset();
                // Restaurar valores originales si es edición
                <c:if test="${accion == 'editar'}">
                    document.getElementById('titulo').value = '${libro.titulo}';
                    document.getElementById('autor').value = '${libro.autor}';
                    document.getElementById('editorial').value = '${libro.editorial}';
                    document.getElementById('anio').value = '${libro.anio}';
                    document.getElementById('categoria').value = '${libro.categoria}';
                    document.getElementById('disponible').checked = ${libro.disponible};
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
            document.getElementById('titulo').focus();
        });

        // Prevenir envío duplicado
        let formSubmitted = false;
        document.getElementById('libroForm').addEventListener('submit', function(e) {
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
    </script>
</body>
</html>