package com.racing.vistas;

/**
 * Interfaz NavegacionEscenas.
 * Define el contrato de abstracción formal para la gestión y transferencia de control
 * entre los diferentes lienzos gráficos (escenas) del videojuego.
 * * Se utiliza como un mecanismo de Callback (Llamada de retorno) para aplicar el principio
 * de Inversión de Dependencias, permitiendo que la pantalla de juego ordene su propia
 * destrucción y el retorno al inicio sin acoplarse con la lógica interna del enrutador central.
 */
public interface NavegacionEscenas {

    /**
     * Emite la orden de interrupción del lienzo actual para orquestar la reconstrucción
     * y transición hacia el Menú de Inicio principal de la aplicación.
     * Los controladores que implementen esta interfaz se encargarán de sincronizar
     * las mutaciones del contenedor gráfico (JFrame) de forma segura.
     */
    void irAlMenuInicio();
}