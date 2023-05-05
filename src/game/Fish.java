package game;

import java.util.Random; // MARK: imported random for fish speed

import javafx.scene.image.Image;

// fish enemy class (virus)
public class Fish extends Sprite {
	public static final int MAX_FISH_SPEED = 3; // speed varies by 3
	public static final int MAX_FISH_DAMAGE = 3;
	public static final int MIN_FISH_DAMAGE = 1;

	private Ship myShip;

	// MARK: FISH IMAGES
	public final static int FISH_WIDTH=50;
	public final static Image FISH_IMAGE1 = new Image("images/virus_1.gif",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);
	public final static Image FISH_IMAGE2 = new Image("images/virus_2.gif",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);
	public final static Image FISH_IMAGE3 = new Image("images/virus_3.gif",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);
	public final static Image FISH_IMAGE4 = new Image("images/virus_4.gif",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);
	public final static Image FISH_IMAGE5 = new Image("images/virus_5.png",Fish.FISH_WIDTH+40,Fish.FISH_WIDTH,false,false);
	public final static Image FISH_IMAGE6 = new Image("images/virus_6.png",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);

	// MARK: OBSTACLE IMAGES
	public final static Image PUDDLE = new Image("images/puddle.png",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);
	public final static Image CONE = new Image("images/cone.png",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);
	public final static Image STONE = new Image("images/stone.png",Fish.FISH_WIDTH,Fish.FISH_WIDTH,false,false);

	private int fishType;
	private boolean alive;
	private int speed;

	// CONSTRUCTOR
	Fish(int x, int y, Ship ship){
		super(x,y);
		this.myShip = ship;
		/*
		 *TODO: Randomize speed of fish and moveRight's initial value
		 */
		this.alive = true;
		Random randomType = new Random();

		this.fishType = randomType.nextInt(9);
		// set fish image depending on type (random)
		switch (this.fishType) {
			case 0: this.loadImage(Fish.FISH_IMAGE1);
						break;
			case 1: this.loadImage(Fish.FISH_IMAGE2);
						break;
			case 2: this.loadImage(Fish.FISH_IMAGE3);
						break;
			case 3: this.loadImage(Fish.FISH_IMAGE4);
						break;
			case 4: this.loadImage(Fish.FISH_IMAGE5);
						break;
			case 5: this.loadImage(Fish.FISH_IMAGE6);
						break;
			case 6: this.loadImage(Fish.PUDDLE);
						break;
			case 7: this.loadImage(Fish.CONE);
						break;
			case 8: this.loadImage(Fish.STONE);
						break;
		}
		this.fishSpeed(this.fishType);

	}

	void fishSpeed(int type){
		if(type<=5){ //viruses that affects health
			//random speed
			Random randomSpeed = new Random();
			this.speed = randomSpeed.nextInt(Fish.MAX_FISH_SPEED+1 - 1) + this.myShip.getSpeed(); // MARK: MAX+1 since nextInt is exclusive right
		}
	}

	//method that changes the x position of the fish
	void move(int type){
		/*
		 * TODO: 				If moveRight is true and if the fish hasn't reached the right boundary yet,
		 *    						move the fish to the right by changing the x position of the fish depending on its speed
		 *    					else if it has reached the boundary, change the moveRight value / move to the left
		 * 					 Else, if moveRight is false and if the fish hasn't reached the left boundary yet,
		 * 	 						move the fish to the left by changing the x position of the fish depending on its speed.
		 * 						else if it has reached the boundary, change the moveRight value / move to the right
		 */
//		if (this.moveRight && this.x < GameStage.WINDOW_WIDTH-FISH_WIDTH) {
//			this.x += this.speed;
//		} else if (this.x >= GameStage.WINDOW_WIDTH-FISH_WIDTH) {
//			this.moveRight = !this.moveRight;
//		}
//
//		if (this.moveRight == false && this.x > 0) {
//			this.x -= this.speed;
//		} else if (this.x <= 0) {
//			this.moveRight = !this.moveRight;
//		}
		if(type == 0) this.x -= this.speed;  //virus fish: random speed
		else this.x -= type;				//obstacle fish: speed of the ship
		// if fish reaches the left bound
		if (this.x <= 0 - FISH_WIDTH) { // to consider the image size
			this.alive = false;
		}
	}

	//GETTERS
	public boolean isAlive() {
		return this.alive;
	}
	public int getType() {
		return this.fishType;
	}

	// SETTER
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
