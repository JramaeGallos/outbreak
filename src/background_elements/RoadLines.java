package background_elements;

import game.Sprite;
import javafx.scene.image.Image;

public class RoadLines extends Sprite {
	private int speed;
	private boolean alive;
	public static final int MAX_LINE_SPEED = 3;
	public static final int LONG_LINE_WIDTH = 850;
	public static final int LONG_LINE_HEIGHT = 60;
	public static final int SHORT_LINE_WIDTH = 100;
	public static final int SHORT_LINE_HEIGHT = 60;

	public final static Image ROAD_LINE_IMAGE = new Image("background_elements_resources/line.png",LONG_LINE_WIDTH, LONG_LINE_HEIGHT,false,false);
	public final static Image ROAD_LINE_IMAGE2 = new Image("background_elements_resources/short_line.png",SHORT_LINE_WIDTH, SHORT_LINE_HEIGHT,false,false);


	public RoadLines(int x, int y, int type){
		super(x,y);
		this.speed= 1;
		this.alive= true;

		switch(type){
		case 0: this.loadImage(RoadLines.ROAD_LINE_IMAGE); break;
		case 1: this.loadImage(RoadLines.ROAD_LINE_IMAGE2); break;
		}
	}

	public void move(int ship_speed){
		this.x -= ship_speed;
		// if fish reaches the left bound
		if (this.x <= 0 - this.img.getWidth()) { // to consider the image size
			this.alive = false;
		}
	}

	public boolean isAlive() {
		return this.alive;
	}

	// SETTER
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
