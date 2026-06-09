package com.racing.eventos;

import com.racing.vistas.Instrucciones;
import com.racing.vistas.MenuInicio;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase InstruccionesControlador.
 * Actúa como el puente de control (Controlador) para la interfaz de instrucciones del usuario.
 * Implementa la interfaz ActionListener para escuchar de forma activa los estímulos gráficos
 * de la vista y gestionar la navegación de retorno al menú principal mediante la reestructuración del contenedor raíz.
 */
public class InstruccionesControlador implements  ActionListener{

    private Instrucciones vistaInstrucciones; // Referencia encapsulada de la interfaz gráfica asociada (Vista)

    /**
     * Constructor del controlador.
     * Incorpora inyección de dependencias para vincular el panel de vista con su respectivo gestor de eventos.
     * @param vistaInstrucciones Instancia de la pantalla informativa de instrucciones.
     */
    public InstruccionesControlador(Instrucciones vistaInstrucciones){
        this.vistaInstrucciones = vistaInstrucciones;
    }

    /**
     * Captura y procesa los eventos de acción ejecutados en la interfaz de instrucciones.
     * Intercepta la pulsación del botón de retorno y altera el árbol de componentes del JFrame principal.
     * @param e Objeto ActionEvent con los metadatos del evento de hardware disparado.
     */
    @Override
    public void actionPerformed(ActionEvent e){

        // Verifica desde donde viene el evento
        if (e.getSource() == vistaInstrucciones.getBtnRegresar()){
            System.out.println("Regresando al menú de inicio");

            JFrame ventanaPrincipal = (JFrame) SwingUtilities.getWindowAncestor(vistaInstrucciones); // Trae el Frame principal

            // Previene excepciones de puntero nulo (NullPointerException) si el panel está huérfano
            if (ventanaPrincipal != null){

                MenuInicio nuevoMenuInicio = new MenuInicio(ventanaPrincipal); // Construye el nuevo panel del Menú de Inicio pasándole la ventana raíz

                //Se remueve la ventana de instrucciones
                ventanaPrincipal.remove(vistaInstrucciones); // Remueve el panel anterior
                ventanaPrincipal.add(nuevoMenuInicio);  // Agrega el nuevo panel

                // Fuerza a Swing a redibujar la pantalla con los nuevos componentes
                ventanaPrincipal.revalidate();
                ventanaPrincipal.repaint();
            }
        }
    }

}
