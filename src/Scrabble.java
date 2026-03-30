import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * CS 351: Scrabble
 *
 * Main Class to run Scrabble GUI
 *
 * @author Kiana Tarter
 */
public class Scrabble extends Application {
    static List<Character> playerTray = new ArrayList<>();
    static Button[] trayButton = new Button[7];
    static int trayIndex = -1;
    static List<Character> CPUTray = new ArrayList<>();
    static Label tilesLeft = new Label();
    static Button done = new Button("Submit Move");
    static Trie dictionary = new Trie();
    static GridPane boardGrid;

    /**
     * Size of the board
     */
    public static int boardSize = 15;
    static String[][] board = {
            {"3.","..","..",".2","..","..","..","3.","..","..","..",".2","..","..","3."},
            {"..","2.","..","..","..",".3","..","..","..",".3","..","..","..","2.",".."},
            {"..","..","2.","..","..","..",".2","..",".2","..","..","..","2.","..",".."},
            {".2","..","..","2.","..","..","..",".2","..","..","..","2.","..","..",".2"},
            {"..","..","..","..","2.","..","..","..","..","..","2.","..","..","..",".."},
            {"..","3.","..","..","..","3.","..","..","..","3.","..","..","..","3.",".."},
            {"..","..",".3","..","..","..",".3","..",".3","..","..","..",".3","..",".."},
            {"3.","..","..",".2","..","..","..","2.","..","..","..",".2","..","..","3."},
            {"..","..",".3","..","..","..",".3","..",".3","..","..","..",".3","..",".."},
            {"..","3.","..","..","..","3.","..","..","..","3.","..","..","..","3.",".."},
            {"..","..","..","..","2.","..","..","..","..","..","2.","..","..","..",".."},
            {".2","..","..","2.","..","..","..",".2","..","..","..","2.","..","..",".2"},
            {"..","..","2.","..","..","..",".2","..",".2","..","..","..","2.","..",".."},
            {"..","2.","..","..","..","3.","..","..","..","3.","..","..","..",".2",".."},
            {"3.","..","..",".2","..","..","..","3.","..","..","..",".2","..","..","3."}
    };
    static List<Character> tileBag = new ArrayList<>();
    static boolean isPlaced = false;
    static int humanScore = 0;
    static int CPUScore = 0;

    static Label humanScoreLabel = new Label("Human Score: " + humanScore);
    static Label CPUScoreLabel = new Label("CPU Score: " + CPUScore);

    static String[][] boardBefore = null;

    /**
     * Method to handle GUI components
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        Main.size = boardSize;
        HBox root = new HBox();
        boardGrid = initBoard(board);
        boardGrid.setStyle("-fx-background-color: #C3A995;");


        root.getChildren().addAll(boardGrid,initPanel());
        Scene scene = new Scene(root);
        stage.setTitle("Scrabble");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Method to initialize a board GUI
     * @param board the string representation of the board
     * @return the GridPane representation of the board
     */
    public GridPane initBoard (String[][] board){
        GridPane grid = new GridPane();

        for(int r = 0; r < boardSize; r++){
            for (int c = 0; c < boardSize; c++){

                String token = board[r][c];
                String color;

                if (token.charAt(0) == ' ') {
                    // placed tile
                    color = "#F5DEB3"; // wheat/wood
                } else if (token.equals("3.")) {
                    color = "#1B4D3E"; // dark green
                } else if (token.equals("2.")) {
                    color = "#E36888"; // pink - double word
                } else if (token.equals(".3")) {
                    color = "#457B9D"; // dark blue - triple letter
                } else if (token.equals(".2")) {
                    color = "#105666"; // light blue - double letter
                } else {
                    color = "#E3DAC9"; // cream - plain
                }
                int finalR = r;
                int finalC = c;

                Button cell = new Button();
                cell.setStyle("-fx-background-color: " + color +
                        "; -fx-border-color: #000000" +
                        "; -fx-background-radius: 8" +
                        "; -fx-border-radius: 8");
                cell.setPrefSize(50, 50);
                cell.setOnAction(event -> {
                    if (board[finalR][finalC].charAt(0) == ' ') {
                        return;
                    }
                    if (trayIndex == -1){
                        cell.setStyle("-fx-background-color: " + color +
                                "; -fx-border-color: #000000" +
                                "; -fx-background-radius: 8" +
                                "; -fx-border-radius: 8");
                    } else {
                        if (!isPlaced){
                            boardBefore = copyBoard(board);
                        }
                        cell.setStyle("-fx-background-color: " +
                                getTintedCell(color) +
                                "; -fx-border-color: gold" +
                                "; -fx-background-radius: 8" +
                                "; -fx-border-radius: 8");
                        cell.setTextFill(Paint.valueOf("#FFFFFF"));
                        if (playerTray.get(trayIndex) == '*'){
                            TextInputDialog enterBlank = new TextInputDialog();
                            enterBlank.setTitle("Blank Tile!");
                            enterBlank.setContentText("What letter would you like to play?");

                            Optional<String> result = enterBlank.showAndWait();
                            result.ifPresent(character -> {
                                char chosen = Character.toUpperCase(character.charAt(0));
                                cell.setText(String.valueOf(chosen));
                                board[finalR][finalC] = " " + chosen;
                            });
                        } else {
                            char played = Character.toLowerCase(playerTray.get(trayIndex));
                            cell.setText(String.valueOf(Character.toUpperCase(played)));
                            board[finalR][finalC] = " " + played;
                        }
                        playerTray.set(trayIndex, null);
                        updateTilesLeft();
                        updateTrayUI();
                        trayIndex = -1;
                        isPlaced = true;
                        done.setDisable(false);
                    }
                });

                grid.setHgap(3);
                grid.setVgap(3);
                grid.setPadding(new Insets(10));
                grid.add(cell, c, r);
            }
        }
        return grid;
    }

    /**
     * Get a tinted version of the cell selected
     * @param color the current color before the tint is applied
     * @return the tinted version of the color
     */
    public static String getTintedCell(String color){
        switch (color){
            case "#F5DEB3":
                return "#E8C98A";

            case "#1B4D3E":
                return "#112e25";

            case "#E36888":
                return "#CC4D6F";

            case "#457B9D":
                return "#336180";

            case "#105666":
                return "#0A3D4A";

            case "#E3DAC9":
                return "#C9BBA8";

            default: return "#E3DAC9";
        }

    }

    /**
     * Initializes a tile bag
     */
    public static void initTileBag() {
        String tiles = "AAAAAAAAABBCCDDDDEEEEEEEEEEEEFFGGGHHIIIIIIIIIJKLLLL" +
                "MMNNNNNNOOOOOOOOPPQRRRRRRSSSSTTTTTTUUUUVVWWXYYZ**";
        for (char c : tiles.toCharArray()) {
            tileBag.add(c);
        }
        Collections.shuffle(tileBag);
    }

    /**
     * Initializes a panel for the side controls
     * @return Vbox representing controls
     */
    public VBox initPanel (){
        VBox sidePanel = new VBox(10);
        sidePanel.setPrefWidth(400);
        sidePanel.setPadding(new Insets(20));
        sidePanel.setStyle("-fx-background-color: #C3A995;");

        Label computerScore = new Label();
        Label humanScore = new Label();

        Label trayLabel = new Label("Your tray:");
        trayLabel.setStyle("-fx-font-weight: bold;");
        trayLabel.setFont(Font.font("Verdana"));

        HBox tray = new HBox(3);
        for (int i = 0; i < 7; i++){
            trayButton[i] = new Button("?");
            trayButton[i].setFont(Font.font("Verdana"));
            trayButton[i].setStyle("-fx-background-color: #E3DAC9" +
                    "; -fx-font-weight: bold" +
                    "; -fx-background-radius: 8" +
                    "; -fx-border-radius: 8");

            trayButton[i].setPrefSize(60,60);
            tray.getChildren().add(trayButton[i]);
            int finalI = i;
            trayButton[i].setOnAction(event -> {
                if (trayIndex == -1){ // if nothing is selected, select it
                    trayButton[finalI].setStyle("-fx-background-color: #C9BBA8" +
                            "; -fx-font-weight: bold" +
                            "; -fx-border-color: gold" +
                            "; -fx-background-radius: 8" +
                            "; -fx-border-radius: 8");
                    trayIndex = finalI;
                } else if (trayIndex == finalI){ // if double click, deselect
                    trayButton[finalI].setStyle("-fx-background-color: #E3DAC9" +
                            "; -fx-font-weight: bold" +
                            "; -fx-background-radius: 8" +
                            "; -fx-border-radius: 8");
                } else {
                    trayButton[trayIndex].setStyle("-fx-background-color: #E3DAC9" +
                            "; -fx-font-weight: bold" +
                            "; -fx-background-radius: 8" +
                            "; -fx-border-radius: 8");
                    trayButton[finalI].setStyle("-fx-background-color: #C9BBA8" +
                            "; -fx-font-weight: bold" +
                            "; -fx-border-color: gold" +
                            "; -fx-background-radius: 8" +
                            "; -fx-border-radius: 8");
                    trayIndex = finalI;
                }
            });

        }

        // initialize human player tray
        Button initTray = new Button("Initialize Tray");
        initTray.setStyle("-fx-background-color: #E3DAC9" +
                "; -fx-font-weight: bold" +
                "; -fx-background-radius: 8" +
                "; -fx-border-radius: 8");
        initTray.setFont(Font.font("Verdana"));

        initTray.setOnAction(event -> {
            initTileBag();
            for (int i = 0; i < 7; i++){
                drawPlayerTiles(i);
            }
            drawCPUTiles();
            updateTrayUI();
            updateTilesLeft();
            initTray.setVisible(false);
            CPU.CPUMove();

        });

        initTray.setOnMouseEntered(event -> {
                initTray.setStyle("-fx-background-color: #E3DAC9" +
                        "; -fx-font-weight: bold" +
                        "; -fx-background-radius: 8" +
                        "; -fx-border-radius: 8");
        } );

        initTray.setOnMouseExited(event -> {
            initTray.setStyle("-fx-background-color: #E3DAC9" +
                    "; -fx-font-weight: bold" +
                    "; -fx-background-radius: 8" +
                    "; -fx-border-radius: 8");

        });
        initTray.setPrefWidth(160);

        humanScoreLabel.setStyle("-fx-font-weight: bold;");
        humanScoreLabel.setFont(Font.font("Verdana"));

        CPUScoreLabel.setStyle("-fx-font-weight: bold;");
        CPUScoreLabel.setFont(Font.font("Verdana"));

        // skip turn button
        Button skipTurn = new Button("Skip Turn?");
        skipTurn.setFont(Font.font("Verdana"));
        skipTurn.setStyle("-fx-background-color: #E3DAC9" +
                "; -fx-font-weight: bold" +
                "; -fx-background-radius: 8" +
                "; -fx-border-radius: 8");
        skipTurn.setPrefWidth(160);
        skipTurn.setOnAction(event -> {
            CPU.CPUMove();
        });

        // tiles remaining in bag
        tilesLeft.setText("Tiles left: " + tileBag.size());
        tilesLeft.setStyle("-fx-font-weight: bold;");
        tilesLeft.setFont(Font.font("Verdana"));

        done.setStyle("-fx-font-weight: bold;");
        done.setFont(Font.font("Verdana"));
        done.setDisable(!isPlaced);

        done.setOnAction(event -> {
            checkHumanMove();
            CPU.CPUMove();
        });


        sidePanel.getChildren().addAll(humanScore, computerScore,
                trayLabel, tray, initTray, skipTurn, done,
                tilesLeft, humanScoreLabel, CPUScoreLabel);

        return sidePanel;
    }


    /**
     * Method to draw tiles for the human player
     * @param traySlot the index of the tray slot
     */
    public static void drawPlayerTiles(int traySlot){
        if (!tileBag.isEmpty()){
            char tile = tileBag.remove(0);
            playerTray.add(tile);
            trayButton[traySlot].setText(String.valueOf(tile));
        }

    }

    /**
     * Draws tiles for the CPU player
     */
    public static void drawCPUTiles(){
        while (CPUTray.size() < 7){
            Character tile = getTile(tileBag);
            if (tile == null){
                break;
            } else {
                CPUTray.add(tile);
            }
        }
    }

    /**
     * Get a random tile from the tilebag
     * @param tilebag the tile bag
     * @return the random tile
     */
    public static Character getTile(List<Character> tilebag){
        // TODO: check who scores higher if bag is empty
        if (tilebag.isEmpty()){
            if (humanScore > CPUScore){
                Gamestate winner = Gamestate.HUMAN_WINS;
            } else {
                Gamestate winner = Gamestate.CPU_WINS;

            }
        }
        Random random = new Random();
        int randIndex = random.nextInt(tilebag.size());
        return tilebag.remove(randIndex);
    }

    /**
     * Replace tray tiles once they've been played
     * @param playerTray current tray
     */
    public static void refreshTiles(List<Character> playerTray){
        for (int i = 0; i < playerTray.size(); i++){
            if (playerTray.get(i) == null){
                Character newTile = getTile(tileBag);
                if (newTile != null){
                    playerTray.set(i,newTile);
                }
            }
        }

        while (playerTray.size() < 7){
            Character newTile = getTile(tileBag);
            if (newTile != null){
                playerTray.add(newTile);
            } else {
                break;
            }
        }
        updateTilesLeft();

    }

    static void updateTrayUI(){
        for (int i = 0; i < trayButton.length; i++){
            if (playerTray.get(i) != null){
                trayButton[i].setText(String.valueOf(playerTray.get(i)));
            } else {
                trayButton[i].setText("");
            }
            trayButton[i].setStyle("-fx-background-color: #E3DAC9" +
                    "; -fx-font-weight: bold" +
                    "; -fx-background-radius: 8" +
                    "; -fx-border-radius: 8");
        }

    }

    static void updateTilesLeft(){
        tilesLeft.setText("Tiles left: " + tileBag.size());
    }

    static String[][] copyBoard(String[][] original){
        String[][] copy = new String[original.length][original[0].length];
        for (int r = 0; r < original.length; r++){
            for (int c = 0; c < original[0].length; c++){
                copy[r][c] = original[r][c];
            }
        }
        return copy;
    }

    static void checkHumanMove(){
        if (boardBefore == null){
            System.out.println("no board saved");
            return;
        }

        List<int[]> move = Main.getMove(boardBefore, board);

        if (move.isEmpty()){
            System.out.println("no tiles detected");
            return;
        }

        String word = Main.getWord(board, move);
        System.out.println(word);


        int score = Main.validBoardChange(boardBefore, board);
        System.out.println("Word being checked: " + Main.getWord(board, Main.getMove(boardBefore, board)));
        System.out.println("Main dictionary knows CAT? " + Main.dictionary.search("cat"));

        if (score < 0){
            System.out.println("Invalid Move.");
            // TODO: remove tiles for invalid move
            return;
        }
        System.out.println(score);

        humanScore += score;
        humanScoreLabel.setText("Human Score: " + humanScore);

        refreshTiles(playerTray);
        updateTrayUI();


        boardBefore = null;
        isPlaced = false;


    }
    static void redrawBoard() {
        boardGrid.getChildren().clear();

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                String token = board[r][c];
                String color;

                if (token.charAt(0) == ' ') {
                    color = "#F5DEB3"; // placed tile
                } else if (token.equals("3.")) {
                    color = "#1B4D3E";
                } else if (token.equals("2.")) {
                    color = "#E36888";
                } else if (token.equals(".3")) {
                    color = "#457B9D";
                } else if (token.equals(".2")) {
                    color = "#105666";
                } else {
                    color = "#E3DAC9";
                }

                Button cell = new Button();
                cell.setPrefSize(50, 50);
                cell.setStyle("-fx-background-color: " + color +
                        "; -fx-border-color: #000000" +
                        "; -fx-background-radius: 8" +
                        "; -fx-border-radius: 8");

                if (token.charAt(0) == ' ') {
                    cell.setText(String.valueOf(token.charAt(1)));
                    cell.setTextFill(Paint.valueOf("#FFFFFF"));
                }

                int finalR = r;
                int finalC = c;

                cell.setOnAction(event -> {
                    if (board[finalR][finalC].charAt(0) == ' ') {
                        return;
                    }

                    if (trayIndex == -1) {
                        return;
                    }

                    if (!isPlaced) {
                        boardBefore = copyBoard(board);
                    }

                    cell.setStyle("-fx-background-color: " +
                            getTintedCell(color) +
                            "; -fx-border-color: gold" +
                            "; -fx-background-radius: 8" +
                            "; -fx-border-radius: 8");
                    cell.setTextFill(Paint.valueOf("#FFFFFF"));

                    if (playerTray.get(trayIndex) == '*') {
                        TextInputDialog enterBlank = new TextInputDialog();
                        enterBlank.setTitle("Blank Tile!");
                        enterBlank.setContentText("What letter would you like to play?");

                        Optional<String> result = enterBlank.showAndWait();
                        result.ifPresent(character -> {
                            char chosen = Character.toUpperCase(character.charAt(0));
                            cell.setText(String.valueOf(chosen));
                            board[finalR][finalC] = " " + chosen;
                        });
                    } else {
                        char played = Character.toLowerCase(playerTray.get(trayIndex));
                        cell.setText(String.valueOf(Character.toUpperCase(played)));
                        board[finalR][finalC] = " " + played;
                    }

                    playerTray.set(trayIndex, null);
                    updateTrayUI();
                    trayIndex = -1;
                    isPlaced = true;
                    done.setDisable(false);
                });

                boardGrid.add(cell, c, r);
            }
        }

        boardGrid.setHgap(3);
        boardGrid.setVgap(3);
        boardGrid.setPadding(new Insets(10));
        boardGrid.setStyle("-fx-background-color: #C3A995;");
    }

    /**
     * Main program to run GUI
     * @param args command line args (ignored)
     */
    public static void main(String[] args) {
        try (Scanner reader = new Scanner(new File("sowpods.txt"))){
            while (reader.hasNextLine()){
                String line = reader.nextLine().trim().toLowerCase();
                if (!line.isEmpty()){
                    dictionary.insert(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("dictionary not found");
            throw new RuntimeException(e);
        }
        Main.size = boardSize;
        Main.dictionary = dictionary;
        Solver.dictionary = dictionary;
        launch(args);
    }
}
