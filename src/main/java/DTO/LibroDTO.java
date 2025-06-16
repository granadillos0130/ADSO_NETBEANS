package DTO;

/**
 * Clase DTO para representar un Libro
 * @author HAWLETH
 */
public class LibroDTO {
    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private int anio;
    private String categoria;
    private boolean disponible;
    
    // Constructor vacío
    public LibroDTO() {
    }
    
    // Constructor completo
    public LibroDTO(int id, String titulo, String autor, String editorial, 
                   int anio, String categoria, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anio = anio;
        this.categoria = categoria;
        this.disponible = disponible;
    }
    
    // Constructor sin ID (para inserción)
    public LibroDTO(String titulo, String autor, String editorial, 
                   int anio, String categoria, boolean disponible) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anio = anio;
        this.categoria = categoria;
        this.disponible = disponible;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getAutor() {
        return autor;
    }
    
    public void setAutor(String autor) {
        this.autor = autor;
    }
    
    public String getEditorial() {
        return editorial;
    }
    
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }
    
    public int getAnio() {
        return anio;
    }
    
    public void setAnio(int anio) {
        this.anio = anio;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    @Override
    public String toString() {
        return "LibroDTO{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", editorial='" + editorial + '\'' +
                ", anio=" + anio +
                ", categoria='" + categoria + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}