package sirkarpfen.breakablock.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import sirkarpfen.breakablock.main.BlockGame;

public class BlockMain {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Break-A-Block";
		config.width = 832;
		config.height = 640;
		config.useGL20 = true; //this is important
		// make the window static
		config.resizable = false;
		new LwjglApplication(BlockGame.getInstance(), config);
	}
	
}
