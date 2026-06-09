package com.racing.vistas;

import com.racing.eventos.RankingControlador;
import com.racing.logica.GestorRanking.PuntajeJugador;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Clase Vista Ranking.
 * Representa el componente de presentación de la interfaz gráfica (UI) para el cuadro de honor.
 * Hereda de JPanel y gestiona la colocación absoluta de componentes, la carga de recursos de fondo
 * y el dibujo superpuesto con transparencias para desplegar los tres mejores puntajes.
 */
public class Ranking extends JPanel {

    private JButton btnRegresar;
    private ArrayList<PuntajeJugador> topTres;
    private Image imgFondoRanking;

    /**
     * Constructor de la clase Ranking.
     * @param topTres Colección pre-ordenada de objetos PuntajeJugador con los récords históricos.
     */
    public Ranking(ArrayList<PuntajeJugador> topTres) {
        this.topTres = topTres;

        setPreferredSize(new Dimension(1280, 720));
        setLayout(null);

        // Inicia los recursos
        cargarFondo();
        inicializarComponentes();

        // Instancia el controlador y suscribe el botón de regreso
        RankingControlador controlador = new RankingControlador(this);
        btnRegresar.addActionListener(controlador);
    }

    /**
     * Carga el fondo de la pantalla desde el Classpath de recursos del proyecto.
     */
    private void cargarFondo() {
        try {
            URL urlFondoRanking = getClass().getResource("/imagenes/fondo-ranking.jpg");

            if (urlFondoRanking != null) {
                imgFondoRanking = new ImageIcon(urlFondoRanking).getImage();
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el fondo del Ranking: " + e.getMessage());
        }
    }

    /**
     * Inicializa, estiliza y posiciona los componentes lúdicos de la interfaz gráfica.
     * Implementa un algoritmo de renderizado dinámico en bucle para construir las filas del podio.
     */
    private void inicializarComponentes() {
        // Estilo de fuentes y colores
        Font fuenteTitulo = new Font("Verdana", Font.BOLD, 60);
        Font fuenteTabla = new Font("Segoe UI", Font.BOLD, 28);
        Font fuenteBotones = new Font("Segoe UI", Font.BOLD, 16);

        Color colorNeonCian = new Color(0, 255, 255);
        Color colorNeonRosa = new Color(255, 0, 128);
        Color colorGris = new Color(0, 255, 0);

        // Algoritmo de mapeo de filas del podio (Top 3) dentro del panel oscuro
        int yInicial = 180;
        for (int i = 0; i < 3; i++) {
            String textoPosicion = (i + 1) + ".   ";
            String textoNombre = "---";
            String textoPuntaje = "0 PTS";

            // Si el archivo tiene registros, los extraemos
            if (i < topTres.size()) {
                PuntajeJugador jugador = topTres.get(i);
                textoNombre = jugador.getNombre();
                textoPuntaje = jugador.getPuntaje() + " PTS";
            }

            // Etiqueta de posición y nombre (Alineada a la izquierda del bloque central)
            JLabel lblJugador = new JLabel(textoPosicion + textoNombre);
            lblJugador.setFont(fuenteTabla);

            // Asignación de colores según el podio
            if (i == 0) lblJugador.setForeground(colorNeonCian);      // 1er Lugar
            else if (i == 1) lblJugador.setForeground(colorNeonRosa);  // 2do Lugar
            else lblJugador.setForeground(Color.LIGHT_GRAY);          // 3er Lugar

            lblJugador.setBounds(400, yInicial, 300, 40);
            add(lblJugador);

            // Etiqueta de puntaje (Alineada a la derecha del bloque central)
            JLabel lblPts = new JLabel(textoPuntaje, SwingConstants.RIGHT);
            lblPts.setFont(fuenteTabla);
            lblPts.setForeground(lblJugador.getForeground());
            lblPts.setBounds(700, yInicial, 180, 40);
            add(lblPts);

            yInicial += 90; // Espacio vertical exacto entre filas
        }

        // Botón Regresar - Centrado matemáticamente en el eje X
        int xCentro = 540;
        int anchoBtn = 200;
        int altoBtn = 45;

        btnRegresar = new JButton("Regresar");
        btnRegresar.setFont(fuenteBotones);
        btnRegresar.setBounds(xCentro, 480, anchoBtn, altoBtn);
        add(btnRegresar);
    }

    /**
     * Sobreescritura del método del ciclo de renderizado del contenedor de Swing.
     * Ejecuta el pintado del mapa de bits de fondo.
     *  @param g Contexto gráfico bidimensional provisto por la JVM.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imgFondoRanking != null) {
            //Dibuja la imagen de fondo a pantalla completa (1280x720)
            g.drawImage(imgFondoRanking, 0, 0, getWidth(), getHeight(), this);

            // Define la capa oscura con opacidad (R, G, B, Alpha)
            g.setColor(new Color(15, 10, 25, 190));

            // Dibuja el contenedor del ranking en el centro de la pantalla
            g.fillRoundRect(290, 140, 700, 310, 25, 25);
        }
    }

    // Método de acceso (Getters)
    public JButton getBtnRegresar() {
        return btnRegresar;
    }
}