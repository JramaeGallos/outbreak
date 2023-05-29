package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import background_elements.RoadLines;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import stage.GameOverStage;
import stage.GameStage;
import stage.GameStage.DataCallback;
import stage.PanelText;

/*
 * The GameTimer is a subclass of the AnimationTimer class. It must override the handle method.
 */

public class GameTimer extends AnimationTimer implements DataCallback{
	private GraphicsContext gc;
	private GraphicsContext gc1;
	private Canvas canvas1;
	private Scene theScene;
	private Ship myShip;
	private Map<String, Integer> rankingList;

	// constants for text position in rendering status
	private final int hpTextX = 40;
	private final int timeTextX = 300;
	private final int healthTextX = 570;
	private final int textY = 40;
	private final int maxDistance = 30;

	private long timeReference;
	private long startSpawn;
	private long startMove;
	private long startSpawnBuff;
	private long startDistance;
	private boolean hasMask;
	private int init_ctr;
	private Image gameBackground;
	private GameStage stage;
	private final static String GAME_BACKGROUND_PATH = "stage/resources/gray_background.jpg";

	private ArrayList<GameTimeSeconds> timers;
	private ArrayList<Fish> fishes;
	private ArrayList<GameBuff> buffs;
	public static final int INITIAL_FISH = 7;
	public static final int INITIAL_BUFF = 0;
	public static final int winGameTime = 60; // in seconds
	public static final int bossFightSec = 30; // in seconds
	public static final int MAX_IMMO_TIMER = 5;

	private ArrayList<RoadLines> lines;
	private ArrayList<RoadLines> initLines;

	// CONSTRUCTOR
	public GameTimer(GraphicsContext gc, GraphicsContext gc1, Canvas canvas1, Scene theScene, GameStage stage, Ship myShip){
		this.rankingList = new HashMap<>();
		this.gc = gc;
		this.gc1 = gc1;
		this.canvas1 = canvas1;
		this.theScene = theScene;
		this.myShip = myShip;
		this.timeReference = System.nanoTime();
		this.startSpawn = System.nanoTime();	//get current nanotime
		this.startMove = System.nanoTime();
		this.startDistance = System.nanoTime(); // distance
		this.startSpawnBuff = System.nanoTime();	//get current nanotime
		this.init_ctr= 1;
		this.stage = stage;
		this.gameBackground = new Image(GameTimer.GAME_BACKGROUND_PATH, 200, GameStage.WINDOW_HEIGHT, false, false);

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
		this.gc1.clearRect(0, 0, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);

		// MARK: stores game time in seconds
		long currentSec = TimeUnit.NANOSECONDS.toSeconds(currentNanoTime);
		long appStartSec = TimeUnit.NANOSECONDS.toSeconds(this.timeReference);


		long startSec = TimeUnit.NANOSECONDS.toSeconds(this.startSpawn);
		long startMove = TimeUnit.NANOSECONDS.toSeconds(this.startMove);
		long startSpawnSecBuff = TimeUnit.NANOSECONDS.toSeconds(this.startSpawnBuff);
		long startSecDistance = TimeUnit.NANOSECONDS.toSeconds(this.startDistance);


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
		this.shipSetDistance(currentSec, startSecDistance, currentNanoTime);
		this.spawnBuffsEvery8(currentSec, startSpawnSecBuff, currentNanoTime);
		this.removeBuffsEvery5(currentSec, startSpawnSecBuff);
		this.collideBuff();

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
		this.gc.drawImage(this.gameBackground, 800, 0, 250, 500);
		this.gc1.setStroke(Color.BLACK); // Set the stroke color
        this.gc1.setLineWidth(1); // Set the stroke width
        this.gc1.strokeRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
		this.rankRender(this.gc);
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
		this.canvas1.setOnMouseEntered(event -> {
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
			this.stage.disableTextField();
		});
		this.canvas1.setOnMouseExited(event -> {
			this.stage.enableTextField();
		});
		this.theScene.setOnMouseExited(event -> {
			this.stage.enableTextField();
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

	private void setUpGameOver(String name){
		this.myShip.die();
		GameOverStage gameover = new GameOverStage(0, name);
		GameStage.stage.setScene(gameover.getScene());
		this.stop();
	}

	private void checkGameOver(long gameTime) {

		boolean onePlayerWon = false;

		for (Map.Entry<String, Integer> entry : this.rankingList.entrySet()) {
			if (entry.getValue() >= this.maxDistance) {
				onePlayerWon = true;
			}
		}

		if (this.myShip.getDistance() >= this.maxDistance) {
			this.myShip.setWin();
			GameOverStage gameover = new GameOverStage(1, null);
			GameStage.stage.setScene(gameover.getScene());
			this.stop();
		// If one of the players already won the game
		} else if (onePlayerWon) {
			// Finding the username of the player who won the game
			String key = null;
	        for (Map.Entry<String, Integer> entry : this.rankingList.entrySet()) {
	            if (entry.getValue() >= this.maxDistance) {
	                key = entry.getKey();
	                break;
	            }
	        }
			this.setUpGameOver(key);
		} else if (this.myShip.getHealth() <= 0) {
			this.setUpGameOver(null);
		}
	}

	// frame distance method (adjust distance every second depending on speed)
	private void shipSetDistance(long currentSec, long startSec, long currentNanoTime) {
		if ((currentSec - startSec) > 1) {
			this.myShip.setDistance();
			stage.setWriter("distance= "+this.myShip.getName()+" : "+Integer.toString(this.myShip.getDistance()));
			this.startDistance = currentNanoTime;
		}
	}

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
	public void textRender(GraphicsContext gc, long currentSec) {
        this.gc.fillText("Speed: " + this.myShip.getSpeed(), this.hpTextX, this.textY);
        this.gc.fillText("Distance: " + this.myShip.getDistance(), this.timeTextX, this.textY);
        this.gc.fillText("Health: " + this.myShip.getHealth(), this.healthTextX, this.textY);
        this.gc.fillText(this.myShip.getName(), this.myShip.getX(), this.myShip.getY() + 100);
        if (this.myShip.getImmortal()) {
        	this.gc.fillText("IMMUNE!", this.myShip.getX()-10, this.myShip.getY());
        }
    }

	@Override
    public void onDataReceived(String data) {
		// System.out.println(data);

		String[] parts = data.split(": ");
		String username = parts[0];
		int distance = Integer.parseInt(parts[1]);

		// add / update distances of players
		this.rankingList.put(username, distance);
    }

	//render in-game rankings
	private void rankRender(GraphicsContext gc){
		this.gc.fillText("RANKINGS", 870, 50);

		try {
			// sorting the dictionary in descending order
			List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(this.rankingList.entrySet());
			sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

	        // Iterate over the sorted entries and print the keys and values
	        int i = 0;
	        for (Map.Entry<String, Integer> entry : sortedEntries) {
	            this.gc.fillText((i+1) + "\t" + entry.getKey() + "\t " + entry.getValue(), 830, 100 + (i * 30));
	            i++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public GraphicsContext getGc() {
		return this.gc;
	}

	// GETTER
	public ArrayList<GameTimeSeconds> getTimers() {
		return this.timers;
	}

	public void setUserName(String user){
		this.myShip.setName(user);
	}
}

