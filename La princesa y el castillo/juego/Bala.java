package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Bala {
	double x, y;
	double velocidadX, velocidadY;
	double escala;
	Image img;

	public Bala(double x, double y, double mouseX, double mouseY) {
		this.x = x;
		this.y = y;

		this.escala = 0.18;
		this.img = Herramientas.cargarImagen("Llamita.png");

		double dx = mouseX - x;
		double dy = mouseY - y;

		double distancia = Math.sqrt(dx * dx + dy * dy);

		this.velocidadX = dx / distancia * 10;
		this.velocidadY = dy / distancia * 10;
	}

	public void mover() {
		this.x += this.velocidadX;
		this.y += this.velocidadY;
	}

	public void dibujar(Entorno e) {
		e.dibujarImagen(this.img, this.x, this.y, 0, this.escala);
	}

	public boolean salioDePantalla() {
		return this.x < 0 || this.x > 800 || this.y < 0 || this.y > 600;
	}
}