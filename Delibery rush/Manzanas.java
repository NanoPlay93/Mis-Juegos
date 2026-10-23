package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Manzanas {
	// estos valores indican cuantas manzanas hay y el tamaño de las manzanas y calles
	private static final int FILAS = 4, COLUMNAS = 4;
	private static final double ANCHO = 120, ALTO = 75, CALLE = 60;

	// aca se cargan las imagenes que se usan para dibujar las manzanas, casas y negocios
	private static final Image imagenManzana = Herramientas.cargarImagen("juego/Manzana.png")
			.getScaledInstance((int) ANCHO, (int) ALTO, Image.SCALE_SMOOTH);
	private static final Image imagenCasa = Herramientas.cargarImagen("juego/Casa.jpg");
	private static final Image imagenNegocio = Herramientas.cargarImagen("juego/Negocio.jpg");

	// este arreglo guarda todas las manzanas del escenario
	private Manzana[] manzanas = new Manzana[FILAS * COLUMNAS];

	// este constructor acomoda las manzanas en filas y columnas y calcula la posision de cada una
	public Manzanas() {
		for (int fila = 0; fila < FILAS; fila++) {
			for (int columna = 0; columna < COLUMNAS; columna++) {
				double x = CALLE + columna * (ANCHO + CALLE) + ANCHO / 2;
				double y = CALLE + fila * (ALTO + CALLE) + ALTO / 2;
				manzanas[fila * COLUMNAS + columna] = new Manzana(x, y);
			}
		}
	}

	// este metodo recorre las manzanas y le pide a cada una que se dibuje
	public void dibujar(Entorno entorno) {
		for (Manzana manzana : manzanas) manzana.dibujar(entorno);
	}

	// este metodo revisa si un objeto puede ocupar una posicion sin meterse adentro de una manzana
	// devuelve false si encuentra un choque y true si el lugar esta libre
	public boolean puedeEstar(double x, double y, double ancho, double alto) {
		for (Manzana manzana : manzanas) {
			boolean chocaX = x + ancho / 2 > manzana.x - manzana.ancho / 2
					&& x - ancho / 2 < manzana.x + manzana.ancho / 2;
			boolean chocaY = y + alto / 2 > manzana.y - manzana.alto / 2
					&& y - alto / 2 < manzana.y + manzana.alto / 2;
			if (chocaX && chocaY) return false;
		}
		return true;
	}

	// este metodo elige al azar un negocio y crea un pedido en la posicion de ese negocio
	public Personaje.Pedido crearPedidoAleatorio() {
		int indice = 4 + (int) (Math.random() * (manzanas.length - 4));
		Negocio negocio = manzanas[indice].negocio;
		return new Personaje.Pedido(negocio.x, negocio.y);
	}

	// este metodo elige al azar una casa y crea una entrega en la posicion de esa casa
	public Personaje.Entrega crearEntregaAleatoria() {
		int indiceManzana = 4 + (int) (Math.random() * (manzanas.length - 4));
		int indiceCasa = (int) (Math.random() * 3);
		Casa casa = manzanas[indiceManzana].casas[indiceCasa];
		return new Personaje.Entrega(casa.x, casa.y);
	}

	// esta clase representa una manzana del mapa y guarda sus casas y su negocio
	private static class Manzana {
		double x;
		double y;
		double ancho = ANCHO;
		double alto = ALTO;
		Casa[] casas = new Casa[3];
		Negocio negocio;

		// este constructor guarda la posicion de la manzana y crea sus edificios
		Manzana(double x, double y) {
			this.x = x;
			this.y = y;
			crearEdificios();
		}

		// este metodo ubica cuatro edificios en la manzana, uno es un negocio y los otros tres son casas, se elige al azar
		private void crearEdificios() {
			double despX = ancho / 4;
			double despY = alto / 4;
			int lugarNegocio = (int) (Math.random() * 4);
			int indiceCasa = 0;

			for (int i = 0; i < 4; i++) {
				double edificioX = x - despX;
				double edificioY = y - despY;
				if (i == 1 || i == 3) edificioX = x + despX;
				if (i == 2 || i == 3) edificioY = y + despY;

				if (i == lugarNegocio) negocio = new Negocio(edificioX, edificioY);
				else casas[indiceCasa++] = new Casa(edificioX, edificioY);
			}
		}

		// este metodo dibuja la manzana y despues dibuja cada casa y el negocio
		private void dibujar(Entorno entorno) {
			entorno.dibujarImagen(imagenManzana, x, y, 0, 1);
			for (Casa casa : casas) casa.dibujar(entorno);
			negocio.dibujar(entorno);
		}
	}

	// esta clase guarda la posicion de una casa y sabe como dibujarla
	private static class Casa {
		double x;
		double y;

		// este constructor guarda las coordenadas de la casa
		Casa(double x, double y) {
			this.x = x;
			this.y = y;
		}

		// este metodo muestra la imagen de la casa en el escenario
		private void dibujar(Entorno entorno) {
			entorno.dibujarImagen(imagenCasa, x, y, 0, 0.025);
		}
	}

	// esta clase guarda la posicion de un negocio y sabe como dibujarlo
	private static class Negocio {
		double x;
		double y;

		// este constructor guarda las coordenadas del negocio
		Negocio(double x, double y) {
			this.x = x;
			this.y = y;
		}

		// este metodo muestra la imagen del negocio en el escenario
		private void dibujar(Entorno entorno) {
			entorno.dibujarImagen(imagenNegocio, x, y, 0, 0.025);
		}
	}
}
