import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import acm.graphics.*;

public class Board{
	
	private Square[][]board;
	private int rows, cols;
	
	public Board(GCompound world, int cols, int rows){
		this.rows = rows;
		this.cols = cols;
		board = new Square[cols][rows];
		for(int i = 0; i<cols; i++){
			for(int j = 0; j<rows; j++){
				board[i][j] = new Square(world, this, i, j, 0);
			}
		}
		
	}
	
	public Board(GCompound world, int cols, int rows, File input, ArrayList<Monster> monsters){
		this.rows = rows;
		this.cols = cols;
		board = new Square[cols][rows];
		int type; String currLine; char currChar;
		try {
			Scanner in = new Scanner(input);
			in.nextLine(); //Gets rid of size/starting info
			Queue<Square> teleport = new ArrayBlockingQueue<Square>(50);
			for(int j = 0; j<rows; j++){
				currLine = (in.hasNext())? in.nextLine():"";
				for(int i = 0; i<cols; i++){
					currChar = (i<currLine.length())? currLine.charAt(2*i):'X'; //X is dealt with on Next Line
					type = (currChar == 'X')? 0:getCode(currChar);
					if (type >= 10 && type <= 19){
						board[i][j] = new Square(world, this, i, j, type);
						GImage slider;
						switch(type%10){
						case 0: slider = new GImage("TopLeft.jpg"); break;
						case 1: slider = new GImage("TopRight.jpg"); break;
						case 2: slider = new GImage("BottomRight.jpg"); break;
						case 3: slider = new GImage("BottomLeft.jpg"); break;
						default: slider = new GImage("TopLeft.jpg"); break;
						}
						world.add(slider, i*Square.WIDTH+1, j*Square.WIDTH+1);
					}
					else if(type >= 20 && type <= 39){
						board[i][j] = new Square(world, this, i, j, type);
						Item item = new Item(world, this, currChar, i, j);
					}
					else if(type >= 40){
						board[i][j] = new Square(world, this, i, j, type);
						Monster monster = new Monster(world, this, currChar, i, j);
						monsters.add(monster);
					}
					else{
						board[i][j] = new Square(world, this, i, j, type);
						if (type == 5) teleport.add(board[i][j]);
					}	
				}
			}
			Square curr;
			while(teleport.peek() != null){
				curr = teleport.poll();
				int col = in.nextInt();
				int row = in.nextInt();
				Square to = board[col][row];
				curr.setTeleport(to);
			}
		}
		catch(FileNotFoundException e){
			System.out.println(e);
			System.exit(0);
		}
		
	}
	
	public Square getSquare(int col, int row){
		if (col>=cols || row>=rows || col<0 || row<0)
			return null;
		return board[col][row];
	}
	
	public Square toRight(Square on){
		if(on.getCol() >= cols-1) return null;
		return board[on.getCol()+1][on.getRow()];
	}

	public Square above(Square on){
		if(on.getRow() <= 0) return null;
		return board[on.getCol()][on.getRow()-1];
	}

	public Square below(Square on){
		if(on.getRow() >= rows-1) return null;
		return board[on.getCol()][on.getRow()+1];
	}

	public Square toLeft(Square on){
		if(on.getCol() <= 0) return null;
		return board[on.getCol()-1][on.getRow()];
	}
	
	public int getCol(double  x){
		int col =  (int) (x/Square.WIDTH);
		if(col < 0 || col > cols-1) return -1;
		return col;
	}
	
	public int getRow(double y){
		int row = (int) y/Square.HEIGHT;
		if(row < 0 || row > rows-1) return -1;
		return row;
	}
	
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}
	
// char is from text file, converts to int from Square class
	private int getCode(char in){
		switch(in){
		case '0': return 0; //Regular
		case '1': return 1; //Water
		case '2': return 2; //Wall
		case '3': return 3; //Winning Square
		case '4': return 4; //Brick
		case '5': return 5; //Teleporter
		case '6': return 6; //Ice
		//case '7': return 7;
		//case '8': return 8;
		//case '9': return 9;
		case 'S': return 10; //Top Left
		case 'T': return 11; //Top Right
		case 'U': return 12; //Bottom Right
		case 'V': return 13; //Bottom Left
		
		case 'w': return 20; //Flippers
		case 'i': return 21; //IceSkates
		case 'y': return 22; //Yellow Key
		case 'b': return 23; //Blue Key
		case 'r': return 24; //Red Key
		case 'g': return 25; //Green Key
		case 'Y': return 26; //Yellow Key Hole
		case 'B': return 27; //Blue Key Hole
		case 'R': return 28; //Red Key Hole
		case 'G': return 29; //Green Key Hole
		
		case 's': return 40; //Shark
		case 'n': return 41; //Snake
		
		default: return 0;
		}
	}
	
}