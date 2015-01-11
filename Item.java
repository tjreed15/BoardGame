import java.awt.Color;
import acm.graphics.*;

public class Item {

	public GImage item;
	private char type;
	private int col, row;
	private Board board;
	private GCompound world;
	
	public Item(GCompound world, Board board, char type, int col, int row){
		this.type = type;
		this.col = col;
		this.row = row;
		this.board = board;
		this.world = world;
		item = getItem();
		board.getSquare(col, row).setItem(this);
		
		int x = (col*Square.WIDTH) + 1;
		int y = (row*Square.HEIGHT) + 1;
		world.add(item, x, y);
	}
	
	public char getType(){
		return type;
	}
	
	public boolean isKey(){
		return type == 'y' || type == 'b' || type == 'r' || type == 'g';
	}
	
	public boolean isKeyHole(){
		return type == 'Y' || type == 'B' || type == 'R' || type == 'G';
	}
	
	public boolean canOpen(Item keyHole){
		if (type == 'y' && keyHole.type == 'Y') return true;
		if (type == 'b' && keyHole.type == 'B') return true;
		if (type == 'r' && keyHole.type == 'R') return true;
		if (type == 'g' && keyHole.type == 'G') return true;
		else return false;
	}
	
	private GImage getItem(){
		switch(type){
		case 'w': return new GImage("Flipper.jpg", 0, 0);
		case 'i': return new GImage("IceSkate.jpg", 0, 0);
		case 'y': return new GImage("YellowKey.jpg", 0, 0);
		case 'Y': return new GImage("YellowKeyHole.jpg", 0, 0);
		case 'b': return new GImage("BlueKey.jpg", 0, 0);
		case 'B': return new GImage("BlueKeyHole.jpg", 0, 0);
		case 'r': return new GImage("RedKey.jpg", 0, 0);
		case 'R': return new GImage("RedKeyHole.jpg", 0, 0);
		case 'g': return new GImage("GreenKey.jpg", 0, 0);
		case 'G': return new GImage("GreenKeyHole.jpg", 0, 0);
		default: return new GImage("Flipper.jpg", 0, 0);
		}
	}

}
