package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Enemigos {
	double x, y, escala;
    double ancho,alto;
    double bordeIzq, bordeDer;
    double bordeSup,bordeInf;
    int direccion;
    Image murcielago1;
	Image murcielago2;
	Image murcielago1invertido;
	Image murcielago2invertido;
	boolean mirandoDerecha;
	boolean alasArriba;
	
	public Enemigos(double x, double y, Entorno e) {
		this.x = x;
        this.y = y;
        this.escala=0.2;
        this.direccion=1;
        this.mirandoDerecha=true;
        this.alasArriba=true;
        this.murcielago1= Herramientas.cargarImagen("murcielago1.png");
        this.murcielago2= Herramientas.cargarImagen("murcielago2.png");
        this.murcielago1invertido= Herramientas.cargarImagen("murcielago1invertido.png");
        this.murcielago2invertido= Herramientas.cargarImagen("murcielago2invertido.png");
        
        this.ancho=this.murcielago1.getWidth(e)*this.escala;
        this.alto=this.murcielago1.getHeight(e)*this.escala;
        if(this.x<400) {
			   this.direccion= 1;
		   }
		   else {
			   this.direccion= -1;
		   }
        
       
	}
	public void dibujar(Entorno e) {
    	if (this.mirandoDerecha) {
    		if(this.alasArriba) {
    			
			e.dibujarImagen(this.murcielago1, this.x, this.y, 0, escala);
		}
		else {
			e.dibujarImagen(this.murcielago2, this.x, this.y, 0, escala);
		}
	}
    	else {
    		if(this.alasArriba) {
    			e.dibujarImagen(this.murcielago1invertido, this.x, this.y, 0, escala);
    		}
    		else {
    			e.dibujarImagen(this.murcielago2invertido, this.x, this.y, 0, escala);
    		}
    	}
        
	}
	 public void mover() {
		   this.x = this.x + direccion * 2;
		   if(Math.random()<0.1) {
		   this.alasArriba= !this.alasArriba;
		   }
		   if(this.direccion >0) {
			   this.mirandoDerecha=false;
		   }
		   if(this.direccion <0) {
			   this.mirandoDerecha=true;
		   }

			actualizarBordes();
			
		}
	 
	 public void moverIzquierda(double velocidad) {
		 this.x -= velocidad;
		 
	 }
	 public void actualizarBordes() {
	    	this.bordeSup=this.y - this.alto/2;
	    	this.bordeInf=this.y + this.alto/2;
	    	this.bordeIzq=this.x - this.ancho/2;
	    	this.bordeDer=this.x + this.ancho/2;
	    }
		
	
}
