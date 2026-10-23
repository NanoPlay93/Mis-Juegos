package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Islas {

	double x;
	double y;

	double ancho;
	double alto;

	double bordeInf;
	double bordeSup;
	double bordeIzq;
	double bordeDer;

	Image imagen;
	Entorno entorno;

	public Islas(double x, double y, Entorno entorno) {
		this.x = x;
		this.y = y;
		this.entorno = entorno;

		this.imagen = Herramientas.cargarImagen("IslaFlotante.png");

		this.ancho = this.imagen.getWidth(null) * 0.15;
		this.alto = this.imagen.getHeight(null) * 0.12;

		
	}

	public void dibujar() {
		this.entorno.dibujarImagen(this.imagen, this.x, this.y, 0, 0.75);
		actualizarBordes();
	}

	public void moverIzquierda(double velocidad) {
		this.x = this.x - velocidad;
	
	}

	public void actualizarBordes() {
		this.bordeSup = this.y - this.alto / 2;
		this.bordeInf = this.y + this.alto / 2;
		this.bordeIzq = this.x - this.ancho / 2;
		this.bordeDer = this.x + this.ancho / 2;
	}
}