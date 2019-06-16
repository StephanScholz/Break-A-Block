package sirkarpfen.breakablock.entities;

import sirkarpfen.breakablock.entities.bodies.BodyFactory;
import sirkarpfen.breakablock.entities.enums.Directions;
import sirkarpfen.breakablock.main.BlockGame;
import sirkarpfen.breakablock.main.Constants;
import sirkarpfen.breakablock.storage.TextureStorage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Paddle extends Entity {
	
	private float startX, startY;
	
	private Directions direction = Directions.STOP;
	public void setDirection(Directions direction) { this.direction = direction; }
	public Directions getDirection() { return direction; }
	
	private boolean isTouched = false;
	public void setTouched(boolean touched) { isTouched = touched; }
	public boolean isTouched() { return isTouched; }
	
	private int touchX;

	private Rectangle rect;
	public void setTouchX(int touchX) { this.touchX = touchX; }
	
	public Paddle(float x, float y) {
		super();
		startX = x;
		startY = y;
		touchX = (int)(x*pixel_to_meter);
	}
	
	@Override
	public void create() {
		this.createBody(startX,startY);
	}
	
	@Override
	public void createBody(float x, float y) {
		this.width = TextureStorage.getInstance().getTexture("paddleBlu").getWidth()/pixel_to_meter;
		this.height = TextureStorage.getInstance().getTexture("paddleBlu").getHeight()/pixel_to_meter;
		
		body = BodyFactory.createBody(new Vector2(x,y), BodyType.KinematicBody);
		
		float[] vertices = new float[16];
		vertices[0] = -width/2;
		vertices[1] = -height/2+height/4;
		vertices[2] = -width/2+8/pixel_to_meter;
		vertices[3] = -height/2;
		vertices[4] = width/2-8/pixel_to_meter;
		vertices[5] = -height/2;
		vertices[6] = width/2;
		vertices[7] = -height/2+height/4;
		vertices[8] = width/2;
		vertices[9] = height/2-height/4;
		vertices[10] = width/2-8/pixel_to_meter;
		vertices[11] = height/2;
		vertices[12] = -width/2+8/pixel_to_meter;
		vertices[13] = height/2;
		vertices[14] = -width/2;
		vertices[15] = height/2-height/4;
		
		
		PolygonShape shape = BodyFactory.createPolygonShape(vertices, 0, 0);
		
		BodyFactory.createFixture(body, shape, new float[]{0f,0f,0f}, false, (short)-1);
		BodyFactory.createFixture(body, shape, new float[]{0, 0, 0}, true, (short)0);
		
		body.setFixedRotation(true);
		body.setUserData(this);
	}

	@Override
	public void render(SpriteBatch spriteBatch, float delta) {
		spriteBatch.begin();
		spriteBatch.draw(
				TextureStorage.getInstance().getTexture("paddleBlu"), 
				(body.getPosition().x-width/2)*pixel_to_meter-delta, 
				(body.getPosition().y-height/2)*pixel_to_meter-delta);
		/*if(rect != null) {
			ShapeRenderer r = new ShapeRenderer();
			r.begin(ShapeType.Line);
			r.box(rect.x, rect.y, 0, rect.width, rect.height, 0);
			r.end();
		}*/
		spriteBatch.end();
	}
	
	public void move(float delta) {
		if(!this.isTouched()) return;
		if(touchX+width/2*pixel_to_meter >= Constants.MAP_WIDTH-Constants.MAP_EDGE) {
			body.setTransform((Constants.MAP_WIDTH-Constants.MAP_EDGE)/pixel_to_meter-width/2-1/pixel_to_meter, body.getPosition().y, 0);
			return;
		} else if(touchX-width/2*pixel_to_meter <= Constants.MAP_EDGE){
			body.setTransform(Constants.MAP_EDGE/pixel_to_meter+width/2+1/pixel_to_meter, body.getPosition().y, 0);
			return;
		} else {
			body.setTransform(touchX/pixel_to_meter, body.getPosition().y, 0);
		}
		/*if(this.isMovementLocked()) {
			this.direction = Directions.STOP;
		}
		switch(direction) {
		
		case LEFT:
			if((body.getPosition().x-width/2)*pixel_to_meter <= Constants.MAP_EDGE) {
				body.setLinearVelocity(0,0);
			} else {
				body.setLinearVelocity(new Vector2(-Constants.PADDLE_VELOCITY,0));
			}
			break;
		case RIGHT:
			if((body.getPosition().x+width/2)*pixel_to_meter >= Gdx.graphics.getWidth()-Constants.MAP_EDGE) {
				body.setLinearVelocity(0,0);
			} else {
				body.setLinearVelocity(new Vector2(Constants.PADDLE_VELOCITY,0));
			}
			break;
		case STOP:
			body.setLinearVelocity(new Vector2(0,0));
			break;
		}*/
	}
	
	public boolean checkTouchDown(int screenX, int screenY) {
		rect = new Rectangle(
				(body.getPosition().x-width/2)*pixel_to_meter,
				(body.getPosition().y-height/2)*pixel_to_meter,
				width*pixel_to_meter,
				height*pixel_to_meter);
		Rectangle mouseRect = new Rectangle(screenX, screenY, 1, 1);
		if(rect.contains(mouseRect)) {
			return true;
		}
		return false;
	}
	
	public void resetToStartPosition() {
		body.setTransform(startX, startY, 0);
		this.setDirection(Directions.STOP);
		this.lockMovement(false);
	}
	
	public void destroy() {
		World world = BlockGame.getWorld();
		world.destroyBody(body);
	}
	
}
