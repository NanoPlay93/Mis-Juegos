package juego;

import java.awt.Color;
import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Personaje {
	// estos valores indican el alto de la barra superior, la velocidad y el tamaño de la pantalla
	private static final int BARRA = 56, VELOCIDAD = 3;
	private static final int ANCHO_PANTALLA = 800, ALTO_PANTALLA = 600;

	// aca se guardan la posicion, las vidas y la ultima direccion del personaje
	int x;
	int y;
	int vidas = 3;
	int ultimaDireccionX = 1;
	int ultimaDireccionY;

	// estos valores forman los bordes invisibles que se usan para revisar choques
	double ancho;
	double alto;
	double bordeIzq;
	double bordeDer;
	double bordeSup;
	double bordeInf;
	// se deja espacio libre en las calles para que pueda pasar y doblar
	double escala = 0.06;

	// estas imagenes muestran al personaje mirando en distintas direcciones
	Image imagenDerecha;
	Image imagenIzquierda;
	Image imagenArriba;
	Image imagenAbajo;
	Entorno entorno;

	// este constructor guarda la posision inicial y el entorno y carga las imagenes del personaje
	public Personaje(int x, int y, Entorno entorno) {
		this.x = x;
		this.y = y;
		this.entorno = entorno;

		imagenDerecha = Herramientas.cargarImagen("juego/PersonajeDerecha.png");
		imagenIzquierda = Herramientas.cargarImagen("juego/PersonajeIzquierda.png");
		imagenArriba = Herramientas.cargarImagen("juego/PersonajeArriba.png");
		imagenAbajo = Herramientas.cargarImagen("juego/PersonajeAbajo.png");

		ancho = imagenDerecha.getWidth(null) * escala;
		alto = imagenDerecha.getHeight(null) * escala;
		actualizarBordes();
	}

	// este metodo mueve al personaje si el camino esta libre y evita que atraviese los edificios o salga de la pantalla, tambien revisa si puede pasar
	// tambien guarda la direccion para que la imagen y el disparo miren hacia donde se movio por ultima vez
	public void mover(int direccionX, int direccionY, Manzanas manzanas) {
		int nuevoX = x + direccionX * VELOCIDAD;
		int nuevoY = y + direccionY * VELOCIDAD;

		if (manzanas.puedeEstar(nuevoX, y, ancho, alto)) x = nuevoX;
		if (nuevoY - alto / 2 >= BARRA && manzanas.puedeEstar(x, nuevoY, ancho, alto)) {
			y = nuevoY;
		}

		// guarda hacia donde apuntar cuando se dispare
		if (direccionX != 0 || direccionY != 0) {
			ultimaDireccionX = direccionX;
			ultimaDireccionY = direccionY;
		}

		// evita que el personaje salga de la pantalla
		int mitadAncho = (int) (ancho / 2);
		int mitadAlto = (int) (alto / 2);
		if (x < mitadAncho) x = mitadAncho;
		if (x > ANCHO_PANTALLA - mitadAncho) x = ANCHO_PANTALLA - mitadAncho;
		if (y < mitadAlto) y = mitadAlto;
		if (y > ALTO_PANTALLA - mitadAlto) y = ALTO_PANTALLA - mitadAlto;
		actualizarBordes();
	}

	// este metodo elige la imagen segun la ultima direccion y dibuja al personaje en su posicion
	public void dibujar() {
		Image imagen = imagenDerecha;
		if (ultimaDireccionY < 0) imagen = imagenArriba;
		else if (ultimaDireccionY > 0) imagen = imagenAbajo;
		else if (ultimaDireccionX < 0) imagen = imagenIzquierda;
		entorno.dibujarImagen(imagen, x, y, 0, escala);
	}

	// este metodo calcula los cuatro bordes invisibles usando la posicion y el tamaño del personaje, para revisar los choques
	public void actualizarBordes() {
		bordeIzq = x - ancho / 2;
		bordeDer = x + ancho / 2;
		bordeSup = y - alto / 2;
		bordeInf = y + alto / 2;
	}

	// este metodo resta una vida si al personaje todavia le queda alguna
	public void perderVida() {
		if (vidas > 0) vidas--;
	}

	// estos metodos devuelven la posicion horizontal y vertical del personaje
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	// esta clase guarda la posicion del negocio donde se retira el pedido y puede dibujarlo
	public static class Pedido {
		private double x;
		private double y;

		// este constructor guarda las coordenadas donde esta el pedido
		public Pedido(double x, double y) {
			this.x = x;
			this.y = y;
		}

		// este metodo dibuja un circulo celeste en el lugar del pedido
		public void dibujar(Entorno entorno) {
			entorno.dibujarCirculo(x, y, 15, Color.CYAN);
		}

		// estos metodos permiten consultar las coordenadas del pedido
		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
	}

	// esta clase guarda la posicion de la casa donde se entrega el pedido y puede dibujarla
	public static class Entrega {
		private double x;
		private double y;

		// este constructor guarda las coordenadas donde se entrega el pedido
		public Entrega(double x, double y) {
			this.x = x;
			this.y = y;
		}

		// este metodo dibuja un circulo amarillo en el lugar de entrega
		public void dibujar(Entorno entorno) {
			entorno.dibujarCirculo(x, y, 15, Color.YELLOW);
		}

		// estos metodos permiten consultar las coordenadas de entrega
		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
	}
}
