package com.racing.vistas;

import com.racing.eventos.InstruccionesControlador;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Clase Vista Instrucciones.
 * Construye la interfaz gráfica de usuario (UI) dedicada a la inducción lúdica del videojuego.
 * Organiza la información en un formato modular de tres columnas (Mecánicas, Controles y Power-Ups)
 * mediante el uso de JTextArea configurados de forma transparente y un renderizado por capas sobre el fondo.
 */
public class Instrucciones extends JPanel {

    // Componentes de control e interactividad de la interfaz
    private JButton btnRegresar;
    private JLabel lblInstrucciones;

    // Etiquetas de encabezados de sección
    private JLabel lblTituloMecanica;
    private JLabel lblTituloControles;
    private JLabel lblTituloPowerUps;

    // Áreas de texto multilínea especializadas para los bloques informativos
    private JTextArea txtMecanica;
    private JTextArea txtControles;
    private JTextArea txtPowerUps;

    // Imagen de fondo
    private Image imgFondoInstrucciones;

    /**
     * Constructor de la clase Instrucciones.
     * Define las dimensiones nativas, desactiva el gestor de diseño automático y enlaza su controlador.
     */
    public Instrucciones(){

        setPreferredSize(new Dimension(1280, 720));
        setLayout(null);

        // Inicializadores
        cargarFondo();
        inicializarComponentes();

        // Instancia el controlador y suscribe el botón de regreso
        InstruccionesControlador instruccionesControlador = new InstruccionesControlador(this);
        btnRegresar.addActionListener(instruccionesControlador);
    }

    /**
     * Carga el fondo de la pantalla desde el Classpath de recursos del proyecto.
     */
    private void cargarFondo(){

        try{
            URL urlFondoInstrucciones = getClass().getResource("/imagenes/fondo-instrucciones.jpg");

            if (urlFondoInstrucciones != null){
                imgFondoInstrucciones = new ImageIcon(urlFondoInstrucciones).getImage();
            }
        }catch (Exception e){
            System.err.println("No se pudo cargar el fondo: " + e.getMessage());
        }
    }

    /**
     * Construye, estiliza y posiciona de forma absoluta todos los componentes gráficos de la vista.
     * Distribuye la información en una matriz visual simétrica de tres bloques independientes.
     */
    private void inicializarComponentes() {

        // Estilo de fuentes y colores
        Font fuenteInstrucciones = new Font("Verdana", Font.BOLD, 60);
        Font fuenteSubtitulos = new Font("Segoe UI", Font.BOLD, 22);
        Font fuenteTextoBlanco = new Font("Arial", Font.PLAIN, 15);
        Font fuenteBotones = new Font("Segoe UI", Font.BOLD, 16);

        Color colorNeonCian = new Color(0, 255, 255);
        Color colorNeonRosa = new Color(255, 0, 128);

        // Nombre principal
        lblInstrucciones = new JLabel("INSTRUCCIONES", JLabel.CENTER);
        lblInstrucciones.setFont(fuenteInstrucciones);
        lblInstrucciones.setForeground(Color.WHITE);
        lblInstrucciones.setBounds(240, 40, 800, 60);
        add(lblInstrucciones);

        // Columna izquierda: mecanicas y reglas
        lblTituloMecanica = new JLabel("MECÁNICA & REGLAS");
        lblTituloMecanica.setFont(fuenteSubtitulos);
        lblTituloMecanica.setForeground(colorNeonCian);
        lblTituloMecanica.setBounds(60, 130, 360, 30);
        add(lblTituloMecanica);

        txtMecanica = new JTextArea();
        txtMecanica.setText(
                "• Sistema de Supervivencia:\nEsquiva obstáculos generados procedimentalmente.\n\n" +
                        "• Dificultad Escalable:\nLa velocidad base aumenta cada 500 puntos, reduciendo el tiempo de reacción.\n\n" +
                        "• Vidas:\nInicias con 3 vidas. Colisionar resta 1 vida.\n\n" +
                        "• Victoria:\nAl llegar a 0 vidas quedas fuera. El oponente sigue acumulando puntos en solitario hasta perder. Gana el de mayor puntaje."
        );
        configurarTextArea(txtMecanica, fuenteTextoBlanco, 60, 180, 360, 320);
        add(txtMecanica);

        // Columna centro: controles
        lblTituloControles = new JLabel("CONTROLES");
        lblTituloControles.setFont(fuenteSubtitulos);
        lblTituloControles.setForeground(colorNeonCian);
        lblTituloControles.setBounds(460, 130, 360, 30);
        add(lblTituloControles);

        txtControles = new JTextArea();
        txtControles.setText(
                " JUGADOR 1 (Auto Izquierda)\n" +
                        "  Mover Izquierda:  [ A ]\n" +
                        "  Mover Derecha:    [ D ]\n\n\n" +
                        " JUGADOR 2 (Auto Derecha)\n" +
                        "  Mover Izquierda:  [ ← ] Flecha Izq\n" +
                        "  Mover Derecha:    [ → ] Flecha Der"
        );
        configurarTextArea(txtControles, fuenteTextoBlanco, 460, 180, 360, 320);
        add(txtControles);

        // Columna derecha: Power-Ups
        lblTituloPowerUps = new JLabel("SISTEMA DE POWER-UPS");
        lblTituloPowerUps.setFont(fuenteSubtitulos);
        lblTituloPowerUps.setForeground(colorNeonRosa);
        lblTituloPowerUps.setBounds(860, 130, 360, 30);
        add(lblTituloPowerUps);

        txtPowerUps = new JTextArea();
        txtPowerUps.setText(
                "• Escudo de Energía:\nInvencibilidad total ante obstáculos durante 15 segundos.\n\n" +
                        "• Sobrecarga Cinética:\nAumenta la velocidad del oponente en 1.5x por 10 segundos.\n\n" +
                        "• Pulso Electromagnético (PEM):\nOscurece el área de visión del rival en un 75% por 10 segundos.\n\n" +
                        "• Reparación de Núcleo:\nRestaura 1 vida (Máximo 3)."
        );
        configurarTextArea(txtPowerUps, fuenteTextoBlanco, 860, 180, 360, 320);
        add(txtPowerUps);

        // Botones centrados en el eje x mediante el calculo: (1280 - 200) / 2
        int xCentro = 540;
        int anchoBtn = 200;
        int altoBtn = 45;

        // Botón iniciar
        btnRegresar = new JButton("Regresar");
        btnRegresar.setFont(fuenteBotones);
        btnRegresar.setBounds(xCentro, 480, anchoBtn, altoBtn);
        add(btnRegresar);
    }

    /**
     * Sobreescritura del método del ciclo de renderizado del contenedor de Swing.
     * Dibuja la imagen de fondo y aplica una máscara con opacidad regulada
     * para conformar el contenedor traslúcido donde reposan las instrucciones.
     * @param g Contexto gráfico bidimensional provisto por la JVM.
     */
    @Override
    protected  void paintComponent(Graphics g){
        super.paintComponent(g);

        if (imgFondoInstrucciones != null){
            // Dibuja el fondo
            g.drawImage(imgFondoInstrucciones, 0, 0, getWidth(), getHeight(), this);

            // Dibuja una capa oscura sobre el fondo para dar un contraste
            g.setColor(new Color(15, 10, 25, 190));

            // Dibuja un rectanculo en la zona en donde se encuentran las instrucciones
            g.fillRoundRect(40, 115, 1200, 420, 25, 25);
        }
    }

    // Métodos de acceso (Getters)
    public JButton getBtnRegresar() {
        return btnRegresar;
    }

    /**
     * Método Auxiliar de Configuración Estructural (Helper).
     * Abstrae y homogeneiza las propiedades físicas, de transparencia y de comportamiento
     * de los bloques JTextArea, mitigando la duplicación innecesaria de código.
     * @param txt    Referencia del componente JTextArea a formatear.
     * @param fuente Recurso tipográfico que se inyectará en el texto.
     * @param x      Coordenada física horizontal del componente.
     * @param y      Coordenada física vertical del componente.
     * @param ancho  Dimensión de anchura en píxeles.
     * @param alto   Dimensión de altura en píxeles.
     */
    private void configurarTextArea(JTextArea txt, Font fuente, int x, int y, int ancho, int alto){
        txt.setFont(fuente);
        txt.setForeground(Color.LIGHT_GRAY);
        txt.setBounds(x, y, ancho, alto);
        txt.setEditable(false);       // Bloquea que el usuario borre el texto
        txt.setOpaque(false);         // Lo hace transparente para ver el fondo
        txt.setLineWrap(true);        // Activa el salto de línea automático si la palabra es muy larga
        txt.setWrapStyleWord(true);   // Corta por palabras completas, no a mitad de una letra
        txt.setBackground(new Color(0,0,0,0)); // Refuerza la transparencia en algunos sistemas operativos
    }
}
