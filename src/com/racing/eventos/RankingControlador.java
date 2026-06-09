package com.racing.eventos;

import com.racing.vistas.MenuInicio;
import com.racing.vistas.Ranking;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase RankingControlador.
 * Actúa como el mediador lúdico (Controlador) para la interfaz gráfica del Ranking.
 * Implementa el modelo de delegación de eventos de AWT para capturar las pulsaciones de botones
 * y orquestar la navegación inversa (retorno) hacia el menú principal mediante la mutación dinámica del JFrame.
 */
public class RankingControlador implements ActionListener {

    private Ranking vistaRanking; // Referencia encapsulada de la interfaz gráfica asociada (Vista)

    /**
     * Constructor del controlador.
     * Aplica inyección de dependencias para vincular la vista con su controlador.
     * @param vistaRanking Instancia de la pantalla de Ranking que emite los eventos.
     */
    public RankingControlador(Ranking vistaRanking) {
        this.vistaRanking = vistaRanking;
    }

    /**
     * Intercepta y procesa las acciones de pulsación emitidas por los componentes de la vista.
     * Evalúa el origen del evento y ejecuta la transición estructural de pantallas sobre el contenedor raíz.
     * @param e Objeto ActionEvent con los metadatos y origen del estímulo de hardware.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaRanking.getBtnRegresar()) {
            System.out.println("Regresando al menú de inicio desde el Ranking");

            JFrame ventanaPrincipal = (JFrame) SwingUtilities.getWindowAncestor(vistaRanking); // Trae el Frame principal

            // Previene excepciones de puntero nulo (NullPointerException) si el panel está huérfano
            if (ventanaPrincipal != null) {
                MenuInicio nuevoMenuInicio = new MenuInicio(ventanaPrincipal); // Construye el nuevo panel del Menú de Inicio pasándole la ventana raíz

                ventanaPrincipal.remove(vistaRanking); // Remueve el panel anterior
                ventanaPrincipal.add(nuevoMenuInicio); // Agrega el nuevo panel

                // Fuerza a Swing a redibujar la pantalla con los nuevos componentes
                ventanaPrincipal.revalidate();
                ventanaPrincipal.repaint();
            }
        }
    }
}