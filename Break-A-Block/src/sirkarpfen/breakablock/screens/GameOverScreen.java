package sirkarpfen.breakablock.screens;

import sirkarpfen.breakablock.entities.Entity;
import sirkarpfen.breakablock.main.BlockGame;
import sirkarpfen.breakablock.storage.TextureStorage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class GameOverScreen extends ScreenAdapter {
	
	private Animation animation;
	private float stateTime;
	private int textureWidth;
	private int textureHeight;
	private Color fontColor;
	private BitmapFont retryFont;
	private BitmapFont quitFont;
	private Rectangle quitRect;
	private Rectangle retryRect;
	private Rectangle mouseRect;
	private boolean backToMenu = false;
	private boolean newGame = false;
	
	public GameOverScreen(BlockGame game) {
		super(game);
		Texture spriteSheet = TextureStorage.getInstance().getTexture("game_over");
		spriteSheet.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion[][] temp;
		temp = TextureRegion.split(spriteSheet, spriteSheet.getWidth(), spriteSheet.getHeight()/5);
		this.textureWidth = spriteSheet.getWidth();
		this.textureHeight = spriteSheet.getHeight()/5;
		TextureRegion[] textureRegion = new TextureRegion[8];
		textureRegion[0] = temp[0][0];
		textureRegion[1] = temp[1][0];
		textureRegion[2] = temp[2][0];
		textureRegion[3] = temp[3][0];
		textureRegion[4] = temp[4][0];
		textureRegion[5] = temp[3][0];
		textureRegion[6] = temp[2][0];
		textureRegion[7] = temp[1][0];
		animation = new Animation(0.08f, textureRegion);
		
		retryFont = new BitmapFont();
		quitFont = new BitmapFont();
		fontColor = new Color(0.87f,0.95f,0.255f, 1F);
		retryFont.setColor(fontColor);
		retryFont.setScale(2F);
		quitFont.setColor(fontColor);
		quitFont.setScale(2F);
		retryRect = new Rectangle(screenWidth/2-retryFont.getBounds("PLAY AGAIN").width/2,
				screenHeight/2,
				retryFont.getBounds("PLAY AGAIN").width,
				retryFont.getBounds("PLAY AGAIN").height);
		quitRect = new Rectangle(screenWidth/2-quitFont.getBounds("BACK TO MENU").width/2,
				screenHeight/2-screenHeight/4+screenHeight/8,
				quitFont.getBounds("BACK TO MENU").width,
				quitFont.getBounds("BACK TO MENU").height);
		mouseRect = new Rectangle(0,0,1,1);
	}
	
	@Override
	public void render(float delta) {
		System.out.println(world.getBodyCount());
		super.render(delta);
		
		if(this.backToMenu) {
			//game.removeBodies();
			game.backToMenu();
			this.backToMenu = false;
		} else if(this.newGame) {
			game.newGame();
			entityManager.removeBricks();
			for(Entity e : entityManager.getEntityList()) {
				if(e.isFlaggedForDestruction()) {
					e.destroy();
				}
			}
			this.newGame = false;
		}
		
		if(world.getBodyCount() == 6) {
			entityManager.createBricks();
			entityManager.resetPaddle();
			entityManager.resetBall();
			game.setScreen(game.getGameScreen());
		}
		
		stateTime += Gdx.graphics.getDeltaTime();
		spriteBatch.begin();
		spriteBatch.draw(
				animation.getKeyFrame(stateTime, true), 
				(screenWidth/2)-(textureWidth/2), 
				(screenHeight-textureHeight*3));
		
		retryFont.draw(spriteBatch, 
				"PLAY AGAIN", 
				retryRect.x, 
				retryRect.y+retryRect.height);
		
		quitFont.draw(spriteBatch, 
				"BACK TO MENU", 
				quitRect.x, 
				quitRect.y+quitRect.height);
		spriteBatch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void checkButtonPressed(int screenX, int screenY) {
		mouseRect.x = screenX;
		mouseRect.y = screenY;
		if(retryRect.overlaps(mouseRect)) {
			this.newGame = true;
		} else if(quitRect.overlaps(mouseRect)) {
			this.backToMenu  = true;
		}
	}
	
	public void checkMouseHover(int screenX, int screenY) {
		mouseRect.x = screenX;
		mouseRect.y = screenY;
		if(retryRect.overlaps(mouseRect)) {
			retryFont.setColor(Color.WHITE);
		} else if(quitRect.overlaps(mouseRect)) {
			quitFont.setColor(Color.WHITE);
		} else {
			retryFont.setColor(fontColor);
			quitFont.setColor(fontColor);
		}
	}
}
