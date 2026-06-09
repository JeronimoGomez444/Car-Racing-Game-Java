# Car Racing Game 

---

## Integrantes del Proyecto
* **Daniel Lemus Zuluaga**
* **Jeronimo Gómez Hernández**

*Universidad Autónoma de Manizales (UAM)*

---

##  ¿De qué trata el juego?

El objetivo es simple: **Sobrevivir el mayor tiempo posible y conseguir el puntaje más alto.** Cada jugador controla un vehículo en su propia mitad de la pantalla (pista izquierda para el Jugador 1 y pista derecha para el Jugador 2). A medida que el reloj avanza, la velocidad base de la pista aumenta linealmente, poniendo a prueba tus reflejos. Chocar contra un obstáculo te restará un núcleo de energía (vida). La partida termina de forma absoluta cuando **ambos pilotos** agotan sus 3 vidas.

### Sistema de Power-Ups e Interrupción (Mecánicas Clave)
A lo largo de la pista aparecerán de forma aleatoria diferentes modificadores que pueden cambiar el rumbo de la carrera:

1. **Reparación (Icono de Vida):** Recupera un núcleo de salud (máximo 3).
2. **Escudo de Energía:** Te vuelve invulnerable a los choques durante 15 segundos.
3. **Sobrecarga Cinética (Overload):** Castiga a tu rival aumentando la velocidad de sus obstáculos en un 50% durante 10 segundos.
4. **Pulso Electromagnético (PEM):** Despliega una onda de choque que "ciega" la pantalla de tu rival con un degradado de oscuridad, dejándole solo un pequeño foco de luz a su alrededor por 10 segundos.

---

## Controles del Juego

El juego se disputa de manera local compartiendo el mismo teclado:

### Jugador 1 (Lado Izquierdo)
* **`A`**: Moverse al carril izquierdo.
* **`D`**: Moverse al carril derecho.

### Jugador 2 (Lado Derecho)
* **`Flecha Izquierda (←)`**: Moverse al carril izquierdo.
* **`Flecha Derecha (→)`**: Moverse al carril derecho.

### Controles Globales
* **`ESC (Escape)`**: Pausar / Reanudar la carrera en cualquier momento (siempre que la partida esté activa).

---

##  Tecnologías Utilizadas

* **Lenguaje:** Java 17 (o superior)
* **Framework Gráfico:** Java Swing & AWT (Renderizado 2D nativo mediante `Graphics2D` y técnicas de *Delta Time* a 60 FPS).
* **Audio:** Clips de sonido independientes sincronizados mediante hilos en segundo plano.
* **Persistencia:** Almacenamiento local de puntajes máximos en formato de texto estructurado (`.txt`) para el panel de Ranking.
