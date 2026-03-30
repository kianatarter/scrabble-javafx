import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CPU {
    static void CPUMove() {
        List<Square> cpuHand = new ArrayList<>();

        for (Character c : Scrabble.CPUTray) {
            if (c != null){
                cpuHand.add(new Square(Character.toLowerCase(c)));
            }
        }
        Solver.dictionary = Scrabble.dictionary;
        System.out.println("dictionary has cat: " + Solver.dictionary.search("cat"));
        System.out.println("cpu hand size: " + cpuHand.size());
        System.out.println("anchors: " + Solver.getAdjacent(Scrabble.board).size());
        Solver.setHand(cpuHand);
        Move best = Solver.getBestMove(Scrabble.board);

        if (best == null){
            System.out.println("CPU has no move!");
            return;
        }

        String[][] result = Move.playMove(Scrabble.board, best);
        for (int r = 0; r < Scrabble.boardSize; r++){
            for (int c = 0; c < Scrabble.boardSize; c++){
                Scrabble.board[r][c] = result[r][c];
            }
        }

        Scrabble.CPUTray.clear();
        Scrabble.drawCPUTiles();
    }

}
