package sirkarpfen.breakablock.screens;

import java.util.concurrent.CopyOnWriteArrayList;

import sirkarpfen.breakablock.entities.Entity;
import sirkarpfen.breakablock.main.BlockGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
/**
 * Menu screen class.
 * @author sirka
 *
 */
public class MainMenuScreen extends ScreenAdapter {

	private OrthographicCamera camera;
	
	private Texture fog;
	
	private BitmapFont quitGameFont;
	private BitmapFont startGameFont;
	private BitmapFont levelEditorFont;
	
	private Rectangle levelEditorRect;
	private Rectangle startGameRect;
	private Rectangle quitGameRect;
	private Rectangle mouseRect;
	
	private Color fontColor;
	
	private boolean gameStarted = false;
	private boolean menuStarted = false;
	private boolean demoGameStarted = false;
	
	private Texture background;
	
	private float opacity;

	public MainMenuScreen(BlockGame game) {
		super(game);
		this.camera = game.getCamera();
		// loads the fog for gradual shading in the background of the menu
		fog = new Texture(Gdx.files.internal("data/tilesets/fog.png"));
		// loads the "demo" map for the background
		background = new Texture(Gdx.files.internal("data/maps/breakout_demo.png"));
		this.opacity = 0f;

		this.createMenuFonts();
		
		this.createCollisionRect();
		
	}
	
	// This is where the show starts. All variables are reset
	// and the menu creates all necessary entities for the background action.
	@Override
	public void show() {
		this.opacity = 0;
		this.menuStarted = false;
		this.gameStarted = false;
		this.demoGameStarted = false;
		entityManager.createEntities();
	}
	
	/**
	 * Creates all fonts used in the menu entries
	 */
	private void createMenuFonts() {
		this.fontColor = Color.YELLOW;
		
		startGameFont = new BitmapFont();
		levelEditorFont = new BitmapFont();
		quitGameFont = new BitmapFont();
		
		startGameFont.setColor(fontColor);
		levelEditorFont.setColor(fontColor);
		quitGameFont.setColor(fontColor);
		
		startGameFont.setScale(2);
		levelEditorFont.setScale(2);
		quitGameFont.setScale(2);
	}
	
	/**
	 * Creates the collision rectangles for all menu entries
	 */
	private void createCollisionRect() {
		startGameRect = new Rectangle(screenWidth/2-startGameFont.getBounds("START GAME").width/2,
				screenHeight/2,
				startGameFont.getBounds("START GAME").width,
				startGameFont.getBounds("START GAME").height);
		levelEditorRect = new Rectangle(screenWidth/2-levelEditorFont.getBounds("EDITOR").width/2,
				screenHeight/2-screenHeight/4+screenHeight/8,
				levelEditorFont.getBounds("EDITOR").width,
				levelEditorFont.getBounds("EDITOR").height);
		quitGameRect = new Rectangle(screenWidth/2-quitGameFont.getBounds("QUIT").width/2,
				screenHeight/4,
				quitGameFont.getBounds("QUIT").width,
				quitGameFont.getBounds("QUIT").height);
		mouseRect = new Rectangle(0, 0, 1, 1);
	}
	
	@Override
	public void render(float delta) {
		//System.out.println(entityManager.getEntityList().size());
		super.render(delta);
		
		spriteBatch.begin();
		spriteBatch.draw(background, 0, 0);
		spriteBatch.end();
		
		this.createWorldBodies();
		
		world.step(1f/60f, 10, 8);
		
		if(gameStarted) {
			game.endDemoGame();
			game.newGame();
			entityManager.removeAllEntities(false);
			entityManager.createPaddle();
			entityManager.createBall();
			entityManager.createEdges();
			entityManager.createBricks();
			gameStarted = false;
			game.setScreen(game.getGameScreen());
		}
		
		this.renderWorldBodies(delta);
		
		spriteBatch.begin();
		Color c = spriteBatch.getColor();
		spriteBatch.setColor(c.r,c.g,c.b,opacity);
		spriteBatch.draw(
				fog, 
				0, 
				0);
		spriteBatch.setColor(c.r,c.g,c.b,1f);
		spriteBatch.end();
		
		if(opacity <= 0.9f) {
			opacity += 0.01f;
		} else {
			this.menuStarted = true;
		}
		
		if(menuStarted) {
			if(!demoGameStarted) {
				game.startDemoGame();
				demoGameStarted = true;
			}
			this.drawMenu();
		}
		
		this.destroyWorldBodies();
		
		debugRenderer.render(BlockGame.getWorld(), camera.combined);
		
	}
	
	private void drawMenu() {
		spriteBatch.begin();
		startGameFont.draw(
				spriteBatch, 
				"START GAME", 
				startGameRect.x, 
				startGameRect.y+startGameRect.height);
		levelEditorFont.draw(
				spriteBatch, 
				"EDITOR", 
				levelEditorRect.x, 
				levelEditorRect.y+levelEditorRect.height);
		quitGameFont.draw(
				spriteBatch, 
				"QUIT", 
				quitGameRect.x, 
				quitGameRect.y+quitGameRect.height);
		spriteBatch.end();
	}
	
	private void createWorldBodies() {
		CopyOnWriteArrayList<Entity> createList = entityManager.getCreateList();
		
		for(Entity e : createList) {
			e.create();
			createList.remove(e);
			entityManager.addEntity(e);
		}
	}
	
	private void renderWorldBodies(float delta) {
		CopyOnWriteArrayList<Entity> entityList = entityManager.getEntityList();
		
		for(Entity e : entityList) {
			if(!e.isMovementLocked() && e.isVisible()) {
    			e.move(delta);
    		}
			if(e.isVisible())
				e.render(spriteBatch, delta);
		}
	}
	
	private void destroyWorldBodies() {
		CopyOnWriteArrayList<Entity> destroyList = entityManager.getDestroyList();
		
		for(Entity e : destroyList) {
			e.destroy();
			destroyList.remove(e);
			entityManager.removeEntity(e);
		}
	}
	
	public void checkButtonPressed(int screenX, int screenY) {
		mouseRect.x = screenX;
		mouseRect.y = screenY;
		if(startGameRect.overlaps(mouseRect)) {
			if(!gameStarted) {
				gameStarted  = true;
			}
		} else if(levelEditorRect.overlaps(mouseRect)) {
			game.startEditor();
		} else if(quitGameRect.overlaps(mouseRect)) {
			game.quitGame();
		}
	}
	
	public void checkMouseHover(int screenX, int screenY) {
		mouseRect.x = screenX;
		mouseRect.y = screenY;
		if(startGameRect.overlaps(mouseRect)) {
			startGameFont.setColor(Color.WHITE);
		} else if(levelEditorRect.overlaps(mouseRect)) {
			levelEditorFont.setColor(Color.WHITE);
		} else if(quitGameRect.overlaps(mouseRect)) {
			quitGameFont.setColor(Color.WHITE);
		} else {
			startGameFont.setColor(fontColor);
			levelEditorFont.setColor(fontColor);
			quitGameFont.setColor(fontColor);
		}
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@Override
	public void hide() {
	}

}
