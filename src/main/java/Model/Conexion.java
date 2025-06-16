package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para manejar la conexión a la base de datos MySQL
 * @author HAWLETH
 */
public class Conexion {
    
    // Configuración de la base de datos para XAMPP
    private static final String URL = "jdbc:mysql://localhost:3306/adso_biblioteca";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private static Connection conexion = null;
    
    /**
     * Método para obtener una conexión a la base de datos
     * @return Connection objeto de conexión
     */
    public static Connection getConexion() {
        try {
            // Cargar el driver de MySQL
            Class.forName(DRIVER);
            
            // Establecer la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            
            System.out.println("Conexión establecida exitosamente");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de MySQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos");
            e.printStackTrace();
        }
        
        return conexion;
    }
    
    /**
     * Método para cerrar la conexión
     */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada exitosamente");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión");
            e.printStackTrace();
        }
    }
    
    /**
     * Método para verificar si la conexión está activa
     * @return boolean true si está conectado, false si no
     */
    public static boolean isConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Método para obtener una nueva conexión (útil para múltiples operaciones)
     * @return Connection nueva conexión
     */
    public static Connection getNuevaConexion() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al crear nueva conexión");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Método para probar la conexión
     */
    public static void probarConexion() {
        Connection con = getConexion();
        if (con != null) {
            System.out.println("¡Conexión exitosa a la base de datos adso_biblioteca!");
            cerrarConexion();
        } else {
            System.out.println("Error: No se pudo conectar a la base de datos");
        }
    }
}