package sirkarpfen.breakablock.screens;

import java.util.concurrent.CopyOnWriteArrayList;

import sirkarpfen.breakablock.entities.Brick;
import sirkarpfen.breakablock.entities.Entity;
import sirkarpfen.breakablock.main.BlockGame;
import sirkarpfen.breakablock.main.Constants;
import sirkarpfen.breakablock.storage.TextureStorage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import sirkarpfen.breakout.entities.BrickStorage;

/**
 * The base game class. Handles rendering/disposal/logic updates and more.
 * 
 * @author sirkarpfen
 * @see com.github.sirkarpfen.main.Main
 */
public class GameScreen extends ScreenAdapter {
	
	private OrthographicCamera camera;

	private BitmapFont font;

	private int winningAnimationTick;

	private boolean wonLevel = false;

	private boolean stepWorld = true;

	public void setWinningScreenTick(int value) { this.winningAnimationTick = value; }
	public int getWinningScreenTick() { return winningAnimationTick; }
	
	public GameScreen(BlockGame game) {
		super(game);
		font = new BitmapFont();
		this.camera = game.getCamera();
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public void show() {
	}
	
	/**
	 * All rendering processes and methods, are called within this method.
	 * it acts as a hook into the different rendering methods, used by all
	 * Entity classes, and the map renderer. A SpriteBatch object is passed on
	 * if a Texture or sprite needs to be drawn.
	 */
	@Override
	public void render(float delta) {
		
		super.render(delta);
		
		// ********** Start rendering area. **********
		spriteBatch.begin();
		spriteBatch.draw(TextureStorage.getInstance().getTexture("background"), 0, 0);
		spriteBatch.end();
		
		this.createWorldBodies();
		
		if(game.gameOver) {
			game.gameOver();
		} else {
			if(!wonLevel && !entityManager.contains(Brick.class)) {
				this.setWinningScreenTick(200);
				wonLevel = true;
			}
		}
		
		world.step(1f/60f, 10, 8);
		
		this.drawMenu();
		if(this.getWinningScreenTick() > 0) {
			if(this.getWinningScreenTick() == 1) {
				entityManager.removeAllEntities(true);
				game.nextLevel();
				this.setWinningScreenTick(0);
			}
			this.setWinningScreenTick(getWinningScreenTick() - 1);
			Texture texture = new Texture(Gdx.files.internal("data/sprites/youwon.gif"));
			spriteBatch.begin();
			spriteBatch.draw(
					texture, Gdx.graphics.getWidth()/2-texture.getWidth()/2, Gdx.graphics.getHeight()/2-texture.getHeight()/2);
			spriteBatch.end();
		}
		
		if(wonLevel && world.getBodyCount() == 0) {
			this.stepWorld = false;
			entityManager.createBricks();
			entityManager.createEdges();
			entityManager.createPaddle();
			entityManager.createBall();
			wonLevel = false;
			stepWorld = true;
		}
		
		this.renderWorldBodies(delta);
		
		this.destroyWorldBodies();
		
		/*
		 * Draw this last, so we can see the collision boundaries on top of the
		 * sprites and map.
		 */
		debugRenderer.render(BlockGame.getWorld(), camera.combined);
	}

	private void drawMenu() {
		spriteBatch.begin();
		
		font.setScale(1.1F);
		font.setColor(Color.WHITE);
		// draw FPS
		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 
				64,
				Constants.MAP_HEIGHT-28);
		
		// draw Highscore
		font.draw(spriteBatch, "Score: " + BlockGame.getHighscore(), 
				(Constants.MAP_WIDTH/2)-(font.getBounds("Score: " + BlockGame.getHighscore()).width/2), 
				Constants.MAP_HEIGHT-28);
		
		font.draw(spriteBatch, "Life: " + game.getLife(),
				Constants.MAP_WIDTH-112, 
				Constants.MAP_HEIGHT-28);
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

	/*
	 * Renders the bodies for this world.
	 */
	private void renderWorldBodies(float delta) {
		CopyOnWriteArrayList<Entity> entityList = entityManager.getEntityList();
		
		for(Entity e : entityList) {
			if(wonLevel)
				e.lockMovement(true);
			if(!e.isMovementLocked() && e.isVisible())
				e.move(delta);
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
	
	@Override
	public void hide() {
	}
}
