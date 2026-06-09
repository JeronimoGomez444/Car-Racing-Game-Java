import com.racing.vistas.MenuInicio;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
/**
 * Clase Principal (Main) - Punto de entrada del videojuego.
 * Se encarga de inicializar el contenedor gráfico principal (JFrame), configurar las propiedades del sistema de ventanas
 * y lanzar la primera escena de la aplicación (Menú de Inicio).
 */
void main() {

    JFrame ventana = new JFrame("Car Racing Game - UAM");
    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventana.setResizable(false);

    MenuInicio menuInicio = new MenuInicio(ventana);
    ventana.add(menuInicio);

    ventana.pack();
    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);
}
