import java.util.ArrayList;
import java.util.List;

/**
 * CS 351: Scrabble
 *
 * Class to handle computer player moves
 *
 * @author Kiana Tarter
 */
public class CPU {

    /**
     * Method to make a computer move
     */
    public static void CPUMove() {
        List<Square> cpuHand = new ArrayList<>();

        for (Character c : Scrabble.CPUTray) {
            if (c != null){
                cpuHand.add(new Square(Character.toLowerCase(c)));
            }
        }

        Solver.setHand(cpuHand);
        Move best = Solver.getBestMove(Scrabble.board);

        if (best == null){
            System.out.println("CPU has no move!");
            return;
        }

        String[][] result = Move.playMove(Scrabble.board, best);
        List<int[]> move = Main.getMove(Scrabble.board, result);

        if (move.isEmpty()){
            System.out.println("CPU Passes");
            return;
        }

        String actualWord = Main.getWord(result, move);
        if (!Scrabble.dictionary.search(actualWord.toLowerCase())) {
            System.out.println("CPU passes.");
            return;
        }

        for (int r = 0; r < Scrabble.boardSize; r++) {
            for (int c = 0; c < Scrabble.boardSize; c++) {
                Scrabble.board[r][c] = result[r][c];
            }
        }
            Scrabble.CPUScore += best.score;
        Scrabble.CPUScoreLabel.setText("CPU Score: " + Scrabble.CPUScore);

        removeUsed(best);
        Scrabble.refreshTiles(Scrabble.CPUTray);

        Scrabble.updateTilesLeft();

        Scrabble.CPUTray.clear();
        Scrabble.drawCPUTiles();
        Scrabble.redrawBoard();
    }

    /**
     * Method to remove the tiles the computer plays from its tray
     * @param best the best move the computer will play
     */
    public static void removeUsed(Move best){
        for (int[] tile : best.tilesPlayed){
            char used = Character.toLowerCase((char) tile[2]);

            for (int i = 0; i < Scrabble.CPUTray.size(); i++){
                Character trayChar = Scrabble.CPUTray.get(i);

                if (trayChar != null && Character.toLowerCase(trayChar) == used){
                    Scrabble.CPUTray.set(i, null);
                    break;
                }
            }
        }
    }
}
