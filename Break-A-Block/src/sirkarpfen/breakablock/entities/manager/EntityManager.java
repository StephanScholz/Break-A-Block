package sirkarpfen.breakablock.entities.manager;

import static sirkarpfen.breakablock.main.Constants.MAP_EDGE;
import static sirkarpfen.breakablock.main.Constants.MAP_HEIGHT;
import static sirkarpfen.breakablock.main.Constants.MAP_WIDTH;
import static sirkarpfen.breakablock.main.Constants.PIXEL_TO_METER;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import sirkarpfen.breakablock.entities.Ball;
import sirkarpfen.breakablock.entities.Brick;
import sirkarpfen.breakablock.entities.Edge;
import sirkarpfen.breakablock.entities.Entity;
import sirkarpfen.breakablock.entities.Item;
import sirkarpfen.breakablock.entities.Paddle;
import sirkarpfen.breakablock.entities.enums.ItemProperty;
import sirkarpfen.breakablock.main.BlockGame;
import sirkarpfen.breakablock.main.Constants;

import com.badlogic.gdx.Gdx;

/**
 * This class manages all entities, that are displayed on the current screen.
 * @author sirka
 *
 */

/* 
 * MAJOR BUGS HERE AND IN ALL CALLED CLASSES. Collision boundaries are not working 
 * and needs improvement on the ball-physics....
 */
public class EntityManager {
	
	private static EntityManager instance;
	public static EntityManager getInstance() {
		if(instance == null) {
			instance = new EntityManager();
		}
		return instance;
	}
	
	private CopyOnWriteArrayList<Entity> entityList;
	private CopyOnWriteArrayList<Entity> createList;
	public CopyOnWriteArrayList<Entity> getCreateList() { return createList; }
	public void addToCreateList(Entity e) { createList.add(e); }
	private CopyOnWriteArrayList<Entity> destroyList;
	public CopyOnWriteArrayList<Entity> getDestroyList() { return destroyList; }
	public void addToDestroyList(Entity e) { destroyList.add(e); }

	private BlockGame game;
	
	private EntityManager() {
		entityList = new CopyOnWriteArrayList<Entity>();
		createList = new CopyOnWriteArrayList<Entity>();
		destroyList = new CopyOnWriteArrayList<Entity>();
		this.game = BlockGame.getInstance();
	}
	
	/**
	 * @return the list of all entities
	 */
	public CopyOnWriteArrayList<Entity> getEntityList() { return entityList; }
	
	/**
	 * Adds a single entity to the list
	 * @param e the entity to add
	 */
	public void addEntity(Entity e) {
		entityList.add(e);
	}
	
	/**
	 * Gets an entity from the list, specified by the given index
	 * @param index the index of the entity to get
	 * @return the entity specified by the index in the list
	 */
	public Entity getEntity(int index) { 
		return entityList.get(index); 
	}
	
	/**
	 * Gets an Entity from the list, specified by the given Class object.
	 * This method returns the first Entity found in the list. If there are more Entities in the list
	 * one should check it before with {@link EntityManager#containsSingleEntity(Class)}
	 * or {@link EntityManager#contains(Class)}
	 * If no Entity is found then null will be returned;
	 * @param c the Class object
	 * @return the entity specified by the Class object, or null if nothing was found
	 */
	public Entity getEntity(Class<? extends Entity> c) {
		for(Entity e : entityList) {
			if(c.isInstance(e)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * @return true if the list is empty, false if not
	 */
	public boolean isEmpty() { return entityList.isEmpty(); }
	
	/**
	 * Removes the specified Entity from the list.
	 * @param e the entity to remove
	 */
	public void removeEntity(Entity e) { entityList.remove(e); }
	
	/**
	 * Removes the Entities in the list who are an instance of the given Class parameter
	 * @param c the Class object
	 */
	public void removeEntities(Class<? extends Entity> c) {
		for(Entity e : entityList) {
			if(c.isInstance(e)) {
				this.destroyList.add(e);
			}
		}
	}
	
	/**
	 * Removes all Entities in the list
	 */
	public void removeAllEntities(boolean destroy) {
		for(Entity e : entityList) {
			if(destroy) {
				e.animateDestruction = false;
			}
			this.destroyList.add(e);
		}
	}
	
	/**
	 * Checks if the list contains an Entity that is an instance of the given Class object.
	 * This method returns as soon as it finds a matching Entity in the list.
	 * @param c the Class object
	 * @return true if a match was found, false if not
	 */
	public boolean contains(Class<? extends Entity> c) {
		for(Entity e : entityList) {
			if(c.isInstance(e)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the list contains 1 and just 1 Entity that is an instance of the given Class object.
	 * @param c the Class object
	 * @return true if a just 1 match was found, false if less or more than 1 match was found.
	 */
	public boolean containsSingleEntity(Class<? extends Entity> c) {
		int counter = 0;
		for(Entity e : entityList) {
			if(c.isInstance(e)) {
				counter++;
			}
		}
		if(counter == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets all Entities that are instances of the given Class object.
	 * @param c the Class object
	 * @return an Array with all Entities that are instances of the Class object.
	 */
	public ArrayList<? extends Entity> getEntities(Class<? extends Entity> c) {
		if(c == Ball.class) {
			ArrayList<Ball> returnList = new ArrayList<Ball>();
			for(Entity e : entityList) {
				if(e instanceof Ball) {
					returnList.add((Ball)e);
				}
			}
			return returnList;
		} else if(c == Paddle.class) {
			ArrayList<Paddle> returnList = new ArrayList<Paddle>();
			for(Entity e : entityList) {
				if(e instanceof Paddle) {
					returnList.add((Paddle)e);
				}
			}
			return returnList;
		} else if(c == Edge.class) {
			ArrayList<Edge>returnList = new ArrayList<Edge>();
			for(Entity e : entityList) {
				if(e instanceof Edge) {
					returnList.add((Edge)e);
				}
			}
			return returnList;
		} else if(c == Brick.class) {
			ArrayList<Brick> returnList = new ArrayList<Brick>();
			for(Entity e : entityList) {
				if(e instanceof Brick) {
					returnList.add((Brick)e);
				}
			}
			return returnList;
		} else if(c == Item.class) {
			ArrayList<Item> returnList = new ArrayList<Item>();
			for(Entity e : entityList) {
				if(e instanceof Item) {
					returnList.add((Item)e);
				}
			}
			return returnList;
		}
		return null;
	}
	
	//-------------------------------------------------//
	//----------------Entity creation------------------//
	//-------------------------------------------------//
	
	/**
	 * Either resets the Paddle to its starting position, or if there are more than 1 paddles
	 * removes them and initializes a new Paddle.
	 */
	public void resetPaddle() {
		if(this.containsSingleEntity(Paddle.class)) {
			Paddle paddle = (Paddle)this.getEntity(Paddle.class);
			paddle.resetToStartPosition();
		} else {
			if(!game.isDemoGame()) {
				this.removeEntities(Paddle.class);
				this.createList.add(new Paddle(Gdx.graphics.getWidth()/2/PIXEL_TO_METER, 48/PIXEL_TO_METER));
			}
		}
	}
	
	/**
	 * Either resets the Ball to its starting position, or if there are more than 1 balls
	 * removes them and initializes a new Ball.
	 */
	public void resetBall() {
		if(this.containsSingleEntity(Ball.class)) {
			Ball ball = (Ball)this.getEntity(Ball.class);
			ball.resetToStartPosition();
		} else {
			this.removeEntities(Ball.class);
			if(game.isDemoGame()) {
				Ball ball1 = new Ball((Constants.MAP_WIDTH-Constants.MAP_EDGE)/PIXEL_TO_METER, Constants.MAP_HEIGHT/2/PIXEL_TO_METER,1);
				Ball ball2 = new Ball(Constants.MAP_EDGE/PIXEL_TO_METER, Constants.MAP_HEIGHT/2/PIXEL_TO_METER,1);
				ball1.setVisible(false);
				ball2.setVisible(false);
				this.createList.add(ball1);
				this.createList.add(ball2);
			} else {
				this.createList.add(new Ball(Gdx.graphics.getWidth()/2/PIXEL_TO_METER, 72/PIXEL_TO_METER, 1));
			}
		}
	}
	
	public void removeBricks() {
		if(this.contains(Brick.class)) {
			for(Entity e : this.getEntities(Brick.class)) {
				e.animateDestruction = false;
				this.destroyList.add(e);
			}
		}
	}
	
	public void removeItems() {
		if(this.contains(Item.class)) {
			for(Entity e : this.getEntities(Item.class)) {
				e.animateDestruction = false;
				this.destroyList.add(e);
			}
		}
	}
	
	public void removeEdges() {
		if(this.contains(Edge.class)) {
			for(Entity e : this.getEntities(Edge.class)) {
				e.animateDestruction = false;
				this.destroyList.add(e);
			}
		}
	}
	
	/**
	 * Creates all entities needed for the respective screen that is currently shown.
	 * Also removes all "old" entities that might linger on the screen, but are not needed anymore.
	 */
	public void createEntities() {
		// nextLevel: 64,32,0,64
		// newGame: 64,32,0,48
		// demoGame: 32,32,48,0
		//System.out.println(BlockGame.getWorld().getBodyCount());
		this.removeAllEntities(true);
		//System.out.println(BlockGame.getWorld().getBodyCount());
		this.createBricks();
		//System.out.println(BlockGame.getWorld().getBodyCount());
		this.createEdges();
		//System.out.println(BlockGame.getWorld().getBodyCount());
		this.createPaddle();
		//System.out.println(BlockGame.getWorld().getBodyCount());
		this.createBall();
		//System.out.println(BlockGame.getWorld().getBodyCount());
	}
	
	public void createBricks() {
		//System.out.println("bodyCount before brick creation: "+BlockGame.getWorld().getBodyCount());
		float brickWidth = 0;
		float brickHeight = 0;
		int xOffset = 0;
		int yOffset = 0;
		if(game.isDemoGame()) {
			brickWidth = 32;
			brickHeight = 32;
			xOffset = 64;
			yOffset = 0;
		} else {
			brickWidth = 64;
			brickHeight = 32;
			xOffset = 0;
			yOffset = 0;
		}
		
		int tileX = xOffset;
		int tileY = MAP_HEIGHT-yOffset;
		int count = 0;
		for (char ch : game.getLevelCharArray()) {
			
	    	switch(ch) {
	    	
	    	case '\n':
	    		tileY -= brickHeight;
	            tileX = xOffset;
	            continue;
	    	case 'N':
	    		count++;
	    		this.createList.add(
	    				new Brick((tileX)/PIXEL_TO_METER,
	    						(tileY)/PIXEL_TO_METER,
	    						ItemProperty.NORMAL,
	    						game.isDemoGame()));
	    		break;
	    	case 'H':
	    		count++;
	    		this.createList.add(
	        			new Brick((tileX)/PIXEL_TO_METER,
	        					(tileY)/PIXEL_TO_METER,
	        					ItemProperty.HEALTH,
	        					game.isDemoGame()));
	    		break;
	    	case 'S':
	    		count++;
	    		this.createList.add(
	        			new Brick((tileX)/PIXEL_TO_METER,
	        					(tileY)/PIXEL_TO_METER,
	        					ItemProperty.SCORE,
	        					game.isDemoGame()));
	    		break;
	    	case 'P':
	    		count++;
	    		this.createList.add(
	        			new Brick((tileX)/PIXEL_TO_METER,
	        					(tileY)/PIXEL_TO_METER,
	        					ItemProperty.PADDLE_LENGTH,
	        					game.isDemoGame()));
	    		break;
	    	case 'B':
	    		count++;
	    		this.createList.add(
	        			new Brick((tileX)/PIXEL_TO_METER,
	        					(tileY)/PIXEL_TO_METER,
	        					ItemProperty.BURNING_BALL,
	        					game.isDemoGame()));
	    		break;
	    	}
			tileX += brickWidth;
		}
		//System.out.println("Count end of for-loop createBricks(): "+count);
		//System.out.println("createList.size(): " + this.createList.size());
	}
	
	public void createEdges() {
		float heightOffset = 0;
		float halfMapEdge = 0;
		if(game.isDemoGame()) {
	    	halfMapEdge = (16/PIXEL_TO_METER)/2;
	    	heightOffset = 0;
	    } else {
	    	halfMapEdge = (MAP_EDGE/PIXEL_TO_METER)/2;
	    	heightOffset = 32;
	    }
		float width = MAP_WIDTH/PIXEL_TO_METER;
		float height = (MAP_HEIGHT-heightOffset)/PIXEL_TO_METER;
		
		this.createList.add(new Edge(halfMapEdge,height/2,halfMapEdge, height/2));
		this.createList.add(new Edge(width/2,height-halfMapEdge,width/2 - (halfMapEdge*2), halfMapEdge));
		this.createList.add(new Edge(width-halfMapEdge,height/2,halfMapEdge, height/2));
		Edge groundEdge = new Edge(width/2,halfMapEdge,width/2 - (halfMapEdge*2), 0);
		groundEdge.setGroundEdge(true);
		this.createList.add(groundEdge);
	}
	
	public void createPaddle() {
		if(!game.isDemoGame()) {
			this.createList.add(new Paddle(Gdx.graphics.getWidth()/2/PIXEL_TO_METER, 48/PIXEL_TO_METER));
		}
	}
	
	public void createBall() {
		if(game.isDemoGame()) {
			Ball ball1 = new Ball((Constants.MAP_WIDTH-Constants.MAP_EDGE)/PIXEL_TO_METER, Constants.MAP_HEIGHT/2/PIXEL_TO_METER,1);
			Ball ball2 = new Ball(Constants.MAP_EDGE/PIXEL_TO_METER, Constants.MAP_HEIGHT/2/PIXEL_TO_METER,1);
			ball1.setVisible(false);
			ball2.setVisible(false);
			this.createList.add(ball1);
			this.createList.add(ball2);
		} else {
			this.createList.add(new Ball(Gdx.graphics.getWidth()/2/PIXEL_TO_METER, 72/PIXEL_TO_METER, 1));
		}
	}
	
}
