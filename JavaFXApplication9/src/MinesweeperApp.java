import java.io.File;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MinesweeperApp extends Application {

    static void setNickname(String nickname) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Stage stage;
    private String nickname;
    private String difficulty;

    private static final String BACKGROUND_IMAGE_PATH = "C:\\Users\\jerom\\OneDrive\\Documents\\NetBeansProjects\\JavaFXApplication9\\resources\\backgroundfinal.jpeg";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // Root Pane
        StackPane root = new StackPane();

        // Scene creation
        Scene scene = new Scene(root, 800, 600);

        // Background Image
        ImageView background = loadBackgroundImage(BACKGROUND_IMAGE_PATH, scene);

        // Center Content
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);

        // Title
        Label title = new Label("LUIGI - MARIO\nMINESWEEPER");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #000; -fx-effect: dropshadow(gaussian, white, 2, 1, 0, 1);");

        // Apply fade-in animation for the title
        FadeTransition fadeInTitle = new FadeTransition(Duration.seconds(2), title);
        fadeInTitle.setFromValue(0);
        fadeInTitle.setToValue(1);
        fadeInTitle.setCycleCount(1);
        fadeInTitle.play();

        // Nickname Input
        HBox nicknameBox = new HBox(10);
        nicknameBox.setAlignment(Pos.CENTER);
        Label nicknameLabel = new Label("User Nickname:");
        nicknameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #000;");
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Enter nickname...");
        nicknameBox.getChildren().addAll(nicknameLabel, nicknameField);

        // Difficulty Dropdown
        HBox difficultyBox = new HBox(10);
        difficultyBox.setAlignment(Pos.CENTER);
        Label difficultyLabel = new Label("Difficulty:");
        difficultyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #000;");
        ChoiceBox<String> difficultyChoice = new ChoiceBox<>();
        difficultyChoice.getItems().addAll("Easy", "Medium", "Hard");
        difficultyChoice.setValue("Easy");
        difficultyBox.getChildren().addAll(difficultyLabel, difficultyChoice);

        // Guide Button
        Button guideButton = new Button("Guide");
        guideButton.setStyle("-fx-font-size: 14px; -fx-background-color: #982B1C; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-color: black; -fx-border-width: 2px;");
        guideButton.setOnAction(e -> {
            Alert guideAlert = new Alert(Alert.AlertType.INFORMATION);
            guideAlert.setTitle("Minesweeper Guide");
            guideAlert.setHeaderText("How to Play Minesweeper");
            guideAlert.setContentText("""
                1. The goal is to clear the board without clicking on a mine.
                2. Numbers indicate how many mines are adjacent to that cell.
                3. Use logic to deduce where the mines are and mark them.
                4. Right-click to flag a mine location.
                5. Clear all safe cells to win the game!
                """);
            guideAlert.showAndWait();
        });

        // Enter Button
        Button enterButton = new Button("ENTER");
        enterButton.setStyle("-fx-font-size: 18px; -fx-background-color: #800000; -fx-text-fill: white; -fx-padding: 10 20; -fx-border-color: black; -fx-border-width: 2px;");
        
        // Apply fade-in animation for the Enter Button
        FadeTransition fadeInEnterButton = new FadeTransition(Duration.seconds(2), enterButton);
        fadeInEnterButton.setFromValue(0);
        fadeInEnterButton.setToValue(1);
        fadeInEnterButton.setCycleCount(1);
        fadeInEnterButton.play();
        
        enterButton.setOnAction(e -> {
            nickname = nicknameField.getText().trim();
            if (nickname.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Nickname");
                alert.setHeaderText("Nickname Required");
                alert.setContentText("Please enter your nickname before starting the game.");
                alert.showAndWait();
                nicknameField.requestFocus();
            } else {
                difficulty = difficultyChoice.getValue().toLowerCase();
                System.out.println("Nickname: " + nickname + ", Difficulty: " + difficulty);
                startGame(difficulty);
            }
        });

        // Add Content to VBox
        centerContent.getChildren().addAll(title, nicknameBox, difficultyBox, guideButton, enterButton);

        // Add Components to Root
        if (background != null) {
            root.getChildren().addAll(background, centerContent);
        } else {
            root.getChildren().add(centerContent);
        }

        // Stage Setup
        primaryStage.setScene(scene);
        primaryStage.setTitle("Luigi-Mario Minesweeper");
        primaryStage.show();
    


    }

    private ImageView loadBackgroundImage(String path, Scene scene) {
        ImageView background = null;
        try {
            File imageFile = new File(path);
            if (imageFile.exists()) {
                background = new ImageView(new Image(imageFile.toURI().toString()));
                background.setPreserveRatio(false); // Stretch the image to fill
                // Bind the image view's width and height to the scene's dimensions
                background.fitWidthProperty().bind(scene.widthProperty());
                background.fitHeightProperty().bind(scene.heightProperty());
            } else {
                System.err.println("Image file not found: " + imageFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
        return background;
    }

    // Modify the startGame method to pass the background to MinesweeperGame
    private void startGame(String difficulty) {
        try {
            // Close the initial menu window
            stage.close();

            // Set grid size and mine count based on difficulty
            int gridSizeX, gridSizeY, mineCount;
            switch (difficulty) {
                case "easy" -> {
                    gridSizeX = 8; gridSizeY = 8; mineCount = 10;
                }
                case "medium" -> {
                    gridSizeX = 9; gridSizeY = 10; mineCount = 15;
                }
                case "hard" -> {
                    gridSizeX = 9; gridSizeY = 14; mineCount = 20;
                }
                default -> throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
            }

            // Create the game instance with the background image
            HighScores highScores = new HighScores(); // Initialize HighScores here
            MinesweeperGame game = new MinesweeperGame(gridSizeX, gridSizeY, mineCount, highScores, BACKGROUND_IMAGE_PATH);

            // Create the game board layout and show the game
            VBox gameLayout = new VBox(10);
            gameLayout.setAlignment(Pos.CENTER);
            gameLayout.getChildren().addAll(game.getGameBoard());

            // Create a new Scene to show the game
            Scene gameScene = new Scene(gameLayout, 800, 600);
            Stage gameStage = new Stage();
            
            // Apply a fade-in animation to the game stage
            FadeTransition fadeInGameStage = new FadeTransition(Duration.seconds(2), gameLayout);
            fadeInGameStage.setFromValue(0);
            fadeInGameStage.setToValue(1);
            fadeInGameStage.setCycleCount(1);
            fadeInGameStage.play();

            gameStage.setScene(gameScene);
            gameStage.setTitle(nickname + "'s Minesweeper Game");
            gameStage.show();

        } catch (IllegalArgumentException e) {
            showErrorDialog("Error starting the game", "Something went wrong while starting the game.");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(message);
        errorAlert.showAndWait();
    }
    
    
}
