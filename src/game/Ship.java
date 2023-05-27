package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.scene.image.Image;
import stage.GameStage;

// ship class (player)
public class Ship extends Sprite implements Serializable{
	private String name;
	private int speed;
	private int health;
	private int distance;
	private final int initialSpeed = 1;
	private boolean alive;
	private boolean immortal;
	private boolean won;

	public final static Image SHIP_IMAGE_WALK = new Image("images/doctor_walk.gif",Ship.SHIP_WIDTH,Ship.SHIP_HEIGHT,false,false);
	public final static Image MASK_IMAGE_WALK = new Image("images/doctor_mask_walk.gif",Ship.SHIP_WIDTH,Ship.SHIP_HEIGHT,false,false);

	private final static int SHIP_HEIGHT = 90;
	private final static int SHIP_WIDTH = 70;

	private GameStage stage;

	// CONSTRUCTOR
	public Ship(int x, int y){
		super(x,y);
		this.immortal = false;
		this.speed = this.initialSpeed;
		this.alive = true;
		this.health = 100;
		this.distance = 0;
		this.won = false;
		this.loadImage(SHIP_IMAGE_WALK);
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

		// if obstacle
		if (virus.getType() == 6 || virus.getType() == 7 || virus.getType() == 8) {
			this.setSpeed(1);

		// if virus
		} else{
			// check first if immortal
			if (!this.getImmortal()) {
				this.health -= 10;
				if(this.health <= 0){
					this.alive = false;
				}
			}
		}
	}

	boolean getImmortal() {
		return this.immortal;
	}

	// SETTERS
	public void setSpeed(int type) {
		if (type == 0){ // To increase
			this.speed = this.speed + 1;
		} else { //To decrease
			// If speed is 1, then will not change
			if (this.speed >= 2){
				this.speed = this.speed - 1;
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
	public void setDistance(){
		this.distance += this.speed;
//		stage.setWriter("distance= "+Integer.toString(this.distance));
	}

	public void setWin(){
		this.won = true;
	}
	public void setName(String name){
		this.name = name;
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
		} else if (this.y + this.dy < 50) {
			this.y = 50;
		} else {
			this.y += this.dy; // MARK: add the dy (vertical move value)
		}
	}


}
