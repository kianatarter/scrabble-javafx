import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CS 351: Scrabble
 *
 * Class to represent a move in the Scrabble game
 *
 * @author Kiana Tarter
 */
public class Move {

    /**
     * List of coords of the tiles played
     */
    public List<int[]> tilesPlayed; // {row, col, letter as int}

    /**
     * String reprenting the word played
     */
    public String word;

    /**
     * The score of the move
     */
    public int score;

    /**
     * Constructor for a move
     */
    public Move() {
        tilesPlayed = new ArrayList<>();
        word = "";
        score = 0;
    }

    /**
     * Constructor for a move if there have been other tiles played
     * @param tp tiles already played
     */
    public Move(List<int[]> tp) {
        tilesPlayed = new ArrayList<>();
        for (int[] tile : tp) {
            tilesPlayed.add(new int[]{tile[0], tile[1], tile[2]});
        }
        word = "";
        score = 0;
    }

    /**
     * Makes a copy of the move
     * @param m the move being copied
     * @return a copy of the given move
     */
    public static Move copyMove(Move m) {
        Move copy = new Move();
        for (int[] tile : m.tilesPlayed) {
            copy.tilesPlayed.add(new int[]{tile[0], tile[1], tile[2]});
        }
        copy.word = m.word;
        copy.score = m.score;
        return copy;
    }

    /**
     * Method to play a move
     * @param board the board before the move is played
     * @param m the move
     * @return a new board after the move is applied
     */
    public static String[][] playMove(String[][] board, Move m) {
        String[][] newBoard = new String[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        for (int[] tile : m.tilesPlayed) {
            newBoard[tile[0]][tile[1]] = " " + (char) tile[2];
        }
        return newBoard;
    }
}
