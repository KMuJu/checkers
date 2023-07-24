package checkers;

import java.io.IOException;

import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void startGame() throws IOException {
        App.setRoot("game");
    }
}
