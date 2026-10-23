package juego;

import java.awt.Image;

import entorno.Herramientas;

import java.awt.Color;

import entorno.Entorno;

import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego
{
	// El objeto Entorno que controla el tiempo y otros
	private Entorno entorno;
	private Islas[][] islas;
	private Princesa princesa;
	private Fondo fondo;
	
	private Castillo castillo;
	
	private boolean gano;
	private Bala bala;
	
	private Enemigos[] enemigos;
	
	private boolean perdio;
	
	private Image imagenVictoria;
	private Image imagenPerdiste;
	
	//BOTON PARA EMPEZAR DE VUELTA!
	private double botonX = 400;
	private double botonY = 280;

	private double botonAncho = 300;
	private double botonAlto = 50;
	
	// Variables y métodos propios de cada grupo
	// ...
	
	// CONSTRUCTOR
	Juego() 
	{
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Proyecto para TP", 800, 600);
		
		princesa = new Princesa(400, 300,this.entorno);
		
		// Generador de islas 
		islas = new Islas[3][9];
		int [] niveles = { 620, 390, 180 };
		

		for (int i = 0; i<islas.length; i++ ) {
			for (int j = 0; j<islas[i].length; j++) {
				
				double x = 250 + 750 * j + Math.random() * 120;
				
				if (i == 1) {
					x = x + 250;
				}

				if (i == 2) {
					x = x + 500;
				}
				double y = niveles[i] + Math.random() * 25;

				islas[i][j] = new Islas(x, y, this.entorno);
				
						
			}
		}
			
		// Isla fija inicial debajo de la princesa
		islas[1][0] = new Islas(400, 420, this.entorno);

		fondo = new Fondo();
		
		castillo = new Castillo(7300, 300, this.entorno);
		// Isla final debajo del castillo
		islas[0][7] = new Islas(7300, 485, this.entorno);
		
		
		 //cantidad de enemigos
        enemigos=new Enemigos[3];
        enemigos[0]= new Enemigos (500,200,this.entorno);
        enemigos[1]= new Enemigos (900,300,this.entorno);
        enemigos[2]= new Enemigos (1300,150,this.entorno);
//        enemigos[3]= new Enemigos (1700,400,this.entorno);
//        enemigos[4]= new Enemigos (800,200,this.entorno);
//        
        this.perdio = false;
        
        this.gano = false;
        
        this.bala = null;
        
        this.imagenVictoria = Herramientas.cargarImagen("Victoria.png");
        this.imagenPerdiste = Herramientas.cargarImagen("Perdiste.png");
        
        
		
		// Inicializar lo que haga falta para el juego
		// ...

		// Inicia el juego!
		this.entorno.iniciar();
	}

	/**
	 * Durante el juego, el método tick() será ejecutado en cada instante y 
	 * por lo tanto es el método más importante de esta clase. Aquí se debe 
	 * actualizar el estado interno del juego para simular el paso del tiempo 
	 * (ver el enunciado del TP para mayor detalle).
	 */
	public void tick()
	{
		//PERDIO
		if (this.perdio) {

			this.entorno.dibujarImagen(this.imagenPerdiste, 400, 300, 0, 0.62);

			this.entorno.dibujarRectangulo(botonX, botonY, botonAncho, botonAlto, 0, Color.WHITE);

			this.entorno.cambiarFont("Arial", 20, Color.BLACK);
			this.entorno.escribirTexto("VOLVER A EMPEZAR", 300, 280);

			if (this.entorno.sePresionoBoton(this.entorno.BOTON_IZQUIERDO)
				&& clickEnBoton()) {

				this.princesa = new Princesa(100, 300, this.entorno);

				this.perdio = false;
				this.gano = false;

				this.bala = null; // para que borre todos los proyectiles presentes
			}

			return; // aca corta el codigo si  se cumple, no sigue de largo
		}
		// GANASTE!
		if (this.gano) {

			this.entorno.dibujarImagen(this.imagenVictoria, 400, 300, 0, 0.8);

			this.entorno.cambiarFont("Arial", 50, Color.YELLOW);
			this.entorno.escribirTexto("GANASTE!", 290, 80);

			return;
		}
		// FONDO
		this.fondo.dibujar(this.entorno);

		// MOVIMIENTO
		if (this.entorno.estaPresionada('d')|| this.entorno.estaPresionada(entorno.TECLA_DERECHA)) {
			if (this.princesa.x < 400) {
				this.princesa.mover(1);
			} else {
				moverMapa();
			}
		}

		if (this.entorno.estaPresionada('a')||this.entorno.estaPresionada(entorno.TECLA_IZQUIERDA)) {
			this.princesa.mover(-1);
		}

		// SALTO
		if (this.entorno.sePresiono(this.entorno.TECLA_ESPACIO)|| this.entorno.sePresiono(entorno.TECLA_ARRIBA)) {
			this.princesa.saltar();
		}

		// DISPARO
		if (this.entorno.sePresionoBoton(this.entorno.BOTON_IZQUIERDO) && this.bala == null) {
			this.bala = new Bala(this.princesa.x, this.princesa.y, this.entorno.mouseX(), this.entorno.mouseY());
		}
		// COLISIONES CON ISLAS
		this.princesa.apoyada = false;

		for (int i = 0; i < this.islas.length; i++) {
			for (int j = 0; j < this.islas[0].length; j++) {
				if (this.islas[i][j] != null && seApoyo(this.islas[i][j], this.princesa)) { // hay una isla en esta posicion? y la princesa esta tocando la parte superior de la isla?
					this.princesa.apoyarEn(this.islas[i][j]);
				}
					
					if (chocaTecho(this.islas [i][j], this.princesa)) {
						this.princesa.velocidadY=0;
						this.princesa.y = this.islas[i][j].bordeInf + (this.princesa.alto/2)+1;
						
					}
				}
			}
		
		
		// DIBUJAR ISLAS
		for (int i = 0; i < this.islas.length; i++) {
			for (int j = 0; j < this.islas[0].length; j++) {
				if (this.islas[i][j] != null) {
					this.islas[i][j].dibujar();
				}
			}
		} 
		
		//CASTILLO
		
		this.castillo.dibujar(this.entorno);

		if (colisionPrincesaCastillo(this.princesa, this.castillo)) {
			this.gano = true;
		}

		// BALA
		if (this.bala != null) {

			this.bala.mover();

			for (int i = 0; i < this.enemigos.length; i++) {

				if (this.enemigos[i] != null &&
					chocan(this.bala, this.enemigos[i])) {

					this.enemigos[i] = null;
					this.bala = null;
					break;
				}
			}

			if (this.bala != null) {

				if (this.bala.salioDePantalla()) {
					this.bala = null;
				}
				else {
					this.bala.dibujar(this.entorno);
				}
			}
		}

		// GRAVEDAD
		if (!this.princesa.apoyada) {
			this.princesa.caer();
		}

		// CAIDA AL VACIO
		if (this.princesa.y > 650) {
			this.princesa.perderVida();
			this.princesa.y = -50;
			this.princesa.velocidadY = 0;
			
		}

		// ENEMIGOS
        //dibujar enemigos
        for (int i = 0; i < enemigos.length; i++) {
        	if (enemigos[i] != null) {
        		enemigos[i].mover();
        		
        		if(enemigos[i].x <-150 || enemigos[i].x >950) {
        			enemigos[i]=null;
        		}

        		else if (colisionPrincesaEnemigo(this.princesa, enemigos[i])) {
        			this.princesa.perderVida();
        			enemigos[i] = null;
        		}
        		else {
        			enemigos[i].dibujar(this.entorno);
        		}
        	}
        }
        reponerEnemigos();
        


		// VIDAS
		for (int i = 0; i < this.princesa.vidas; i++) {
			this.entorno.dibujarImagen(this.princesa.corazon, 30 + i * 40, 30, 0, 0.15);
		}

		// DERROTA
		if (this.princesa.estaMuerta()) {
			this.perdio = true;
		}

		// PRINCESA
		this.princesa.dibujar();
	}
	
	
	private boolean chocan(Bala b, Enemigos e) {
		return b.x > e.bordeIzq && 
			   b.x < e.bordeDer &&
			   b.y > e.bordeSup &&
			   b.y < e.bordeInf;
	}

	private boolean seApoyo(Islas i, Princesa p)
	{
		return  
			   Math.abs(p.bordeInf - i.bordeSup) < 20 && //No representa masa ni peso. Es un margen de error de 20 píxeles que permite detectar correctamente la colisión aunque la princesa avance varios píxeles por actualización debido a la gravedad.
			   p.bordeIzq< i.bordeDer && //¿La derecha de la princesa entró dentro de la isla?
			   p.bordeDer > i.bordeIzq && //¿La izquierda de la princesa también está dentro del ancho de la isla?
			   p.velocidadY >= 0; // La princesa tiene que estar cayendo o quieta
	}
	private boolean chocaTecho(Islas i, Princesa p) {
		return Math.abs(p.bordeSup - i.bordeSup) < 20 &&
				p.bordeDer > i.bordeIzq &&
				p.bordeIzq < i.bordeDer && p.velocidadY < 0;
	}
	
	private boolean colisionPrincesaEnemigo(Princesa p, Enemigos e) {
		double margen = 30;

		return p.bordeDer - margen > e.bordeIzq + margen &&
			   p.bordeIzq + margen < e.bordeDer - margen &&
			   p.bordeInf - margen > e.bordeSup + margen &&
			   p.bordeSup + margen < e.bordeInf - margen;
	}
	
	private void reponerEnemigos() {
		for(int i =0; i< enemigos.length; i++) {
			//si el espacio esta vacio, creamos un enemigo nuevo
			if(enemigos[i]==null) {
				double x;
		        if(Math.random()<0.5) {
		        	x = -50;
		        } else {
		        	x = 850;
		        }
				
				//aparecea una altura aleatoria
				double y= 100 + Math.random() * 400;
				enemigos[i]=new Enemigos(x, y, this.entorno);
			}
		}
	}
	
	private boolean colisionPrincesaCastillo(Princesa p, Castillo c) {
		double margen = 62;

		return p.bordeDer - margen > c.bordeIzq &&
			   p.bordeIzq + margen < c.bordeDer &&
			   p.bordeInf - margen > c.bordeSup &&
			   p.bordeSup + margen < c.bordeInf;
	}
	// aca se mueven las islas y generan la ilusion de movimiento
	public void moverMapa() {

		for (int i = 0; i < this.islas.length; i++) {

			for (int j = 0; j < this.islas[0].length; j++) {

				if (this.islas[i][j] != null) {

					this.islas[i][j].moverIzquierda(4); // aca esta la velocidad de deplazamiento de las islas
				}
			}
		}
		this.castillo.moverIzquierda(4); //Velocidad del castillo
		this.fondo.moverIzquierda(1); //aca se mueve el fondo
	}
	//para apretar el boton de volver a empezar
	private boolean clickEnBoton() {

		return this.entorno.mouseX() > botonX - botonAncho / 2 &&
			   this.entorno.mouseX() < botonX + botonAncho / 2 &&
			   this.entorno.mouseY() > botonY - botonAlto / 2 &&
			   this.entorno.mouseY() < botonY + botonAlto / 2;
	}
		// Procesamiento de un instante de tiempo
		// ...
		

	

	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Juego juego = new Juego();
	}
}
