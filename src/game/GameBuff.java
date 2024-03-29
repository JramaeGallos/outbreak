package game;

import java.util.Random;

import javafx.scene.image.Image;

// buff class
public class GameBuff extends Sprite {

	private Ship affectedShip;
	private int type;
	private boolean alive;

	public final static int BUFF_WIDTH = 60;
	public final static Image FACEMASK = new Image("images/buff_facemask.png",BUFF_WIDTH,BUFF_WIDTH-20,false,false);
	private static final Image ENERGY_DRINK = new Image("images/buff_energydrink.png",BUFF_WIDTH-20,BUFF_WIDTH-10,false,false);
	private static final Image VACCINE = new Image("images/buff_vaccine.png",BUFF_WIDTH,BUFF_WIDTH,false,false);

	// CONSTRUCTOR
	GameBuff(int x, int y, Ship affectedShip) {
		super(x,y);
		this.alive = true;
		this.affectedShip = affectedShip;
		Random type = new Random();
		this.type = type.nextInt(3);
		if (this.type == 0) {
			this.loadImage(FACEMASK);
		} else if (this.type == 1) {
			this.loadImage(ENERGY_DRINK);
		} else if (this.type == 2) {
			this.loadImage(VACCINE);
		}
	}

	// called in GameTimer when buff is picked up
	void affectShip(int type) {
		if (type == 0) {
			this.affectedShip.setImmortal(true);
		} else if (type == 1) {
			this.affectedShip.setSpeed(0);
		} else if (type == 2) {
			this.affectedShip.setHealth();
		}
	}

	//method that changes the x position of the buff
	void move(int speed){
		this.x -= speed;
		if (this.x <= 0 - 60) {
			this.alive = false;
		}
	}

	// GETTER
	public int getType() {
		return this.type;
	}
	public boolean isAlive() {
		return this.alive;
	}

	// SETTER
	public void setAffectedShip(Ship ship) {
		this.affectedShip = ship;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
