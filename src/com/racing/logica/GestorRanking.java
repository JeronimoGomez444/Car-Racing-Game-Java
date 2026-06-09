package com.racing.logica;

import java.io.*;
import java.util.ArrayList;


/**
 * Clase GestorRanking.
 * Controla la persistencia de datos en el almacenamiento local y gestiona el cuadro del ganador de la carrera.
 */
public class GestorRanking {

    private static final String ruta_archivo = "resources/ranking.txt"; // Ruta de acceso relativa para archivo de datos

    /**
     * Clase modelo para los datos que se almacenan.
     */
    public static class PuntajeJugador {
        private String nombre;
        private int puntaje;

        public PuntajeJugador(String nombre, int puntaje){
            this.nombre = nombre;
            this.puntaje = puntaje;
        }

        public String getNombre() { return nombre; }
        public int getPuntaje() { return puntaje; }

        @Override
        public String toString(){
            return nombre + "," + puntaje;
        }
    }

    /**
     * Escribe y consolida el puntaje de un piloto en un archivo de texto.
     * @param nombre  Alias asignado o capturado del jugador.
     * @param puntaje Puntuación acumulada durante la partida.
     */
    public static void guardarPuntaje(String nombre, int puntaje) {
        File archivo = new File(ruta_archivo);
        File directorio = archivo.getParentFile();

        if (directorio != null && !directorio.exists()){
            directorio.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(nombre.trim().toUpperCase() + "," + puntaje);
            bw.newLine();
            System.out.println("Puntaje guardado con éxito en el archivo.");
        } catch (IOException e) {
            System.err.println("Error al escribir en el ranking: " + e.getMessage());
        }
    }

    /**
     * Retorna el top tres de pilotos con mejor puntaje.
     * Carga el historial completo, invoca la ordenación mediante Merge Sort recursivo y
     * extrae exclusivamente los tres mejores registros históricos.
     * @return Una sublista ordenada con un tamaño máximo de 3 elementos de tipo PuntajeJugador.
     */
    public static ArrayList<PuntajeJugador> obtenerTopTres() {
        ArrayList<PuntajeJugador> lista = leerTodoElArchivo();

        if (lista.isEmpty()) {
            return lista;
        }

        mergeSort(lista, 0, lista.size() - 1); // Ejecución del algoritmo Merge Sort cubriendo desde el índice inicial

        // Extra únicamente los 3 primeros elementos
        ArrayList<PuntajeJugador> topTres = new ArrayList<>();
        int limite = Math.min(3, lista.size());
        for (int i = 0; i < limite; i++) {
            topTres.add(lista.get(i));
        }

        return topTres;
    }

    /**
     * Realiza un escaneo del archivo txt y convierte las cadenas almacenadas en una instancia de la clase PuntajeJugador.
     * @return Una colección dinámica ArrayList con todos los registros parseados del archivo.
     */
    private static ArrayList<PuntajeJugador> leerTodoElArchivo() {
        ArrayList<PuntajeJugador> lista = new ArrayList<>();
        File archivo = new File(ruta_archivo);

        if (!archivo.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] partes = linea.split(",");
                    // Valida que el registro contenga exactamente la dupla Nombre,Puntaje
                    if (partes.length == 2) {
                        String nombre = partes[0];
                        int puntaje = Integer.parseInt(partes[1].trim());
                        lista.add(new PuntajeJugador(nombre, puntaje));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer el archivo de ranking: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Algoritmo de división recursiva Merge Sort.
     * Divide la colección dinámicamente en mitades lógicas calculando el punto medio de los índices
     * hasta que los subarreglos contengan un solo elemento.
     * @param lista Colección de objetos a ordenar.
     * @param left  Puntero o índice del extremo izquierdo de la subdivisión actual.
     * @param right Puntero o índice del extremo derecho de la subdivisión actual.
     */
    private static void mergeSort(ArrayList<PuntajeJugador> lista, int left, int right) {
        if (left < right) {

            int mid = (left + right) / 2; // Cálcula el pivote o punto medio

            mergeSort(lista, left, mid);
            mergeSort(lista, mid + 1, right);

            merge(lista, left, mid, right); // Combina los ordenamientos
        }
    }

    /**
     * Fase de mezcla del algoritmo.
     * Integra dos subconjuntos contiguos de la lista y los ordena de forma descendente (de mayor a menor).
     * @param lista Colección de datos bajo ordenación.
     * @param left  Índice inicial del bloque izquierdo.
     * @param mid   Índice de división central.
     * @param right Índice terminal del bloque derecho.
     */
    private static void merge(ArrayList<PuntajeJugador> lista, int left, int mid, int right) {

        ArrayList<PuntajeJugador> temp = new ArrayList<>(); // Creamos una lista temporal para almacenar la fusión de manera segura

        int i = left;
        int j = mid + 1;

        // Bucle de comparación
        while (i <= mid && j <= right) {
            if (lista.get(i).getPuntaje() >= lista.get(j).getPuntaje()) { // El puntaje mayor se posiciona primero
                temp.add(lista.get(i++));
            } else {
                temp.add(lista.get(j++));
            }
        }

        while (i <= mid) {
            temp.add(lista.get(i++));
        }

        while (j <= right) {
            temp.add(lista.get(j++));
        }

        for (int x = 0; x < temp.size(); x++) {
            lista.set(left + x, temp.get(x));
        }
    }
}