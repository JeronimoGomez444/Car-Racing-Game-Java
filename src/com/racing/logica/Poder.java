package com.racing.logica;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

/**
 * Clase Modelo Poder.
 * Modela las entidades de recompensa y modificadores de estado (Power-Ups) que caen aleatoriamente en la pista.
 * Administra el movimiento rectilíneo vertical, la delimitación geométrica de impacto (hitbox) y la
 * carga automatizada de texturas indexadas según el tipo identificador del potenciador.
 */
public class Poder {

    private int x;
    private int y;

    // Dimensiones estáticas para el renderizado y la hitbox de los poderes
    private final int ancho = 40;
    private final int alto = 40;

    private int velocidad; // Píxeles que baja por cada frame del Game Loop

    private int tipoPoder; // Identificador numérico del tipo de poder (0: Escudo, 1: Sobrecarga, 2: PEM, 3: Reparación)

    private  Image spritePoder; // Referencias para la gestión gráfica del sprite de los poderes

    /**
     * Constructor de la clase Poder.
     * @param xInicial Coordenada fija en el eje X (carril seleccionado para la caída).
     * @param tipoPoder Entero que define el poder.
     * @param velocidadActual Velocidad de arrastre vertical calculada por la dificultad escalable.
     */
    public Poder(int xInicial, int tipoPoder, int velocidadActual){
        this.x = xInicial;
        this.y = -alto; // Iniciación segura en el eje y, justo arriba, oculto fuera de la pantalla
        this.tipoPoder = tipoPoder;
        this.velocidad = velocidadActual;
        cargarSprite();
    }

    /**
     * Recupera el sprite del poder de manera dinamica desde el Classpath de recursos de la aplicación.
     * Aplica una técnica de convención sobre configuración encadenando la variable 'tipoPoder'
     * en la ruta de la cadena, evitando la necesidad de estructuras de control condicionales redundantes.
     */
    private void cargarSprite(){
        try {
            String ruta = "/imagenes/poder" + tipoPoder + ".png";
            URL url = getClass().getResource(ruta);
            if (url != null) {
                spritePoder = new ImageIcon(url).getImage();
            } else {
                System.err.println("No se encontró el sprite del poder en: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar sprite del poder: " + e.getMessage());
        }
    }

    /**
     *  Actualiza el estado físico del poder.
     * Incrementa la posición en el eje Y según la velocidad actual y recalcula la proyección
     * en el eje X para mantener la coherencia del efecto visual de perspectiva.
     */
    public void actualizar(){
        this.y += velocidad;
    }

    /**
     * Renderiza el componente en la interfaz gráfica.
     * @param g Contexto de renderizado de la biblioteca AWT.
     */
    public void dibujar(Graphics g){
        if (spritePoder != null){
            g.drawImage(spritePoder, x, y, ancho, alto, null); // Dibuja el sprite cargado
        }else{
            g.setColor(java.awt.Color.YELLOW);
            g.fillRect(x, y, ancho, alto); // Respaldo visual en caso de no cargar el sprite
        }
    }

    /**
     * Genera un volumen delimitador envolvente de tipo geométrico rectangular.
     * Es utilizado por el gestor de física del ciclo de juego para calcular colisiones asíncronas.
     * @return Objeto Rectangle con el área exacta ocupada por el obstaculo.
     */
    public java.awt.Rectangle getBounds(){
        return new java.awt.Rectangle(x, y, ancho, alto);
    }

    // Mutador de Estado
    public void setVelocidad(int nuevaVelocidad){
        this.velocidad = nuevaVelocidad;
    }

    // Métodos de acceso (Getters)
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getTipoPoder() { return tipoPoder; }
}
