package sirkarpfen.breakablock.entities;

import sirkarpfen.breakablock.entities.bodies.BodyFactory;
import sirkarpfen.breakablock.entities.enums.ItemProperty;
import sirkarpfen.breakablock.main.BlockGame;
import sirkarpfen.breakablock.storage.TextureStorage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;


public class Brick extends Entity {
	
	private TextureRegion textureRegion;
	
	private ItemProperty prop;
	public ItemProperty getItemProperty() { return prop; }
	
	private boolean createdItem = false;
	public void setCreatedItem(boolean createdItem) { this.createdItem = createdItem; }
	public boolean hasCreatedItem() { return createdItem; }
	
	private float textureScale;
	private boolean scoreAdded = false;

	private Item item;
	public void setScoreAdded(boolean scoreAdded) { this.scoreAdded = scoreAdded; }
	public boolean scoreAdded() { return scoreAdded; }
	
	public Brick(float x, float y, ItemProperty prop, boolean isDemoGame) {
		super();
		this.prop = prop;
		textureScale = 1;
		textureRegion = new TextureRegion(TextureStorage.getInstance().getTexture((this.getBrickType(prop,isDemoGame))));
		this.startX = x;
		this.startY = y;
	}
	
	@Override
	public void create() {
		this.createBody(startX,startY);
		if(!BlockGame.getInstance().isDemoGame() && prop != ItemProperty.NORMAL) {
			System.out.println("+1");
			item = new Item(body.getPosition().x, body.getPosition().y, this.getItemProperty());
			entityManager.addToCreateList(item);
			this.setCreatedItem(true);
		}
	}
	
	private String getBrickType(ItemProperty prop, boolean isDemoGame) {
		String key = "";
		switch(prop) {
		
		case NORMAL: 
			if(isDemoGame) {
				key = "main_blue";
				break;
			}
			key = "brick_blue"; 
			break;
		case HEALTH: 
			if(isDemoGame) {
				key = "main_green";
				break;
			}
			key = "brick_green"; 
			break;
		case SCORE: 
			if(isDemoGame) {
				key = "main_yellow";
				break;
			}
			key = "brick_score"; 
			break;
		case PADDLE_LENGTH: 
			if(isDemoGame) {
				key = "main_purple";
				break;
			}
			key = "brick_purple"; 
			break;
		case BURNING_BALL: 
			if(isDemoGame) {
				key = "main_red";
				break;
			}
			key = "brick_ball"; 
			break;
		default: 
			if(isDemoGame) {
				key = "main_blue";
				break;
			}
			key = "brick_blue"; 
			break;
		}
		return key;
	}
	@Override
	public void createBody(float x, float y) {
		this.width = textureRegion.getTexture().getWidth()/pixel_to_meter;
		this.height = textureRegion.getTexture().getHeight()/pixel_to_meter;
		
		body = BodyFactory.createBody(new Vector2(x,y), BodyType.StaticBody);
		
		BodyFactory.createFixture(body, BodyFactory.createBoxShape(width/2, height/2), new float[]{0,0,0}, false, (short)1);
		
		body.setUserData(this);
	}
	
	@Override
	public void render(SpriteBatch batch, float delta) {
		batch.begin();
		if(flaggedForDestruction) {
			if(animateDestruction) {
				if(textureScale > 0.125f) {
					textureScale *= 0.8f;
				} else {
					BlockGame.addScore(100);
					animateDestruction = false;
					if(item != null)
						item.setVisible(true);
					entityManager.addToDestroyList(this);
					batch.end();
					return;
				}
				batch.draw(textureRegion, 
						(body.getPosition().x-width/2)*pixel_to_meter-delta, 
						(body.getPosition().y-height/2)*pixel_to_meter-delta, 
						width/2*pixel_to_meter, 
						height/2*pixel_to_meter, 
						width*pixel_to_meter, 
						height*pixel_to_meter, 
						textureScale, 
						textureScale, 
						0f);
			}
		} else {
			batch.draw(textureRegion, (body.getPosition().x-width/2)*pixel_to_meter, (body.getPosition().y-height/2)*pixel_to_meter);
		}
		batch.end();
	}
	
	@Override
	public void move(float delta) {}
	
	@Override
	public void destroy() {
		if(!animateDestruction) {
			World world = BlockGame.getWorld();
			world.destroyBody(body);
		}
	}
	
}
