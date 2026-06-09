package com.racing.vistas;

import com.racing.logica.Auto;
import com.racing.logica.GestorAudio;
import com.racing.logica.Obstaculo;
import com.racing.logica.Poder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase Central Juego (Parte 1: Infraestructura y Atributos de Estado).
 * Actúa como el Motor Gráfico y Físico (Game Loop) del videojuego.
 * Administra el ciclo de vida concurrente mediante hilos (Threads), la captura sincrónica
 * de periféricos de entrada (KeyListener), el control de variables de estado de los pilotos,
 * y la inicialización geométrica de los carriles sobre el lienzo.
 */
public class Juego extends JPanel implements Runnable, KeyListener {

    // Assets Gráficos y Multimedia
    private Image imgFondoJuego;
    private GestorAudio gestorAudio;
    private Image imgVida;

    // Game Loop
    private Thread hiloJuego; // Subproceso dedicado a la ejecución del ciclo lógico/gráfico
    private boolean enEjecucion; // Flag de control para el ciclo de vida del hilo

    // Entidades modelo
    private Auto jugador1;
    private Auto jugador2;

    // Sistema de obstaculos
    private ArrayList<Obstaculo> obstaculosJ1;
    private ArrayList<Obstaculo> obstaculosJ2;
    private Timer temporizadorObstaculos;
    private Random aleatorio;

    // Posicionamientos
    private int[] carrilesJ1;
    private int[] carrilesJ2;

    // Poderes
    private ArrayList<Poder> poderesJ1;
    private ArrayList<Poder> poderesJ2;

    // Estado del Jugador 1
    private int vidasJ1 = 3;
    private int puntajeJ1 = 0;
    private long tiempoInicioJ1; // Para medir los segundos exactos
    private int tiempoTranscurridoJ1 = 0;

    // Estado del Jugador 2
    private int vidasJ2 = 3;
    private int puntajeJ2 = 0;
    private long tiempoInicioJ2;
    private int tiempoTranscurridoJ2 = 0;

    // Banderas de control para el sonido GameOver
    private boolean sonidoGameOverJ1Procesado = false;
    private boolean sonidoGameOverJ2Procesado = false;

    // Velocidad inicial escalable
    private int velocidadActual = 5;

    // Power UP: Escudo
    private boolean escudoActivoJ1;
    private Timer temporizadorEscudoJ1;
    private boolean escudoActivoJ2;
    private Timer temporizadorEscudoJ2;

    // Power UP: Sobrecarga
    private boolean sobrecargaActivaJ1 = false;
    private Timer temporizadorSobrecargaJ1;
    private boolean sobrecargaActivaJ2 = false;
    private Timer temporizadorSobrecargaJ2;

    // Power UP: PEM
    private boolean pemActivoJ1 = false;
    private Timer temporizadorPemJ1;
    private boolean pemActivoJ2 = false;
    private Timer temporizadorPemJ2;

    // Variables para el resultado al concluir la partida
    private boolean mostrarResultados = false;
    private String textoGanador = "";
    private int puntajeFinalGanador = 0;
    private java.awt.Rectangle areaBtnRanking;
    private java.awt.Rectangle areaBtnInicio;

    // Interfaz de enrutamiento
    private NavegacionEscenas navegador;

    private String nombreJ1;
    private String nombreJ2;

    // Sistema de Pausa
    private boolean juegoPausado = false;
    private JButton btnContinuar;

    /**
     * Constructor del Motor de Juego.
     * Inicializa las variables de entorno, configura los canales de hardware, inyecta las dependencias
     * y arranca de forma inmediata los hilos lógicos y musicales del juego.
     * @param navegador Interfaz de Callback para la navegación inversa de escenas.
     * @param nombreJ1  Alias de identidad registrado para el Piloto 1.
     * @param nombreJ2  Alias de identidad registrado para el Piloto 2.
     */
    public Juego(NavegacionEscenas navegador, String nombreJ1, String nombreJ2){

        this.navegador = navegador;
        this.nombreJ1 = nombreJ1.trim().isEmpty() ? "JUGADOR 1" : nombreJ1.toUpperCase();
        this.nombreJ2 = nombreJ2.trim().isEmpty() ? "JUGADOR 2" : nombreJ2.toUpperCase();

        setPreferredSize(new Dimension(1280, 720));
        setLayout(null);

        // Permite que el panel reciba eventos del teclado
        setFocusable(true);
        addKeyListener(this);

        // Inicializadores
        inicializarComponentes();
        inicializarAutos();

        // Botón de reanudar
        btnContinuar = new JButton("CONTINUAR");
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnContinuar.setBackground(Color.BLACK);
        btnContinuar.setForeground(new Color(0, 255, 255)); // Texto Cian Neón
        btnContinuar.setBounds(540, 380, 200, 45); // Centrado en la pantalla (1280x720)
        btnContinuar.setVisible(false); // Oculto al iniciar el juego
        btnContinuar.addActionListener(e -> reanudarJuego());
        add(btnContinuar);

        // Instanciación de colecciones dinámicas de hilos de objetos en pista
        obstaculosJ1 = new ArrayList<>();
        obstaculosJ2 = new ArrayList<>();
        aleatorio = new Random();

        poderesJ1 = new ArrayList<>();
        poderesJ2 = new ArrayList<>();

        // Carga el sprite de la vida
        try {
            URL urlVida = getClass().getResource("/imagenes/vida.png");
            if (urlVida != null) {
                imgVida = new ImageIcon(urlVida).getImage();
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el sprite de vida: " + e.getMessage());
        }

        // Guarda el tiempo en milisegundos en el que arranca la partida
        long tiempoActual = System.currentTimeMillis();
        this.tiempoInicioJ1 = tiempoActual;
        this.tiempoInicioJ2 = tiempoActual;

        // Definición de las hitboxes virtuales de interacción para el menú final de juego
        areaBtnRanking = new java.awt.Rectangle(440, 480, 180, 50);
        areaBtnInicio = new java.awt.Rectangle(660, 480, 180, 50);

        // Configura el click del mouse
        configurarEventosMouse();

        // Inicia la música
        this.gestorAudio = GestorAudio.getInstancia();
        gestorAudio.reproducirMusica("Juego.wav");

        // Activa el temporizador
        configurarTemporizador();

        // Inica el hilo del juego
        iniciarHiloJuego();
    }

    /**
     * Recupera el recurso de textura del mapa de fondo del juego desde el Classpath.
     */
    private void inicializarComponentes(){

        try{
            URL urlFondoJuego = getClass().getResource("/imagenes/fondo-juego.png");

            if (urlFondoJuego != null){
                imgFondoJuego = new ImageIcon(urlFondoJuego).getImage();
            }
        }catch (Exception e){
            System.err.println("No se pudo cargar el fondo: " + e.getMessage());
        }
    }

    /**
     * Inicializa los vectores de coordenadas físicas de la pista e instancia las entidades de los autos.
     * Segmenta el lienzo de 1280 píxeles a la mitad exacta para aislar los entornos de carrera de ambos jugadores.
     */
    private void inicializarAutos(){
        // Coordenadas X para los 3 carriles del Jugador 1 (Mitad izquierda: 0 a 640)
        carrilesJ1 = new int[]{260, 302, 345};
        // Coordenadas X para los 3 carriles del Jugador 2 (Mitad derecha: 640 a 1280)
        carrilesJ2 = new int[]{900, 942, 985};

        // Posición Y fija en la parte inferior de la pantalla (resolución 720)
        int posicionY = 560;

        // Instancia el objeto lógico del vehículo pasando su matriz de carriles y su sprite asignado
        jugador1 = new Auto(carrilesJ1, posicionY, "/imagenes/jugador1.png");
        jugador2 = new Auto(carrilesJ2, posicionY, "/imagenes/jugador2.png");
    }

    /**
     * Inicializa y arranca el subproceso (Thread) asíncrono secundario del juego.
     * Activa el bucle interno desviando el flujo principal del hilo EDT de Swing.
     */
    private void iniciarHiloJuego(){
        enEjecucion = true;
        hiloJuego = new Thread(this); // Vincula este objeto Runnable al contexto del subproceso
        hiloJuego.start(); // Desvía la ejecución invocando internamente al método run()
    }

    /**
     * Configura e inicia el subsistema de generación asíncrona de obstáculos y modificadores.
     * Utiliza un temporizador Swing recurrente configurado a una tasa de refresco de 1.5 segundos.
     */
    private void configurarTemporizador(){
        // Cada 1.5 segundos el timer llamar este bloque de código
        temporizadorObstaculos = new Timer(1500, e ->{
            // Generación para el J1
            // Hay 3 carriles (0, 1, 2). Elegimos un carril obligatorio para dejar libre
            int carrilLibreJ1 = aleatorio.nextInt(3);
            boolean seCreoObstaculoJ1 = false;
            for (int i = 0; i < 3; i++){
                if (i != carrilLibreJ1){
                    // Decisión aleatoria (50% de probabilidad) de poner obstáculo en los carriles permitidos
                    // Pasamos el índice del carril (i) y el arreglo de carriles de destino
                    if (aleatorio.nextBoolean()){
                        obstaculosJ1.add(new Obstaculo(i, carrilesJ1, velocidadActual));
                        seCreoObstaculoJ1 = true;
                    }
                }
            }

            // Caso de control: Si el azar decidió no poner nada, forzamos al menos uno para mantener el reto
            if (!seCreoObstaculoJ1) {
                int carrilObstaculoForzado1 = (carrilLibreJ1 + 1) % 3;
                obstaculosJ1.add(new Obstaculo(carrilObstaculoForzado1, carrilesJ1, velocidadActual));
            }

            if (aleatorio.nextInt(100) < 15){
                int tipoPoderAleatorio = aleatorio.nextInt(4);
                // Agregamos el poder en la coordenada X del carril que quedó vacío
                poderesJ1.add(new Poder(carrilesJ1[carrilLibreJ1], tipoPoderAleatorio, velocidadActual));
            }

            // Generación para el J1
            int carrilLibreJ2 = aleatorio.nextInt(3);
            boolean seCreoObstaculoJ2 = false;
            for (int i = 0; i < 3; i++) {
                if (i != carrilLibreJ2) {
                    if (aleatorio.nextBoolean()) {
                        obstaculosJ2.add(new Obstaculo(i, carrilesJ2, velocidadActual));
                        seCreoObstaculoJ2 = true;
                    }
                }
            }

            // Caso de control: Si el azar decidió no poner nada, forzamos al menos uno para mantener el reto
            if (!seCreoObstaculoJ2) {
                int carrilObstaculoForzado2 = (carrilLibreJ2 + 1) % 3;
                obstaculosJ2.add(new Obstaculo(carrilObstaculoForzado2, carrilesJ2, velocidadActual));
            }

            if (aleatorio.nextInt(100) < 15) {
                int tipoPoderAleatorio = aleatorio.nextInt(4);
                // Agregamos el poder en la coordenada X del carril que quedó vacío
                poderesJ2.add(new Poder(carrilesJ2[carrilLibreJ2], tipoPoderAleatorio, velocidadActual));
            }
        });

        // Inicia el temporizador
        temporizadorObstaculos.start();
    }

    /**
     * Evalúa y actualiza la física, estados de salud, temporizadores de ventajas,
     * colisiones por hitboxes y condiciones de fin de partida de todo el entorno lúdico.
     */
    private void actualizarLogica(){

        long tiempoActual = System.currentTimeMillis();

        // detiene el procesamiento si el switch de pausa está activo
        if (juegoPausado) {
            return;
        }

        // Gestión de tiempo del J1
        if (vidasJ1 > 0){
            int nuevoTiempoJ1 = (int) ((tiempoActual - tiempoInicioJ1) / 1000);
            if (nuevoTiempoJ1 > tiempoTranscurridoJ1) {
                tiempoTranscurridoJ1 = nuevoTiempoJ1;
                puntajeJ1 += 10; // Sumamos 10 puntos por segundo sobrevivido
            }
        }else{
            // Si ya no tiene vidas y no hemos reproducido el sonido de fin
            if (!sonidoGameOverJ1Procesado) {
                gestorAudio.reproducirEfecto("GameOver.wav");
                sonidoGameOverJ1Procesado = true;
            }
        }

        // Gestión de tiempo del J1
        if (vidasJ2 > 0) {
            int nuevoTiempoJ2 = (int) ((tiempoActual - tiempoInicioJ2) / 1000);
            if (nuevoTiempoJ2 > tiempoTranscurridoJ2) {
                tiempoTranscurridoJ2 = nuevoTiempoJ2;
                puntajeJ2 += 10; // Sumamos 10 puntos por segundo sobrevivido
            }
        }else{
            // Si ya no tiene vidas y no hemos reproducido el sonido de fin
            if (!sonidoGameOverJ2Procesado) {
                gestorAudio.reproducirEfecto("GameOver.wav");
                sonidoGameOverJ2Procesado = true;
            }
        }

        // Dificultad progresiva, se toma el tiempo del jugador que vaya más lejos
        int tiempoMaximo = Math.max(tiempoTranscurridoJ1, tiempoTranscurridoJ2);

        // Fórmula: velocidad base (5) + 1 píxel por cada 15 segundos transcurridos (Aceleración)
        int nuevaVelocidad = 5 + (tiempoMaximo / 15);

        // Velocidad máxima en 15 para que el juego no se vuelva tan imposible
        if (nuevaVelocidad > 15){
            nuevaVelocidad = 15;
        }

        this.velocidadActual = nuevaVelocidad;

        // Avanza los obstáculos del J1 y remueve los que salen de la pantalla
        for (int i = 0; i < obstaculosJ1.size(); i++) {
            Obstaculo o = obstaculosJ1.get(i);

            // Si J1 está sobrecargado, aumentamos temporalmente la velocidad de sus objetos
            if (sobrecargaActivaJ1) {
                o.setVelocidad((int)(velocidadActual * 1.5)); // Forzamos un incremento del 50% sobre la velocidad base actual del juego
            } else {
                o.setVelocidad(velocidadActual);
            }

            o.actualizar(); // Calcula la nueva coordenada Y de la entidad

            // Detecta el choque
            if (jugador1 != null && jugador1.getBounds().intersects(o.getBounds())){
                if (vidasJ1 > 0 && !escudoActivoJ1){
                    vidasJ1 --;
                }

                // Eliminamos el obstaculo con el cual choco
                obstaculosJ1.remove(i);
                i--; // Ajustamos el índice
                continue; // Saltamos al siguiente ciclo
            }

            if (o.getY() > 720) {
                obstaculosJ1.remove(i);
                i--; // Ajustamos el índice al remover
            }
        }

        // Mover y limpiar poderes del Jugador 1
        for (int i = 0; i < poderesJ1.size(); i++) {
            Poder p = poderesJ1.get(i);

            // Si J1 está sobrecargado, aumentamos temporalmente la velocidad de sus objetos
            if (sobrecargaActivaJ1) {
                p.setVelocidad((int)(velocidadActual * 1.5)); // Forzamos un incremento del 50% sobre la velocidad base actual del juego
            } else {
                p.setVelocidad(velocidadActual);
            }

            p.actualizar(); // Calcula la nueva coordenada Y de la entidad

            // Detecta las colisiones del J1
            if (jugador1 != null && jugador1.getBounds().intersects(p.getBounds())) {
                // Solo interactúa si el jugador sigue vivo
                if (vidasJ1 > 0) {
                    // Evaluamos qué tipo de poder es
                    int tipo = p.getTipoPoder();

                    if (tipo == 0) {
                        // Recupera vida solo si tiene menos de 3
                        if (vidasJ1 < 3) {
                            vidasJ1++;
                        }
                        gestorAudio.reproducirEfecto("Vida.wav");
                    } else if (tipo == 1){
                        escudoActivoJ1 = true;
                        gestorAudio.reproducirEfecto("Poder1.wav");

                        // Si ya había un escudo corriendo, lo detenemos para reiniciar los 10 segundos
                        if (temporizadorEscudoJ1 != null && temporizadorEscudoJ1.isRunning()){
                            temporizadorEscudoJ1.stop();
                        }

                        // Crea un Timer de un solo disparo que se ejecuta a los 15,000 ms (15 segundos)
                        temporizadorEscudoJ1 = new Timer(15000, e -> {
                            escudoActivoJ1 = false;
                            ((Timer)e.getSource()).stop(); // Detiene el timer para que no se repita
                        });
                        temporizadorEscudoJ1.start();
                    } else if (tipo == 2){
                        sobrecargaActivaJ2 = true;
                        gestorAudio.reproducirEfecto("Poder2.wav"); // Sonido de activación

                        if (temporizadorSobrecargaJ2 != null && temporizadorSobrecargaJ2.isRunning()) {
                            temporizadorSobrecargaJ2.stop();
                        }

                        // Castigo durante 10 segundos al J2
                        temporizadorSobrecargaJ2 = new Timer(10000, e -> {
                            sobrecargaActivaJ2 = false;
                            ((Timer)e.getSource()).stop();
                        });
                        temporizadorSobrecargaJ2.start();
                    } else if (tipo == 3){
                        pemActivoJ2 = true;
                        gestorAudio.reproducirEfecto("Poder3.wav");

                        if (temporizadorPemJ2 != null && temporizadorPemJ2.isRunning()) {
                            temporizadorPemJ2.stop();
                        }

                        // Deja "ciego" al J2 por 10 segundos
                        temporizadorPemJ2 = new Timer(10000, e -> {
                            pemActivoJ2 = false;
                            ((Timer)e.getSource()).stop();
                        });
                        temporizadorPemJ2.start();
                    }
                }

                // El poder se remueve de la pantalla al ser tocado
                poderesJ1.remove(i);
                i--;
                continue;
            }

            if (p.getY() > 720) {
                poderesJ1.remove(i);
                i--;
            }
        }

        // Avanza los obstáculos del J2 y remueve los que salen de la pantalla
        for (int i = 0; i < obstaculosJ2.size(); i++) {
            Obstaculo o = obstaculosJ2.get(i);

            // Si J2 está sobrecargado, aumentamos temporalmente la velocidad de sus objetos
            if (sobrecargaActivaJ2) {
                o.setVelocidad((int)(velocidadActual * 1.5)); // Forzamos un incremento del 50% sobre la velocidad base actual del juego
            } else {
                o.setVelocidad(velocidadActual);
            }

            o.actualizar(); // Calcula la nueva coordenada Y de la entidad

            // Detecta el choque
            if (jugador2 != null && jugador2.getBounds().intersects(o.getBounds())) {
                if (vidasJ2 > 0 && !escudoActivoJ2) {
                    vidasJ2--;
                }

                obstaculosJ2.remove(i);
                i--;
                continue;
            }

            if (o.getY() > 720) {
                obstaculosJ2.remove(i);
                i--;
            }
        }

        // Mueve y limpia los poderes del Jugador 2
        for (int i = 0; i < poderesJ2.size(); i++) {
            Poder p = poderesJ2.get(i);

            // Si J2 está sobrecargado, aumentamos temporalmente la velocidad de sus objetos
            if (sobrecargaActivaJ2) {
                p.setVelocidad((int)(velocidadActual * 1.5)); // Forzamos un incremento del 50% sobre la velocidad base actual del juego
            } else {
                p.setVelocidad(velocidadActual);
            }

            p.actualizar();

            // Detecta las colisiones del J1
            if (jugador2 != null && jugador2.getBounds().intersects(p.getBounds())) {
                if (vidasJ2 > 0) {
                    int tipo = p.getTipoPoder();

                    if (tipo == 0) {
                        if (vidasJ2 < 3) {
                            vidasJ2++;
                        }
                        gestorAudio.reproducirEfecto("Vida.wav");
                    } else if (tipo == 1){
                        escudoActivoJ2 = true;
                        gestorAudio.reproducirEfecto("Poder1.wav");

                        // Si ya había un escudo corriendo, lo detenemos para reiniciar los 10 segundos
                        if (temporizadorEscudoJ2 != null && temporizadorEscudoJ2.isRunning()){
                            temporizadorEscudoJ2.stop();
                        }

                        // Crea un Timer de un solo disparo que se ejecuta a los 15,000 ms (15 segundos)
                        temporizadorEscudoJ2 = new Timer(15000, e -> {
                            escudoActivoJ2 = false;
                            ((Timer)e.getSource()).stop(); // Detiene el timer para que no se repita
                        });
                        temporizadorEscudoJ2.start();
                    }else if (tipo == 2){
                        sobrecargaActivaJ1 = true;
                        gestorAudio.reproducirEfecto("Poder2.wav");

                        if (temporizadorSobrecargaJ1 != null && temporizadorSobrecargaJ1.isRunning()) {
                            temporizadorSobrecargaJ1.stop();
                        }

                        temporizadorSobrecargaJ1 = new Timer(10000, e -> {
                            sobrecargaActivaJ1 = false;
                            ((Timer)e.getSource()).stop();
                        });
                        temporizadorSobrecargaJ1.start();
                    }else if (tipo ==3){
                        pemActivoJ1 = true;
                        gestorAudio.reproducirEfecto("Poder3.wav");

                        if (temporizadorPemJ1 != null && temporizadorPemJ1.isRunning()) {
                            temporizadorPemJ1.stop();
                        }

                        temporizadorPemJ1 = new Timer(10000, e -> {
                            pemActivoJ1 = false;
                            ((Timer)e.getSource()).stop();
                        });
                        temporizadorPemJ1.start();
                    }
                }

                poderesJ2.remove(i);
                i--;
                continue;
            }

            if (p.getY() > 720) {
                poderesJ2.remove(i);
                i--;
            }
        }

        if (vidasJ1 <= 0 && vidasJ2 <= 0 && !mostrarResultados) {
            mostrarResultados = true;
            gestorAudio.detenerMusica();

            // Guarda los dos registros en el archivo .txt de forma persistente
            com.racing.logica.GestorRanking.guardarPuntaje(nombreJ1, puntajeJ1);
            com.racing.logica.GestorRanking.guardarPuntaje(nombreJ2, puntajeJ2);

            if (puntajeJ1 > puntajeJ2) {
                textoGanador = nombreJ1;
                puntajeFinalGanador = puntajeJ1;
            } else if (puntajeJ2 > puntajeJ1) {
                textoGanador = nombreJ2;
                puntajeFinalGanador = puntajeJ2;
            } else {
                textoGanador = "EMPATE";
                puntajeFinalGanador = puntajeJ1;
            }
        }
    }

    /**
     * Acopla un Escuchador de Mouse asíncrono sobre el lienzo para interceptar las pulsaciones del usuario.
     * Gestiona las colisiones de coordenadas del puntero sobre las hitboxes virtuales de los botones finales.
     */
    private void configurarEventosMouse(){
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (mostrarResultados) {
                    if (areaBtnRanking.contains(e.getPoint())) {
                        System.out.println("Cargando Ranking...");

                        SwingUtilities.invokeLater(() -> {
                            // Obtenemos la ventana principal (JFrame) a partir del panel actual
                            JFrame ventanaPrincipal = (JFrame) SwingUtilities.getWindowAncestor(Juego.this);

                            if (ventanaPrincipal != null) {
                                // Detenemos cualquier hilo o temporizador que quede activo (por seguridad)
                                enEjecucion = false;

                                // Extrae el Top 3 ordenado recursivamente desde el archivo .txt
                                java.util.ArrayList<com.racing.logica.GestorRanking.PuntajeJugador> topTres =
                                        com.racing.logica.GestorRanking.obtenerTopTres();

                                // Instancia la vista física del Ranking pasándole los datos
                                com.racing.vistas.Ranking pantallaRanking = new com.racing.vistas.Ranking(topTres);

                                // Limpia la ventana por completo y acopla la nueva pantalla
                                ventanaPrincipal.getContentPane().removeAll();
                                ventanaPrincipal.getContentPane().add(pantallaRanking);

                                // Fuerza el re-cálculo geométrico y repintado absoluto
                                ventanaPrincipal.revalidate();
                                ventanaPrincipal.repaint();

                                System.out.println("¡Pantalla de Ranking renderizada con éxito!");
                            } else {
                                System.err.println("Error: No se pudo encontrar la ventana principal.");
                            }
                        });
                    }

                    // Boton inicio
                    if (areaBtnInicio.contains(e.getPoint())) {
                        // Apaga la bandera para romper el ciclo while
                        enEjecucion = false;

                        // Detiene todos los temporizadores
                        if (temporizadorObstaculos != null) temporizadorObstaculos.stop();
                        if (temporizadorEscudoJ1 != null) temporizadorEscudoJ1.stop();
                        if (temporizadorEscudoJ2 != null) temporizadorEscudoJ2.stop();
                        if (temporizadorSobrecargaJ1 != null) temporizadorSobrecargaJ1.stop();
                        if (temporizadorSobrecargaJ2 != null) temporizadorSobrecargaJ2.stop();
                        if (temporizadorPemJ1 != null) temporizadorPemJ1.stop();
                        if (temporizadorPemJ2 != null) temporizadorPemJ2.stop();

                        // Detiene el audio de inmediato
                        if (gestorAudio != null) {
                            gestorAudio.detenerMusica();
                        }

                        // Espera a que el hilo de juego muera de verdad para evitar colisiones en memoria
                        if (hiloJuego != null) {
                            try {
                                hiloJuego.join(500); // Espera máximo medio segundo a que el ciclo run() termine
                            } catch (InterruptedException ex) {
                                System.err.println("Error esperando al hilo: " + ex.getMessage());
                            }
                        }

                        // Una vez el hilo está 100% muerto, notificamos de forma segura al controlador
                        if (navegador != null) {
                            navegador.irAlMenuInicio();
                        }
                    }
                }
            }
        });
    }

    /**
     * Interrumpe temporalmente las rutinas lógicas de actualización y expone el componente JComponent de pausa.
     */
    private void pausarJuego() {
        juegoPausado = true;
        btnContinuar.setVisible(true);

        repaint();
    }

    /**
     * Reactiva los flujos del juego y restablece de forma imperativa el foco del hardware del teclado sobre el panel.
     */
    private void reanudarJuego() {
        juegoPausado = false;
        btnContinuar.setVisible(false);

        this.requestFocusInWindow();
    }

    /**
     * Punto de entrada ejecutable del subproceso secundario (Hilo de Juego).
     * Implementa un Game Loop síncrono a una tasa fija de 60 Hercios mediante la medición
     * del tiempo de hardware en nanosegundos (Delta Time).
     */
    @Override
    public void run(){
        // Bucle del juego a 60 FPS
        int FPS = 60;
        double tiempoPorFrame = 1000000000.0 / FPS;
        long ultimoTiempo = System.nanoTime();
        long tiempoActual;
        double delta = 0;

        // Ciclo operativo infinito controlado por una bandera
        while (enEjecucion){
            tiempoActual = System.nanoTime();

            // Calcula la fracción de frame consumida y la acopla al Delta acumulado
            delta += (tiempoActual - ultimoTiempo) / tiempoPorFrame;
            ultimoTiempo = tiempoActual;

            if (delta >= 1){
                actualizarLogica(); // Ejecuta físicas, colisiones y estados del modelo de datos
                repaint(); // Invoca indirectamente a paintComponent() para refrescar la gráfica
                delta--; // Sustrae el cuadro procesado manteniendo el residuo del desfase
            }
        }
    }

    /**
     * Sobreescritura del método del ciclo de renderizado del contenedor de Swing.
     * Construye cuadro por cuadro toda la visual del juego, organizando los componentes
     * en un esquema estricto de profundidad (Z-Index) para evitar solapamientos.
     * @param g Contexto gráfico bidimensional provisto por la JVM.
     */
    @Override
    protected  void paintComponent(Graphics g){

        // Limpieza mandatoria del buffer anterior para prevenir el efecto "Ghosting"
        super.paintComponent(g);

        if (imgFondoJuego != null){

            // Se calcula la mitad de la pantalla
            int anchoMitad = getWidth() / 2;
            int altoTotal = getHeight();

            // Dibuja el lado izquierdo
            g.drawImage(imgFondoJuego, 0, 0, anchoMitad, altoTotal, this);

            // Dibuja el lado derecho
            g.drawImage(imgFondoJuego, anchoMitad, 0, anchoMitad, altoTotal, this);

            // Dibuja una línea divisora central
            g.setColor(Color.BLACK);
            g.fillRect(anchoMitad - 2, 0, 4, altoTotal);
        }

        // Dibuja los poderes en pantalla
        for (Poder p : poderesJ1) {
            p.dibujar(g);
        }
        for (Poder p : poderesJ2) {
            p.dibujar(g);
        }

        // Dibuja los obstaculos
        for (Obstaculo o : obstaculosJ1) {
            o.dibujar(g);
        }
        for (Obstaculo o : obstaculosJ2) {
            o.dibujar(g);
        }

         // Dibuja los autos por encima del fondo
         if (jugador1 != null && jugador2 != null) {
             jugador1.dibujar(g);
             jugador2.dibujar(g);
         }

         // HUD
         g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));

         // HUD J1
        g.setColor(Color.CYAN); //
        g.drawString(nombreJ1 + ": " + puntajeJ1, 50, 40);
        g.drawString("TIME: " + tiempoTranscurridoJ1 + "s", 50, 70);

        // Dibuja las vidas del J1 en fila (separadas 35 píxeles entre sí)
        for (int i = 0; i < vidasJ1; i++) {
            if (imgVida != null) {
                g.drawImage(imgVida, 50 + (i * 35), 90, 30, 30, this);
            } else {
                // Respaldo por si no encuentra el sprite (un óvalo rojo)
                g.setColor(Color.RED);
                g.fillOval(50 + (i * 35), 90, 20, 20);
            }
        }

        // HUD J2
        g.setColor(Color.MAGENTA);
        g.drawString(nombreJ2 + ": " + puntajeJ2, 690, 40);
        g.drawString("TIME: " + tiempoTranscurridoJ2 + "s", 690, 70);

        // Dibuja las vidas del J2 en fila (separadas 35 píxeles entre sí)
        for (int i = 0; i < vidasJ2; i++) {
            if (imgVida != null) {
                g.drawImage(imgVida, 690 + (i * 35), 90, 30, 30, this);
            } else {
                // Respaldo por si no encuentra el sprite (un óvalo rojo)
                g.setColor(Color.RED);
                g.fillOval(690 + (i * 35), 90, 20, 20);
            }
        }

        // GAMEOVER
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
        g.setColor(Color.RED);

        // Anuncio para el Jugador 1 (Mitad izquierda de 0 a 640)
        if (vidasJ1 <= 0) {
            // 320 es el centro de la sub-pantalla del J1
            g.drawString("GAME OVER", 320 - 150, 360);
        }

        // Anuncio para el Jugador 2 (Mitad derecha de 640 a 1280)
        if (vidasJ2 <= 0) {
            // 960 es el centro de la sub-pantalla del J2 (640 + 320)
            g.drawString("GAME OVER", 960 - 150, 360);
        }

        // Escudo Jugador 1
        if (escudoActivoJ1 && jugador1 != null) {
            g.setColor(new Color(0, 255, 255, 130));
            // Dibuja un óvalo protector rodeando el ancho y alto del auto
            g.fillOval(jugador1.getX() - 10, jugador1.getY() - 5, jugador1.getAncho() + 20, jugador1.getAlto() + 10);
        }

        // Escudo Jugador 2
        if (escudoActivoJ2 && jugador2 != null) {
            g.setColor(new Color(255, 0, 255, 130));
            // Dibuja un óvalo protector rodeando el ancho y alto del auto
            g.fillOval(jugador2.getX() - 10, jugador2.getY() - 5, jugador2.getAncho() + 20, jugador2.getAlto() + 10);
        }

        // Efecto visual sobrecarga cinetica
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 25));

        if (sobrecargaActivaJ1) {
            g.setColor(new Color(255, 0, 0, 50));
            g.fillRect(0, 0, 640, 720);
            g.setColor(Color.RED);
            g.drawString("OVERLOAD 1.5x!", 220, 150);
        }

        if (sobrecargaActivaJ2) {
            g.setColor(new Color(255, 0, 0, 50));
            g.fillRect(640, 0, 640, 720);
            g.setColor(Color.RED);
            g.drawString("OVERLOAD 1.5x!", 860, 150);
        }

        // Efecto visual del PEM
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();

        if (pemActivoJ1 && jugador1 != null) {
            float radioLuz = 160f; // Tamaño del círculo de visibilidad

            // El centro del foco de luz será el centro del auto del J1
            float centroX = jugador1.getX() + (jugador1.getAncho() / 2f);
            float centroY = jugador1.getY() + (jugador1.getAlto() / 2f);

            java.awt.geom.Point2D centro = new java.awt.geom.Point2D.Float(centroX, centroY);
            float[] distribucion = {0.0f, 1.0f}; // 0% centro (luz), 100% extremos (oscuridad)

            Color[] colores = {
                    new Color(0, 0, 0, 0),       // Totalmente transparente en el auto
                    new Color(10, 15, 30, 220)   // Azul oscuro casi opaco en los bordes (75%-85% oscuridad)
            };

            java.awt.RadialGradientPaint degradadoRadial = new java.awt.RadialGradientPaint(centro, radioLuz, distribucion, colores);
            g2d.setPaint(degradadoRadial);

            // Recorta el dibujo para que solo afecte la sub-pantalla del Jugador 1 (0 a 640)
            g2d.setClip(0, 0, 640, 720);
            g2d.fillRect(0, 0, 640, 720);
        }

        if (pemActivoJ2 && jugador2 != null) {
            float radioLuz = 160f; // Tamaño del círculo de visibilidad

            // El centro del foco de luz será el centro del auto del J1
            float centroX = jugador2.getX() + (jugador2.getAncho() / 2f);
            float centroY = jugador2.getY() + (jugador2.getAlto() / 2f);

            java.awt.geom.Point2D centro = new java.awt.geom.Point2D.Float(centroX, centroY);
            float[] distribucion = {0.0f, 1.0f}; // 0% centro (luz), 100% extremos (oscuridad)

            Color[] colores = {
                    new Color(0, 0, 0, 0), // Totalmente transparente en el auto
                    new Color(10, 15, 30, 220) // Azul oscuro casi opaco en los bordes (75%-85% oscuridad)
            };

            java.awt.RadialGradientPaint degradadoRadial = new java.awt.RadialGradientPaint(centro, radioLuz, distribucion, colores);
            g2d.setPaint(degradadoRadial);

            // Recorta el dibujo para que solo afecte la sub-pantalla del Jugador 2 (640 a 1280)
            g2d.setClip(640, 0, 640, 720);
            g2d.fillRect(640, 0, 1280, 720);
        }

        g2d.dispose(); // Libera los recursos del Graphics2D clonado

        // Muestra los resultados
        if (mostrarResultados) {
            // Fondo oscuro semi-transparente que cubre todo el juego
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, 1280, 720);

            // Recuadro central (Modal)
            int modalAncho = 500;
            int modalAlto = 400;
            int modalX = (1280 - modalAncho) / 2;
            int modalY = (720 - modalAlto) / 2;

            g.setColor(new Color(30, 30, 30)); // Fondo del modal
            g.fillRoundRect(modalX, modalY, modalAncho, modalAlto, 30, 30);
            g.setColor(Color.CYAN); // Borde brillante
            g.drawRoundRect(modalX, modalY, modalAncho, modalAlto, 30, 30);

            // Textos de Victoria
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
            g.setColor(Color.WHITE);
            g.drawString("¡FIN DE LA CARRERA!", modalX + 45, modalY + 80);

            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 25));
            g.setColor(Color.YELLOW);
            g.drawString("GANADOR:", modalX + 180, modalY + 150);

            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
            g.setColor(Color.WHITE);
            g.drawString(textoGanador, modalX + 120, modalY + 220);

            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g.drawString("PUNTAJE TOTAL: " + puntajeFinalGanador, modalX + 150, modalY + 280);

            // Botón Ranking
            g.setColor(areaBtnRanking.contains(getMousePosition() != null ? getMousePosition() : new java.awt.Point(0,0)) ? Color.WHITE : Color.CYAN);
            g.drawRoundRect(areaBtnRanking.x, areaBtnRanking.y, areaBtnRanking.width, areaBtnRanking.height, 15, 15);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
            g.drawString("RANKING", areaBtnRanking.x + 45, areaBtnRanking.y + 32);

            // Botón Inicio
            g.setColor(areaBtnInicio.contains(getMousePosition() != null ? getMousePosition() : new java.awt.Point(0,0)) ? Color.WHITE : Color.MAGENTA);
            g.drawRoundRect(areaBtnInicio.x, areaBtnInicio.y, areaBtnInicio.width, areaBtnInicio.height, 15, 15);
            g.drawString("INICIO", areaBtnInicio.x + 60, areaBtnInicio.y + 32);
        }

        // Pausa
        if (juegoPausado) {
            Graphics2D g2 = (Graphics2D) g;

            // Capa translúcida sobre toda la pantalla de la carrera
            g2.setColor(new Color(10, 5, 20, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Rectángulo redondeado central
            g2.setColor(new Color(15, 10, 25, 220));
            g2.fillRoundRect(290, 180, 700, 310, 25, 25);

            // Texto de Pausa
            g2.setFont(new Font("Verdana", Font.BOLD, 55));
            g2.setColor(Color.WHITE);

            String textoPausa = "JUEGO PAUSADO";
            FontMetrics fm = g2.getFontMetrics();
            int xTexto = (getWidth() - fm.stringWidth(textoPausa)) / 2;
            g2.drawString(textoPausa, xTexto, 290);
        }
    }

    /**
     * Captura las señales eléctricas físicas del teclado y las mapea hacia la lógica del juego.
     * @param e Evento de hardware del teclado.
     */
    @Override
    public void keyPressed(KeyEvent e){
        int codigoTecla = e.getKeyCode();

        // Controles J1 (A y D)
        if (vidasJ1 > 0){
            if (codigoTecla == KeyEvent.VK_A){
                jugador1.moverIzquierda();
            } else if (codigoTecla == KeyEvent.VK_D) {
                jugador1.moverDerecha();
            }
        }

        // Controles J2 (Flechas Izquierda y Derecha)
        if (vidasJ2 > 0){
            if (codigoTecla == KeyEvent.VK_LEFT) {
                jugador2.moverIzquierda();
            } else if (codigoTecla == KeyEvent.VK_RIGHT) {
                jugador2.moverDerecha();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!mostrarResultados) { // Solo pausar si la carrera no ha terminado
                if (!juegoPausado) {
                    pausarJuego();
                } else {
                    reanudarJuego();
                }
            }
        }
    }

    // Métodos obligatorio de la interfaz KeyListener
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    // Método de acceso (Getters) público para que el controlador pueda acceder a la lógica de audio
    public GestorAudio getGestorAudio(){
        return  gestorAudio;
    }
}
