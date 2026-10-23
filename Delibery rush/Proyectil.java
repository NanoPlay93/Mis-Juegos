package juego;

import java.awt.Color;

import entorno.Entorno;
import entorno.Herramientas;

public class Proyectil {
	// aca se guardan la posicion, direccion y tamaño del disparo, activo indica si sigue en pantalla
	int x;
	int y;
	int direccionX;
	int direccionY;
	boolean activo = true;
	double ancho;
	double alto;
	double bordeIzq;
	double bordeDer;
	double bordeSup;
	double bordeInf;

	// este constructor guarda los datos iniciales del disparo, calcula sus bordes y reproduce el sonido
	public Proyectil(int x, int y, double ancho, double alto, int direccionX, int direccionY) {
		this.x = x;
		this.y = y;
		this.ancho = ancho;
		this.alto = alto;
		this.direccionX = direccionX;
		this.direccionY = direccionY;
		actualizarBordes();

		try {
			Herramientas.play("juego/disparo.wav");
		} catch (Exception e) {
			System.out.println("No se pudo reproducir el sonido del disparo.");
		}
	}

	// este metodo avanza el disparo hacia la direcion indicada y lo desactiva si sale de la pantalla
	public void mover() {
		if (!activo) return;

		x += direccionX * 6;
		y += direccionY * 6;
		if (x < 0 || x > 800 || y < 0 || y > 600) activo = false;
		actualizarBordes();
	}

	// este metodo dibuja el disparo amarillo mientras siga activo
	public void dibujar(Entorno entorno) {
		if (activo) entorno.dibujarRectangulo(x, y, ancho, alto, 0, Color.YELLOW);
	}

	// este metodo calcula los bordes invisibles del disparo para poder revisar si choca con un enemigo
	public void actualizarBordes() {
		bordeIzq = x - ancho / 2;
		bordeDer = x + ancho / 2;
		bordeSup = y - alto / 2;
		bordeInf = y + alto / 2;
	}

	// este metodo compara los bordes del disparo y del enemigo y devuelve true si se superponen, osea si se tocaron
	public boolean colisionaCon(Enemigos enemigo) {
		return bordeDer > enemigo.bordeIzq
				&& bordeIzq < enemigo.bordeDer
				&& bordeInf > enemigo.bordeSup
				&& bordeSup < enemigo.bordeInf;
	}
}
