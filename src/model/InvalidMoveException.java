package model;

/**
 * InvalidMoveException; called when a move is invalid
 * @param string
 */

public class InvalidMoveException extends Exception {
	public InvalidMoveException(String string) {
		super(string);
	}
}
