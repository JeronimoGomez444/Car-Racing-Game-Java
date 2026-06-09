package com.racing.logica;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

/**
 * Clase Modelo Auto.
 * Representa la entidad lógica y gráfica de los vehículos controlados por los jugadores.
 * Administra las coordenadas de posicionamiento discretizado por carriles, la carga dinámica de texturas (sprites),
 * la animación de suspensión y las hitboxes de colisión.
 */
public class Auto {

    private int x;
    private int y;

    // Dimensiones estáticas para el renderizado y la hitbox del vehículo
    private int ancho = 40;
    private int alto = 65;

    // Arreglo indexado con las coordenadas fijas en X para cada carril de la pista
    private int[] carrilesX;

    // 0 = Izquierda, 1 = Centro, 2 = Derecha
    private int carrilActual;

    // Referencias para la gestión gráfica del sprite del vehículo
    private Image spriteAuto;
    private String rutaImagen;

    // Variable usaba para calcular el movimiento de "suspensión" de los vehículos
    private double balanceo = 0;

    /**
     * Constructor de la clase Auto.
     * @param posicionesCarriles Arreglo con las posiciones X de los carriles disponibles en la pista.
     * @param yInicial Coordenada fija en el eje Y (línea base del jugador).
     * @param rutaImagen Ruta relativa en el classpath para el recurso del sprite.
     */
    public Auto(int[] posicionesCarriles, int yInicial, String rutaImagen) {
        this.carrilesX = posicionesCarriles;
        this.y = yInicial;
        this.carrilActual = 1; // Inicia al vehiculo por defecto en el carril central
        this.x = carrilesX[carrilActual];
        this.rutaImagen = rutaImagen;

        // Carga los sprites apenas se instancia la clase
        cargarSprite();
    }

    /**
     * Carga el sprite del vehículo desde el sistema de archivos del proyecto utilizando el classpath.
     * Si no encuentra el recurso, deja un mensaje indicando esto en la consola.
     */
    private void cargarSprite(){
        try{
            URL url = getClass().getResource(rutaImagen);

            if (url != null){
                spriteAuto = new ImageIcon(url).getImage();
            }else {
                System.err.println("Error: No se encontró el sprite del auto en: " + rutaImagen);
            }
        }catch (Exception e){
            System.err.println("Error al cargar el sprite del auto: " + e.getMessage());
        }
    }

    /**
     * Desplaza el vehículo de forma segura hacia el carril izquierdo.
     */
    public void moverIzquierda(){
        if (carrilActual > 0){
            carrilActual --;
            x = carrilesX[carrilActual]; // Actualiza la posición física en el eje X
        }
    }

    /**
     * Desplaza el vehículo de forma segura hacia el carril derecho.
     */
    public void moverDerecha(){
        if (carrilActual < 2){
            carrilActual ++;
            x = carrilesX[carrilActual]; // Actualiza la posición física en el eje X
        }
    }

    /**
     * Gestiona el renderizado visual del vehículo en el lienzo gráfico.
     * Incorpora un desfase sinusoidal simulando el balanceo mecánico de la amortiguación a alta velocidad.
     * @param g Contexto gráfico del componente sobre el cual se realiza el dibujo.
     */
    public void dibujar(Graphics g){
        if (spriteAuto != null) {
            balanceo += 0.1; // Efecto de balanceo visual
            int yConBalanceo = y + (int)(Math.sin(balanceo) * 2);
            g.drawImage(spriteAuto, x, yConBalanceo, ancho, alto, null); // Dibuja el sprite cargado en la hitbox
        } else {
            g.fillRect(x, y, ancho, alto); // Respaldo visual por si no carga la imagen
        }
    }

    /**
     * Genera un volumen delimitador envolvente de tipo geométrico rectangular.
     * Es utilizado por el gestor de física del ciclo de juego para calcular colisiones asíncronas.
     * @return Objeto Rectangle con el área exacta ocupada por el vehículo.
     */
    public java.awt.Rectangle getBounds(){
        return new java.awt.Rectangle(x, y, ancho, alto);
    }

    // Métodos de acceso (Getters)
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}
