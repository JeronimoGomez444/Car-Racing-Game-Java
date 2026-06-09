package com.racing.logica;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

/**
 * Clase Modelo Obstaculo.
 * Representa las entidades de "amenaza" que salen aleatoriamente en la pista de carreras.
 * Controla el ciclo de actualización física (descenso vertical) y la renderización de sprites.
 */
public class Obstaculo {

    private int x;
    private int y;

    // Dimensiones de la caja de impacto (hitbox)
    private int ancho = 40;
    private int alto = 40;

    private int velocidad; // Píxeles que baja por cada frame del Game Loop

    private int carril; // Índice del carril asignado: 0 = Izquierda, 1 = Centro, 2 = Derecha
    private int[] carrilesDestino;

    private Image spriteObstaculo; // Recurso gráfico del obstáculo

    /**
     * Constructor de la clase Obstaculo.
     * @param carril Índice de carril por el cual descenderá el obstaculo (0, 1 o 2).
     * @param carrilesDestino Arreglo de coordenadas X destino correspondientes a la pista.
     * @param velocidadActual Velocidad inicial de descenso mecánico.
     */
    public Obstaculo(int carril, int[]carrilesDestino, int velocidadActual){
        this.carril = carril;
        this.carrilesDestino = carrilesDestino;
        this.y = -alto; // Iniciación segura en el eje y, justo arriba, oculto fuera de la pantalla
        this.velocidad = velocidadActual;

        cargarSprite();
        actualizarPosicionX(); // Calcula la X inicial
    }

    /**
     * Recupera el sprite del obstáculo desde el Classpath de recursos de la aplicación.
     */
    private void  cargarSprite(){
        try {
            URL url = getClass().getResource("/imagenes/policia.png");
            if (url != null) {
                spriteObstaculo = new ImageIcon(url).getImage();
            } else {
                System.err.println("Error: No se encontró el sprite del obstáculo.");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el sprite del obstáculo: " + e.getMessage());
        }
    }

    /**
     * Actualiza el estado físico del obstaculo.
     * Incrementa la posición en el eje Y según la velocidad actual y recalcula la proyección
     * en el eje X para mantener la coherencia del efecto visual de perspectiva.
     */
    public void actualizar(){
        this.y += velocidad; // Mueve el obstáculo hacia abajo de manera vertical
        actualizarPosicionX(); // Recalcula la X según baje en la pantalla
    }

    /**
     * Calcula la posición matemática exacta en el eje X.
     * Permite que los obstáculos nazcan en el punto de fuga del horizonte y se expandan hacia los laterales.
     */
    public void actualizarPosicionX(){
        int xDestino = carrilesDestino[carril] + 2; // El centro exacto de la parte baja

        if (carril == 1){
            this.x = xDestino;
        }else{
            // Buscamos el centro de la sub-pantalla (donde está el punto de fuga arriba)
            // Si el destino es menor a 640 es el J1 (centro aprox 302), si no, es el J2 (centro aprox 942)
            int puntoFugaX = (xDestino < 640) ? carrilesDestino[1] + 2 : carrilesDestino[1] + 2;

            // Interpolación lineal básica: calculamos qué tan abajo está el obstáculo (0.0 arriba, 1.0 abajo)
            double progresoY = (double) (this.y + alto) / 560.0;
            if (progresoY > 1.0) progresoY = 1.0;
            if (progresoY < 0.0) progresoY = 0.0;

            // Ecuación de interpolación lineal fundamental: x = PuntoInicial + (PuntoFinal - PuntoInicial) * Progreso
            // Desplaza lateralmente el objeto hacia afuera a medida que se incrementa el progreso vertical
            this.x = (int) (puntoFugaX + (xDestino - puntoFugaX) * progresoY);
        }

    }

    /**
     * Renderiza el componente en la interfaz gráfica.
     * @param g Contexto de renderizado de la biblioteca AWT.
     */
    public void dibujar(Graphics g){
        if (spriteObstaculo != null) {
            g.drawImage(spriteObstaculo, x, y, ancho, alto, null); // Dibuja el sprite cargado
        } else {
            g.setColor(java.awt.Color.RED);
            g.fillRect(x, y, ancho, alto);  // Respaldo visual en caso de no cargar el sprite
        }
    }

    /**
     * Genera un volumen delimitador envolvente de tipo geométrico rectangular.
     * Es utilizado por el gestor de física del ciclo de juego para calcular colisiones asíncronas.
     * @return Objeto Rectangle con el área exacta ocupada por el obstaculo.
     */
    public java.awt.Rectangle getBounds() {
        return new java.awt.Rectangle(x, y, ancho, alto);
    }

    // Mutador de Estado
    public void setVelocidad(int nuevaVelocidad){
        this.velocidad = nuevaVelocidad;
    }

    // Métodos de acceso (Getters)
    public int getY() { return y; }
    public int getX() { return x; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}
