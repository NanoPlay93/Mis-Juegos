package juego;

import java.awt.Color; // para elejir los colores, como el de la bomba y explosion
import java.awt.Image; // para guardar y dibujar imagenes

import entorno.Entorno; // esto sirve para dibujar la bomba y la explosion
import entorno.Herramientas; //para poder reproducir sonidos

public class Enemigos {
	//aca se guarda la posicion horizontal y vertical en (X y Y) del enemigo y la direccion indica donde se mueve
	// se guarda la velocidad en la que va, luego en "proximaBomba", al principio vale -1 para indicar que no se programo todavia la primera bomba, es un valor inicial
	int x;
	int y;
	int direccion;
	int velocidad = 2;
	int proximaBomba = -1;
	
	//aca esta el ancho y alto del enemigo, luego estan los limites de la caja invisible al rededor del enemigo
	// la escala es para agrandar o achicar la imagen del enemigo
	double ancho;
	double alto;
	double bordeIzq;
	double bordeDer;
	double bordeSup;
	double bordeInf;
	double escala = 0.06;
	
	//aca se guardar las imagenes del personaje segun donde mire y al final hay un booleano que indica si el enemigo 
	//esta en vertical o horizontal. Al principio es false y luego el juego lo hace true si el enemigo esta vertical
	Image imagenIzquierda;
	Image imagenDerecha;
	Image imagenArriba;
	Image imagenAbajo;
	boolean movimientoVertical;

	//aca esta el constructor, guarda donde aparece (x, y) y hacia donde se mueve, amedida que avanza se va actualizando
	public Enemigos(int x, int y, int direccion) {
		this.x = x;
		this.y = y;
		this.direccion = direccion;
		
		//aca se cargan las imagenes segun para donde el enemigo esta mirando
		imagenIzquierda = Herramientas.cargarImagen("juego/EnemigoIzquierda.png");
		imagenDerecha = Herramientas.cargarImagen("juego/EnemigoDerecha.png");
		imagenArriba = Herramientas.cargarImagen("juego/EnemigoArriba.png");
		imagenAbajo = Herramientas.cargarImagen("juego/EnemigoAbajo.png");
		
		//aca se calcula el ancho y alto de la cajita invisible de las colisiones
		ancho = imagenIzquierda.getWidth(null) * escala * 0.5;
		alto = imagenIzquierda.getHeight(null) * escala * 0.5;
		actualizarBordes(); // y aca se llama a la funcion que calcula la caja invisible del enemigo, para que despues sea ajustada, justo arriba
	}
	
	//este metodo mueve al enemgo a los costados, va cambiando su posicion y hay un condicional
	// que indica que una vez que llegue a los limites de la pantalla el enemigo cambie de direccion para volver
	public void mover() {
		movimientoVertical = false;
		x += direccion * velocidad;
		if (x >= 780) direccion = -1;
		if (x <= 20) direccion = 1;
		actualizarBordes(); // aca se vuelve a llamar este metodo para que se acomode la caja invisible de colisiones segun se va moviendo el enemigo
	}
	//lo mismo que el horizontal
	public void moverVertical() {
		movimientoVertical = true;
		y += direccion * velocidad;
		if (y >= 580) direccion = -1;
		// Llega hasta debajo de la barra, pero no la atraviesa
		if (y <= 75) {
			y = 75;
			direccion = 1;
		}
		// calcula los bordes de colision otra vez 
		actualizarBordes();
	}
	
	//este metodo decide cuando el enemigo deja la bomba
	//si proximaBomba vale -1, programa la primera para dentro de 5 a 10 segundos
	//si todavia falta para ese momento, return null significa “no dejar bomba ahora”
	//cuando llega la hora, programa la siguiente para 8 segundos despues
	
	public Bomba dejarBomba(int tiempo) {
		if (proximaBomba == -1) {
			proximaBomba = tiempo + 5000 + (int) (Math.random() * 5000);
		}
		if (tiempo < proximaBomba) return null;

		proximaBomba = tiempo + 8000;
		if (y < 85 || (y >= 135 && y <= 195)) return null; //aca este el rango de donde se evita dejar la bomba
		return new Bomba(x, y, movimientoVertical, tiempo); //aca se guarda la posicion del enemigo donde y en que momento deja la bomba, segun las coordenadas y el tiempo, osea se lo da al juego
	}
	
	//esto es una clase bomba, donde se guardan los datos de cada bomba
	public static class Bomba {
		//estas son las coordenadas de en donde quedo la bomba
		private int x;
		private int y;
		//aca indica si el enemigo que dejo la bomba se mova en vertical o horizontal. La bomba usa ese dato para decidir la forma de la explosion
		//alta si el enemigo iba en vertical o mas ancha si el el enemigo iba en horizontal
		private boolean vertical; 
		private int tiempoInicio; // aca esto sirve para saber cuando se creo y contar cada 3 segundos
		
		//aca guarda si la bomba ya hizo sonar la explocion. FALSE: todavia no sono, TRUE: ya sono
		private boolean sonidoReproducido;

		//este es el constructor de la bomba. Se ejecuta cuando se crea una bomba nueva
		public Bomba(int x, int y, boolean vertical, int tiempoInicio) {
			//donde aparece, significa; guarda el valor recivido en X y Y de esta bomba
			this.x = x;
			this.y = y;
			// en que posicion esta, ancha o alta
			this.vertical = vertical;
			this.tiempoInicio = tiempoInicio; //el tiempo cuando se creo la bomba para contar 3 segundos
		}
		
		//este metodo actualiza la bomba a acada momento del juego, calcula cuanto tiempo paso desde que se creo
		// prepara el tamaño de la explosion en ancho o en vertical. Este metodo se usa para avisar si la explosion alcanzo al personaje
		public boolean actualizar(Entorno entorno, Personaje personaje, int tiempo) {
			int transcurrido = tiempo - tiempoInicio;
			double ancho = 180;
			double alto = 75;
			if (vertical) {
				ancho = 75;
				alto = 180;
			}
			
			// antes de los 3 segundos dibuja la bomba negra, si toca al personaje devuelve true
			if (transcurrido < 3000) {
				entorno.dibujarCirculo(x, y, 20, Color.BLACK);
				if (colisiona(personaje, 20, 20)) return true; //si toca la bomba el personaje muere
			} else if (transcurrido < 3500) { //entre los 3 y los 3,5 segundos se dibujo la explosion. Si la explosion alcanza al personaje se devuelve true
				dibujarExplosion(entorno, ancho, alto);
				if (colisiona(personaje, ancho, alto)) return true;
			}
			return false; // si no lo tonca devuelve false
		}
		
		//aca se dibujo la explision y hace sonar el audio
		private void dibujarExplosion(Entorno entorno, double ancho, double alto) {
			if (!sonidoReproducido) { //revisa que el sonido todavía no haya sonado
				
				//aca reproduce el archivo de sonido y si no lo encuenta se ejecuta el catch
				try {
					Herramientas.play("juego/explosion.wav");
				} catch (Exception e) {
					System.out.println("No se pudo reproducir el sonido de la explosion.");
				}
				sonidoReproducido = true; //evita repetir el sonido
			}
			//aca se dibuja la explosion en naranja, ancho y alto
			entorno.dibujarRectangulo(x, y, ancho, alto, 0, Color.ORANGE);
		}
		// este metodo revisa si la bomba ya termino, el juego usa el resultado para sacar la bomba de la partida. Osea el juego deja de mostrarla
		public boolean termino(int tiempo) {
			return tiempo - tiempoInicio >= 3500; //si pasaron 3,5 segundos o mas devuelve true. Si paso menos tiempo devuelve false
		}
		
		//aca este metodo comprueba si este personaje choca con la bomba o la explosion. Usa una caja invisible para cada uno
		private boolean colisiona(Personaje personaje, double ancho, double alto) {
			return personaje.bordeDer > x - ancho / 2
					&& personaje.bordeIzq < x + ancho / 2
					&& personaje.bordeInf > y - alto / 2
					&& personaje.bordeSup < y + alto / 2;
		} //si se superponen devuelve true, sino false
	}

		//aca se elije la imagen que motrar segun como se mueve el enemigo, indicando el tamaño con la escala
	public void dibujar(Entorno entorno) {
		Image imagen;
		if (movimientoVertical) {
			if (direccion > 0) imagen = imagenAbajo;
			else imagen = imagenArriba;
		} else {
			// aca se elige la imagen para el movimiento horizontal
			if (direccion > 0) imagen = imagenDerecha;
			else imagen = imagenIzquierda;
		}
		entorno.dibujarImagen(imagen, x, y, 0, escala); //aca se dibuja la imagen
	}

	//este metodo calcula la caja invisible del enemigo, usa x e y como centro, y resta o suma la mitad del ancho y del alto para encontrar cada borde
	public void actualizarBordes() {
		bordeIzq = x - ancho / 2;
		bordeDer = x + ancho / 2;
		bordeSup = y - alto / 2;
		bordeInf = y + alto / 2;
	}
	
	//Son metodos para consultar la posicion del enemigo
	public int getX() { return x; } //devuelve su posicion horizontal (x)
	public int getY() { return y; } //devuelve su posicion vertical (y)
}
