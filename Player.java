import java.awt.Color;
import java.util.Iterator;
import java.util.ListIterator;

import acm.graphics.*;
import acmx.export.java.util.ArrayList;
import acmx.export.java.util.LinkedList;

public class Player {

	public GOval user;
	private int row, col;
	private Board in;
	private GCompound world;
	private ArrayList items;
	
	public Player(GCompound world, Board in, int col, int row){
		this.col = col;
		this.row = row;
		this.world = world;
		this.in = in;
		items = new ArrayList();
		user = new GOval(WIDTH, HEIGHT);
		user.setFilled(true);
		user.setFillColor(Color.pink);
		
		double x = (Square.WIDTH * col) + ((Square.WIDTH - WIDTH)/2);
		double y = (Square.HEIGHT * row) + ((Square.HEIGHT - HEIGHT)/2);
		world.add(user, x, y);
	}
	
	public void move(int nCol, int nRow){
		Square to = in.getSquare(col+nCol, row+nRow);
		if(to == null || (in().isIce() && !hasItem('i'))){
			boolean bounceBack = (to == null);
			bounceBack = bounceBack && !in().isSlider();
			char direction;
			if (nCol<0) direction = 'l';
			else if(nCol>0)direction = 'r';
			else if(nRow<0)direction = 'd';
			else direction = 'u';
			if(bounceBack || !to.isPlayable(this, direction)){
				col += nCol;
				row += nRow;
				nCol *= -1;
				nRow *= -1;
			}
			if(to != null && to.isPlayable(this, direction)){
				if(to.isBrick())to.move(nCol, nRow);
			}
			col += nCol;
			row += nRow;
			int[] directionChange = iceTurn(nCol, nRow, to);
			if (in().isIce())move(directionChange[0], directionChange[1]);
		}
		else if (to.isIce() && !hasItem('i')){
			col += nCol;
			row += nRow;
			int[] directionChange = iceTurn(nCol, nRow, to);
			if (in().isIce())move(directionChange[0], directionChange[1]);
		}
		else{//Ice skate on ice, or normal movement
			col += nCol;
			row += nRow;
			if (to.isBrick()){
				to.move(nCol, nRow);
			}
		}
		refactor();
	}
	
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
		refactor();
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int col) {
		this.col = col;
		refactor();
	}
	
	public boolean died(){
		boolean dead = (in().isWater() && !hasItem('w'));
		return dead || in().isBrick();
	}
	
	public Square in(){
		return in.getSquare(col, row);
	}
	
	public void addItem(Item newItem){
		String itemType = newItem.getType() + "";
		if (newItem.isKeyHole()) removeItem(itemType.toLowerCase().charAt(0));
		items.add(newItem);
	}
	
	public void removeItem(char type){
		acmx.export.java.util.Iterator itemIterator = items.iterator();
		while(itemIterator.hasNext()){
			Item curr = (Item) itemIterator.next();
			if(curr.getType() == type){ 
				items.remove(curr);
				break;
			}
		}
		
		
	}
	
	public boolean hasItem(char itemCode){
		acmx.export.java.util.Iterator itemIterator = items.iterator();
		while(itemIterator.hasNext()){
			if(((Item) itemIterator.next()).getType() == itemCode) return true;
		}
		return false;
	}

	private int[] iceTurn(int nCol, int nRow, Square to){
		int[] toReturn = new int[2];
		toReturn[0]= nCol; toReturn[1]= nRow;
		if (to == null) return toReturn;
		if(to.isTopLeft()){
			if(nCol>0 || nRow>0) {
				nCol *= -1;
				nRow *= -1;
			}
			else if (nCol <0){
				nCol = 0;
				nRow = 1;
			}
			else {
				nCol = 1;
				nRow = 0;
			}
		}
		else if(to.isBottomRight()){
			if(nCol<0 || nRow<0) {
				nCol *= -1;
				nRow *= -1;
			}
			else if (nCol >0){
				nCol = 0;
				nRow = -1;
			}
			else {
				nCol = -1;
				nRow = 0;
			}
		}
		else if(to.isTopRight()){
			if(nCol<0 || nRow>0) {
				nCol *= -1;
				nRow *= -1;
			}
			else if (nCol >0){
				nCol = 0;
				nRow = 1;
			}
			else {
				nCol = -1;
				nRow = 0;
			}
		}
		else if(to.isBottomLeft()) { 
			if(nCol>0 || nRow<0) {
				nCol *= -1;
				nRow *= -1;
			}
			else if (nCol <0){
				nCol = 0;
				nRow = -1;
			}
			else {
				nCol = 1;
				nRow = 0;
			}
		}
		//Else it stays as was input from function call
		toReturn[0] = nCol;
		toReturn[1] = nRow;
		return toReturn;
	}
	
	
	private void refactor(){
		world.remove(user);
		
		double x = (Square.WIDTH * col) + ((Square.WIDTH - WIDTH)/2);
		double y = (Square.HEIGHT * row) + ((Square.HEIGHT - HEIGHT)/2);
		world.add(user, x, y);
	}

	private static final int WIDTH = Square.WIDTH/2;
	private static final int HEIGHT = Square.HEIGHT/2;
	
}
