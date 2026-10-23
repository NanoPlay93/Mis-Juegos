package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Princesa {

	double x;
	double y;

	double ancho;
	double alto;

	double bordeInf;
	double bordeSup;
	double bordeIzq;
	double bordeDer;
	
	int vidas;
	
	int disparando;
	
	double velocidadY;
	boolean apoyada;
	
	Image imagenIzq;
	Image imagenDer;

	boolean mirandoDerecha;
	
	Image corazon;
	Entorno entorno;

	public Princesa(double x, double y, Entorno entorno) {
		this.x = x;
		this.y = y;
		this.entorno = entorno;
		this.vidas = 10;
		
		this.corazon = Herramientas.cargarImagen("Corazon.png");

		this.imagenDer = Herramientas.cargarImagen("princesa1.png");
		this.imagenIzq = Herramientas.cargarImagen("princesa1IZQ.png");

		this.mirandoDerecha = true;

		this.ancho = this.imagenDer.getWidth(null) * 0.15;
		this.alto = this.imagenDer.getHeight(null) * 0.12;

		this.velocidadY = 0;
		this.apoyada = false;
		
        this.disparando=1;

	}

	public void dibujar() {

		if (this.mirandoDerecha) {
			this.entorno.dibujarImagen(this.imagenDer, this.x, this.y, 0, 0.25);
			
		}
		else {
			this.entorno.dibujarImagen(this.imagenIzq, this.x, this.y, 0, 0.25);
		}
		actualizarBordes();
	}
	
	public void mover(int direccion) {

		this.x = this.x + direccion * 4;

		if (direccion > 0) {
			this.mirandoDerecha = true;
			this.disparando=1;
		}

		if (direccion < 0) {
			this.mirandoDerecha = false;
			this.disparando=-1;
		}

	}

	public void caer() {

		this.velocidadY = this.velocidadY + 0.5;
		this.y = this.y + this.velocidadY; //aca aumenta la velocidad

		 //Sin actualizarBordes() las colisiones usarían posiciones viejas y la princesa atravesaría las islas o los enemigos.

	}

	public void saltar() {
		if (this.apoyada) {
			this.velocidadY = -18;
			this.apoyada = false;
		}
	}

	public void apoyarEn(Islas isla) {//Este método es el que hace que la princesa quede parada sobre la isla cuando detectás una colisión
		this.y = isla.bordeSup - this.alto / 2;
		this.velocidadY = 0;
		this.apoyada = true;
		
	}

	public void actualizarBordes() { //hitbox de la princesa.
		this.bordeSup = this.y - this.alto / 2;
		this.bordeInf = this.y + this.alto / 2;
		this.bordeIzq = this.x - this.ancho / 2;
		this.bordeDer = this.x + this.ancho / 2;
	}
	
	public boolean estaMuerta() {
		return this.vidas <= 0;
	}
	
	public void perderVida() {
		this.vidas--;

		if (this.vidas < 0) {
			this.vidas = 0;
		}
	}
}
	
