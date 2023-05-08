package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import background_elements.RoadLines;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import stage.GameOverStage;
import stage.GameStage;
import stage.PanelText;

/*
 * The GameTimer is a subclass of the AnimationTimer class. It must override the handle method.
 */

public class GameTimer extends AnimationTimer{

	private GraphicsContext gc;
	private Scene theScene;
	private Ship myShip;

	// constants for text position in rendering status
	private final int hpTextX = 40;
	private final int timeTextX = 300;
	private final int healthTextX = 570;
	private final int textY = 40;
	private final int maxDistance = 500;

	private long timeReference;
	private long startSpawn;
	private long startMove;
	private long startSpawnBuff;
	private boolean hasMask;
	private int init_ctr;

	private ArrayList<GameTimeSeconds> timers;
	private ArrayList<Fish> fishes;
	private ArrayList<GameBuff> buffs;
	public static final int INITIAL_FISH = 7;
	public static final int INITIAL_BUFF = 0;
	public static final int winGameTime = 60; // in seconds
	public static final int bossFightSec = 30; // in seconds
	public static final int MAX_IMMO_TIMER = 5;
	public static boolean bossSpawned = false;
	public static boolean bossDead = false;
	public static boolean ImmoTimerStarted = false;
	public static int fishKilled = 0;


	private ArrayList<RoadLines> lines;
	private ArrayList<RoadLines> initLines;
	// CONSTRUCTOR
	public GameTimer(GraphicsContext gc, Scene theScene){
		this.gc = gc;
		this.theScene = theScene;
		this.timeReference = System.nanoTime();
		this.startSpawn = System.nanoTime();	//get current nanotime
		this.startMove = System.nanoTime();
		this.startSpawnBuff = System.nanoTime();	//get current nanotime
		this.myShip = new Ship("Peter",100,100);
		this.init_ctr= 1;

		//instantiate the ArrayList of Fish
		this.fishes = new ArrayList<Fish>();
		this.buffs = new ArrayList<GameBuff>();
		this.timers = new ArrayList<GameTimeSeconds>();
		//call the spawnFishes method
		this.spawnFishes(INITIAL_FISH);
		this.hasMask = false;
		//call method to handle mouse click event
		this.handleKeyPressEvent();
		this.initStatus();

		this.lines= new ArrayList<RoadLines>();
		this.initLines= new ArrayList<RoadLines>();
	}

	// HANDLE METHOD
	@Override
	public void handle(long currentNanoTime) {

		// MARK: clear canvas
		this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
		System.out.println(this.myShip.getSpeed());

		// MARK: stores game time in seconds
		long currentSec = TimeUnit.NANOSECONDS.toSeconds(currentNanoTime);
		long appStartSec = TimeUnit.NANOSECONDS.toSeconds(this.timeReference);


		long startSec = TimeUnit.NANOSECONDS.toSeconds(this.startSpawn);
		long startMove = TimeUnit.NANOSECONDS.toSeconds(this.startMove);
		long startSpawnSecBuff = TimeUnit.NANOSECONDS.toSeconds(this.startSpawnBuff);

		// will be used as time (in seconds)
		long gameTimeSec = (currentSec - appStartSec);

		// check gameover
		this.checkGameOver(gameTimeSec);

		//speed animation
		if(this.init_ctr > 0){
			this.initLines(5);
			this.init_ctr--;
		}
		this.backgroundAnimation(currentSec, startMove, currentNanoTime);

		/*
		 * Call the spawn methods
		 */
		this.spawnFishesEverySec(currentSec, startSec, currentNanoTime);
		this.spawnBuffsEvery8(currentSec, startSpawnSecBuff, currentNanoTime);
		this.removeBuffsEvery5(currentSec, startSpawnSecBuff);
		this.collideBuff();

//		if (this.myShip.getImmortal() == true) {
//			// one-time starting of immortal timer
//			if (!GameTimer.ImmoTimerStarted) {
//				GameTimeSeconds time = this.timers.get(timerIndex);
//				timerIndex += 1;
//				time.start();
//				GameTimer.ImmoTimerStarted = true;
//			}
//			this.textRenderImmortal(this.gc, GameTimeSeconds.secondsPassed);
//			// if timer reaches 3 seconds
//			if (GameTimeSeconds.secondsPassed == 3) {
//				this.myShip.setImmortal(false);
//				GameTimer.ImmoTimerStarted = false;
//			}
//		}

		this.moveLines();
		this.renderLines();
		this.moveinitLines();
		this.renderinitLines();

		/*
		 * Call the move methods
		 */
		this.myShip.move();
		this.moveFishes();
		this.moveBuff();
		this.myShip.render(this.gc);  //render the ship

		/*
		 * Call the render methods
		 */
		this.renderFishes();
		this.renderBuffs();
		this.fishHitsShip();  // MARK: check fish-ship collision

		this.textRender(this.gc, gameTimeSec);
	}

	//Road Lines Animation
	private void initLines(int howMany){
		int loc=80;
		for(int i=0;i<= howMany;i++){
			int x = -0;
			int y = (GameStage.WINDOW_HEIGHT - (loc * i)) ;
			RoadLines initLine= new RoadLines(x,y,0);
			this.initLines.add(initLine);
		}
	}

	private void renderinitLines() {
		for (RoadLines l : this.initLines){
			l.render(this.gc);
		}
	}

	private void moveinitLines(){
		for(int i = 0; i < this.initLines.size(); i++){
			RoadLines r = this.initLines.get(i);
			if (r.isAlive()) {
				r.move(this.myShip.getSpeed());
			} else {
				this.initLines.remove(r);
			}
		}
	}

	private void backgroundAnimation(long currentSec, long startMove,long currentNanoTime){
		if((currentSec - startMove) > (1/this.myShip.getSpeed())){
			this.animateLines(5);
			this.startMove = currentNanoTime;
		}
	}

	private void animateLines(int howMany){
		int loc=80;
		for(int i=0;i<= howMany;i++){
			int x = GameStage.WINDOW_WIDTH;
			int y = (GameStage.WINDOW_HEIGHT - (loc * i)) ;

			RoadLines line= new RoadLines(x,y,1);
			this.lines.add(line);
		}
	}

	private void renderLines() {
		for (RoadLines l : this.lines){
			l.render(this.gc);
		}
	}

	private void moveLines(){
		for(int i = 0; i < this.lines.size(); i++){
			RoadLines r = this.lines.get(i);
			if (r.isAlive()) {
				r.move(this.myShip.getSpeed());
			} else {
				this.lines.remove(r);
			}
		}
	}

	// FISH METHODS
	private void renderFishes() {
		for (Fish f : this.fishes){
			f.render(this.gc);
		}
	}
	private void spawnFishes(int howMany){
		Random r = new Random();
		for(int i = 0; i < howMany; i++){
			int x = GameStage.WINDOW_WIDTH;
			int y = r.nextInt(5)*80 + 80;

			 // Add a new object Fish to the fishes arraylist
			Fish fish = new Fish(x,y, this.myShip);
			this.fishes.add(fish);
		}
	}
	private void spawnFishesEverySec(long currentSec, long startSec, long currentNanoTime) {
		if((currentSec - startSec) > 1){
			// Ley: Number of obstacle spawned is based on the speed of the player
			this.spawnFishes(this.myShip.getSpeed());
			this.startSpawn = currentNanoTime;
		}
	}
	private void moveFishes(){
		//Loop through the fishes arraylist
		for(int i = 0; i < this.fishes.size(); i++){
			Fish f = this.fishes.get(i);
			if (f.isAlive()) {
				if(f.getType() <=5) f.move(0);		//virus fish
				else f.move(this.myShip.getSpeed());  //obstacle fish - speed is in sync with the background speed
			} else {
				this.fishes.remove(f); // remove fish from arraylist
			}
		}
	}

	// BUFF METHODS
	private void renderBuffs() {
		for (GameBuff b : this.buffs){
			b.render(this.gc);
		}
	}
	private void spawnBuffs() {
		Random pos = new Random();
		GameBuff buff = new GameBuff((GameStage.WINDOW_WIDTH/2), pos.nextInt(5)*80 + 80, this.myShip);
		this.buffs.add(buff);
	}
	private void spawnBuffsEvery8(long currentSec, long startSpawnSecBuff, long currentNanoTime) {
		if ((currentSec - startSpawnSecBuff) > 8) {
			spawnBuffs();
			this.startSpawnBuff = currentNanoTime;
		}
	}
	private void moveBuff(){
		for(int i = 0; i < this.buffs.size(); i++){
			GameBuff b = this.buffs.get(i);
			if (b.isAlive()) {
				b.move(this.myShip.getSpeed());
			} else {
				this.buffs.remove(b);
			}
		}
	}
	private void removeBuffsEvery5(long currentSec, long startSpawnSecBuff) {
		if (currentSec - startSpawnSecBuff == 5) {
			for (int i=0; i<this.buffs.size(); i++) {
				if(this.buffs.get(i).getType()==0){
					this.hasMask = false;
					this.myShip.setImmortal(false);
				}
				this.buffs.get(i).setVisible(false);
				this.buffs.remove(i);
			}
		}
	}

	// collision methods
	private void collideBuff() {
		try {
			for (int i=0; i<this.buffs.size(); i++) {
				if (this.myShip.collidesWith(this.buffs.get(i))) {
					this.buffs.get(i).affectShip(this.buffs.get(i).getType());
					if (this.buffs.get(i).getType() == 0) {  // secondary buff effects (primary effects of gamebuff class)
						this.hasMask = true;
					}
					this.buffs.get(i).setVisible(false);
					this.buffs.remove(i);
				}
			}
		} catch (Exception e) {}
	}

	private void fishHitsShip() {
		try {
			for (int i=0; i<this.fishes.size(); i++) {
				if (this.myShip.collidesWith(this.fishes.get(i))) {
					// NOTE: Move the implementation of immortal in Ship.gotHit() method
					// if (!this.myShip.getImmortal()) {
					this.myShip.gotHit(this.fishes.get(i));
					// }
					this.fishes.get(i).setAlive(false);
				}
			}
		} catch (Exception e) {}
	}

	// listen and handle the key press events
	private void handleKeyPressEvent() {
		this.theScene.setOnKeyPressed(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e){
            	KeyCode code = e.getCode();
            	if(code == KeyCode.UP ||
            	   code == KeyCode.LEFT ||
            	   code == KeyCode.DOWN ||
            	   code == KeyCode.RIGHT)
            	   {
            		if (hasMask) {
            			myShip.loadImage(Ship.MASK_IMAGE_WALK);
            		} else {
            			myShip.loadImage(Ship.SHIP_IMAGE_WALK);
            		}
            	}
                moveMyShip(code);
			}
		});

		this.theScene.setOnKeyReleased(new EventHandler<KeyEvent>(){
            public void handle(KeyEvent e){
            	if (hasMask) {
            		myShip.loadImage(Ship.MASK_IMAGE_WALK);
            	} else {
            		myShip.loadImage(Ship.SHIP_IMAGE_WALK);
            	}
            	KeyCode code = e.getCode();
                stopMyShip(code);
            }
		});
    }

	// move the ship depending on the key pressed
	private void moveMyShip(KeyCode ke) {
		if(ke==KeyCode.UP) this.myShip.setDY(-3);
		if(ke==KeyCode.DOWN) this.myShip.setDY(3);
 		//if(ke==KeyCode.RIGHT) this.myShip.setDX(3);
		//if(ke==KeyCode.LEFT) this.myShip.setDX(-3);
   	}

	// stop the ship's movement; set the ship's DX and DY to 0
	private void stopMyShip(KeyCode ke){
		this.myShip.setDX(0);
		this.myShip.setDY(0);
	}

	private void checkGameOver(long gameTime) {
		if (this.myShip.getHealth() <= 0) {
			this.myShip.die();
			GameOverStage gameover = new GameOverStage(0);
			GameStage.stage.setScene(gameover.getScene());
			this.stop();
		} else if (this.myShip.getDistance() >= this.maxDistance) {
			this.myShip.setWin();
			GameOverStage gameover = new GameOverStage(1);
			GameStage.stage.setScene(gameover.getScene());
			this.stop();
		}
	}

	/*
	// initialize timer for immortality
	private void initTimer() {
		for (int i=0; i<MAX_IMMO_TIMER; i++) {
			GameTimeSeconds timer = new GameTimeSeconds();
			this.timers.add(timer);
		}
	}
	*/

	// UI IN CLASS
	// initialize the text status in gc
	private void initStatus() {
		this.gc.setFill(Color.CHARTREUSE);
		try {
			this.gc.setFont(Font.loadFont(new FileInputStream(new File(PanelText.FONT_PATH)), 20));
		} catch (FileNotFoundException e) {
			this.gc.setFont(Font.font("Verdana", 20));
		}
	}

	// render text (speed, distance, health)
	private void textRender(GraphicsContext gc, long currentSec) {
        this.gc.fillText("Speed: " + this.myShip.getSpeed(), this.hpTextX, this.textY);
        this.myShip.setDistance(currentSec);
        this.gc.fillText("Distance: " + this.myShip.getDistance(), this.timeTextX, this.textY);
        this.gc.fillText("Health: " + this.myShip.getHealth(), this.healthTextX, this.textY);
        if (this.myShip.getImmortal()) {
        	this.gc.fillText("IMMUNE!", this.myShip.getX()-10, this.myShip.getY());
        }
    }

	// GETTER
	public ArrayList<GameTimeSeconds> getTimers() {
		return this.timers;
	}
}

