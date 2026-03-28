import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Move {
    public List<int[]> tilesPlayed; // {row, col, letter as int}
    public String word;
    public int score;

    public Move() {
        tilesPlayed = new ArrayList<>();
        word = "";
        score = 0;
    }

    public Move(List<int[]> tp) {
        tilesPlayed = new ArrayList<>();
        for (int[] tile : tp) {
            tilesPlayed.add(new int[]{tile[0], tile[1], tile[2]});
        }
        word = "";
        score = 0;
    }

    public static Move copyMove(Move m) {
        Move copy = new Move();
        for (int[] tile : m.tilesPlayed) {
            copy.tilesPlayed.add(new int[]{tile[0], tile[1], tile[2]});
        }
        copy.word = m.word;
        copy.score = m.score;
        return copy;
    }

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
