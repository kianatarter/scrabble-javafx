import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class Scrabble extends Application {
    static List<Character> playerTray = new ArrayList<>();
    static Button[] trayButton = new Button[7];


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
                    color = "#1B4D3E";
                    System.out.println("found triple");// red - triple word
                } else if (token.equals("2.")) {
                    color = "#E36888"; // pink - double word
                    System.out.println("found double word");
                } else if (token.equals(".3")) {
                    color = "#457B9D"; // dark blue - triple letter
                    System.out.println("found triple letter");
                } else if (token.equals(".2")) {
                    color = "#105666"; // light blue - double letter
                    System.out.println("found double letter");
                } else {
                    color = "#E3DAC9"; // cream - plain
                }

                Button cell = new Button();
                cell.setStyle("-fx-background-color: " + color +
                        "; -fx-border-color: #000000" +
                        "; -fx-background-radius: 8" +
                        "; -fx-border-radius: 8");
                cell.setPrefSize(50, 50);

                grid.setHgap(3);
                grid.setVgap(3);
                grid.setPadding(new Insets(10));

                grid.add(cell, c, r);

            }
        }
        return grid;
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
        sidePanel.setStyle("-fx-background-color: C3A995;");

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
        }

        // initialize human player tray
        Button initTray = new Button("Initialize Tray");
        initTray.setStyle("-fx-background-color: #E3DAC9" +
                "; -fx-font-weight: bold" +
                "; -fx-background-radius: 8" +
                "; -fx-border-radius: 8");
        initTray.setFont(Font.font("Verdana"));

        // TODO: make random tray generator
        initTray.setOnAction(event -> {
            initTileBag();
            for (int i = 0; i < 7; i++){
                drawTiles(i);
            }
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





        // skip turn button
        Button skipTurn = new Button("Skip Turn?");
        skipTurn.setFont(Font.font("Verdana"));
        skipTurn.setStyle("-fx-background-color: #E3DAC9" +
                "; -fx-font-weight: bold" +
                "; -fx-background-radius: 8" +
                "; -fx-border-radius: 8");
        skipTurn.setPrefWidth(160);

        // tiles remaining in bag
        Label tilesLeft = new Label("Tiles left: 93");

        sidePanel.getChildren().addAll(humanScore, computerScore,
                trayLabel, tray, initTray, skipTurn);



        return sidePanel;
    }

    static void drawTiles(int traySlot){
        if (!tileBag.isEmpty()){
            char tile = tileBag.remove(0);
            playerTray.add(tile);
            trayButton[traySlot].setText(String.valueOf(tile));
        }
    }







    public static void main(String[] args) {
        launch(args);
    }

}
