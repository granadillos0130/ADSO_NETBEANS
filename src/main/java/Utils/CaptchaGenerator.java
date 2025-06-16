package Utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Clase para generar y validar CAPTCHA
 * @author HAWLETH
 */
public class CaptchaGenerator {
    
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CAPTCHA = 6;
    private static final int ANCHO_IMAGEN = 200;
    private static final int ALTO_IMAGEN = 80;
    private static final Random random = new Random();
    
    /**
     * Genera un texto aleatorio para el CAPTCHA
     * @return String texto aleatorio de 6 caracteres
     */
    public static String generarTextoCaptcha() {
        StringBuilder texto = new StringBuilder();
        
        for (int i = 0; i < LONGITUD_CAPTCHA; i++) {
            int indice = random.nextInt(CARACTERES.length());
            texto.append(CARACTERES.charAt(indice));
        }
        
        return texto.toString();
    }
    
    /**
     * Genera una imagen CAPTCHA con el texto proporcionado
     * @param texto El texto a incluir en la imagen
     * @return String imagen en formato Base64
     */
    public static String generarImagenCaptcha(String texto) {
        try {
            // Crear imagen con fondo blanco
            BufferedImage imagen = new BufferedImage(ANCHO_IMAGEN, ALTO_IMAGEN, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = imagen.createGraphics();
            
            // Configurar renderizado
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fondo blanco
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, ANCHO_IMAGEN, ALTO_IMAGEN);
            
            // Agregar líneas aleatorias de ruido
            agregarLineasRuido(g2d);
            
            // Dibujar el texto con caracteres aleatorios
            dibujarTexto(g2d, texto);
            
            g2d.dispose();
            
            // Convertir imagen a Base64
            return imagenABase64(imagen);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Agrega líneas aleatorias de ruido a la imagen
     * @param g2d Graphics2D para dibujar
     */
    private static void agregarLineasRuido(Graphics2D g2d) {
        // Dibujar entre 3 y 7 líneas aleatorias
        int numeroLineas = 3 + random.nextInt(5);
        
        for (int i = 0; i < numeroLineas; i++) {
            // Color aleatorio
            g2d.setColor(new Color(
                random.nextInt(100) + 100,  // R
                random.nextInt(100) + 100,  // G
                random.nextInt(100) + 100   // B
            ));
            
            // Coordenadas aleatorias
            int x1 = random.nextInt(ANCHO_IMAGEN);
            int y1 = random.nextInt(ALTO_IMAGEN);
            int x2 = random.nextInt(ANCHO_IMAGEN);
            int y2 = random.nextInt(ALTO_IMAGEN);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * Dibuja el texto del CAPTCHA con efectos aleatorios
     * @param g2d Graphics2D para dibujar
     * @param texto Texto a dibujar
     */
    private static void dibujarTexto(Graphics2D g2d, String texto) {
        // Configurar fuente
        Font fuente = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(fuente);
        
        // Calcular espaciado entre caracteres
        int espaciado = ANCHO_IMAGEN / (texto.length() + 1);
        
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            
            // Color aleatorio para cada caracter
            g2d.setColor(new Color(
                random.nextInt(100),        // R (más oscuro)
                random.nextInt(100),        // G
                random.nextInt(100)         // B
            ));
            
            // Posición con pequeña variación aleatoria
            int x = espaciado * (i + 1) + random.nextInt(10) - 5;
            int y = ALTO_IMAGEN / 2 + random.nextInt(10) - 5;
            
            // Rotar el caracter ligeramente
            double angulo = Math.toRadians(random.nextInt(30) - 15);
            g2d.rotate(angulo, x, y);
            
            // Dibujar el caracter
            g2d.drawString(String.valueOf(caracter), x, y);
            
            // Restaurar rotación
            g2d.rotate(-angulo, x, y);
        }
    }
    
    /**
     * Convierte una imagen a formato Base64
     * @param imagen BufferedImage a convertir
     * @return String imagen en Base64
     * @throws IOException si hay error en la conversión
     */
    private static String imagenABase64(BufferedImage imagen) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagen, "png", baos);
        byte[] datosImagen = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(datosImagen);
    }
    
    /**
     * Valida si el texto ingresado coincide con el CAPTCHA real
     * @param textoIngresado Texto que ingresó el usuario
     * @param textoReal Texto real del CAPTCHA
     * @return boolean true si coinciden (ignorando mayúsculas/minúsculas)
     */
    public static boolean validarCaptcha(String textoIngresado, String textoReal) {
        if (textoIngresado == null || textoReal == null) {
            return false;
        }
        
        return textoIngresado.trim().equalsIgnoreCase(textoReal.trim());
    }
    
    /**
     * Método para generar un CAPTCHA completo (texto e imagen)
     * @return String[] array con [0] = texto, [1] = imagen en Base64
     */
    public static String[] generarCaptchaCompleto() {
        String texto = generarTextoCaptcha();
        String imagen = generarImagenCaptcha(texto);
        return new String[]{texto, imagen};
    }
}