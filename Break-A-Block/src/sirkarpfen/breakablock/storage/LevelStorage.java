package sirkarpfen.breakablock.storage;

import java.util.ArrayList;
import java.util.List;

import sirkarpfen.breakablock.levels.BasicLevel;
import sirkarpfen.breakablock.levels.DebugLevel;
import sirkarpfen.breakablock.levels.DemoLevel;
import sirkarpfen.breakablock.levels.Level1;
import sirkarpfen.breakablock.levels.Level2;

public class LevelStorage {

	private static LevelStorage instance = null;
	
	private List<BasicLevel> levelList;
	public void addLevel(BasicLevel level) {
		levelList.add(level);
	}
	public void removeLevel(BasicLevel level) {
		levelList.remove(level);
	}
	public void removeLevel(int index) {
		levelList.remove(index);
	}
	public BasicLevel getLevel(int index) {
		return levelList.get(index);
	}
	public boolean containsLevel(BasicLevel level) {
		return levelList.contains(level);
	}
	
	private BasicLevel currentLevel;
	public void setCurrentLevel(BasicLevel currentLevel) { this.currentLevel = currentLevel; }
	public void setCurrentLevel(int index) { this.currentLevel = levelList.get(index); }
	public BasicLevel getCurrentLevel() { return currentLevel; }
	
	public BasicLevel debugLevel;

	private LevelStorage() {
		this.levelList = new ArrayList<BasicLevel>();
	}
	
	public static LevelStorage getInstance() {
		if(instance == null) {
			instance = new LevelStorage();
		}
		return instance;
	}
	
	public void initLevelData() {
		debugLevel = new DebugLevel();
		this.addLevel(new DemoLevel());
		this.addLevel(new Level1());
		this.addLevel(new Level2());
		currentLevel = this.getLevel(0);
	}
	
	public void nextLevel() {
		int index = levelList.indexOf(currentLevel);
		if(index+1 > levelList.size()) {
			return;
		}
		currentLevel = this.getLevel(index+1);
	}

}
