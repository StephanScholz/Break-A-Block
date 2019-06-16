package sirkarpfen.breakablock.entities;

import sirkarpfen.breakablock.entities.manager.EntityManager;
import sirkarpfen.breakablock.main.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * The base class of all entities.
 * 
 * @author sirkarpfen
 *
 */
public abstract class Entity {
	
	protected float pixel_to_meter;
	
	protected float startX, startY;
	
	protected boolean lock = false;
	public void lockMovement(boolean lock) { this.lock = lock; }
	public boolean isMovementLocked() { return lock; }
	
	protected float width, height;
	public float getWidth() { return width; }
	public float getHeight() { return height; }
	
	protected Body body;
	public Body getBody() { return body; }
	
	protected boolean flaggedForDestruction;
	public void flagForDestruction() { flaggedForDestruction = true; }
	public boolean isFlaggedForDestruction() { return flaggedForDestruction; }
	
	public boolean animateDestruction = false;
	
	private boolean visible = true;
	public boolean isVisible() { return visible; }
	public void setVisible(boolean visible) { this.visible = visible; }
	
	protected EntityManager entityManager;
	
	protected Entity() {
		pixel_to_meter = Constants.PIXEL_TO_METER;
		this.entityManager = EntityManager.getInstance();
	}
	
	/**
	 * Renders the Entity using the SpriteBatch provided.
	 * 
	 * @param SpriteBatch The SpriteBatch to use for rendering.
	 */
	public abstract void render(SpriteBatch spriteBatch, float delta);
	public abstract void move(float delta);
	protected abstract void createBody(float x, float y);
	public abstract void create();
	public abstract void destroy();
	public Vector2 getPosition() { return body.getPosition(); }
	
}
