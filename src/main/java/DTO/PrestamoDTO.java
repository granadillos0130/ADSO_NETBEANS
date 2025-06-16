package DTO;

import java.sql.Date;

/**
 * Clase DTO para representar un Préstamo
 * @author HAWLETH
 */
public class PrestamoDTO {
    private int id;
    private int idUsuario;
    private int idLibro;
    private Date fechaPrestamo;
    private Date fechaDevolucion;
    private boolean devuelto;
    
    // Campos adicionales para mostrar información completa
    private String nombreUsuario;
    private String tituloLibro;
    private String autorLibro;
    
    // Constructor vacío
    public PrestamoDTO() {
    }
    
    // Constructor completo
    public PrestamoDTO(int id, int idUsuario, int idLibro, Date fechaPrestamo, 
                      Date fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }
    
    // Constructor sin ID (para inserción)
    public PrestamoDTO(int idUsuario, int idLibro, Date fechaPrestamo, 
                      Date fechaDevolucion, boolean devuelto) {
        this.idUsuario = idUsuario;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }
    
    // Constructor con información adicional
    public PrestamoDTO(int id, int idUsuario, int idLibro, Date fechaPrestamo, 
                      Date fechaDevolucion, boolean devuelto, String nombreUsuario, 
                      String tituloLibro, String autorLibro) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
        this.nombreUsuario = nombreUsuario;
        this.tituloLibro = tituloLibro;
        this.autorLibro = autorLibro;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public int getIdLibro() {
        return idLibro;
    }
    
    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }
    
    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }
    
    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }
    
    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }
    
    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
    
    public boolean isDevuelto() {
        return devuelto;
    }
    
    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public String getTituloLibro() {
        return tituloLibro;
    }
    
    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }
    
    public String getAutorLibro() {
        return autorLibro;
    }
    
    public void setAutorLibro(String autorLibro) {
        this.autorLibro = autorLibro;
    }
    
    @Override
    public String toString() {
        return "PrestamoDTO{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", idLibro=" + idLibro +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucion=" + fechaDevolucion +
                ", devuelto=" + devuelto +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", tituloLibro='" + tituloLibro + '\'' +
                ", autorLibro='" + autorLibro + '\'' +
                '}';
    }
}