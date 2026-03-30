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


import java.util.*;


public class Scrabble extends Application {
    static List<Character> playerTray = new ArrayList<>();
    static Button[] trayButton = new Button[7];
    static int trayIndex = -1;
    static List<Character> CPUTray = new ArrayList<>();
    static Label tilesLeft = new Label();
    static Button done = new Button("Submit Move");



    public static int boardSize = 15;
    static String[][] board = {
            {"3.","..","..",".2","..","..","..","3.","..","..","..",".2","..","..","3."},
            {"..","2.","..","..","..",".3","..","..","..",".3","..","..","..","2.",".."},
            {"..","..","2.","..","..","..",".2","..",".2","..","..",".","2.","..",".."},
            {".2","..","..","2.","..","..","..",".2","..","..","..","2.","..","..",".2"},
            {"..","..","..","..","2.","..","..","..","..","..","2.","..","..","..",".."},
            {"..","3.","..","..",".","3.","..","..","..","3.","..","..",".","3.",".."},
            {"..",".",".3","..","..",".",".3",".",".3","..","..",".",".3","..",".."},
            {"3.","..","..",".2","..","..","..","2.","..","..","..",".2","..","..","3."},
            {"..",".",".3","..","..",".",".3",".",".3","..","..",".",".3","..",".."},
            {"..","3.","..","..",".","3.","..","..","..","3.","..","..",".","3.",".."},
            {"..","..","..","..","2.","..","..","..","..","..","2.","..","..","..",".."},
            {".2","..","..","2.","..","..","..",".2","..","..","..","2.","..","..",".2"},
            {"..","..","2.","..","..","..",".2","..",".2","..","..",".","2.","..",".."},
            {"..","2.","..","..",".","3.","..","..","..","3.","..","..","..",".2",".."},
            {"3.","..","..",".2","..","..","..","3.","..","..","..",".2","..","..","3."}
    };
    static List<Character> tileBag = new ArrayList<>();
    static boolean isPlaced = false;

    @Override
    public void start(Stage stage) throws Exception {
        HBox root = new HBox();
        GridPane grid = initBoard(board);
        grid.setStyle("-fx-background-color: #C3A995;");


        root.getChildren().addAll(grid,initPanel());
        Scene scene = new Scene(root);
        stage.setTitle("Scrabble");
        stage.setScene(scene);
        stage.show();
    }

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

                Button cell = new Button();
                cell.setStyle("-fx-background-color: " + color +
                        "; -fx-border-color: #000000" +
                        "; -fx-background-radius: 8" +
                        "; -fx-border-radius: 8");
                cell.setPrefSize(50, 50);
                cell.setOnAction(event -> {
                    if (trayIndex == -1){
                        cell.setStyle("-fx-background-color: " + color +
                                "; -fx-border-color: #000000" +
                                "; -fx-background-radius: 8" +
                                "; -fx-border-radius: 8");
                    } else {
                        isPlaced = true;
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
                            result.ifPresent(cell::setText);

                        } else {
                            cell.setText(String.valueOf(playerTray.get(trayIndex)));
                        }
                        playerTray.set(trayIndex, null);
                        tileBag.remove(trayIndex);
                        updateTilesLeft();
                        updateTrayUI();
                        trayIndex = -1;
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

    static String getTintedCell(String color){
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

    static void initTileBag() {
        String tiles = "AAAAAAAAABBCCDDDDEEEEEEEEEEEEFFGGGHHIIIIIIIIIJKLLLL" +
                "MMNNNNNNOOOOOOOOPPQRRRRRRSSSSTTTTTTUUUUVVWWXYYZ**";
        for (char c : tiles.toCharArray()) {
            tileBag.add(c);
        }
        Collections.shuffle(tileBag);
    }

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


        // skip turn button
        Button skipTurn = new Button("Skip Turn?");
        skipTurn.setFont(Font.font("Verdana"));
        skipTurn.setStyle("-fx-background-color: #E3DAC9" +
                "; -fx-font-weight: bold" +
                "; -fx-background-radius: 8" +
                "; -fx-border-radius: 8");
        skipTurn.setPrefWidth(160);

        // tiles remaining in bag
        tilesLeft.setText("Tiles left: " + tileBag.size());
        tilesLeft.setStyle("-fx-font-weight: bold;");
        tilesLeft.setFont(Font.font("Verdana"));

        done.setStyle("-fx-font-weight: bold;");
        done.setFont(Font.font("Verdana"));
        done.setDisable(!isPlaced);


        sidePanel.getChildren().addAll(humanScore, computerScore,
                trayLabel, tray, initTray, skipTurn, done,
                tilesLeft);

        return sidePanel;
    }

    static String getCPUTray(List<Character> CPUTray){
        StringBuilder sb = new StringBuilder();
        for (Character c : CPUTray){
            sb.append(c);
        }
        return sb.toString();
    }

    static void drawPlayerTiles(int traySlot){
        if (!tileBag.isEmpty()){
            char tile = tileBag.remove(0);
            playerTray.add(tile);
            trayButton[traySlot].setText(String.valueOf(tile));

            //TODO: add letter value to buttons
//            String val = String.valueOf(Main.letterVals(tile));
//            Label letterVal = new Label(val);

        }

    }

    static void drawCPUTiles(){
        while (CPUTray.size() < 7){
            Character tile = getTile(tileBag);
            if (tile == null){
                break;
            } else {
                CPUTray.add(tile);
            }
        }
    }

    static Character getTile(List<Character> tilebag){
        // TODO: check who scores higher if bag is empty
        if (tilebag.isEmpty()){
            return null;
        }
        Random random = new Random();
        int randIndex = random.nextInt(tilebag.size());
        return tilebag.remove(randIndex);
    }

    static void refreshTiles(List<Character> playerTray){
        for (int i = 0; i < playerTray.size(); i++){
            if (playerTray.get(i) == null){
                Character newTile = getTile(tileBag);
                if (newTile == null){
                    playerTray.set(i,newTile);
                }
            }
        }

        while (playerTray.size() < 7){
            Character newTile = getTile(tileBag);
            if (newTile == null){
                playerTray.add(newTile);
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


    

    public static void main(String[] args) {
        launch(args);
    }



}
