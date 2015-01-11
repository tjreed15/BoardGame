import java.awt.Color;
import acm.graphics.*;

/**
 * 
 * When switching/adding a type, must check these sections:
 * **nextType() for creation purposes
 * **is_____() for checking/useage purposes
 * 
 * And in Board:
 * **getCode() must match return type listed below, then treat accordingly
 * 
 * Type	| Meaning
 *  0	| Regular (gray) space
 *  1	| Water (Blue) space
 *  2	| Wall (Dark Gray) space
 *  3	| Winning (green) space
 *  4	| Brick (Red) space
 *  5	| Teleporter (Magenta) space
 *  6	| Ice (Cyan) space
 * 7-9	| Reserved for spaces
 * 
 *  10	|	Top Left Corner
 *  11	|	Top Right Corner
 *  12	|	Bottom Left Corner
 *  13	|	Bottom Right Corner
 * 14-19| Reserved for Special Spaces
 * 
 *  20	| Flippers
 *  21	| Ice Skates 
 *  22	| Yellow Key
 *  23	| Blue Key
 *  24	| Red Key
 *  25	| Green Key
 *  26	| Yellow Key Hole
 *  27	| Blue Key Hole
 *  28	| Red Key Hole
 *  29	| Green Key Hole
 * 30-39| Reserved for Items
 * 
 * 40	| Shark
 * 41	| Snake
 * 41+	| Reserved for Monsters
 */


public class Square {

	public Square teleport;
	public Item item;
	public GRect square;
	private Board board;
	private int type;
	private int row, col;
	public GCompound world;
	
	public Square(GCompound world, Board board, int col, int row, int type){
		this.row = row;
		this.col = col;
		this.board = board;
		this.world = world;
		this.type = type;
		teleport = null;
		item = null;
		
		square = new GRect(col*WIDTH, row*HEIGHT, WIDTH, HEIGHT);
		square.setFilled(true);
		square.setFillColor(getColor());
		
		world.add(square);
	}
	
	public Square toRight(){
		return board.toRight(this);
	}

	public Square above(){
		return board.above(this);
	}

	public Square below(){
		return board.below(this);
	}

	public Square toLeft(){
		return board.toLeft(this);
	}
	
	public void setTeleport(Square to){
		teleport = to;
	}
	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
		refactor();
	}
	
	public void nextType(){
		if (type < 6) type++;
		else if(type == 6) type = 10;
		else if(type>=10 && type <13) type++;
		else if(type == 13) type = 20;
		else if (type >=20 && type <29) type++;
		else if (type == 29) type = 40;
		else if(type >= 40 && type  <41) type++; 
		else type = 0;
		refactor();
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
		refactor();
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
		refactor();
	}
	
	public Square teleportTo(){
		return teleport;
	}
	
	public void setItem(Item item){
		this.item = item;
	}
	
	public boolean hasItem(){
		return item != null;
	}
	
	public Item getItem(){
		return item;
	}

	public boolean isPlayable(Player player, char direction){
		if(isWall()){
			if (hasItem()){
				if (isYellowKeyHole())
					return player.hasItem('y');
				else if (isBlueKeyHole())
					return player.hasItem('b');
				else if (isRedKeyHole())
					return player.hasItem('r');
				else if (isGreenKeyHole())
					return player.hasItem('g');
				else return false;
			}
			else return false;
		}
		else if (isBrick()){
			switch (direction){
			case 'l': return (toLeft() != null && !toLeft().isBrick() && !toLeft().isWinningSquare()  && toLeft().isPlayable(player, 'l'));
			case 'r': return (toRight() != null && !toRight().isBrick() && !toRight().isWinningSquare() && toRight().isPlayable(player, 'r'));
			case 'u': return (above() != null && !above().isBrick() && !above().isWinningSquare() && above().isPlayable(player, 'u'));
			case 'd': return (below() != null && !below().isBrick() && !below().isWinningSquare() && below().isPlayable(player, 'd'));
			default: return false;
			}
		}
		/**
		else if (type == 5){
			Square to = teleport;
			switch (direction){
			case 'l': to = (to.toLeft()); break;
			case 'r': to = (to.toRight()); break;
			case 'u': to = (to.above()); break;
			case 'd': to = (to.below()); break;
			default: return false;
			}
			return to.isPlayable(direction);
		}
		*/
		else return true;
	}
	
	public boolean isRegular(){
		return type == 0 || (type>=20 && type<26) || (type>29 && type<40) || (type>40);
	}
	
	public boolean isWater(){
		return type == 1 || type == 40;
	}
	
	public boolean isWall(){
		return type == 2 || (type>=26 && type<=29);
	}
	public boolean isWinningSquare(){
		return type == 3;
	}
	
	public boolean isBrick(){
		return type == 4;
	}
	
	public boolean isTeleporter(){
		return type == 5;
	}
	
	public boolean isIce(){
		return (type == 6 || type == 10 || type == 11 || type == 12 || type == 13);
	}
	
	public boolean isSlider(){
		return (type == 10 || type == 11 || type == 12 || type == 13);
	}
	public boolean isTopLeft(){
		return type == 10;
	}
	
	public boolean isTopRight(){
		return type == 11;
	}
	
	public boolean isBottomRight(){
		return type == 12;
	}
	
	public boolean isBottomLeft(){
		return type == 13;
	}
	
	public boolean isYellowKeyHole(){
		return type == 26;
	}
	
	public boolean isBlueKeyHole(){
		return type == 27;
	}
	
	public boolean isRedKeyHole(){
		return type == 28;
	}
	
	public boolean isGreenKeyHole(){
		return type == 29;
	}
	
	//Moving this square as a brick to location at given distance from this square
	
	/**
	 * check if on board edge
	 * Regular: move onto it
	 * Water: fill it
	 * Wall: dont move
	 * Winning: dont move
	 * Brick: Dont Move
	 * Teleporter: Teleport
	 * Ice: move accross it, check next for playability
	 * Item?
	 */
	public void move(int nCol, int nRow){
		boolean intoWall = ((col == board.getCols()-1 && nCol<0) || (col == 0 && nCol<0));
		intoWall = intoWall || ((row == board.getRows()-1 && nRow>0) || (row == 0 && nRow<0));
		if(intoWall && !isSlider()){
			if(isIce()) move(-nCol, -nRow);
			return;
		}
		
		Square into = board.getSquare(col+nCol, row+nRow);
		
		if(!isIce() && !isTeleporter())type = 0;
		if (into.isIce() || (isIce() && (into.isBrick() || into.isWall()))){
			boolean bounceBack = into.getCol()== board.getCols()-1 && nCol>0;
			bounceBack = bounceBack || (into.getCol()==0 && nCol<0);
			bounceBack = bounceBack || (into.getRow()==board.getRows()-1 && nRow>0);
			bounceBack = bounceBack || (into.getRow()==0 && nRow<0);
			bounceBack = bounceBack && !into.isSlider();
			if(bounceBack || into.isBrick() || into.isWall()){
				nCol *= -1;
				nRow *= -1;
			}
			int[] directionChange = iceTurn(nCol, nRow, into);
			into.move(directionChange[0], directionChange[1]);
		}
		else if (into.isTeleporter()){
			into.teleport.move(nCol, nRow);
		}
		else if (into.isWater()) into.setType(0);
		else into.setType(4);
		refactor();
	}
	
	
	
	private Color getColor(){
		if (isRegular()) return Color.lightGray;
		else if (isWater())return Color.blue;
		else if (isWall())return Color.darkGray;
		else if (isWinningSquare())return Color.green;
		else if (isBrick())return Color.red;
		else if (isTeleporter())return Color.magenta;
		else if (isIce())return Color.cyan;
		else return Color.lightGray;
	}

	private int[] iceTurn(int nCol, int nRow, Square into){
		int[] toReturn = new int[2];
		if(into.isTopLeft()){
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
		else if(into.isBottomRight()){
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
		else if(into.isTopRight()){
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
		else if(into.isBottomLeft()) { 
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
		world.remove(square);
		square = new GRect(col*WIDTH, row*HEIGHT, WIDTH, HEIGHT);
		square.setFilled(true);
		square.setFillColor(getColor());
		world.add(square);
		
		if (item != null) world.add(item.item);
		
		if (isSlider()){
			String slider;
			if (isTopLeft()) slider = "TopLeft.jpg";
			else if (isTopRight()) slider = "TopRight.jpg";
			else if (isBottomRight()) slider = "BottomRight.jpg";
			else slider = "BottomLeft.jpg";
			world.add(new GImage(slider), col*WIDTH+1, row*HEIGHT+1);
		}
	}
	
	static final int WIDTH = 50;
	static final int HEIGHT = 50;
}
