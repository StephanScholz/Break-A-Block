package sirkarpfen.breakablock.main;

import java.util.ArrayList;

import sirkarpfen.breakablock.entities.Ball;
import sirkarpfen.breakablock.entities.Entity;
import sirkarpfen.breakablock.entities.Paddle;
import sirkarpfen.breakablock.entities.eventhandler.EntityContactEventHandler;
import sirkarpfen.breakablock.entities.manager.EntityManager;
import sirkarpfen.breakablock.screens.GameOverScreen;
import sirkarpfen.breakablock.screens.GameScreen;
import sirkarpfen.breakablock.screens.LevelEditorScreen;
import sirkarpfen.breakablock.screens.MainMenuScreen;
import sirkarpfen.breakablock.storage.LevelStorage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class BlockGame extends Game {
	
	public static boolean debug = false;
	
	private static BlockGame instance;
	public static BlockGame getInstance() {
		if(instance == null) {
			instance = new BlockGame();
		}
		return instance;
	}
	
	private LevelEditorScreen levelEditorScreen;
	private GameScreen gameScreen;
	public GameScreen getGameScreen() { return gameScreen; }
	private GameOverScreen gameOverScreen;
	private MainMenuScreen menuScreen;
	public boolean isMenuScreen() {
		if(this.getScreen() instanceof MainMenuScreen) {
			return true;
		}
		return false;
	}
	
	private OrthographicCamera camera;
	public OrthographicCamera getCamera() {	return camera; }
	
	public boolean gameOver;
	private boolean isDemoGame = true;
	public boolean isDemoGame() { return isDemoGame; }
	
	private int screenWidth;
	private int screenHeight;
	
	private static int highscore;
	public static int getHighscore() { return highscore; }
	public static void setHighscore(int score) { highscore = score; }
	public static void addScore(int score) { highscore += score; }
	
	private int life = 3;
	public int getLife() { return life; }
	public void setLife(int life) { this.life = life; }
	public void addLife(int value) { this.life += value; }
	public void removeLife(int value) { this.life -= value; }
	
	// Stores LevelData according to Block-Size: 32x64
	private char[] levelCharArray;
	public char[] getLevelCharArray() { return levelCharArray; }
	private LevelStorage levelStorage;

	private EntityManager entityManager;
	
	private static World world;
	/**
	 * @return The world that contains all Box2D bodies.
	 */
	public static World getWorld() { return world; }
	
	private BlockGame() {}
	
	/**
	 * Sets up the Map/Player and Camera. Camera is always centered on the player,
	 * when a new game is started, or a savegame loaded.
	 */
	@Override
	public void create() {
		
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		levelStorage = LevelStorage.getInstance();
		levelStorage.initLevelData();
		levelCharArray = levelStorage.getCurrentLevel().getLevelData().toCharArray();
		entityManager = EntityManager.getInstance();
		
		// use GL10 without "power of two" enforcement.
		Texture.setEnforcePotImages(false);
		
		Gdx.input.setInputProcessor(new KeyInputHandler());
		
		// prepares the OrthographicCamera, for projection.
		this.prepareCamera();
		
		// Creates the world, for our Box2D Entities.
		this.createWorld();
		
		this.createScreens();
		
		this.createDemoGame();
		
	}
	
	public void createDemoGame() {
		this.isDemoGame = true;
		this.setScreen(menuScreen);
	}
	
	public void startDemoGame() {
		ArrayList<? extends Entity> balls = entityManager.getEntities(Ball.class);
		for(int i = 0; i < balls.size(); i++) {
			Ball ball = (Ball)balls.get(i);
			ball.setVisible(true);
			ball.start();
		}
	}
	
	public void endDemoGame() {
		this.isDemoGame = false;
		for(Entity e : entityManager.getEntities(Ball.class)) {
			e.lockMovement(true);
		}
	}
	
	public void startEditor() {
		this.isDemoGame = false;
		this.setScreen(levelEditorScreen);
	}
	
	public void endEditor() {
		this.isDemoGame = true;
		this.setScreen(menuScreen);
	}
	
	public void newGame() {
		this.setLife(3);
		BlockGame.setHighscore(0);
		if(debug) {
			levelCharArray = levelStorage.debugLevel.getLevelData().toCharArray();
		} else {
			levelStorage.setCurrentLevel(1);
			levelCharArray = levelStorage.getCurrentLevel().getLevelData().toCharArray();
		}
		
		if(gameOver) {
			gameOver = false;
		}	
	}
	
	public void nextLevel() {
		this.addLife(1);
		if(debug) {
			levelCharArray = levelStorage.debugLevel.getLevelData().toCharArray();
		} else {
			levelStorage.nextLevel();
			levelCharArray = levelStorage.getCurrentLevel().getLevelData().toCharArray();
		}
	}
	public void gameOver() {
		this.removeLife(1);
		if(this.life < 1) {
			for(Entity e : entityManager.getEntityList()) {
				e.lockMovement(true);
			}
			this.setScreen(gameOverScreen);
			BlockGame.setHighscore(0);
		} else {
			entityManager.resetPaddle();
			entityManager.resetBall();
		}
		gameOver = false;
	}
	
	public void backToMenu() {
		this.isDemoGame = true;
		levelStorage.setCurrentLevel(0);
		levelCharArray = levelStorage.getCurrentLevel().getLevelData().toCharArray();
		this.createDemoGame();
	}

	private void createWorld() {
		world = new World(new Vector2(0,0), true);
		world.setContactListener(new EntityContactEventHandler(this));
		World.setVelocityThreshold(0);
	}
	
	private void createScreens() {
		gameScreen = new GameScreen(this);
		gameOverScreen = new GameOverScreen(this);
		menuScreen = new MainMenuScreen(this);
		levelEditorScreen = new LevelEditorScreen(this);
	}
	
	public void quitGame() {
		Gdx.app.exit();
	}

	/*
	 * Prepares the OrthographicCamera and sets it on the startPosition.
	 */
	private void prepareCamera() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = Gdx.graphics.getWidth()/2;
		camera.position.y = Gdx.graphics.getHeight()/2;
		camera.update();
	}
	
	/**
	 * Handles the key input from the player.
	 * 
	 * @author sirkarpfen
	 *
	 */
	private class KeyInputHandler extends InputAdapter {
		
		@Override
		public boolean keyDown(int keycode) {
			if(keycode == Input.Keys.SPACE) {
				Ball ball = (Ball)entityManager.getEntity(Ball.class);
				if(ball != null && !ball.hasStarted()) {
					ball.start();
				}
			} else if(keycode == Input.Keys.ESCAPE) {
				BlockGame.this.quitGame();
			}
			return true;
			
		}
		
		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			if(BlockGame.this.getScreen().equals(gameScreen) && button == Buttons.LEFT) {
				for(Entity e : entityManager.getEntities(Paddle.class)) {
					Paddle p = (Paddle)e;
					if(p.checkTouchDown(screenX, screenHeight-screenY)) {
						p.setTouched(true);
					}
				}
			} else if(BlockGame.this.getScreen().equals(gameOverScreen)) {
				gameOverScreen.checkButtonPressed(screenX, screenHeight-screenY);
			} else if(BlockGame.this.getScreen().equals(menuScreen)) {
				menuScreen.checkButtonPressed(screenX, screenHeight-screenY);
			}
			return true;
		}
		
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if(BlockGame.this.getScreen().equals(gameScreen) && button == Buttons.LEFT) {
				Paddle p = (Paddle)entityManager.getEntity(Paddle.class);
				if(p != null && p.isTouched()) {
					p.setTouched(false);
				}
			}
			return true;
		}
		
		@Override
		public boolean touchDragged (int screenX, int screenY, int pointer) {
			if(BlockGame.this.getScreen().equals(gameScreen)) {
				Paddle p = (Paddle)entityManager.getEntity(Paddle.class);
				if(p != null) {
					if(p.isTouched()) {
						p.setTouchX(screenX);
						Ball b = (Ball)entityManager.getEntity(Ball.class);
						if(b != null && !b.hasStarted()) {
							b.setTouchX(screenX);
						}
					}
					
					
				}
			}
			return true;
		}
		
		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			if(BlockGame.this.getScreen().equals(gameOverScreen)) {
				gameOverScreen.checkMouseHover(screenX, screenHeight-screenY);
			} else if(BlockGame.this.getScreen().equals(menuScreen)) {
				menuScreen.checkMouseHover(screenX, screenHeight-screenY);
			} else if(BlockGame.this.getScreen().equals(levelEditorScreen)) {
				levelEditorScreen.checkMouseHover(screenX, screenHeight-screenY);
			}
			return true;
		}
	}
}
