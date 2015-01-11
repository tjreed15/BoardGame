import java.awt.Color;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import acm.graphics.*;
import acm.util.RandomGenerator;

public class Monster {

	public GImage monster;
	
	private GCompound world;
	private Board board;
	private char type;
	private int col, row;
	
	private int direction; //1,2,3,4 = N,E,S,W
	private Queue<Square> destinations;
	
	
	public Monster(GCompound world, Board board, char type, int col, int row){
		this.world = world;
		this.board = board;
		this.type = type;
		this.col = col;
		this.row = row;
		direction = 1;
		destinations = new ArrayBlockingQueue<Square>(10);
		monster = getImage();
		
		int x = (col*Square.WIDTH) + 1;
		int y = (row*Square.HEIGHT) + 1;
		world.add(monster, x, y);
	}

	private GImage getImage(){
		switch(type){
		case 's': return new GImage("Shark.jpg");
		case 'n': return new GImage("Snake.jpg");
		//case '': return new GImage("");
		default: return new GImage("Shark.jpg");
		}
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public boolean isUp() {
		return direction==1;
	}
	
	public boolean isRight() {
		return direction==2;
	}
	
	public boolean isDown() {
		return direction==3;
	}
	
	public boolean isLeft() {
		return direction==4;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Square nextDestination() {
		if(destinations.peek() == null) return null;
		return destinations.poll();
	}

	public void addDestination(Square destination) {
		destinations.add(destination);
	}
	
//returns  true if in same square
	public boolean kill(Player player){
		return (col == player.getCol() && row == player.getRow());
	}
	
	public void move(Player player){
		switch(type){
		case 's':moveShark(player); break;
		case 'n':moveSnake(); break;
		}
	}
	
	private void moveShark(Player player){
		int xDir = player.getCol() - getCol();
		int yDir = player.getRow() - getRow();
		xDir = (xDir>0)? 1:-1;
		yDir = (yDir>0)? 1:-1;
		if ((yDir == 0 || rgen.nextBoolean()) && board.getSquare(col+xDir, row) != null && board.getSquare(col+xDir, row).isWater()){
			col += xDir;
		}
		else if(board.getSquare(col, row+yDir) != null && board.getSquare(col, row+yDir).isWater()){
			row += yDir;
		}
		refactor();
	}
	
	private boolean hasMove(){
		boolean toReturn = board.getSquare(col, row-1) != null && board.getSquare(col, row-1).isRegular();
		toReturn = toReturn || (board.getSquare(col+1, row)!= null && board.getSquare(col+1, row).isRegular());
		toReturn = toReturn || (board.getSquare(col, row+1) != null && board.getSquare(col, row+1).isRegular());
		toReturn = toReturn || (board.getSquare(col-1, row) != null && board.getSquare(col-1, row).isRegular());
		return toReturn;
	}
	
	private void moveSnake(){
		Square moveTo = nextSquare();
		if (moveTo == null || (moveTo.isWater() || moveTo.isWall() || moveTo.isBrick())){
			turnRight();
			if(hasMove())moveSnake();
			return;
		}
		col = moveTo.getCol();
		row = moveTo.getRow();
		Square toLeft = leftSquare();
		if (toLeft != null && !toLeft.isWall() && !toLeft.isBrick() && !toLeft.isWater())
			for(int i=0; i<3; i++) turnRight();
		refactor();
	}
	
	private void turnRight(){
		direction++;
		if (direction == 5) direction = 1;
	}
	
	private Square nextSquare(){
		if (direction == 1)	return board.getSquare(col, row-1);
		else if(direction==2)return board.getSquare(col+1, row);
		else if(direction==3)return board.getSquare(col, row+1);
		else return board.getSquare(col-1, row);
	}
	
	private Square leftSquare(){
		if (direction == 1)	return board.getSquare(col-1, row);
		else if(direction==2)return board.getSquare(col, row-1);
		else if(direction==3)return board.getSquare(col+1, row);
		else return board.getSquare(col, row+1);
	}
	
	private void refactor(){
		world.remove(monster);
		int x = (col*Square.WIDTH) + 1;
		int y = (row*Square.HEIGHT) + 1;
		world.add(monster, x, y);
	}
	
	private RandomGenerator rgen = new RandomGenerator();
	
}
