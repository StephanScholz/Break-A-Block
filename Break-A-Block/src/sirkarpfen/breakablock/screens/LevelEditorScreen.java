package sirkarpfen.breakablock.screens;

import sirkarpfen.breakablock.levels.levelEditor.LevelEditor;
import sirkarpfen.breakablock.main.BlockGame;
import sirkarpfen.breakablock.main.Constants;
import sirkarpfen.breakablock.storage.TextureStorage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class LevelEditorScreen extends ScreenAdapter {

	private LevelEditor levelEditor;
	private int menuWidth;
	private Texture background;
	public void setBackground(Texture background) { this.background = background; }
	private Texture toolbar;
	private Rectangle editableField;
	private Rectangle mouseRect;
	float[] colorArray = new float[3];
	private TextureStorage textureStorage;
	private ShapeRenderer shapeRenderer;
	private Rectangle drawRect;

	public LevelEditorScreen(BlockGame game) {
		super(game);
		textureStorage = TextureStorage.getInstance();
		background = TextureStorage.getInstance().getTexture("background");
		toolbar = TextureStorage.getInstance().getTexture("toolbar");
		shapeRenderer = new ShapeRenderer();
		drawRect = new Rectangle(0,0,1,1);
		this.menuWidth = toolbar.getWidth();
		levelEditor = new LevelEditor(this);
		this.createRects();
		colorArray[0] = 0;
		colorArray[1] = 0;
		colorArray[2] = 0;
	}
	
	private void createRects() {
		editableField = new Rectangle(32, 256, Constants.MAP_WIDTH-64, 320);
		mouseRect = new Rectangle(0,0,1,1);
	}

	@Override
	public void show() {
		Gdx.graphics.setDisplayMode(Constants.MAP_WIDTH+menuWidth, Constants.MAP_HEIGHT, false);
		game.getCamera().setToOrtho(false, Constants.MAP_WIDTH+menuWidth, Constants.MAP_HEIGHT);
		shapeRenderer.setProjectionMatrix(game.getCamera().combined);
		spriteBatch.setProjectionMatrix(game.getCamera().combined);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		spriteBatch.begin();
		Color oldColor = spriteBatch.getColor();
		spriteBatch.draw(background, 0, 0, Constants.MAP_WIDTH, background.getHeight());
		spriteBatch.draw(toolbar, background.getWidth(), 0, toolbar.getWidth(), toolbar.getHeight());
		spriteBatch.setColor(colorArray[0], colorArray[1], colorArray[2], 0.5F);
		spriteBatch.draw(TextureStorage.getInstance().getTexture("toolbar"), 
				32, 
				256,
				Constants.MAP_WIDTH-64,
				320);
		spriteBatch.setColor(oldColor);
		spriteBatch.draw(textureStorage.getTexture("brick_blue"), 
				Constants.MAP_WIDTH+64, 
				Constants.MAP_HEIGHT-64);
		spriteBatch.end();
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.box(drawRect.x, drawRect.y, 0, drawRect.width, drawRect.height, 0);
		shapeRenderer.end();
		}
	
	public void checkMouseHover(int screenX, int screenY) {
		mouseRect.x = screenX;
		mouseRect.y = screenY;
		if(editableField.overlaps(mouseRect)) {
			
		}
		for(int i = 0; i < levelEditor.getBrickArray().length; i++) {
			if(levelEditor.getBrickArray()[i].overlaps(mouseRect)) {
				drawRect = levelEditor.getBrickArray()[i];
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		//super.resize(width, height);
	}
	
	@Override
	public void hide() {
		
	}
	
}
