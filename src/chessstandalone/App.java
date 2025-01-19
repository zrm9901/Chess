package chessstandalone;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class App extends Application {
    String turn = "white";
    String originalPos;
    public GridPane bottomRight;
    ArrayList<String> movableSpaces = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) {
        bottomRight = new GridPane();
        bottomRight.setStyle("-fx-background-color:rgb(39, 184, 31);");


        for (int i = 0; i < 8; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(12.5);  
            bottomRight.getColumnConstraints().add(col);
        }

        for (int i = 0; i < 8; i++) {
            RowConstraints x = new RowConstraints();
            x.setPercentHeight(12.5);  
            bottomRight.getRowConstraints().add(x);
        }

        
        
       setup();

        Scene scene = new Scene(bottomRight, 850, 850);
        primaryStage.setTitle("Chess");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    EventHandler<ActionEvent> board = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            StackPane pane = (StackPane) button.getParent();
            Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
            String pos = (String) pane.getUserData();
            int row = pos.charAt(0) - '0';
            int col = pos.charAt(2) - '0';
            String color = (String) dataMap.get("color");
            String piece = (String) dataMap.get("piece");
            if (movableSpaces.isEmpty()) {
                if (color.equals(turn)) {
                    if (piece.equals("Knight")) {
                        movableSpaces.addAll(horse(row, col, color));
                        originalPos = pos;
                        updateImages();
                    }
                    if (piece.equals("Bishop")) {
                        movableSpaces.addAll(lines(new int[][] {{-1,-1}, {1,1}, {-1,1}, {1,-1}}, row, col, color));
                        originalPos = pos;
                        updateImages();
                    }
                    if (piece.equals("Rook")) {
                        movableSpaces.addAll(lines(new int[][] {{-1,0}, {1,0}, {0,1}, {0,-1}}, row, col, color));
                        originalPos = pos;
                        updateImages();
                    }
                    if (piece.equals("Pawn")) {
                        movableSpaces.addAll(pawn(row, col, color));
                        originalPos = pos;
                        updateImages();
                    }
                    if (piece.equals("Queen")) {
                        movableSpaces.addAll(lines(new int[][] {{-1,-1}, {1,1}, {-1,1}, {1,-1}, {-1,0}, {1,0}, {0,1}, {0,-1}}, row, col, color));
                        originalPos = pos;
                        updateImages();
                    }
                    if (piece.equals("King")) {
                        movableSpaces.addAll(king(row, col, color));
                        originalPos = pos;
                        updateImages();
                    }
                }
            } else {
                boolean movedWrongly = true;
                Search:
                for (String x : movableSpaces) {
                    if (x.equals(row + "," + col)) {
                        move(originalPos, x);
                        movableSpaces.clear();
                        updateImages();
                        if (turn.equals("white")) {
                            turn = "black";  
                            if (!checkKings("black")) {
                                winText("White");
                                break Search;
                            }
                        } else {
                            turn = "white";
                            if (!checkKings("white")) {
                                winText("Black");
                                break Search;
                            }
                        }
                        updateImages();
                        originalPos = null;
                        movedWrongly = false;
                        break Search;
                    }
                }
                if (movedWrongly) {
                    movableSpaces.clear();
                    originalPos = null;
                    updateImages();
                }
            } 
            

            
        }
    };
    public static void main(String[] args) {
        launch(args);
    }

    public void setup() {
        board();
        whitePieces();
        blackPieces();
        updateImages();
        turn = "white";
        originalPos = null;
        movableSpaces.clear();
    }
    
    Map<String, StackPane> paneMap = new HashMap<>();

    public void board() {
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                StackPane pane = new StackPane();
                Button button = new Button();
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("row", row);
                dataMap.put("col", col);
                dataMap.put("piece", "");
                dataMap.put("color", "");
                button.setUserData(dataMap);
                button.setMaxHeight(Double.MAX_VALUE);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setOnAction(board);
                if (((row + 1) % 2) != 0) {
                    if (((col + 1) % 2) != 0) {
                        button.setStyle("-fx-background-color:rgb(236, 238, 210);");
                    } else {
                        button.setStyle("-fx-background-color:rgb(118, 150, 86);");
                    }
                } else {
                    if (((col + 1) % 2) != 0) {
                        button.setStyle("-fx-background-color:rgb(118, 150, 86);");
                    } else {
                        button.setStyle("-fx-background-color:rgb(236, 238, 210);");
                    }
                }
                
                pane.getChildren().add(button);
                pane.setUserData(row + "," + col);
                bottomRight.add(pane, row, col);
                paneMap.put((row + "," + col), pane);   
            }
    }
    public void whitePieces() {
        putPieces("0,0", "white", "Rook");
        putPieces("7,0", "white", "Rook");
        putPieces("1,0", "white", "Knight");
        putPieces("6,0", "white", "Knight");
        putPieces("2,0", "white", "Bishop");
        putPieces("5,0", "white", "Bishop");
        putPieces("3,0", "white", "Queen");
        putPieces("4,0", "white", "King");
        for (int i = 0; i < 8; i++) {
            putPieces(i + ",1", "white", "Pawn");
        }
    }

    public void blackPieces() {
        putPieces("0,7", "black", "Rook");
        putPieces("7,7", "black", "Rook");
        putPieces("1,7", "black", "Knight");
        putPieces("6,7", "black", "Knight");
        putPieces("2,7", "black", "Bishop");
        putPieces("5,7", "black", "Bishop");
        putPieces("3,7", "black", "Queen");
        putPieces("4,7", "black", "King");
        for (int i = 0; i < 8; i++) {
            putPieces(i + ",6", "black", "Pawn");
        }
    }

    public void putPieces(String index, String color, String piece) {
        StackPane pane = paneMap.get(index);
        Button button = null;
        ObservableList<javafx.scene.Node> childrenList = pane.getChildren();
        ArrayList<javafx.scene.Node> children = new ArrayList<>(childrenList);
        for (Object x : children) {
            button = (Button) x;
        }
        Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
        dataMap.put("color", color);
        dataMap.put("piece", piece);
    }
    
    public void updateImages() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (paneMap.get(row + "," + col) != null) {
                    StackPane pane = paneMap.get(row + "," + col);
                    Button button = null;
                    ObservableList<javafx.scene.Node> childrenList = pane.getChildren();
                    ArrayList<javafx.scene.Node> children = new ArrayList<>(childrenList);
                    for (Object x : children) {
                        if (x instanceof Button) {
                            button = (Button) x;
                        } else {
                            pane.getChildren().remove(x);
                        }
                    }
                    Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();

                    String color = (String) dataMap.get("color");
                    String piece = (String) dataMap.get("piece");
                    String[] y = {"Bishop", "Knight", "King", "Pawn", "Queen", "Rook"};
                    
                    for (String x : y) {
                        if (x.equals(piece)) {
                            String i = "file:pieces/" + color + x + ".png";
                            Image image = new Image(i);
                            ImageView view = new ImageView(image);
                            view.setMouseTransparent(true);
                            pane.getChildren().add(view);
                            
                        }   
                    }
                    if (!movableSpaces.isEmpty()) {
                        if (movableSpaces.contains(row + "," + col)) {
                            button.setStyle("-fx-background-color:rgb(238, 255, 0);");
                        } else {
                            if (((row + 1) % 2) != 0) {
                                if (((col + 1) % 2) != 0) {
                                    button.setStyle("-fx-background-color:rgb(236, 238, 210);");
                                } else {
                                    button.setStyle("-fx-background-color:rgb(118, 150, 86);");
                                }
                            } else {
                                if (((col + 1) % 2) != 0) {
                                    button.setStyle("-fx-background-color:rgb(118, 150, 86);");
                                } else {
                                    button.setStyle("-fx-background-color:rgb(236, 238, 210);");
                                }
                            }
                        }
                    } else {
                        if (((row + 1) % 2) != 0) {
                            if (((col + 1) % 2) != 0) {
                                button.setStyle("-fx-background-color:rgb(236, 238, 210);");
                            } else {
                                button.setStyle("-fx-background-color:rgb(118, 150, 86);");
                            }
                        } else {
                            if (((col + 1) % 2) != 0) {
                                button.setStyle("-fx-background-color:rgb(118, 150, 86);");
                            } else {
                                button.setStyle("-fx-background-color:rgb(236, 238, 210);");
                            }
                        }
                    }
                }
                
           }
        }
    }

    public void winText(String winner) {
        for (StackPane pane : paneMap.values()) {
            pane.getChildren().clear();
        }
        bottomRight.getChildren().removeAll();
        TextField field = new TextField(winner + " Won!");
        bottomRight.add(field, 3, 3);
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> {
            bottomRight.getChildren().removeAll();
            setup();
        });
        bottomRight.add(resetButton, 4, 3);
    }
    
    public ArrayList<String> horse(int row, int col, String color) {
        ArrayList<String> i = new ArrayList<>();
        if (!friendlyFire(row - 2, col + 1, color)) {
            i.add((row - 2) + "," + (col + 1));
        }
        if (!friendlyFire(row - 2, col - 1, color)) {
            i.add((row - 2) + "," + (col - 1));
        }
        if (!friendlyFire(row + 2, col + 1, color)) {
            i.add((row + 2) + "," + (col + 1));
        }
        if (!friendlyFire(row + 2, col - 1, color)) {
            i.add((row + 2) + "," + (col - 1));
        }
        if (!friendlyFire(row - 1, col + 2, color)) {
            i.add((row - 1) + "," + (col + 2));
        }
        if (!friendlyFire(row + 1, col + 2, color)) {
            i.add((row + 1) + "," + (col + 2));
        }
        if (!friendlyFire(row - 1, col -2, color)) {
            i.add((row - 1) + "," + (col -2));
        }
        if (!friendlyFire(row + 1, col -2, color)) {
            i.add((row + 1) + "," + (col -2));
        }
        return i;
    }
    
    public ArrayList<String> lines(int[][] directions, int row, int col, String check) {
        ArrayList<String> output = new ArrayList<>();
        for (int[] direction : directions) {
            int i = 0;
            boolean inBounds = true;
            int rowIncrement = direction[0];
            int colIncrement = direction[1];
            
            while (inBounds) {
                i++;
                String position = (row + i * rowIncrement) + "," + (col + i * colIncrement);
                if (paneMap.get(position) != null) {
                    StackPane pane = paneMap.get(position);
                    Button button = (Button) pane.getChildren().get(0);
                    Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
                    String color = (String) dataMap.get("color");
                    String piece = (String) dataMap.get("piece");
    
                    if (!piece.equals("") && !color.equals(check)) {
                        output.add(position);
                        inBounds = false; 
                    } else if (!piece.equals("") && color.equals(check)) {
                        inBounds = false; 
                    } else {
                        output.add(position); 
                    }
                } else {
                    inBounds = false; 
                }
            }
        }
    
        return output;
    }

    

    public ArrayList<String> pawn(int row, int col, String check) {
        ArrayList<String> i = new ArrayList<>();
        if (check.equals("black")) {
            if (col == 6) {
                if (!checkForPiece(row, col - 2)) {
                    i.add(row + "," + (col - 2));
                }
            }
            if (!checkForPiece(row, col - 1)) {
                i.add(row + "," + (col - 1));
            }
            if (checkForPiece(row - 1, col - 1) && !whatColor(row  -1, col - 1).equals(check)) {
                i.add((row - 1) + "," + (col - 1));
            }
            if (checkForPiece(row + 1, col - 1) && !whatColor(row + 1, col - 1).equals(check)) {
                i.add((row + 1) + "," + (col - 1));
            }
        } else {
            if (col == 1) {
                if (!checkForPiece(row, col + 2)) {
                    i.add(row + "," + (col + 2));
                }
            }
            if (!checkForPiece(row, col + 1)) {
                i.add(row + "," + (col + 1));
            }
            if (checkForPiece(row - 1, col + 1) && !whatColor(row  -1, col + 1).equals(check)) {
                i.add((row - 1) + "," + (col + 1));
            }
            if (checkForPiece(row + 1, col + 1) && !whatColor(row  +1, col + 1).equals(check)) {
                i.add((row + 1) + "," + (col + 1));
            }
        }
        return i;
    }

    public boolean checkForPiece(int row, int col) {
        if (paneMap.get(row + "," + col) != null) {
            StackPane pane = paneMap.get(row + "," + col);
            Button button = (Button) pane.getChildren().get(0);
            Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
            if (!dataMap.get("piece").equals("")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    
    public String whatColor(int row, int col) {
        StackPane pane = paneMap.get(row + "," + col);
        Button button = (Button) pane.getChildren().get(0);
        Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
        String color = (String) dataMap.get("color");
        return color;
    }
    public ArrayList<String> king(int row, int col, String color) {
        ArrayList<String> i = new ArrayList<>();
        if (!friendlyFire(row - 1, col - 1, color)) {
            i.add((row - 1) + "," + (col - 1));
        }
        if (!friendlyFire(row - 1, col, color)) {
            i.add((row - 1) + "," + (col));
        }
        if (!friendlyFire(row - 1, col + 1, color)) {
            i.add((row - 1) + "," + (col + 1));
        }
        if (!friendlyFire(row, col - 1, color)) {
            i.add((row) + "," + (col - 1));
        }
        if (!friendlyFire(row, col + 1, color)) {
            i.add((row) + "," + (col + 1));
        }
        if (!friendlyFire(row + 1, col - 1, color)) {
            i.add((row + 1) + "," + (col - 1));
        }
        if (!friendlyFire(row + 1, col, color)) {
            i.add((row + 1) + "," + (col));
        }
        if (!friendlyFire(row + 1, col + 1, color)) {
            i.add((row + 1) + "," + (col + 1));
        }
        return i;
    }

    public boolean friendlyFire(int row, int col, String color) {
        if (paneMap.get(row + "," + col) != null) {
            StackPane pane = paneMap.get(row + "," + col);
            Button button = (Button) pane.getChildren().get(0);
            Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
            if (dataMap.get("color").equals(color)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void move(String origin, String destination) {
        StackPane pane1 = paneMap.get(origin);
        Button button1 = (Button) pane1.getChildren().get(0);
        Map<String, Object> dataMap1 = (Map<String, Object>) button1.getUserData();
        String piece = (String) dataMap1.get("piece");
        String color = (String) dataMap1.get("color");

        StackPane pane2 = paneMap.get(destination);
        Button button2 = (Button) pane2.getChildren().get(0);
        Map<String, Object> dataMap2 = (Map<String, Object>) button2.getUserData();
        dataMap2.put("piece", piece);
        dataMap2.put("color", color);
        dataMap1.put("piece", "");
        dataMap1.put("color", "");

        checkPawns();
    }

    public boolean checkKings(String check) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane pane = paneMap.get(i + "," + j);
                Button button = (Button) pane.getChildren().get(0);
                Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
                String piece = (String) dataMap.get("piece");
                String color = (String) dataMap.get("color");
                if (piece.equals("King") && color.equals(check)) {
                    return true;
                }
            }
        }
        return false;
    }
    String promotedPiece;
    public int checkPawns() {
        for (int i = 0; i < 8; i++) {
            StackPane pane = paneMap.get(i + "," + 0);
            Button button = (Button) pane.getChildren().get(0);
            Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
            String piece = (String) dataMap.get("piece");
            if (piece.equals("Pawn")) {
                showPromotionDialog((i + "," + 0), "black");
            }
        }
        for (int i = 0; i < 8; i++) {
            StackPane pane = paneMap.get(i + "," + 7);
            Button button = (Button) pane.getChildren().get(0);
            Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
            String piece = (String) dataMap.get("piece");
            if (piece.equals("Pawn")) {
                showPromotionDialog((i + "," + 7), "white");
            }
        }
        return 0;
    }

    private void showPromotionDialog(String location, String color) {
        // Create a new window for piece selection
        Stage stage = new Stage();
        GridPane pane = new GridPane();

        // Set up image buttons for each promotion choice
        Button queenButton = createPromotionButton(location, "Queen", color);
        Button rookButton = createPromotionButton(location, "Rook", color);
        Button bishopButton = createPromotionButton(location, "Bishop", color);
        Button knightButton = createPromotionButton(location, "Knight", color);

        // Place buttons on the grid
        pane.add(queenButton, 0, 0);
        pane.add(rookButton, 1, 0);
        pane.add(bishopButton, 0, 1);
        pane.add(knightButton, 1, 1);

        // Create scene for promotion dialog
        Scene scene = new Scene(pane, 200, 200);
        stage.setTitle("Choose Promotion");
        stage.setScene(scene);
        stage.show();
    }

    // Function to create a promotion button with an image
    private Button createPromotionButton(String location, String pieceType, String color) {
        Button button = new Button();
        String imagePath = "file:pieces/" + color + pieceType + ".png"; 
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        button.setGraphic(imageView);
        
        button.setOnAction(event -> {
            promotedPiece = pieceType;
            replace(location, pieceType);
            ((Stage) button.getScene().getWindow()).close();
        });

        return button;
    }

    public void replace(String location, String replace) {
        StackPane pane = paneMap.get(location);
        Button button = (Button) pane.getChildren().get(0);
        Map<String, Object> dataMap = (Map<String, Object>) button.getUserData();
        dataMap.put("piece", replace);
        updateImages();
    }

}
