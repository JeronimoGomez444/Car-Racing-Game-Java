package com.racing.eventos;

import com.racing.vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase MenuInicioControlador.
 * Actúa como el enrutador principal de escenas (Controlador Central) de la aplicación.
 * Implementa ActionListener para capturar comandos del menú y NavegacionEscenas para resolver
 * ciclos de retorno asíncronos desde el canvas de carrera. Administra sub-ventanas modales de registro
 * e inyecta flujos de datos clasificados hacia las interfaces de destino.
 */
public class MenuInicioControlador implements ActionListener, NavegacionEscenas {

    // Referencias de control de la capa de presentación (Vista y Marco raíz)
    private MenuInicio menuInicioVista;
    private JFrame ventanaPrincipal; // Guardamos la ventana de forma segura en memoria

    /**
     * Constructor del controlador central.
     * @param menuInicioVista  Referencia del panel que expone el menú inicial.
     * @param ventanaPrincipal Contenedor jerárquico superior (JFrame) de la aplicación.
     */
    public MenuInicioControlador(MenuInicio menuInicioVista, JFrame ventanaPrincipal){
        this.menuInicioVista = menuInicioVista;
        this.ventanaPrincipal = ventanaPrincipal;
    }

    /**
     * Intercepta las interacciones de los botones del menú principal..
     * @param e Objeto ActionEvent descriptor del estímulo de entrada.
     */
    @Override
    public void actionPerformed(ActionEvent e){

        // Cierra el juego
        if (e.getSource() == menuInicioVista.getBtnSalir()){
            System.out.println("Cerrando el juego...");
            menuInicioVista.getGestorAudio().detenerMusica(); // Detiene la música
            System.exit(0); // Finaliza la ejecución de la Máquina Virtual de Java (JVM) con estado 0 (sin errores)
        }

        // Accede a la ventana de instrucciones
        if (e.getSource() == menuInicioVista.getBtnInstrucciones()){
            System.out.println("Abriendo las instrucciones");

            JFrame ventanaPrincipal = (JFrame) SwingUtilities.getWindowAncestor(menuInicioVista);

            if (ventanaPrincipal != null){

                Instrucciones vistaInstrucciones = new Instrucciones(); // Instancia la clase/vista Instrucciones

                ventanaPrincipal.remove(menuInicioVista); // Remueve el panel anterior
                ventanaPrincipal.add(vistaInstrucciones); // Agrega el nuevo panel

                // Fuerza a Swing a redibujar la pantalla con los nuevos componentes
                ventanaPrincipal.revalidate();
                ventanaPrincipal.repaint();
            }
        }

        // Accede a la ventana de juego
        if (e.getSource() == menuInicioVista.getBtnIniciar()){

            System.out.println("Iniciando el juego");

            JFrame ventanaPrincipal = (JFrame) SwingUtilities.getWindowAncestor(menuInicioVista);

            if (ventanaPrincipal != null){

                String[] nombresPilotos = mostrarModalNombres(ventanaPrincipal); // Despliega el diálogo modal de registro de pilotos bloqueando el hilo de eventos de forma controlada

                // No inicial el juego si no se presiona "A Correr" y hay los nombres
                if (nombresPilotos[0] == null || nombresPilotos[1] == null) {
                    System.out.println("Inicio de juego cancelado por el usuario.");
                    return;
                }

                // Detener la música del Menú de inicio antes de cambiar de pantalla
                if (menuInicioVista.getGestorAudio() != null){
                    menuInicioVista.getGestorAudio().detenerMusica();
                }

                Juego pantallaJuego =  new Juego(this, nombresPilotos[0], nombresPilotos[1]);  // Instancia la clase/vista Juego

                ventanaPrincipal.remove(menuInicioVista); // Remueve el panel anterior
                ventanaPrincipal.add(pantallaJuego); // Agrega el nuevo panel

                // Fuerza a Swing a redibujar la pantalla con los nuevos componentes
                ventanaPrincipal.revalidate();
                ventanaPrincipal.repaint();

                // Pone un foco sobre la nueva vista para que los controles del teclado funcionen
                pantallaJuego.requestFocusInWindow();
            }
        }

        // Accede a la ventana de ranking
        if (e.getSource() == menuInicioVista.getBtnRanking()) {
            System.out.println("Abriendo la pantalla de Ranking desde el Menú Principal...");

            if (ventanaPrincipal != null) {
                java.util.ArrayList<com.racing.logica.GestorRanking.PuntajeJugador> topTres =
                        com.racing.logica.GestorRanking.obtenerTopTres(); // Obtiene el ranking con los tres mejores resultados registrados en el juego

                Ranking vistaRanking = new Ranking(topTres); // Instancia la clase/vista Ranking

                // Desacopla de forma masiva los elementos anteriores y monta la nueva escena
                ventanaPrincipal.getContentPane().removeAll();
                ventanaPrincipal.getContentPane().add(vistaRanking);

                // Fuerza a Swing a redibujar la pantalla con los nuevos componentes
                ventanaPrincipal.revalidate();
                ventanaPrincipal.repaint();
            }
        }
    }

    /**
     * Construye y despliega un cuadro de diálogo bloqueante (JDialog Modal) en el centro de la pantalla
     * para la capturar los nombres de los pilotos.
     * @param ventanaPrincipal Marco contenedor padre sobre el cual se ancla la modalidad del diálogo.
     * @return Un arreglo unidimensional indexado con las cadenas de los nombres [Jugador1, Jugador2].
     */
    private String[] mostrarModalNombres(JFrame ventanaPrincipal) {
        String[] nombres = new String[2];

        // Definición del modal
        JDialog dialogo = new JDialog(ventanaPrincipal, "Registro de Pilotos", true);
        dialogo.setSize(400, 250);
        dialogo.setLayout(null);
        dialogo.setLocationRelativeTo(ventanaPrincipal);
        dialogo.setResizable(false);
        dialogo.getContentPane().setBackground(new java.awt.Color(30, 30, 30));

        // Interfaz gráfica para el Jugador 1
        JLabel lblJ1 = new JLabel("Piloto 1");
        lblJ1.setForeground(java.awt.Color.CYAN);
        lblJ1.setBounds(50, 30, 150, 25);
        dialogo.add(lblJ1);

        JTextField txtJ1 = new JTextField("");
        txtJ1.setBounds(50, 60, 280, 25);
        dialogo.add(txtJ1);

        // Interfaz gráfica para el Jugador 2
        JLabel lblJ2 = new JLabel("Piloto 2");
        lblJ2.setForeground(java.awt.Color.MAGENTA);
        lblJ2.setBounds(50, 100, 150, 25);
        dialogo.add(lblJ2);

        JTextField txtJ2 = new JTextField("");
        txtJ2.setBounds(50, 130, 280, 25);
        dialogo.add(txtJ2);

        // Botón para iniciar la carrera
        JButton btnAceptar = new JButton("¡A CORRER!");
        btnAceptar.setBounds(130, 175, 120, 30);
        btnAceptar.setBackground(java.awt.Color.BLACK);
        btnAceptar.setForeground(java.awt.Color.WHITE);

        // Escucha el evento del botón aceptar y cierra el modal para que inicie la carrera
        btnAceptar.addActionListener(evt -> {
            nombres[0] = txtJ1.getText();
            nombres[1] = txtJ2.getText();
            dialogo.dispose();
        });
        dialogo.add(btnAceptar);

        // Muestra el modal (el código se detiene aquí hasta que el diálogo se cierre)
        dialogo.setVisible(true);

        return nombres;
    }

    /**
     * Implementación obligatoria del contrato de navegación de escenas.
     * Ejecuta el retorno seguro y asíncrono hacia la pantalla de inicio una vez concluidos los subprocesos de la carrera.
     */
    @Override
    public void irAlMenuInicio() {
        System.out.println("Regresando al Menú de Inicio desde el Juego...");

        // Delega la manipulación del árbol de vistas de forma segura en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            if (ventanaPrincipal != null) {
                ventanaPrincipal.getContentPane().removeAll(); // Elimina el juego anterior para liberar recursos de memoria RAM

                // Re-inyecta el menú original con dimensiones explícitas
                menuInicioVista.setBounds(0, 0, 1280, 720);
                ventanaPrincipal.getContentPane().add(menuInicioVista);

                // Re-empaqueta y pinta
                ventanaPrincipal.pack();
                ventanaPrincipal.setLocationRelativeTo(null);

                // Doble capa de sincronización y repintado de buffers gráficos
                ventanaPrincipal.getContentPane().revalidate();
                ventanaPrincipal.getContentPane().repaint();
                ventanaPrincipal.revalidate();
                ventanaPrincipal.repaint();

                // Restaura la música ambiental
                if (menuInicioVista.getGestorAudio() != null) {
                    menuInicioVista.getGestorAudio().reproducirMusica("MenuInicio.wav");
                }

                System.out.println("¡Transición exitosa!");
            } else {
                System.err.println("Error grave: La referencia de la ventana es nula.");
            }
        });
    }

}
