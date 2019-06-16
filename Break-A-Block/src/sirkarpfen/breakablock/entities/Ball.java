package sirkarpfen.breakablock.entities;

import java.util.Random;

import sirkarpfen.breakablock.entities.bodies.BodyFactory;
import sirkarpfen.breakablock.main.BlockGame;
import sirkarpfen.breakablock.main.Constants;
import sirkarpfen.breakablock.storage.TextureStorage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Ball extends Entity {
	
	private Sprite ballGrey, ballBurning;
	
	private float radius;
	public float getRadius() { return radius; }
	
	private boolean started = false;
	public void setStarted(boolean started) { this.started = started; }
	public boolean hasStarted() { return started; }
	
	private int touchX;
	public void setTouchX(int touchX) { this.touchX = touchX; }
	
	public Ball(float x, float y, float scale) {
		super();
		startX = x;
		startY = y;
		this.radius = TextureStorage.getInstance().getTexture("ballGrey").getWidth()/2/pixel_to_meter*scale;
		touchX = (int)(x*pixel_to_meter);
	}
	
	public Ball(float x, float y, float scale, float radius) {
		super();
		startX = x;
		startY = y;
		this.radius = radius*scale;
	}
	
	@Override
	public void create() {
		this.createBody(startX,startY);
	}
	
	@Override
	public void createBody(float x, float y) {
		ballGrey = new Sprite(TextureStorage.getInstance().getTexture("ballGrey"));
		body = BodyFactory.createBody(new Vector2(x, y), BodyType.DynamicBody);
		BodyFactory.createFixture(body, BodyFactory.createCircleShape(radius), new float[] {0f,0f,1f}, false, (short)1);
		body.setUserData(this);
	}
	
	@Override
	public void render(SpriteBatch spriteBatch, float delta) {
		spriteBatch.begin();
		
		ballGrey.setX((body.getPosition().x-radius)*pixel_to_meter);
		ballGrey.setY((body.getPosition().y-radius)*pixel_to_meter);
		ballGrey.draw(spriteBatch);
		spriteBatch.end();
		
	}
	
	public void move(float delta) {
		if(Gdx.input.justTouched() && !this.hasStarted()) {
			this.start();
		}
		if(!started && !BlockGame.getInstance().isDemoGame()) {
			if(touchX+52 >= Constants.MAP_WIDTH-Constants.MAP_EDGE) {
				body.setTransform((Constants.MAP_WIDTH-Constants.MAP_EDGE-53)/pixel_to_meter, body.getPosition().y, 0);
				return;
			} else if(touchX-52 <= Constants.MAP_EDGE){
				body.setTransform((Constants.MAP_EDGE+53)/pixel_to_meter, body.getPosition().y, 0);
				return;
			} else {
				body.setTransform(touchX/pixel_to_meter, body.getPosition().y, 0);
			}
			/*switch (direction) {
			
			case LEFT:
				if((body.getPosition().x-paddle.getWidth()/2)*pixel_to_meter <= Constants.MAP_EDGE) {
					body.setLinearVelocity(0,0);
				} else {
					body.setLinearVelocity(-Constants.PADDLE_VELOCITY,0);
				}
				break;
			case RIGHT:
				if((body.getPosition().x+paddle.getWidth()/2)*pixel_to_meter  >= Gdx.graphics.getWidth()-Constants.MAP_EDGE) {
					body.setLinearVelocity(0,0);
				} else {
					body.setLinearVelocity(Constants.PADDLE_VELOCITY,0);
				}
				break;
			case STOP:
				body.setLinearVelocity(0,0);
				break;
			}*/
		}
	}
	
	@Override
	public void lockMovement(boolean lock) {
		super.lockMovement(lock);
		if(lock)
			body.setLinearVelocity(0,0);
	}
	
	public void start() {
		body.setLinearVelocity(0,0);
		Random generator = new Random();
		if(generator.nextBoolean()) {
			body.applyLinearImpulse(new Vector2(-10f,10f), body.getPosition());
		} else {
			body.applyLinearImpulse(new Vector2(10f,10f), body.getPosition());
		}
		this.setStarted(true);
	}
	
	public void resetToStartPosition() {
		body.setTransform(startX, startY, 0);
		this.setStarted(false);
		this.lockMovement(false);
	}
	
	public void destroy() {
		World world = BlockGame.getWorld();
		world.destroyBody(body);
		this.setStarted(false);
	}
	
}
