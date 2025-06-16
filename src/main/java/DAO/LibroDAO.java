package DAO;

import DTO.LibroDTO;
import Model.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para operaciones con libros
 * @author HAWLETH
 */
public class LibroDAO {
    
    /**
     * Crear un nuevo libro
     * @param libro LibroDTO con los datos del libro
     * @return boolean true si se creó exitosamente
     */
    public boolean crearLibro(LibroDTO libro) {
        String sql = "INSERT INTO libros (titulo, autor, editorial, anio, categoria, disponible) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getAnio());
            ps.setString(5, libro.getCategoria());
            ps.setBoolean(6, libro.isDisponible());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear libro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtener todos los libros
     * @return List<LibroDTO> lista de libros
     */
    public List<LibroDTO> obtenerTodosLibros() {
        List<LibroDTO> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LibroDTO libro = mapearResultSetALibro(rs);
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener libros: " + e.getMessage());
            e.printStackTrace();
        }
        
        return libros;
    }
    
    /**
     * Obtener libro por ID
     * @param id ID del libro
     * @return LibroDTO libro encontrado o null
     */
    public LibroDTO obtenerLibroPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetALibro(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener libro por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Actualizar un libro
     * @param libro LibroDTO con los datos actualizados
     * @return boolean true si se actualizó exitosamente
     */
    public boolean actualizarLibro(LibroDTO libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, anio = ?, categoria = ?, disponible = ? WHERE id = ?";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getAnio());
            ps.setString(5, libro.getCategoria());
            ps.setBoolean(6, libro.isDisponible());
            ps.setInt(7, libro.getId());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Eliminar un libro
     * @param id ID del libro a eliminar
     * @return boolean true si se eliminó exitosamente
     */
    public boolean eliminarLibro(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Buscar libros por título
     * @param titulo Título a buscar (búsqueda parcial)
     * @return List<LibroDTO> lista de libros que coinciden
     */
    public List<LibroDTO> buscarPorTitulo(String titulo) {
        List<LibroDTO> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ? ORDER BY titulo";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + titulo + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LibroDTO libro = mapearResultSetALibro(rs);
                    libros.add(libro);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar por título: " + e.getMessage());
            e.printStackTrace();
        }
        
        return libros;
    }
    
    /**
     * Buscar libros por autor
     * @param autor Autor a buscar (búsqueda parcial)
     * @return List<LibroDTO> lista de libros que coinciden
     */
    public List<LibroDTO> buscarPorAutor(String autor) {
        List<LibroDTO> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE autor LIKE ? ORDER BY titulo";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + autor + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LibroDTO libro = mapearResultSetALibro(rs);
                    libros.add(libro);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar por autor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return libros;
    }
    
    /**
     * Buscar libros por categoría
     * @param categoria Categoría a buscar (búsqueda parcial)
     * @return List<LibroDTO> lista de libros que coinciden
     */
    public List<LibroDTO> buscarPorCategoria(String categoria) {
        List<LibroDTO> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE categoria LIKE ? ORDER BY titulo";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + categoria + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LibroDTO libro = mapearResultSetALibro(rs);
                    libros.add(libro);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        
        return libros;
    }
    
    /**
     * Obtener libros disponibles
     * @return List<LibroDTO> lista de libros disponibles
     */
    public List<LibroDTO> obtenerLibrosDisponibles() {
        List<LibroDTO> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE disponible = 1 ORDER BY titulo";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LibroDTO libro = mapearResultSetALibro(rs);
                libros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener libros disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return libros;
    }
    
    /**
     * Cambiar disponibilidad de un libro
     * @param idLibro ID del libro
     * @param disponible Nueva disponibilidad
     * @return boolean true si se cambió exitosamente
     */
    public boolean cambiarDisponibilidad(int idLibro, boolean disponible) {
        String sql = "UPDATE libros SET disponible = ? WHERE id = ?";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setBoolean(1, disponible);
            ps.setInt(2, idLibro);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al cambiar disponibilidad: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Búsqueda general (título, autor o categoría)
     * @param termino Término de búsqueda
     * @return List<LibroDTO> lista de libros que coinciden
     */
    public List<LibroDTO> busquedaGeneral(String termino) {
        List<LibroDTO> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ? OR autor LIKE ? OR categoria LIKE ? ORDER BY titulo";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String patron = "%" + termino + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LibroDTO libro = mapearResultSetALibro(rs);
                    libros.add(libro);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error en búsqueda general: " + e.getMessage());
            e.printStackTrace();
        }
        
        return libros;
    }
    
    /**
     * Obtener todas las categorías únicas
     * @return List<String> lista de categorías
     */
    public List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM libros WHERE categoria IS NOT NULL ORDER BY categoria";
        
        try (Connection con = Conexion.getNuevaConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categorias;
    }
    
    /**
     * Método auxiliar para mapear ResultSet a LibroDTO
     * @param rs ResultSet de la consulta
     * @return LibroDTO objeto libro
     * @throws SQLException si hay error en el mapeo
     */
    private LibroDTO mapearResultSetALibro(ResultSet rs) throws SQLException {
        LibroDTO libro = new LibroDTO();
        libro.setId(rs.getInt("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnio(rs.getInt("anio"));
        libro.setCategoria(rs.getString("categoria"));
        libro.setDisponible(rs.getBoolean("disponible"));
        
        return libro;
    }
}