package game;

import java.util.Random;

import javafx.scene.image.Image;

// buff class
public class GameBuff extends Sprite {

	private Ship affectedShip;
	private int type;

	public final static Image FACEMASK = new Image("images/buff_facemask.png",93,35,false,false);
	private static final Image MEDICINE = new Image("images/pill.png",50,50,false,false);
	private static final Image VACCINE = new Image("images/buff_vaccine.png",50,50,false,false);

	// CONSTRUCTOR
	GameBuff(int x, int y, Ship affectedShip) {
		super(x,y);
		this.affectedShip = affectedShip;
		Random type = new Random();
		this.type = type.nextInt(3);
		if (this.type == 0) {
			this.loadImage(FACEMASK);
		} else if (this.type == 1) {
			this.loadImage(MEDICINE);
		} else if (this.type == 2) {
			this.loadImage(VACCINE);
		}
	}

	// called in GameTimer when buff is picked up
	void affectShip(int type) {
		if (type == 0) {
			/*
			 * For checking
			 */
			// this.affectedShip.setImmortal(true);
		} else if (type == 1) {
			this.affectedShip.setSpeed(0);
		} else if (type == 2) {
			this.affectedShip.setHealth();
		}
	}

	// GETTER
	public int getType() {
		return this.type;
	}

	// SETTER
	public void setAffectedShip(Ship ship) {
		this.affectedShip = ship;
	}
}
