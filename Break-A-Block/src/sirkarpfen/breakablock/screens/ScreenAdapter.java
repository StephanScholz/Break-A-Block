package sirkarpfen.breakablock.screens;

import sirkarpfen.breakablock.entities.manager.EntityManager;
import sirkarpfen.breakablock.main.BlockGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class ScreenAdapter implements Screen {
	
	protected SpriteBatch spriteBatch;
	protected BlockGame game;
	protected Box2DDebugRenderer debugRenderer;
	private long lastRender;
	public int screenWidth, screenHeight;
	protected EntityManager entityManager;
	protected World world;

	protected ScreenAdapter(BlockGame game) {
		this.game = game;
		lastRender = System.nanoTime();
		this.screenWidth = game.getScreenSize().width;
		this.screenHeight = game.getScreenSize().height;
		this.spriteBatch = new SpriteBatch();
		this.debugRenderer = new Box2DDebugRenderer();
		world = BlockGame.getWorld();
		entityManager = EntityManager.getInstance();
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void render(float delta) {
		//System.out.println(world.getBodyCount());
		long now = System.nanoTime();
		Gdx.gl.glClearColor(0.55f, 0.55f, 0.55f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Limiting FPS. On test-machine made 50-55 FPS.
		now = System.nanoTime();
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}
		
		lastRender = now;
	}

	@Override
	public void resize(int width, int height) {
		//this.screenWidth = width;
		//this.screenHeight = height;
	}

	@Override
	public void resume() {

	}

	@Override
	public void show() {

	}

}
