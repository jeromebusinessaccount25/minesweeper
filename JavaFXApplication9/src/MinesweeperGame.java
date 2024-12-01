import java.awt.Color;
import java.awt.Point;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.application.Platform;



import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MinesweeperGame {
    private static final int BUTTON_SIZE = 40;
    private static final String FLAG_COLOR = "file:\\C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources\\flag1.png";
    private static final String BOMB_IMAGE_PATH = "file:\\C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources/bomb.png";
    private static final String TILE_IMAGE_PATH = "file:\\C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources/tile.png";
    private static final String BACKGROUND_IMAGE_PATH= "C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources\\backgroundfinal.jpeg";
    private Timeline timerTimeline;
   

    private final int gridSizeX, gridSizeY, mineCount;
    private int revealedCount;
    private boolean gameOver;

    private final Button[][] buttons;
    private final boolean[][] mines;
    private final boolean[][] revealed;
    private final boolean[][] flagged;

    private final Text timerText;
    private final VBox root;
    private long startTime;
    private final HighScores highScores;
    // Thread to handle the game timer
    private ScheduledExecutorService timerExecutor;
    
   public MinesweeperGame(int gridSizeX, int gridSizeY, int mineCount, HighScores highScores, String BACKGROUND_IMAGE_PATH) {
    this.gridSizeX = gridSizeX;
    this.gridSizeY = gridSizeY;
    this.mineCount = mineCount;
    this.revealedCount = 0;
    this.gameOver = false;

    this.buttons = new Button[gridSizeX][gridSizeY];
    this.mines = new boolean[gridSizeX][gridSizeY];
    this.revealed = new boolean[gridSizeX][gridSizeY];
    this.flagged = new boolean[gridSizeX][gridSizeY];

    this.timerText = new Text("Time: 00:00:00");
    this.startTime = System.currentTimeMillis();
    
    this.highScores = highScores;
    this.root = new VBox();
    placeMines();
    setupUI();
    
    
    startGameTimer();
   
    
    
    
}

   
 
    
     public boolean isMine(int x, int y) {
        return mines[x][y];
    }

     private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < gridSizeX && ny >= 0 && ny < gridSizeY && mines[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }


     

private void setupUI() {
    try {
        // Ensure the file path is correct and valid
        String validBackgroundImagePath = BACKGROUND_IMAGE_PATH;
        File backgroundImageFile = new File(validBackgroundImagePath);

        if (!backgroundImageFile.exists()) {
            System.err.println("Background image file does not exist: " + backgroundImageFile.getAbsolutePath());
            return; // Exit if the file doesn't exist
        }

        // Convert the file path to a valid URI format (absolute file system path)
        String imageURI = backgroundImageFile.toURI().toString();

        // Load the background image
        Image backgroundImage = new Image(imageURI);
        if (backgroundImage.isError()) {
            System.err.println("Error loading background image: " + backgroundImage.getException());
            return; // Exit if there is an error loading the image
        }

        // Set up the background using the Background class
        BackgroundImage backgroundImageObj = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,          // Do not repeat the image
            BackgroundRepeat.NO_REPEAT,          // Do not repeat the image vertically
            BackgroundPosition.CENTER,           // Center the image
            new BackgroundSize(                  // Scale the image to fit the window
                BackgroundSize.AUTO,             // Automatic width
                BackgroundSize.AUTO,             // Automatic height
                true,                            // Stretch the width to fit
                true,                            // Stretch the height to fit
                false,                           // Preserve the aspect ratio
                true                             // Cover the entire area
            )
        );

        root.setBackground(new Background(backgroundImageObj));

    } catch (Exception e) {
        System.err.println("Error setting up UI: " + e.getMessage());
    }
}







    private Button createCellButton(int x, int y) {
    Button button = new Button();
    button.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
    button.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
    button.setGraphic(createImageView(TILE_IMAGE_PATH, BUTTON_SIZE, BUTTON_SIZE));

    button.setOnAction(e -> {
        if (!gameOver) revealCell(x, y);
    });

    button.setOnContextMenuRequested(e -> {
        if (!gameOver) toggleFlag(x, y);
    });

    // Add scale transition to simulate button press effect
    button.setOnMousePressed(e -> {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
        scaleDown.setToX(0.9);
        scaleDown.setToY(0.9);
        scaleDown.play();
    });

    button.setOnMouseReleased(e -> {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);
        scaleUp.play();
    });

    return button;
}


     private ImageView createImageView(String path, int width, int height) {
        ImageView imageView = new ImageView(new Image(path));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);
        return imageView;
    }

   private void placeMines() {
    // Clear previous mines
    for (int x = 0; x < gridSizeX; x++) {
        for (int y = 0; y < gridSizeY; y++) {
            mines[x][y] = false;  // Reset the mines array
        }
    }

    // Place new mines randomly
    Set<Point> mineLocations = new HashSet<>();
    while (mineLocations.size() < mineCount) {
        int x = (int) (Math.random() * gridSizeX);
        int y = (int) (Math.random() * gridSizeY);
        mineLocations.add(new Point(x, y));
    }

    // Set the mines in the grid
    mineLocations.forEach(point -> mines[point.x][point.y] = true);
}


   private void revealCell(int x, int y) {
    if (revealed[x][y] || flagged[x][y] || gameOver) return;

    revealed[x][y] = true;
    revealedCount++;

    // Start a fade transition on the button
    FadeTransition fade = new FadeTransition(Duration.millis(300), buttons[x][y]);
    fade.setFromValue(0);
    fade.setToValue(1);
    fade.play();

    buttons[x][y].setGraphic(null);

    if (mines[x][y]) {
        gameOver = true;
        displayBomb(x, y);  // Display the bomb at the clicked location
        playSound("C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources\\bombsoundfinal.mp3");
        
        // Reveal all bombs on the grid with animation
        revealAllBombs();
        showGameOverDialog();
    } else {
        int adjacentMines = countAdjacentMines(x, y);
        buttons[x][y].setText(adjacentMines == 0 ? "" : String.valueOf(adjacentMines));
        buttons[x][y].setStyle("-fx-background-color: white;");
        if (adjacentMines == 0) revealAdjacentCells(x, y);

        if (revealedCount == gridSizeX * gridSizeY - mineCount) {
            showWinDialog();
        }
    }
}


private void revealAllBombs() {
        for (int x = 0; x < gridSizeX; x++) {
            for (int y = 0; y < gridSizeY; y++) {
                if (mines[x][y]) {
                    buttons[x][y].setGraphic(createImageView(BOMB_IMAGE_PATH, BUTTON_SIZE, BUTTON_SIZE));

                    // Apply bomb reveal animation (bounce effect)
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), buttons[x][y]);
                    scaleTransition.setFromX(0);
                    scaleTransition.setToX(1);
                    scaleTransition.setFromY(0);
                    scaleTransition.setToY(1);
                    scaleTransition.play();
                    showExplosionEffect();
                }
            }
        }
    }

 private void showExplosionEffect() {
    Circle explosion = new Circle(10);
        int x = 0;
        int y = 0;
    explosion.setTranslateX(buttons[x][y].getLayoutX());
    explosion.setTranslateY(buttons[x][y].getLayoutY());

    ScaleTransition explode = new ScaleTransition(Duration.seconds(0.5), explosion);
    explode.setFromX(1);
    explode.setToX(5);
    explode.setFromY(1);
    explode.setToY(5);
    explode.setOnFinished(e -> root.getChildren().remove(explosion));
    explode.play();
   
}


    
    private void revealAdjacentCells(int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < gridSizeX && ny >= 0 && ny < gridSizeY) {
                    revealCell(nx, ny);
                }
            }
        }
    }

     private void toggleFlag(int x, int y) {
        if (revealed[x][y]) return;

        flagged[x][y] = !flagged[x][y];
        if (flagged[x][y]) {
            buttons[x][y].setGraphic(createImageView(FLAG_COLOR, BUTTON_SIZE, BUTTON_SIZE));
            playSound("C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources\\soundflag.mp3");

            // Flagging animation (bounce effect)
            ScaleTransition flagTransition = new ScaleTransition(Duration.seconds(0.3), buttons[x][y]);
            flagTransition.setFromX(1.0);
            flagTransition.setToX(1.2);
            flagTransition.setFromY(1.0);
            flagTransition.setToY(1.2);
            flagTransition.setCycleCount(2);
            flagTransition.setAutoReverse(true);
            flagTransition.play();
        } else {
            buttons[x][y].setGraphic(createImageView(TILE_IMAGE_PATH, BUTTON_SIZE, BUTTON_SIZE));
        }
    }




   private void showGameOverDialog() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText("Game Over!");
    alert.setContentText("You hit a bomb! Time taken: " + formatTime(System.currentTimeMillis() - startTime));

    ButtonType playAgainButton = new ButtonType("Play Again");
    ButtonType exitButton = new ButtonType("Exit");
    alert.getButtonTypes().setAll(playAgainButton, exitButton);

    // Add a fade transition to the alert dialog
    alert.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
        if (isNowShowing) {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            FadeTransition fade = new FadeTransition(Duration.millis(500), stage.getScene().getRoot());
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    });

    alert.showAndWait().ifPresent(response -> {
        if (response == playAgainButton) {
            resetGame();
        } else {
            System.exit(0);
        }
    });
}



   private void showWinDialog() {
    String timeTaken = formatTime(System.currentTimeMillis() - startTime);
    highScores.addScore(timeTaken);

    // Play victory music
    playSound("C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources\\victory.mp3");

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Congratulations!");
    alert.setHeaderText("You Win!");
    alert.setContentText("You cleared the board in " + timeTaken + "\nWould you like to play again?");

    ButtonType playAgainButton = new ButtonType("Play Again");
    ButtonType exitButton = new ButtonType("Exit");
    alert.getButtonTypes().setAll(playAgainButton, exitButton);

    alert.showAndWait().ifPresent(response -> {
        if (response == playAgainButton) {
            resetGame();  // Reset the game if "Play Again" is clicked
        } else {
            System.exit(0);  // Exit the game if "Exit" is clicked
        }
    });
}




    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int hours = minutes / 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    private void startGameTimer() {
    stopGameTimer(); // Stop any existing timer to avoid duplication

    timerTimeline = new Timeline(
        new KeyFrame(Duration.millis(100), e -> {
            if (!gameOver) {
                long elapsed = System.currentTimeMillis() - startTime;
                String formattedTime = formatTime(elapsed);

                // Update timer text dynamically
                Platform.runLater(() -> {
                    timerText.setText("Time: " + formattedTime);
                    updateTimerStyle(elapsed);
                });
            }
        })
    );
    timerTimeline.setCycleCount(Timeline.INDEFINITE);
    timerTimeline.play();
}
private void updateTimerStyle(long elapsedMillis) {
    int seconds = (int) (elapsedMillis / 1000);

    if (seconds < 15) {
        timerText.setStyle("-fx-fill: green; -fx-font-weight: bold;"); // Safe time
    } else if (seconds < 30) {
        timerText.setStyle("-fx-fill: orange; -fx-font-weight: bold;"); // Warning zone
    } else {
        timerText.setStyle("-fx-fill: red; -fx-font-weight: bold;"); // Critical time
    }
}

private void stopGameTimer() {
    if (timerTimeline != null) {
        timerTimeline.stop();
    }
}

    private void showHighScoresDialog() {
    List<String> topScores = highScores.getTopScores();
    
    StringBuilder scoresText = new StringBuilder("Top Scores:\n");
    for (int i = 0; i < topScores.size(); i++) {
        scoresText.append(i + 1).append(". ").append(topScores.get(i)).append("\n");
    }

    // Show the high scores dialog
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("High Scores");
    alert.setHeaderText("Top  High Scores");
    alert.setContentText(scoresText.toString());
    alert.showAndWait();
}


Node[] getGameBoard() {

    // Create an HBox for the top bar containing buttons
    Button highScoreButton = new Button("High Scores");
    highScoreButton.setOnAction(e -> showHighScoresDialog());
    highScoreButton.setOnMouseEntered(e -> highScoreButton.setStyle(
    "-fx-font-size: 14px; " +
    "-fx-background-color: #FFD700; " +   // Maintain gold background
    "-fx-text-fill: #FFFFFF; " +         // Change to white text on hover
    "-fx-border-color: #8B8000; " +
    "-fx-border-width: 2px;"
));

highScoreButton.setOnMouseExited(e -> highScoreButton.setStyle(
    "-fx-font-size: 14px; " +
    "-fx-background-color: #FFD700; " +
    "-fx-text-fill: #000000; " +
    "-fx-border-color: #8B8000; " +
    "-fx-border-width: 2px;"
));



    Button guideButton = new Button("Guide");
    guideButton.setOnAction(e -> showGuideDialog());  // Trigger guide on action
    guideButton.setOnMouseEntered(e -> guideButton.setStyle(
    "-fx-font-size: 14px; " +
    "-fx-background-color: #FFD700; " +   // Maintain gold background
    "-fx-text-fill: #FFFFFF; " +         // Change to white text on hover
    "-fx-border-color: #8B8000; " +
    "-fx-border-width: 2px;"
));

guideButton.setOnMouseExited(e -> guideButton.setStyle(
    "-fx-font-size: 14px; " +
    "-fx-background-color: #FFD700; " +
    "-fx-text-fill: #000000; " +
    "-fx-border-color: #8B8000; " +
    "-fx-border-width: 2px;"
));


    HBox topBar = new HBox(highScoreButton, guideButton);
    topBar.setAlignment(Pos.TOP_RIGHT);  // Align the buttons to the top-right
    topBar.setPadding(new Insets(10, 10, 10, 0));  // Padding for the buttons

    // Create the grid and configure resizing behavior
    GridPane grid = createGameGrid();

    // Dynamically adjust tile sizes based on grid size
    grid.widthProperty().addListener((obs, oldWidth, newWidth) -> adjustTileSizes(grid, newWidth.doubleValue(), grid.getHeight()));
    grid.heightProperty().addListener((obs, oldHeight, newHeight) -> adjustTileSizes(grid, grid.getWidth(), newHeight.doubleValue()));

    // Center the GridPane properly
    grid.setAlignment(Pos.CENTER); // Set GridPane's alignment to center itself

    // Grid container
    HBox gridContainer = new HBox(grid);
    gridContainer.setAlignment(Pos.CENTER); // Ensure the grid is centered within the HBox
    gridContainer.setPadding(new Insets(10)); // Add some padding for better visuals

    // Main game container
    VBox gameContainer = new VBox();
    gameContainer.setAlignment(Pos.CENTER); // Center everything vertically in the VBox
    gameContainer.setSpacing(10); // Add spacing between elements
    gameContainer.getChildren().addAll(topBar, timerText, gridContainer);

    // Bind the grid container's size to the game container
    grid.prefWidthProperty().bind(gameContainer.widthProperty().multiply(0.9)); // Adjust width
    grid.prefHeightProperty().bind(gameContainer.heightProperty().multiply(0.8)); // Adjust height

    return new Node[]{gameContainer};
}

// Adjusts the size of tiles dynamically based on the GridPane size
private void adjustTileSizes(GridPane grid, double newWidth, double newHeight) {
    int rowCount = grid.getRowCount();
    int columnCount = grid.getColumnCount();

    if (rowCount > 0 && columnCount > 0) {
        double tileWidth = newWidth / columnCount; // Width per tile
        double tileHeight = newHeight / rowCount; // Height per tile
        double tileSize = Math.max(20, Math.min(tileWidth, tileHeight)); // Ensure a minimum size of 20px
 // Keep square tiles

        for (Node child : grid.getChildren()) {
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setMinSize(tileSize, tileSize); // Set the tile size
                button.setMaxSize(tileSize, tileSize); // Ensure the button size is fixed
            }
        }
    }
}



private void showGuideDialog() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Guide");
    alert.setHeaderText("How to Play Minesweeper");

    String guideText = """
                       1. Left-click to reveal a tile.
                       2. Right-click to flag a tile if you think it's a mine.
                       3. If you reveal a mine, the game is over!
                       4. If you reveal all non-mine tiles, you win!
                       """ ;
                       
    alert.setContentText(guideText);
    alert.showAndWait();
}


     


private void resetGame() {
    // Play music when the user clicks "Play Again"
    playSound("C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources\\playagainmusic.mp3");

    // Reset the game state variables
    gameOver = false;
    revealedCount = 0;
    startTime = System.currentTimeMillis();  // Reset the timer start time

    // Reset internal arrays: revealed, flagged, and mines
    for (int x = 0; x < gridSizeX; x++) {
        for (int y = 0; y < gridSizeY; y++) {
            // Reset each tile's revealed and flagged state
            revealed[x][y] = false;
            flagged[x][y] = false;

            // Reset the button state (text, graphic, and styles)
            buttons[x][y].setText("");  // Clear any numbers from the button
            buttons[x][y].setGraphic(createImageView(TILE_IMAGE_PATH, BUTTON_SIZE, BUTTON_SIZE));  // Reset tile image
            buttons[x][y].setDisable(false);  // Re-enable button so it can be clicked again
            buttons[x][y].setStyle("-fx-background-color: #e0e0e0;");  // Reset background to default hidden state
        }
    }

    // Reinitialize mines randomly
    placeMines();  // Reset mines

    // Reset the timer display
    timerText.setText("Time: 00:00:00");

    // Start the game timer
    startGameTimer();
}


private GridPane createGameGrid() {
    GridPane grid = new GridPane();
    grid.setGridLinesVisible(true);

    for (int x = 0; x < gridSizeX; x++) {
        for (int y = 0; y < gridSizeY; y++) {
            Button button = createCellButton(x, y);  // Dynamically create buttons
            buttons[x][y] = button;
            grid.add(button, y, x);  // Add button to the grid
        }
    }
    
    grid.prefWidthProperty().bind(root.widthProperty().multiply(0.9)); // Adjust grid width
    grid.prefHeightProperty().bind(root.heightProperty().multiply(0.8)); // Adjust grid height

    return grid;
}


public void playSound(String filePath) {
    File soundFile = new File(filePath);
    if (soundFile.exists()) {
        try {
            Media sound = new Media(soundFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    } else {
        System.err.println("Sound file not found: " + soundFile.getPath());
    }
}






  


   private void displayBomb(int x, int y) {
        buttons[x][y].setGraphic(createImageView(BOMB_IMAGE_PATH, BUTTON_SIZE, BUTTON_SIZE));
    }



}