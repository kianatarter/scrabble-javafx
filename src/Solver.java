import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * CS 351: Scrabble
 *
 * Main program to run the solver
 *
 * @author Kiana Tarter
 *
 */
public class Solver {
    static Trie dictionary = new Trie();
    static List<Square> hand = new ArrayList<>();

    static void setHand(List<Square> handNew) {
        hand = handNew;
    }

    static List<int[]> getAdjacent(String[][] board) {
        List<int[]> anchors = new ArrayList<>();
        int boardSize = board.length;
        boolean boardEmpty = true;

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                // only check for tiles adjacent to a letter
                if (board[r][c].charAt(0) == ' ') {
                    boardEmpty = false;
                    if (isInBounds(boardSize, new int[]{r - 1, c}) &&
                            board[r - 1][c].charAt(0) == '.') {
                        anchors.add(new int[]{r - 1, c});
                    }

                    if (isInBounds(boardSize, new int[]{r + 1, c}) &&
                            board[r + 1][c].charAt(0) == '.') {
                        anchors.add(new int[]{r + 1, c});
                    }

                    if (isInBounds(boardSize, new int[]{r, c - 1}) &&
                            board[r][c - 1].charAt(0) == '.') {
                        anchors.add(new int[]{r, c - 1});
                    }

                    if (isInBounds(boardSize, new int[]{r, c + 1}) &&
                            board[r][c + 1].charAt(0) == '.') {
                        anchors.add(new int[]{r, c + 1});
                    }
                }
            }
        }
            if (boardEmpty){
                anchors.add(new int[]{boardSize / 2, boardSize / 2});
            }
        return anchors;
    }

    static Move getBestMove(String[][] board) {
        List<Move> horizontalWords = getHorizontalWords(board);
        List<Move> verticalWords = getVerticalWords(board);
        int max = 0;
        Move maxMove = null;


        for (Move m : horizontalWords) {
            if (m.tilesPlayed.isEmpty()) continue;

            String[][] result = Move.playMove(board, m);
            List<int[]> move = Main.getMove(board, result);
            if (move.isEmpty()) continue;
            if (!Main.isConnected(board, move)) continue;
            if (!Main.allCrossWordsLegal(board,result,move)) continue;

            String actualWord = Main.getWord(result,move);
            if(!dictionary.search(actualWord.toLowerCase())) continue;

            boolean horizontal = Main.isHorizontalMove(result,move);
            int score = Main.getScore(board, result, move, m.word) +
                    Main.scoreCrossWord(result, board, move, horizontal);
            if (score > max) {
                max = score;
                m.word = actualWord;
                maxMove = m;
                maxMove.score = score;
            }
        }

        for (Move m : verticalWords) {
            if (m.tilesPlayed.isEmpty()) continue;
            if (!dictionary.search(m.word.toLowerCase())) continue;

            String[][] result = Move.playMove(board, m);
            List<int[]> move = Main.getMove(board, result);
            if (move.isEmpty()) continue;
            if (!Main.isConnected(board, move)) continue;
            if (!Main.allCrossWordsLegal(board,result, move)) continue;

            String actualWord = Main.getWord(result,move);
            if(!dictionary.search(actualWord.toLowerCase())) continue;

            boolean horizontal = Main.isHorizontalMove(result,move);
            int score = Main.getScore(board, result, move, m.word) +
                    Main.scoreCrossWord(result, board, move, horizontal);

            if (score > max) {
                max = score;
                m.word = actualWord;
                m.score = score;
                maxMove = m;
            }
        }
        System.out.println("horizontal words found: " + horizontalWords.size());
        System.out.println("vertical words found: " + verticalWords.size());
        return maxMove;
    }

    static List<Move> getPossibleWords(String[][] board) {
        //call getVerticalWords and getHorizontalWords and add both to list
        List<Move> allWords = new ArrayList<>();

        allWords.addAll(getHorizontalWords(board));
        allWords.addAll(getVerticalWords(board));


        return allWords;
    }

    static boolean isInBounds(int boardSize, int[] letter) {
        int r = letter[0];
        int c = letter[1];

        return r >= 0 && r < boardSize && c >= 0 && c < boardSize;
    }

    static boolean isValidMove(String[][] board, Move m) {
        if (m.tilesPlayed.isEmpty()) return false;
        if (m.word.isEmpty()) return false;
        return dictionary.search(m.word.toLowerCase());
    }

    /**
     * Only bother checking vertically since we assume plays are horizontal
     */
    static boolean formsCrossword(String[][] board, int[] crossLetter) {
        int r = crossLetter[0];
        int c = crossLetter[1];

        while (r > 0 && board[r-1][c].charAt(0) == ' ') {
            r--;
        }

        StringBuilder sb = new StringBuilder();
        while (r < board.length && board[r][c].charAt(0) == ' ') {
            sb.append(board[r][c].charAt(1));
            r++;
        }

        if (sb.length() <= 1) {
            return true;
        }

        return dictionary.search(sb.toString().toLowerCase());
    }

    static List<Move> getVerticalWords(String[][] board) {
        String[][] transposedBoard = Main.transposeBoard(board);
        List<Move> words = getHorizontalWords(transposedBoard);
        List<Move> out = new ArrayList<>();

        for (Move m : words) {
            List<int[]> untransposed = new ArrayList<>();
            for (int[] tile : m.tilesPlayed) {
                untransposed.add(new int[]{tile[1], tile[0], tile[2]});
            }

            Move untransposedMove = new Move(untransposed);
            String[][] result = Move.playMove(board, untransposedMove);
            List<int[]> moveList = Main.getMove(board, result);

            if (!moveList.isEmpty()) {
                untransposedMove.word = Main.getWord(result, moveList);

                if (Main.isConnected(board, moveList)) {
                    boolean horizontal = Main.isHorizontalMove(result, moveList);
                    untransposedMove.score =
                            Main.getScore(board, result, moveList, untransposedMove.word) +
                                    Main.scoreCrossWord(result, board, moveList, horizontal);
                }
            }

            out.add(untransposedMove);

        }
        return out;
    }

    static List<Move> getHorizontalWords(String[][] board) {
        // determine adjacent tiles
        // recursive call on each tile
        List<Move> results = new ArrayList<>();
        List<int[]> adjacentTiles = getAdjacent(board);

//
        for (int[] adj : adjacentTiles) {
            results.addAll(getWordsRecur(board, adj, hand, Direction.RIGHT, new Move(), true));
            results.addAll(getWordsRecur(board, adj, hand, Direction.LEFT, new Move(), true));

//            getWordsRecur();

        }


        return results;
    }


    static List<Move> getWordsRecur(String[][] board, int[] square,
                                    List<Square> hand, Direction d,
                                    Move currentMove, boolean turnAround) {


        List<Move> result = new ArrayList<>();
        // if current move is valid, add to list. do not return yet
        if (isValidMove(board, currentMove)) result.add(currentMove);

        int row = square[0];
        int col = square[1];


        //if square out of bounds, return empty list
        if (!isInBounds(board.length, square)) {
            return result;
        }
        //if square is a letter in board, go past it and keep searching
        if (board[row][col].charAt(0) == ' ') {
            char letter = board[row][col].charAt(1);

            Move newMove = Move.copyMove(currentMove);
            int step;

            if (d == Direction.RIGHT) {
                newMove.word += letter;
                step = 1;
            } else {
                newMove.word = letter + newMove.word;
                step = -1;
            }

            int[] newCoord = {row, col + step};

            result.addAll(getWordsRecur(board, newCoord,
                    hand, d, newMove, turnAround));

            return result;
        }

        // if square is empty, try each letter:
        for (int i = 0; i < hand.size(); i++) {
            char letter = hand.get(i).letter;

            List<Square> newHand = new ArrayList<>();
            for (int j = 0; j < hand.size(); j++) {
                if (i != j) newHand.add(new Square(hand.get(j).letter));
            }

            int[] rightCoord = {row, col + 1};
            int[] leftCoord = {row, col - 1};

            Move newMove = Move.copyMove(currentMove);
            newMove.tilesPlayed.add(new int[]{row, col, (int)letter});

            if (d == Direction.RIGHT) newMove.word += letter;
            else newMove.word = letter + newMove.word;

            String original = board[row][col];
            board[row][col] = " " + letter;
            boolean crossOk = formsCrossword(board, new int[]{row, col});
            board[row][col] = original;

            if (!crossOk) continue;


            boolean newTurnAroundRight = false;
            boolean newTurnAroundLeft = false;
            if (turnAround && d == Direction.RIGHT) {
                newTurnAroundLeft = false;
                newTurnAroundRight = true;
            }
            if (turnAround && d == Direction.LEFT) {
                newTurnAroundLeft = true;
                newTurnAroundRight = false;
            }

            boolean allowedLeft = false;
            boolean allowedRight = false;


            //turnAround is true if you have not turned around yet
            if (turnAround) {
                allowedRight = true;
                allowedLeft = true;
            } else {
                if (d == Direction.RIGHT) allowedRight = true;
                if (d == Direction.LEFT) allowedLeft = true;
            }

            if (allowedRight) result.addAll(getWordsRecur(board, rightCoord,
                    newHand, Direction.RIGHT, newMove, newTurnAroundRight));
            if (allowedLeft) result.addAll(getWordsRecur(board, leftCoord,
                    newHand, Direction.LEFT, newMove, newTurnAroundLeft));

            //result.addAll()
        }


        /**
         * if square is empty:
         * add all from recursive calls:
         *   check for each tile in hand:
         *      if cross word of using tile at coordinate is invalid, continue. else:
         *
         *      add to list: recursive call from moving left 1, removing tile from hand, adding tile to start of word
         *      add to list: recursive call from moving right 1, removing tile from hand, adding tile to end of word
         * return list
         **/

        return result;
    }

    /**
     * Main program to read in dictionary
     * @param args the command line args
     */
    public static void main(String[] args) {

        String dictPath = args[0];

        try (Scanner reader = new Scanner(new File(dictPath))) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim().toLowerCase();
                if (!line.isEmpty()) {
                    dictionary.insert(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) {
            String[][] board = Main.readBoard(in);

            // read tray
            String trayLine = in.nextLine().trim().toLowerCase();
            List<Square> hand = new ArrayList<>();
            for (char c : trayLine.toCharArray()) {
                hand.add(new Square(c)); // '*' for blank
            }

            // print input board
            System.out.println();
            System.out.println("Input Board:");
            for (int r = 0; r < Main.size; r++) {
                for (int c = 0; c < Main.size; c++) {
                    System.out.print(board[r][c]);
                    if (c < Main.size - 1) System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println("Tray: " + trayLine);

            // find best move
            Solver.setHand(hand);
            Move best = Solver.getBestMove(board);

            if (best == null) {
                System.out.println("Solution: no move found");
                continue;
            }


            // print solution
            System.out.println("Solution " + best.word + " has " + best.score + " points");

            // print solution board
            System.out.println("Solution Board:");
            String[][] result = Move.playMove(board, best);
            for (int r = 0; r < Main.size; r++) {
                for (int c = 0; c < Main.size; c++) {
                    System.out.print(result[r][c]);
                    if (c < Main.size - 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }
    }
}
