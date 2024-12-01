import javafx.scene.control.Button;

public class button {

    // Method to handle button click action
    public void setupButton(Button button) {
        // Set action for the button when it's clicked
        button.setOnAction(e -> {
            // Your custom code for the button click
            System.out.println("Button clicked!");
        });
    }
}
