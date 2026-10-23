package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;


public class Castillo {
	double x, y;
	double ancho, alto;
	double bordeIzq, bordeDer;
	double bordeSup, bordeInf;
	Image img;
	
	
	
	public Castillo(double x, double y, Entorno entorno ) {
        this.x = x;
        this.y = y;
        

        
        this.img = Herramientas.cargarImagen("Castillo.png");
        
        this.ancho=this.img.getWidth(null)*0.59;
        this.alto=this.img.getHeight(null)*0.59;
        actualizarBordes();
	}
	public void dibujar(Entorno e) {

		e.dibujarImagen(img, this.x, this.y, 0, 0.59);


	}
	public void actualizarBordes() {
		
		double puertaX = this.x - 0.1;   // mueve la hitbox a la izquierda
		double puertaY = this.y + 80;   // mueve la hitbox hacia abajo/arriba
		
		this.bordeIzq = puertaX - 5;
		this.bordeDer = puertaX + 5;

		this.bordeSup = puertaY - 30;
		this.bordeInf = puertaY + 30;
	}
	public void moverIzquierda(double velocidad) {
		this.x -= velocidad;
		actualizarBordes();

	}
}
