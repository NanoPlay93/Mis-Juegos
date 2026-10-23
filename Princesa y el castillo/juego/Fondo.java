package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Fondo {

	private Image imagen;
	private double x;

	public Fondo() {
		this.imagen = Herramientas.cargarImagen("fondo1.png");
		this.x = 1000; // centro de una imagen de 4000 px
	}

	public void dibujar(Entorno entorno) {
		entorno.dibujarImagen(this.imagen, this.x, 300, 0);
	}

	public void moverIzquierda(double velocidad) {
		this.x -= velocidad;
	}
}