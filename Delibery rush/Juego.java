package juego;

import java.awt.Color;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	// estos valores guardan tiempos, distancias y posisiones que se usan en distintas partes de la partida
	// al ser constantes se mantienen iguales mientras el juego esta funcionando
	private static final int TIEMPO_LIMITE_PEDIDO = 60000;
	private static final int DISTANCIA_INTERACCION = 150;
	private static final int DURACION_MENSAJE_PEDIDO = 1000;
	private static final int[] CALLES_HORIZONTALES = { 300, 435, 570 };
	private static final int[] CALLES_VERTICALES = { 30, 210, 390, 570, 760 };

	// aca se guardan los objetos y datos principales que necesita el juego para funcionar
	// entorno maneja la ventana, personaje guarda al jugador y los arreglos guardan enemigos y bombas
	private Entorno entorno;
	private Personaje personaje;
	private Enemigos[] enemigosHorizontales;
	private Enemigos[] enemigosVerticales;
	private int[] tiemposAparicionHorizontales;
	private int[] tiemposAparicionVerticales;
	private Proyectil proyectil;
	private Enemigos.Bomba[] bombas;
	private boolean colisionAnterior;
	private boolean jugando;
	private boolean gano;
	private boolean reproduciendo;
	private Clip musica;
	private int tiempoInicio;
	private int enemigosEliminados;
	private int pedidosEntregados;
	private String estadoPedido;
	private int tiempoCambioEstadoPedido;
	private Manzanas manzanas;
	private Personaje.Pedido pedidoActual;
	private Personaje.Entrega entregaActual;
	private int tiempoLimitePedido;

	// este constructor prepara el juego, crea el entorno y los objetos iniciales y luego muestra la ventana
	Juego() {
		entorno = new Entorno(this, "Delivery Rush", 800, 600);
		manzanas = new Manzanas();
		iniciarPartida();
		jugando = false;
		reproduciendo = false;
		generarPedido();
		entorno.iniciar();
	}

	// este metodo se repite mientras la ventana esta abierta y controla que pasa en cada momemto
	// revisa si se sale, si la partida termino o si hay que mostrar el menu o actualizar el juego
	public void tick() {
		if (entorno.sePresiono(entorno.TECLA_ESCAPE)) {
			System.exit(0);
		}

		if (gano) {
			mostrarFinal("GANASTE", Color.GREEN, 48);
			return;
		}

		if (!jugando) {
			mostrarMenu();
			return;
		}

		if (!reproduciendo) {
			iniciarMusica();
			reproduciendo = true;
		}

		if (personaje.vidas <= 0) {
			pararMusica();
			mostrarFinal("PERDISTE", Color.RED, 42);
			return;
		}

		actualizarJuego();
		if (pedidosEntregados >= 10) {
			gano = true;
			jugando = false;
			pararMusica();
			reproduciendo = false;
		}
	}

	// este metodo dibuja la pantalla inicial y espera que se presione enter para empezar
	private void mostrarMenu() {
		entorno.cambiarFont("Arial", 42, Color.WHITE);
		entorno.escribirTexto("DELIVERY RUSH", 220, 210);
		entorno.cambiarFont("Arial", 24, Color.YELLOW);
		entorno.escribirTexto("ENTER: jugar", 315, 310);
		entorno.escribirTexto("ESC: salir", 330, 360);

		if (entorno.sePresiono(entorno.TECLA_ENTER)) {
			jugando = true;
			tiempoInicio = entorno.tiempo();
		}
	}

	// este metodo muestra si se gano o se perdio y permite volver a jugar con la tecla r
	private void mostrarFinal(String mensaje, Color color, int tamanio) {
		entorno.cambiarFont("Arial", tamanio, color);
		entorno.escribirTexto(mensaje, 300, 230);
		entorno.cambiarFont("Arial", 24, Color.WHITE);
		entorno.escribirTexto("R: volver a jugar", 290, 320);
		entorno.escribirTexto("ESC: salir", 330, 370);

		if (entorno.sePresiono('r')) {
			reiniciarJuego();
		}
	}

	// este metodo vuelve a preparar los objetos y valores para comenzar otra partida
	private void reiniciarJuego() {
		iniciarPartida();
		generarPedido();
		jugando = true;
		gano = false;
		reproduciendo = false;
		tiempoInicio = entorno.tiempo();
	}

	// este metodo organiza lo que se hace durante cada momento de la partida, osea junta varias acciones
	// dibuja el escenario y actualiza movimientos, enemigos, bombas, pedidos y la informacion de arriba
	private void actualizarJuego() {
		entorno.colorFondo(Color.GRAY);
		manzanas.dibujar(entorno);

		moverPersonaje();
		crearProyectil();
		moverProyectil();

		personaje.dibujar();

		boolean colisionActual = actualizarEnemigos(enemigosHorizontales, false);
		if (actualizarEnemigos(enemigosVerticales, true)) colisionActual = true;

		if (proyectil != null) {
			proyectil.dibujar(entorno);
		}

		reponerEnemigos();
		actualizarBombas();

		if (colisionActual && !colisionAnterior) {
			personaje.perderVida();
			if ("En camino".equals(estadoPedido)) perderPedido();
		}
		colisionAnterior = colisionActual;

		actualizarPedido();
		dibujarIndicadorPedido();
		dibujarBarra();
	}

	// este metodo lee las teclas wasd y mueve al personaje en la direción correspondiente
	private void moverPersonaje() {
		if (entorno.estaPresionada('a')) {
			personaje.mover(-1, 0, manzanas);
		}
		if (entorno.estaPresionada('d')) {
			personaje.mover(1, 0, manzanas);
		}
		if (entorno.estaPresionada('w')) {
			personaje.mover(0, -1, manzanas);
		}
		if (entorno.estaPresionada('s')) {
			personaje.mover(0, 1, manzanas);
		}
	}

	// este metodo crea un proyectil cuando se hace clic y no hay otro disparo activo
	private void crearProyectil() {
		if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO) && proyectil == null) {
			proyectil = new Proyectil(personaje.getX(), personaje.getY(), 14, 14,
					personaje.ultimaDireccionX, personaje.ultimaDireccionY);
		}
	}

	// este metodo mueve el proyectil y lo elimina si ya salio de la pantalla o choco con una manzana
	private void moverProyectil() {
		if (proyectil != null) {
			proyectil.mover();
			if (!proyectil.activo || !manzanas.puedeEstar(
					proyectil.x, proyectil.y, proyectil.ancho, proyectil.alto)) {
				proyectil = null;
			}
		}
	}

	// este metodo mueve y dibuja los enemigos de una lista y revisa sus choques con el personaje o el proyectil
	// devuelve true si algun enemigo choco con el personaje
	private boolean actualizarEnemigos(Enemigos[] lista, boolean vertical) {
		boolean colision = false;

		for (int i = 0; i < lista.length; i++) {
			if (lista[i] == null) continue;

			if (vertical) lista[i].moverVertical();
			else lista[i].mover();

			if (proyectil != null && proyectil.colisionaCon(lista[i])) {
				lista[i] = null;
				if (vertical) tiemposAparicionVerticales[i] = entorno.tiempo();
				else tiemposAparicionHorizontales[i] = entorno.tiempo();
				proyectil = null;
				enemigosEliminados++;
			} else {
				if (colisionan(personaje, lista[i])) colision = true;
				lista[i].dibujar(entorno);
			}
		}
		return colision;
	}

	// este metodo dibuja la barra superior con el tiempo, las vidas, los pedidos y otros datos de la partida
	private void dibujarBarra() {
		int[] x = { 75, 190, 322, 502, 700 };
		int[] ancho = { 130, 90, 170, 190, 180 };
		String[] datos = {
			"Tiempo: " + tiempoTranscurrido(),
			"Vidas: " + personaje.vidas,
			"Pedidos entregados: " + pedidosEntregados,
			"Enemigos eliminados: " + enemigosEliminados,
			"Pedido actual: " + estadoPedido
		};

		entorno.dibujarRectangulo(400, 28, 800, 56, 0, new Color(20, 35, 55));
		for (int i = 0; i < datos.length; i++) {
			entorno.dibujarRectangulo(x[i], 28, ancho[i], 42, 0, new Color(45, 65, 88));
			Color color = Color.WHITE;
			if (i == 4 && !"Esperando".equals(estadoPedido)) color = Color.YELLOW;
			entorno.cambiarFont("Arial", 12, color);
			entorno.escribirTexto(datos[i], x[i] - ancho[i] / 2 + 6, 33);
		}
	}

	// este metodo calcula cuanto tiempo paso desde que comenzo la partida y lo devuelve como minutos y segundos
	private String tiempoTranscurrido() {
		int segundos = (entorno.tiempo() - tiempoInicio) / 1000;
		int minutos = segundos / 60;
		segundos = segundos % 60;
		return dosDigitos(minutos) + ":" + dosDigitos(segundos);
	}

	// este metodo agrega un cero adelante cuando el numero tiene una sola cifra
	private String dosDigitos(int numero) {
		if (numero < 10) return "0" + numero;
		return String.valueOf(numero);
	}

	// este metodo controla el estado del pedido, inicia el tiempo, marca la entrega o lo da por perdido
	private void actualizarPedido() {
		int tiempo = entorno.tiempo();

		if ("Entregado".equals(estadoPedido) || "Perdido".equals(estadoPedido)) {
			if (tiempo >= tiempoCambioEstadoPedido) generarPedido();
			return;
		}

		if ("En camino".equals(estadoPedido) && tiempo >= tiempoLimitePedido) {
			perderPedido();
			return;
		}

		if (!entorno.sePresionoBoton(entorno.BOTON_DERECHO)) return;

		if ("Esperando".equals(estadoPedido)
				&& cerca(personaje.x, personaje.y, pedidoActual.getX(), pedidoActual.getY())) {
			estadoPedido = "En camino";
			tiempoLimitePedido = tiempo + TIEMPO_LIMITE_PEDIDO;
		} else if ("En camino".equals(estadoPedido)
				&& cerca(personaje.x, personaje.y, entregaActual.getX(), entregaActual.getY())) {
			pedidosEntregados++;
			estadoPedido = "Entregado";
			tiempoCambioEstadoPedido = tiempo + DURACION_MENSAJE_PEDIDO;
		}
	}

	// este metodo muestra una flecha sobre el lugar al que tiene que ir el personaje
	private void dibujarIndicadorPedido() {
		double x;
		double y;
		if ("Esperando".equals(estadoPedido)) {
			x = pedidoActual.getX();
			y = pedidoActual.getY();
		} else if ("En camino".equals(estadoPedido)) {
			x = entregaActual.getX();
			y = entregaActual.getY();
		} else {
			return;
		}

		Color colorFlecha = Color.YELLOW;
		if ("En camino".equals(estadoPedido)) colorFlecha = Color.BLUE;
		entorno.cambiarFont("Arial", 32, colorFlecha);
		entorno.escribirTexto("▼", x - 10, y - 24);
	}

	// este metodo calcula la distancia entre dos puntos y revisa si estan lo bastante cerca para interatuar
	private boolean cerca(double x1, double y1, double x2, double y2) {
		return Math.hypot(x1 - x2, y1 - y2) < DISTANCIA_INTERACCION;
	}

	// este metodo crea al personaje y a los enemigos y reinicia los contadores y objetos de la partida
	private void iniciarPartida() {
		personaje = new Personaje(760, 570, entorno);

		enemigosHorizontales = new Enemigos[2];
		tiemposAparicionHorizontales = new int[2];
		enemigosHorizontales[0] = new Enemigos(100, 435, 1);
		enemigosHorizontales[1] = new Enemigos(700, 300, -1);

		enemigosVerticales = new Enemigos[2];
		tiemposAparicionVerticales = new int[2];
		enemigosVerticales[0] = new Enemigos(210, 300, 1);
		enemigosVerticales[1] = new Enemigos(570, 435, -1);

		colisionAnterior = false;
		proyectil = null;
		bombas = new Enemigos.Bomba[20];
		enemigosEliminados = 0;
		pedidosEntregados = 0;
	}

	// este metodo elige al azar un negocio para retirar el pedido y una casa para entregarlo
	private void generarPedido() {
		pedidoActual = manzanas.crearPedidoAleatorio();
		entregaActual = manzanas.crearEntregaAleatoria();
		estadoPedido = "Esperando";
	}

	// este metodo cambia el pedido a perdido y guarda hasta cuando se muestra ese mensaje
	private void perderPedido() {
		estadoPedido = "Perdido";
		tiempoCambioEstadoPedido = entorno.tiempo() + DURACION_MENSAJE_PEDIDO;
	}

	// este metodo revisa y repone por separado los enemigos horizontales y verticales
	private void reponerEnemigos() {
		reponerEnemigos(enemigosHorizontales, tiemposAparicionHorizontales, false);
		reponerEnemigos(enemigosVerticales, tiemposAparicionVerticales, true);
	}

	// este metodo agrega las bombas que dejan los enemigos y revisa si explotan o terminan
	private void actualizarBombas() {
		agregarBombas(enemigosHorizontales);
		agregarBombas(enemigosVerticales);

		int tiempo = entorno.tiempo();
		for (int i = 0; i < bombas.length; i++) {
			if (bombas[i] != null) {
				if (bombas[i].actualizar(entorno, personaje, tiempo)) {
					personaje.vidas = 0;
				}
				if (bombas[i].termino(tiempo)) {
					bombas[i] = null;
				}
			}
		}
	}

	// este metodo pregunta a cada enemigo si debe dejar una bomba y la guarda en el primer espacio libre
	private void agregarBombas(Enemigos[] enemigos) {
		for (int i = 0; i < enemigos.length; i++) {
			if (enemigos[i] != null) {
				Enemigos.Bomba bomba = enemigos[i].dejarBomba(entorno.tiempo());
				if (bomba != null) {
					for (int j = 0; j < bombas.length; j++) {
						if (bombas[j] == null) {
							bombas[j] = bomba;
							break;
						}
					}
				}
			}
		}
	}

	// este metodo vuelve a crear a los enemigos que fueron eliminados cuando llega el momento de su aparicion
	private void reponerEnemigos(Enemigos[] lista, int[] apariciones, boolean vertical) {
		int tiempo = entorno.tiempo();

		for (int i = 0; i < lista.length; i++) {
			if (lista[i] == null) {
				if (tiempo >= apariciones[i]) {
					if (vertical) {
						lista[i] = crearEnemigoVertical();
					} else {
						lista[i] = crearEnemigoHorizontal();
					}
					apariciones[i] = 0;
				}
			}
		}
	}

	// este metodo crea un enemigo vertical en una calle al azar y elige desde que extremo aparece
	private Enemigos crearEnemigoVertical() {
		int x = CALLES_VERTICALES[(int) (Math.random() * CALLES_VERTICALES.length)];
		int direccion = direccionAleatoria();
		int y = 570;

		if (direccion > 0) {
			y = 75;
		}

		return new Enemigos(x, y, direccion);
	}

	// este metodo crea un enemigo horizontal en una calle al azar y elige desde que extremo aparece
	private Enemigos crearEnemigoHorizontal() {
		int direccion = direccionAleatoria();
		int x = 760;
		int y = CALLES_HORIZONTALES[(int) (Math.random() * CALLES_HORIZONTALES.length)];

		if (direccion > 0) {
			x = 30;
		}

		return new Enemigos(x, y, direccion);
	}

	// este metodo devuelve al azar una direccion positiva o negativa para el movimiento
	private int direccionAleatoria() {
		if (Math.random() < 0.5) {
			return 1;
		}
		return -1;
	}

	// este metodo compara los bordes invisibles del personaje y del enemigo para saber si se tocaron
	private boolean colisionan(Personaje personaje, Enemigos enemigo) {
		return personaje.bordeDer > enemigo.bordeIzq
				&& personaje.bordeIzq < enemigo.bordeDer
				&& personaje.bordeInf > enemigo.bordeSup
				&& personaje.bordeSup < enemigo.bordeInf;
	}

	// este metodo carga la musica, baja un poco el volumen y la reproduce en forma continua
	private void iniciarMusica() {
		try {
			musica = Herramientas.cargarSonido("juego/musica.wav");
			if (musica.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl volumen = (FloatControl) musica.getControl(FloatControl.Type.MASTER_GAIN);
				volumen.setValue(-10.0f);
			}
			musica.setFramePosition(0);
			musica.loop(Clip.LOOP_CONTINUOUSLY);
			musica.start();
		} catch (Exception e) {
			System.out.println("No se pudo reproducir el archivo de musica.");
		}
	}

	// este metodo detiene la musica si existe y se esta reproduciendo
	private void pararMusica() {
		if (musica != null && musica.isRunning()) {
			musica.stop();
		}
	}

	// este es el punto de inicio del programa, crea un objeto juego para que comience la partida
	public static void main(String[] args) {
		new Juego();
	}
}
