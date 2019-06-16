package sirkarpfen.breakablock.entities;

import sirkarpfen.breakablock.entities.bodies.BodyFactory;
import sirkarpfen.breakablock.main.BlockGame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Edge extends Entity {
	
	private boolean isGroundEdge = false;
	public boolean isGroundEdge() { return isGroundEdge; }
	public void setGroundEdge(boolean isGroundEdge) { this.isGroundEdge = isGroundEdge; }

	public Edge(float x, float y, float width, float height) {
		super();
		startX = x;
		startY = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void create() {
		this.createBody(startX, startY);
	}
	
	@Override
	public void render(SpriteBatch spriteBatch, float delta) {
		
	}

	@Override
	public void move(float delta) {
		
	}

	@Override
	protected void createBody(float x, float y) {
		body = BodyFactory.createBody(new Vector2(x,y), BodyType.StaticBody);
		BodyFactory.createFixture(
				body, BodyFactory.createBoxShape(width, height), new float[] {10f,0f,0f}, false, (short)1);
		body.setUserData(this);
	}

	@Override
	public void destroy() {
		BlockGame.getWorld().destroyBody(body);
	}

}
