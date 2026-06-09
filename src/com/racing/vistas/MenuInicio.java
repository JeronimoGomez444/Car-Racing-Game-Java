package com.racing.vistas;

import com.racing.eventos.MenuInicioControlador;
import com.racing.logica.GestorAudio;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.net.URL;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

/**
 * Clase Vista MenuInicio.
 * Representa la interfaz gráfica principal (UI) y el punto de partida visual del videojuego.
 * Hereda de JPanel y se encarga de la disposición absoluta de los botones operacionales,
 * la visualización de los créditos académicos y la inicialización del hilo de audio ambiental
 * a través del subsistema centralizado de sonido.
 */
public class MenuInicio extends JPanel {

    // Componentes interactivos y etiquetas informativas (UI)
    private JButton btnIniciar;
    private JButton btnInstrucciones;
    private JButton btnRanking;
    private JButton btnSalir;
    private JLabel lblNombreJuego;
    private JLabel lblNombreIntegrantes;
    private JLabel lblNombreAsignatura;
    private JLabel lblLogoUAM;

    // Referencias de control de persistencia gráfica y multimedia
    private Image imgFondoMenu;
    private GestorAudio gestorAudio;
    private JFrame ventanaPrincipal;

    /**
     * Constructor de la clase MenuInicio.
     * Configura el entorno gráfico inicial y enlaza la vista con su respectivo controlador.
     * @param ventanaPrincipal Marco jerárquico superior (JFrame) de la aplicación.
     */
    public MenuInicio(JFrame ventanaPrincipal){

        this.ventanaPrincipal = ventanaPrincipal;

        setPreferredSize(new Dimension(1280, 720));
        setLayout(null);

        // Inicializadores
        cargarFondo();
        inicializarComponentes();

        //Se crea el controlador pasando el this (este mismo panel como argumento)
        MenuInicioControlador menuInicioControlador = new MenuInicioControlador(this, ventanaPrincipal);
        // Le asigna al controlador el botón salir
        btnSalir.addActionListener(menuInicioControlador);
        // Le asigna al controlador el botón instrucciones
        btnInstrucciones.addActionListener(menuInicioControlador);
        // Le asigna al controlador el botón iniciar
        btnIniciar.addActionListener(menuInicioControlador);
        // Le asigna al controlador el botón ranking
        btnRanking.addActionListener(menuInicioControlador);

        // Inicia la música
        this.gestorAudio = GestorAudio.getInstancia();
        gestorAudio.reproducirMusica("MenuInicio.wav");
    }

    /**
     * Recupera el mapa de bits del fondo del menú desde el Classpath de recursos del proyecto.
     */
    private void cargarFondo(){

        try{
            URL urlFondoMenuInicio = getClass().getResource("/imagenes/fondo-menu-inicio.jpg");

            if (urlFondoMenuInicio != null){
                imgFondoMenu = new ImageIcon(urlFondoMenuInicio).getImage();
            }
        }catch (Exception e){
            System.err.println("No se pudo cargar el fondo: " + e.getMessage());
        }
    }

    /**
     * Construye, estiliza y posiciona los componentes gráficos del menú.
     */
    private void inicializarComponentes(){

        // Estilo de fuentes
        Font fuenteNombreJuego = new Font("Verdana", Font.BOLD, 60);
        Font fuenteNombreIntegrantesYAsignatura = new Font("Arial", Font.PLAIN, 18);
        Font fuenteBotones = new Font("Segoe UI", Font.BOLD, 16);

        // Nombre del juego
        lblNombreJuego = new JLabel("CAR RACING GAME", JLabel.CENTER);
        lblNombreJuego.setFont(fuenteNombreJuego);
        lblNombreJuego.setForeground(Color.WHITE);
        lblNombreJuego.setBounds(240, 60, 800, 80);
        add(lblNombreJuego);

        // Botones centrados en el eje x mediante el calculo: (1280 - 200) / 2
        int xCentro = 540;
        int anchoBtn = 200;
        int altoBtn = 45;

        // Botón iniciar
        btnIniciar = new JButton("Iniciar juego");
        btnIniciar.setFont(fuenteBotones);
        btnIniciar.setBounds(xCentro, 280, anchoBtn, altoBtn);
        add(btnIniciar);

        // Botón instrucciones
        btnInstrucciones = new JButton("Instrucciones");
        btnInstrucciones.setFont(fuenteBotones);
        btnInstrucciones.setBounds(xCentro, 350, anchoBtn, altoBtn);
        add(btnInstrucciones);

        btnRanking = new JButton("Ranking");
        btnRanking.setFont(fuenteBotones);
        btnRanking.setBounds(xCentro, 420, anchoBtn, altoBtn);
        add(btnRanking);

        // Botón salir
        btnSalir = new JButton("Salir");
        btnSalir.setFont(fuenteBotones);
        btnSalir.setBounds(xCentro, 490, anchoBtn, altoBtn);
        add(btnSalir);

        // Nombre integrantes
        lblNombreIntegrantes = new JLabel("Elaborado Por: Daniel Lemus & Jeronimo Gomez");
        lblNombreIntegrantes.setFont(fuenteNombreIntegrantesYAsignatura);
        lblNombreIntegrantes.setForeground(Color.LIGHT_GRAY);
        lblNombreIntegrantes.setBounds(40, 610, 450, 30);
        add(lblNombreIntegrantes);

        // Nombre de la asignatura
        lblNombreAsignatura = new JLabel("Técnicas de Programación");
        lblNombreAsignatura.setFont(fuenteNombreIntegrantesYAsignatura);
        lblNombreAsignatura.setForeground(Color.LIGHT_GRAY);
        lblNombreAsignatura.setBounds(40, 640, 400, 30);
        add(lblNombreAsignatura);

        // Logo de la UAM
        try{
            URL urlLogo = getClass().getResource("/imagenes/logo_uam.png");

            if(urlLogo != null){
                ImageIcon imgUAM = new ImageIcon(urlLogo);
                Image logoOriginalUAM = imgUAM.getImage();

                Image logoUAMEscalado = logoOriginalUAM.getScaledInstance(220, 180, Image.SCALE_SMOOTH);
                ImageIcon imgUAMEscalado = new ImageIcon(logoUAMEscalado);

                lblLogoUAM = new JLabel(imgUAMEscalado);
                lblLogoUAM.setBounds(1020, 550, 220, 180);
                add(lblLogoUAM);

            }else{
                System.out.println("No se encontró el logo en el resource/imagenes");
            }
        }catch (Exception e){
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }
    }

    /**
     * Sobreescritura del método del ciclo de renderizado del contenedor de Swing.
     * Ejecuta el pintado del mapa de bits de fondo.
     *  @param g Contexto gráfico bidimensional provisto por la JVM.
     */
    @Override
    protected  void paintComponent(Graphics g){
        super.paintComponent(g);

        if (imgFondoMenu != null){
            g.drawImage(imgFondoMenu, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Métodos de acceso (Getters)
    public GestorAudio getGestorAudio(){
        return  gestorAudio;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }

    public JButton getBtnInstrucciones() {
        return btnInstrucciones;
    }

    public JButton getBtnRanking() {
        return btnRanking;
    }

    public JButton getBtnIniciar(){ return  btnIniciar; }
}
