package game;

import java.util.ArrayList;
import javafx.scene.image.Image;
import stage.GameStage;

// ship class (player)
public class Ship extends Sprite{

	private String name;
	private int speed;
	private int health;
	private int distance;
	private final int initialSpeed = 1;
	private boolean alive;
	private boolean immortal;
	private boolean won;

	private ArrayList<Bullet> bullets;
	public final static Image SHIP_IMAGE_STAND = new Image("images/doctor_stand.png",Ship.SHIP_WIDTH,Ship.SHIP_HEIGHT,false,false);
	public final static Image SHIP_IMAGE_WALK = new Image("images/doctor_walk.gif",Ship.SHIP_WIDTH,Ship.SHIP_HEIGHT,false,false);
	public final static Image MASK_IMAGE_STAND = new Image("images/doctor_mask_stand.png",Ship.SHIP_WIDTH,Ship.SHIP_HEIGHT,false,false);
	public final static Image MASK_IMAGE_WALK = new Image("images/doctor_mask_walk.gif",Ship.SHIP_WIDTH,Ship.SHIP_HEIGHT,false,false);

	private final static int SHIP_HEIGHT = 150; // scaled down from 525
	private final static int SHIP_WIDTH = 90; // scaled down from 300

	// CONSTRUCTOR
	public Ship(String name, int x, int y){
		super(x,y);
		this.name = name;
		this.immortal = false;
		this.speed = this.initialSpeed;
		this.alive = true;
		this.bullets = new ArrayList<Bullet>();
		this.health = 100;
		this.distance = 0;
		this.won = false;

		// this.loadImage(Ship.SHIP_IMAGE_STAND);
		this.loadImage(MASK_IMAGE_WALK);  // ship walks initially
	}

	// GETTERS
	public boolean isAlive(){
		if(this.alive) return true;
		return false;
	}
	public String getName(){
		return this.name;
	}
	public int getSpeed() {
		return this.speed;
	}
	public int getHealth(){
		return this.health;
	}
	public int getDistance(){
		return this.distance;
	}

	// If player encounter virus, health - 10
	// If health is <= 0, alive = false
	void gotHit(Fish virus) {
		if (virus.getType() == 6) {
			this.setSpeed(1);
		} else{
			this.health -= 10;
			if(this.health <= 0){
				this.alive = false;
			}
		}
	}

	boolean getImmortal() {
		return this.immortal;
	}

	// SETTERS
	public void setSpeed(int type) {
		if (type == 0){ // To increase
			this.speed = this.speed * 2;
		} else { //To decrease
			// If speed is 1, then will not change
			if (this.speed > 1){
				this.speed = this.speed / 2;
			}
		}
	}
	public void die() {
    	this.alive = false;
    }
	void setImmortal(boolean value) {
		this.immortal = value;
	}
	// If player encounter medicine, health + 20
	public void setHealth(){
		this.health += 20;
	}
	public void setDistance(long time){
		this.distance = (int) (this.speed * time);
	}
	public void setWin(){
		this.won = true;
	}

	//method called if up/down/left/right arrow key is pressed.
	public void move() {
		/*
		 *TODO: 		Only change the x and y position of the ship if the current x,y position
		 *				is within the gamestage width and height so that the ship won't exit the screen
		 */
		if (this.x + this.dx >= GameStage.WINDOW_WIDTH-SHIP_WIDTH) { // MARK: if ship goes to EAST bound (add dx for the program to know if ship will go beyond the bounds)
			this.x = GameStage.WINDOW_WIDTH-SHIP_WIDTH; // MARK: subtract SHIP_WIDTH to take into consideration the png size of ship
		} else if (this.x + this.dx < 0) { // MARK: if ship goes to WEST bound
			this.x = 0;
		} else {
			this.x += this.dx; // MARK: add the dx (horizontal move value)
		}

		if (this.y + this.dy >= GameStage.WINDOW_HEIGHT-SHIP_HEIGHT) { // MARK: if ship goes to SOUTH bound (add dx for the program to know if ship will go beyond the bounds)
			this.y = GameStage.WINDOW_HEIGHT-SHIP_HEIGHT; // MARK: subtract SHIP_WIDTH to take into consideration the png size of ship
		} else if (this.y + this.dy < 0) { // MARK: if ship goes to NORTH bound
			this.y = 0;
		} else {
			this.y += this.dy; // MARK: add the dy (vertical move value)
		}
	}
}
