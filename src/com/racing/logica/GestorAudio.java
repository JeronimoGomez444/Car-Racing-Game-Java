package com.racing.logica;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;


/**
 * Clase GestorAudio.
 * Administra el subsistema de audio del videojuego aplicando el patrón de diseño creacional Singleton.
 * Proporciona canales independientes para la reproducción en bucle de la música de fondo (BGM)
 * y la ejecución asíncrona de efectos de sonido de corta duración (SFX) con liberación automática de memoria.
 */
public class GestorAudio {

    // Se crea para poder pausar o detener la música ya que se necesita guardar una referencia global de la canción
    private Clip musica;

    // Instancia única a nivel de aplicación (Singleton)
    private static GestorAudio instanciaGestorAudio;

    /**
     * Constructor privado de la clase.
     * Bloquea la instanciación externa forzando el acceso centralizado a través del punto de entrada global.
     */
    private GestorAudio(){

    }

    /**
     * Permite obtener la música
     * @return La instancia única y centralizada de GestorAudio.
     */
    public static GestorAudio getInstancia(){
        if (instanciaGestorAudio == null){
            instanciaGestorAudio = new GestorAudio();
        }

        return  instanciaGestorAudio;
    }

    /**
     * Carga y reproduce de forma continua (en bucle) un archivo de audio como música de fondo.
     * @param nombreArchivo Nombre del archivo de audio con su extensión (ej: "MenuInicio.wav").
     */
    public void reproducirMusica(String nombreArchivo){

        //Si la música ya se esta reproduciendo, no permite reproducir nuevamente
        if (musica != null && musica.isRunning()){
            return;
        }

        try {

            URL urlSonido = getClass().getResource("/sonidos/" + nombreArchivo); // Localiza el recurso de audio

            if (urlSonido != null){
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(urlSonido);
                musica = AudioSystem.getClip();
                musica.open(audioInputStream);
                musica.loop(Clip.LOOP_CONTINUOUSLY);  //Configuración para que la canción sea un loop
                musica.start();
            }else {
                System.err.println("No se encontró el archivo MenuInicio.wav en la carpeta de recursos.");
            }
        }catch (Exception e){
            System.err.println("Error al reproducir el sonido del menú" + e.getMessage());
        }
    }

    /**
     * Detiene de forma inmediata la música de fondo activa.
     */
    public void detenerMusica(){

        if (musica != null && musica.isRunning()){
            musica.stop();
            musica.close();
        }
    }

    /**
     * Reproduce un efecto de sonido corto.
     * @param nombreArchivo Nombre del archivo de efecto de sonido (ej: "Poder1.wav").
     */
    public void reproducirEfecto(String nombreArchivo) {
        try {
            URL urlEfecto = getClass().getResource("/sonidos/" + nombreArchivo); // Localiza el recurso de audio

            if (urlEfecto != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(urlEfecto);

                // Creamos un Clip local e independiente en memoria para este sonido corto
                Clip efectoCorto = AudioSystem.getClip();
                efectoCorto.open(audioInputStream);

                // Escuchador para liberar los recursos de memoria RAM cuando el sonido termine de sonar
                efectoCorto.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        efectoCorto.close();
                    }
                });

                // Reproduce el sonido una sola vez (Single Shot)
                efectoCorto.start();
            } else {
                System.err.println("No se encontró el efecto de sonido: " + nombreArchivo);
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir el efecto " + nombreArchivo + ": " + e.getMessage());
        }
    }
}
