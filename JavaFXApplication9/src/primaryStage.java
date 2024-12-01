import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A custom wrapper class for managing the primary JavaFX Stage.
 */
public class primaryStage {

    private Stage stage;

    // Constructor to initialize the Stage
    public primaryStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the scene for the primary stage.
     *
     * @param scene The Scene to set for the primary stage.
     */
    public void setScene(Scene scene) {
        if (stage != null && scene != null) {
            stage.setScene(scene);
        } else {
            System.out.println("Stage or Scene is null.");
        }
    }

    /**
     * Displays the primary stage on the screen.
     */
    public void show() {
        if (stage != null) {
            stage.show();
        } else {
            System.out.println("Stage is null.");
        }
    }
}
