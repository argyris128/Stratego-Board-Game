package model;

/**
 * Piece; 1 of 4 main classes
 * rank; rank of piece
 * pos; position[x,y]
 */

public abstract class Piece {
	private int rank;
	private int posX, posY;
	private String image;
	
	/**
	 * setters and getters of rank, pos and image
	 * @param rank pos image
	 * @return rank pos image
	 */
	
	public void setRank(int rank) { this.rank = rank; }
	public int getRank() { return rank; }
	
	public void setPosX(int posX) { this.posX = posX; }
	public int getPosX() { return posX; }
	
	public void setPosY(int posY) { this.posY = posY; }
	public int getPosY() { return posY; }
	
	public void setImage(String image) { this.image = image; }
	public String getImage() { return image; }
}