module checkers {
    requires javafx.controls;
    requires javafx.fxml;

    opens checkers to javafx.fxml;
    exports checkers;
}
