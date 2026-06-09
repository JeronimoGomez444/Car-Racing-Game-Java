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

# Car Racing Game

---

## Project Team Members
* **Daniel Lemus Zuluaga**
* **Jeronimo Gómez Hernández**

*Autonomous University of Manizales (UAM)*

---

## What is the game about?

The objective is simple: **Survive as long as possible and get the highest score.** Each player controls a vehicle on their own half of the screen (left track for Player 1 and right track for Player 2). As the clock ticks, the base speed of the track increases linearly, testing your reflexes. Hitting an obstacle will cost you one energy core (life). The game ends when **both drivers** run out of their 3 lives.

### Power-Up and Interrupt System (Key Mechanics)
Throughout the track, different modifiers will randomly appear that can change the course of the race:

1. **Repair (Health Icon):** Restores one health core (maximum 3).

2. **Energy Shield:** Makes you invulnerable to collisions for 15 seconds.

3. **Kinetic Overload:** Punishes your opponent by increasing the speed of their obstacles by 50% for 10 seconds.

4. **Electromagnetic Pulse (EMP):** Unleashes a shockwave that blinds your opponent's screen with a gradient of darkness, leaving only a small point of light around them for 10 seconds.

---

## Game Controls

The game is played locally using the same keyboard:

### Player 1 (Left Side)
* **`A`**: Move to the left lane.

* **`D`**: Move to the right lane.

### Player 2 (Right Side)
* **`Left Arrow (←)`**: Move to the left lane.

* **`Right Arrow (→)`**: Move to the right lane.

### Global Controls
* **`ESC (Escape)`**: Pause/Resume the race at any time (as long as the game is active).

---

## Technologies Used

* **Language:** Java 17 (or higher)
* **Graphics Framework:** Java Swing & AWT (Native 2D rendering using Graphics2D and Delta Time techniques at 60 FPS).

* **Audio:** Independent sound clips synchronized using background threads.

* **Persistence:** Local storage of high scores in structured text format (`.txt`) for the Ranking panel.
