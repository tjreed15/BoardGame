import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import acm.graphics.*;
import acm.program.*;

public class Creator extends GraphicsProgram{

	private File toSave;
	private Board gameBoard;
	private Square teleportFrom;
	private GCompound world;
	private int playerCol, playerRow;
	private int numTel;
	
	public void run(){
		Scanner kb = new Scanner(System.in);
		System.out.println("Would you like to open an old file");
		String ans = kb.nextLine();
		System.out.println("Game Level");
		toSave = new File("Level" + kb.nextLine());
		int col = 0;
		int row = 0;
		playerCol = playerRow = 0;
		if(ans.equalsIgnoreCase("Y")){
			try {
				Scanner file = new Scanner(toSave);
				col = file.nextInt();
				row = file.nextInt();
				playerCol = file.nextInt();
				playerRow = file.nextInt();
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		else{
			System.out.println("Enter board size (col row)");
			col = kb.nextInt();
			row = kb.nextInt();
		}
		System.out.println("How many teleporters?");
		numTel = kb.nextInt();
		world = new GCompound();
		add(world);
		gameBoard = (ans.equalsIgnoreCase("Y"))? new Board(world, col, row, toSave, new ArrayList<Monster>()):new Board(world, col, row);
		addMouseListeners();
		addKeyListeners();
	}
	
	public void mouseClicked(MouseEvent e){
		int col = gameBoard.getCol(e.getX()-world.getX());
		int row = gameBoard.getRow(e.getY()-world.getY());
		if(e.getButton() == 1){
			Square clicked = (row == -1 || col == -1)? null:gameBoard.getSquare(col, row);
			if (clicked != null){
				if (clicked.hasItem()) clicked.setItem(null);
				clicked.nextType();
				if (clicked.getType() >= 20 && clicked.getType() <40){
					Item item = new Item(world, gameBoard, getChar(clicked.getType()), col, row);
				}
			}
			
		}
		else{
			playerCol = col;
			playerRow = row;
		}
	}
	
	public void mousePressed(MouseEvent e){
		int col = gameBoard.getCol(e.getX());
		int row = gameBoard.getRow(e.getY());
		Square pressed = (row == -1 || col == -1)? null:gameBoard.getSquare(col, row);
		if (pressed != null && pressed.isTeleporter()) teleportFrom = pressed; 
		else teleportFrom = null;
	}
	
	public void mouseReleased(MouseEvent e){
		int col = gameBoard.getCol(e.getX());
		int row = gameBoard.getRow(e.getY());
		Square released = (row == -1 || col == -1)? null:gameBoard.getSquare(col, row);
		if (teleportFrom != null && released != null && released.isTeleporter()){
			teleportFrom.setTeleport(released);
			teleportFrom = null;
		}
	}
	
	public void keyPressed(KeyEvent e){
		if(e.getKeyChar() == KeyEvent.VK_SPACE) saveFile();
		if(e.getKeyCode() == KeyEvent.VK_LEFT)scroll(1, 0);
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) scroll(-1, 0);
		if(e.getKeyCode() == KeyEvent.VK_UP) scroll(0, 1);
		if(e.getKeyCode() == KeyEvent.VK_DOWN) scroll(0, -1);
	}
	
	private void saveFile(){
		FileWriter write = null;
		String cbuf = gameBoard.getCols() + " " + gameBoard.getRows() + "  " + playerCol + " " + playerRow +  " \n";
		Queue<Square> teleport = new ArrayBlockingQueue<Square>(numTel);
		try{
			write = new FileWriter(toSave);
			for (int i = 0; i<gameBoard.getRows(); i++){
				for (int j = 0; j<gameBoard.getCols(); j++){
					int toAdd = (gameBoard.getSquare(j, i).getType());
					char append = getChar(toAdd);
					cbuf = cbuf + append + " ";
					if(gameBoard.getSquare(j, i).getType() == 5)teleport.add(gameBoard.getSquare(j, i));
				}
				cbuf = cbuf + " \n";
			}
			while(teleport.peek() != null){
				cbuf = cbuf +" \n";
				Square to = teleport.poll();
				String add1 = ((to.isTeleporter())? (to.teleportTo().getCol()+""):"0");
				String add2 = ((to.isTeleporter())? (to.teleportTo().getRow()+""):"0");
				cbuf = cbuf + "" + add1 + " " + add2;
			}
			write.write(cbuf);
			write.close();
		}
		catch(IOException e){
			System.out.println(e);
			System.exit(0);
		}
	}
	
	private char getChar(int in){
		switch(in){
		case 0: return '0'; //Regular
		case 1: return '1'; //Water
		case 2: return '2'; //Wall
		case 3: return '3'; //Winning Square
		case 4: return '4'; //Brick
		case 5: return '5'; //Teleporter
		case 6: return '6'; //Ice
		//case 7': return '7';
		//case 8': return '8';
		//case 9': return '9';
		case 10: return 'S'; //Top Left
		case 11: return 'T'; //Top Right
		case 12: return 'U'; //Bottom Right
		case 13: return 'V'; //Bottom Left
		
		case 20: return 'w'; //Flippers
		case 21: return 'i'; //IceSkates
		case 22: return 'y'; //Yellow Key
		case 23: return 'b'; //Blue Key
		case 24: return 'r'; //Red Key
		case 25: return 'g'; //Green Key
		case 26: return 'Y'; //Yellow Key Hole
		case 27: return 'B'; //Blue Key Hole
		case 28: return 'R'; //Red Key Hole
		case 29: return 'G'; //Green Key Hole
		
		case 40: return 's'; //Shark
		
		default: return '0';
		}
	}
	
	private void scroll(int x, int y){
		world.move(x*SCROLL, y*SCROLL);
	}
	
	private static final int SCROLL = Square.WIDTH/2;
}
