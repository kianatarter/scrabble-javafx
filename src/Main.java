import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CS 351: Scrabble
 *
 * Main class used to implement score checking
 *
 * @author Kiana Tarter
 */
public class Main {
    static Trie dictionary = new Trie();
    static int size = 0;

    /**
     * Main program to read in dictionary + run score checking logic
     *
     * @param args command-line args
     *
     */
    public static void main (String[] args){
        String dictPath = args[0];

        try (Scanner reader = new Scanner(new File(dictPath))) {
            while (reader.hasNextLine()){
                String line = reader.nextLine().trim().toLowerCase();
                if (!line.isEmpty()){
                    dictionary.insert(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Scanner in = new Scanner(System.in);
        while(in.hasNextInt()){
            String[][] board = readBoard(in);
            String[][] afterBoard = readBoard(in);

            System.out.println();

            System.out.println("original board: ");
            for (int r = 0; r < size; r++){
                for (int c = 0; c < size; c++){
                    System.out.print(board[r][c]);
                    if (c < size - 1){
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }

            System.out.println();

            System.out.println("result board: ");
            for (int r = 0; r < size; r++){
                for (int c = 0; c < size; c++){
                    System.out.print(afterBoard[r][c]);
                    if (c < size - 1){
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }

            String boardError = validateBoards(board,afterBoard);
            if (boardError != null){
                System.out.println(boardError);
                continue;
            }

            List<int[]> move = getMove(board,afterBoard);
            if (move.isEmpty()) {
                System.out.println("play is empty");
                System.out.println("play is not legal ");
                continue;
            }

            String word = getWord(afterBoard, move);
            StringBuilder sb = new StringBuilder("play is ");
            for (int i = 0; i < move.size(); i++) {
                char letter = afterBoard[move.get(i)[0]]
                        [move.get(i)[1]].charAt(1);

                sb.append(letter + " at (" + move.get(i)[0] + ", " +
                        move.get(i)[1] + ")");
                if (i < move.size() - 1) sb.append(", ");
            }
            System.out.println(sb);


            if (isLegal(word) && isConnected(board,move)){
                System.out.println("play is legal");
                int total = getTotalScore(board, afterBoard);
                System.out.println("score is " + total);
            } else if (move.isEmpty()) {
                System.out.println("play is empty");
                System.out.println("play is not legal");
            } else {
                System.out.println("play is not legal");
            }

            word = "";
            move.clear();

        }

    }

    /**
     * Checks if two input boards show a valid change
     * @param board the before board
     * @param afterBoard the board after a move/change is applied
     * @return -1 if invalid, otherwise return the score of the move
     */
    public static int validBoardChange(String[][] board, String[][] afterBoard) {
        List<int[]> move = getMove(board,afterBoard);

        if (move.isEmpty()) {
            return -1;
        }

        String word = getWord(afterBoard, move);

        if (isLegal(word) && isConnected(board,move)){
            int total = getTotalScore(board, afterBoard);
            return total;
        } else if (move.isEmpty()) {
            return -1;
        } else {
            return -1;
        }



    }

    /**
     * Read in the given input board
     * @param in Scanner to read standard input
     * @return the String 2D array representing the board
     */
    public static String[][] readBoard(Scanner in){
        size = in.nextInt();
        in.nextLine();
        String[][] board = new String[size][size];
        for (int row = 0; row < size; row++){
            String line = in.nextLine();
            String[] tokens = line.split(" ");
            int tokenIndex = 0;
            for (int col = 0; col < size; col++){

                while (tokens[tokenIndex].isEmpty()){
                    tokenIndex++;
                }
                String token = tokens[tokenIndex];
                tokenIndex++;

                if (token.length() == 1){
                    token = " " + token;
                }
                board[row][col] = token;
            }
        }
        return board;
    }

    /**
     * Get the move given two boards
     * @param before the board before a move is applied
     * @param after the board after a move is applied
     * @return a list of coordinates representing the move
     */
    public static List<int[]> getMove(String[][] before, String[][] after){
        List<int[]> move = new ArrayList<>();
        for (int r = 0; r < size; r++){
            for (int c = 0; c < size; c++){
                if (!before[r][c].equals(after[r][c])){
                    if (after[r][c].charAt(0) != '.'){
                        move.add(new int[]{r,c});
                    }
                }
            }
        }

        return move;
    }

    /**
     * Get the word from a move
     * @param after the board after the move is played
     * @param move the list of coords representing the move
     * @return the String representing the word
     */
    public static String getWord(String[][] after, List<int[]> move){
        StringBuilder sb = new StringBuilder();

        boolean horizontal = isHorizontalMove(after, move);
        int row = move.get(0)[0];
        int col = move.get(0)[1];


        if (horizontal){
            while (col > 0 && after[row][col - 1].charAt(0) == ' '){
                col--;
            }
            while (col < size && after[row][col].charAt(0) == ' '){
                sb.append(after[row][col].charAt(1));
                col++;
            }
        } else {
            while (row > 0 && after[row - 1][col].charAt(0) == ' '){
                row--;
            }
            while (row < size && after[row][col].charAt(0) == ' '){
                sb.append(after[row][col].charAt(1));
                row++;
            }
        }

        return sb.toString();
    }


    /**
     * Transpose the board
     * @param board the board to transpose
     * @return the board after it's rows and cols have been switched
     */
    public static String[][] transposeBoard(String[][] board){
        int rows = board.length;
        int cols = board[0].length;

        String[][] transposed = new String[cols][rows];

        for (int i = 0; i < rows; i++){
            for (int j = 0 ; j < cols; j++){
                transposed[j][i] = board[i][j];
            }
        }
        return transposed;
    }


    /**
     * Check if the word is legal
     * @param word the word being checked
     * @return true if legal, false if not
     */
    public static boolean isLegal(String word){
        return dictionary.search(word.toLowerCase());
    }

    /**
     * Point value of each letter
     * @param c the letter
     * @return the integer value of the letter
     */
    public static int letterVals (char c){
        if (Character.isUpperCase(c)) {
            return 0;
        }

        switch (c){
            case 'a': case 'e': case 'i': case 'o': case 'u':
            case 'l': case 'n': case 's': case 't': case 'r':
                return 1;
            case 'd': case 'g':
                return 2;
            case 'b': case 'c': case 'm': case 'p':
                return 3;
            case 'f': case 'h': case 'v': case 'w': case 'y':
                return 4;
            case 'k':
                return 5;
            case 'j': case 'x':
                return 8;
            case 'q': case 'z':
                return 10;
            default:
                return 0;
        }
    }

    /**
     * Get the score of a given play
     * @param before the board before the play
     * @param after the board after the play
     * @param move the move played
     * @param word the word played
     * @return the score of the move
     */
    public static int getScore(String[][] before, String[][] after, List<int[]> move, String word){
        int total = 0;
        int totalWordMult = 1;
        int wordMult = 1;

        boolean horizontal = isHorizontalMove(after, move);
        int r = move.get(0)[0];
        int c = move.get(0)[1];

        if (horizontal){
            while (c > 0 && after[r][c-1].charAt(0) == ' '){
                c--;
            }
        } else {
            while (r > 0 && after[r-1][c].charAt(0) == ' '){
                r--;
            }
        }

        while ( r < size && c < size && after[r][c].charAt(0) == ' '){
            String square = before[r][c];
            boolean isNew = isInMove(move,r,c);
            int letterMult = 1;

            if (isNew) {
                if (square.charAt(1) == '2') {
                    letterMult = 2;

                } else if (square.charAt(1) == '3') {
                    letterMult = 3;
                } else {
                    letterMult = 1;
                }

                if (square.charAt(0) == '2') {
                    totalWordMult *= 2;
                } else if (square.charAt(0) == '3') {
                    totalWordMult *= 3;
                }
            }
            total += letterVals(after[r][c].charAt(1)) * letterMult;
            if (horizontal){
                c++;
            } else {
                r++;
            }

        }
        total *= totalWordMult;

        if (move.size() == 7){
            total += 50;
        }


        return total;
    }

    /**
     * Get the score of the crossword
     * @param after the board after the move is applied
     * @param before the board before the move is applied
     * @param move the move applied
     * @param horizontal boolean to check if the word is horizontal
     * @return score of the crosswords
     */
    public static int scoreCrossWord(String[][] after, String[][] before,
                              List<int[]> move, boolean horizontal){
        int total = 0;

        String[][] board = horizontal ? transposeBoard(after) : after;
        String[][] beforeBoard = horizontal ? transposeBoard(before) : before;

        for (int[] tile : move){
            int row = horizontal ? tile[1] : tile[0];
            int col = horizontal ? tile[0] : tile[1];

            while (col > 0 && board[row][col - 1].charAt(0) == ' '){
                col--;
            }

            StringBuilder sb = new StringBuilder();
            int wordMult = 1;
            int crossTotal = 0;

            while (col < size && board[row][col].charAt(0) == ' '){
                String square = beforeBoard[row][col];
                int letterMult = 1;

                boolean isNew;
                if (horizontal) {
                    isNew = isInMove(move, col, row); // transpose back
                } else {
                    isNew = isInMove(move, row, col);
                }

                if (isNew) {
                    if (square.charAt(1) == '2'){
                        letterMult = 2;
                    } else if (square.charAt(1) == '3'){
                        letterMult = 3;
                    }

                    if (square.charAt(0) == '2'){
                        wordMult *= 2;
                    } else if (square.charAt(0) == '3'){
                        wordMult *= 3;
                    }
                }

                crossTotal += letterVals(board[row][col].charAt(1)) * letterMult;
                sb.append(board[row][col].charAt(1));
                col++;
            }

            if (sb.length() > 1){
                total += crossTotal * wordMult;
            }
        }

        return total;

    }

    static boolean isInMove(List<int[]> move, int r, int c) {
        for (int[] tile : move) {
            if (tile[0] == r && tile[1] == c) return true;
        }
        return false;
    }

    static boolean isConnected(String[][] before, List<int[]> move){
        // if the board is empty, only one tile needs to cover the center tile
        int row = before.length;
        int col = before[0].length;

        if (isBoardEmpty(before)){
            int center = size / 2;

            for (int[] tile : move){
                if (tile[0] == center && tile[1] == center){
                    return true;
                }
            }
            return false;
        }


        for (int[] square : move){
            int r = square[0];
            int c = square[1];

            if (r > 0 && before[r-1][c].charAt(0) == ' '){
                return true;
            }
            if (r < size - 1 && before[r+1][c].charAt(0) == ' '){
                return true;
            }
            if (c > 0 && before[r][c-1].charAt(0) == ' '){
                return true;
            }
            if (c < size - 1 && before[r][c+1].charAt(0) == ' '){
                return true;
            }
        }
        return false;
    }

    static boolean isBoardEmpty(String[][] board){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++){
                if (board[i][j].charAt(0) == ' '){
                    return false;
                }
            }
        }
        return true;

    }

    static String validateBoards(String[][] before, String[][] after){
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                String b = before[i][j];
                String a = after[i][j];

                if (b.charAt(0) == ' ' && !b.equals(a)){
                    return "Incompatible boards: tile removed at (" + i + ", "
                            + j + ")";
                }

                if (b.charAt(0) != ' ' && a.charAt(0) != ' ' && !b.equals(a)){
                    return "Incompatible boards: multiplier mismatch at (" + i +
                            ", " + j + ")";
                }
            }
        }
        return null;
    }

    static int getTotalScore (String[][] before, String[][] after){
        List<int[]> move = getMove(before, after);
        if (move.isEmpty()) return 0;

        String word = getWord(after, move);
        boolean horizontal = isHorizontalMove(after, move);

        int crosswordScore = scoreCrossWord(after, before, move, horizontal);
        int regScore = getScore(before, after, move, word);

        return crosswordScore + regScore;
    }

    static boolean isHorizontalMove(String[][] after, List<int[]> move) {
        if (move.isEmpty()) return true;

        int r = move.get(0)[0];
        int c = move.get(0)[1];

        if (move.size() == 1) {
            boolean hasLeftRight =
                    (c > 0 && after[r][c - 1].charAt(0) == ' ') ||
                            (c < size - 1 && after[r][c + 1].charAt(0) == ' ');

            boolean hasUpDown =
                    (r > 0 && after[r - 1][c].charAt(0) == ' ') ||
                            (r < size - 1 && after[r + 1][c].charAt(0) == ' ');

            if (hasUpDown && !hasLeftRight) {
                return false;
            }
            return true;
        }

        return move.get(0)[0] == move.get(1)[0];
    }

    static boolean allCrossWordsLegal(String[][] before, String[][] after, List<int[]> move) {
        boolean horizontal = isHorizontalMove(after, move);

        for (int[] tile : move) {
            int r = tile[0];
            int c = tile[1];

            StringBuilder sb = new StringBuilder();

            if (horizontal) {
                // check vertical crossword
                int row = r;
                while (row > 0 && after[row - 1][c].charAt(0) == ' ') {
                    row--;
                }
                while (row < size && after[row][c].charAt(0) == ' ') {
                    sb.append(after[row][c].charAt(1));
                    row++;
                }
            } else {
                // check horizontal crossword
                int col = c;
                while (col > 0 && after[r][col - 1].charAt(0) == ' ') {
                    col--;
                }
                while (col < size && after[r][col].charAt(0) == ' ') {
                    sb.append(after[r][col].charAt(1));
                    col++;
                }
            }

            if (sb.length() > 1 && !dictionary.search(sb.toString().toLowerCase())) {
                return false;
            }
        }

        return true;
    }


}
