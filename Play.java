import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import acm.graphics.*;
import acm.program.*;
import acm.util.RandomGenerator;

public class Play extends GraphicsProgram{
	
	private static final int START_LEVEL = 1;
	private static final int PAUSE_TIME = 100;
	
	private Board gameBoard;
	private GCompound world;
	private Player player;
	private int level, move; //Move is represented as an int (0 is no motion, 1-4 are N,E,S,W)
	private boolean quit, alive, monsterMash;
	private ArrayList<Monster> monsters; 
	
	public void run(){
		waitForClick();
		addKeyListeners();
		level = NLEVELS+2; quit = false;
		while(level<=NLEVELS+10){
			playLevel(level);
			if(quit) break;
		}
		gameOver(level);
	}
	
	private void playLevel(int level){
		while(!quit){
			alive = true;
			world = new GCompound();
			monsters = new ArrayList<Monster>();
			add(world);
			File levelFile = new File("Level" + ((START_LEVEL-1)+level));
			int[] boardInfo = getInfo(levelFile);
			gameBoard = new Board(world, boardInfo[0], boardInfo[1], levelFile, monsters);
			player = new Player(world, gameBoard, boardInfo[2], boardInfo[3]);
			checkForWin();
			removeAll();
			if(this.level > level) break;
		}
		
	}
	

	public void keyPressed(KeyEvent e){
		if(level > 0 && alive){
			if (e.getKeyCode() == KeyEvent.VK_UP){
				if (player.getRow() > 0 && player.in().above().isPlayable(player, 'u')){
					move = 1;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT){
				if (player.getCol() < gameBoard.getCols()-1 && player.in().toRight().isPlayable(player, 'r')){
					move = 2;
				}
			}
			
			if (e.getKeyCode() == KeyEvent.VK_DOWN){
				if (player.getRow() < gameBoard.getRows()-1 && player.in().below().isPlayable(player, 'd')){
					move = 3;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT){
				if (player.getCol() > 0 && player.in().toLeft().isPlayable(player, 'l')){
					move = 4;
				}
			}
			if (e.getKeyChar() == 'q'){
				quit = true;
			}
		}
	}
	
	public void checkForWin(){
		while(true){
			while(player.user.getX() + world.getX() < SCROLL*Square.WIDTH) scroll(1, 0);
			while(player.user.getX() + world.getX() > getWidth()-SCROLL*Square.WIDTH) scroll(-1, 0);
			while(player.user.getY() + world.getY() < SCROLL*Square.HEIGHT) scroll(0, 1);
			while(player.user.getY() + world.getY() > getHeight()-SCROLL*Square.HEIGHT) scroll(0, -1);
			
			if(move != 0){
				switch(move){
				case 1:	scroll(0, 1);
						player.move(0, -1);
						break;
				case 2:	scroll(-1, 0);
						player.move(1, 0);
						break;
				case 3:	player.move(0, 1);
						scroll(0, -1);
						break;
				case 4: player.move(-1, 0);
						scroll(1, 0);
						break;
				default:break;
				}
				move = 0;
				if(player.in().isTeleporter()){
					Square curr = player.in();
					player.setCol(curr.teleportTo().getCol());
					player.setRow(curr.teleportTo().getRow());
				}
				if(player.in().hasItem()){
					player.addItem(player.in().getItem());
					world.remove(player.in().getItem().item);
					player.in().setItem(null);
					player.in().setType(0);
					player.user.sendToFront();
				}
				
				if (player.died()) alive = false;
			}
			
			if (player.in() != null && player.in().isWinningSquare()){
				level++;
				break;
			}
			moveMonsters();
			if(quit || !alive){
				waitForClick();
				break;
			}
			pause(PAUSE_TIME);
		}
	}
	
	private void moveMonsters(){
		Iterator<Monster> itr = monsters.iterator();
		Monster curr;
		while(itr.hasNext()){
			curr = itr.next();
			if(monsterMash) curr.move(player);
			alive = !curr.kill(player);
			if(!alive) break;
		}
		monsterMash = !monsterMash;
	}
	
	private void gameOver(int level){
		this.level = -1;
		removeAll();
		GLabel end = new GLabel("Conratulations! You got to level " + (level-1) + "!");
		add(end, 600, 250);
		System.out.println("Game Over");
	}
	
	private int[] getInfo(File level){
		int[] toReturn = new int[4];
		try {
			Scanner LFS = new Scanner(level);
			for (int i = 0; i<4; i++){
				toReturn[i] = LFS.nextInt();
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return toReturn;
	}
	
	private void scroll(int x, int y){
		if (player.user.getX() + world.getX() < SCROLL*Square.WIDTH && x > 0){
			world.move(x*Square.WIDTH, 0);
		}
		if (player.user.getX() + world.getX() > getWidth() - SCROLL*Square.WIDTH && x < 0){
			world.move(x*Square.WIDTH, 0);
		}
		if (player.user.getY() + world.getY() < SCROLL*Square.WIDTH && y > 0){
			world.move(0, y*Square.WIDTH);
		}
		if (player.user.getY() + world.getY() > getHeight() - SCROLL*Square.HEIGHT && y < 0){
			world.move(0, y*Square.WIDTH);
		}
	}
	
	private static final int NLEVELS = 17;
	private static final int SCROLL = 3;
	
	
}