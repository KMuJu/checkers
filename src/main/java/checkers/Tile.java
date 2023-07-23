package checkers;

import checkers.game.util.Piece;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Tile extends StackPane {
    
    int x, y;
    byte piece;
    int TILESIZE;
    Tile(int index, int piece, int TILESIZE){
        x = index % 8;
        y = index / 8;
        this.TILESIZE = TILESIZE;

        String farge = (y*8 + y+x)%2==0 ? "#b08d5f" : "#dacbb6";

        relocate(x*TILESIZE, (7-y)*TILESIZE);
        setPrefSize(TILESIZE, TILESIZE);
        setStyle("-fx-background-color:" + farge);
        setBorder(new Border(new BorderStroke(Color.BLACK, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, null)));
    }

    public void update(byte piece){
        Paint borderPaint = (Piece.color(piece) == Piece.white) ? Color.BLACK : Color.WHITE;
        if (piece == 0){
            getChildren().clear();
        }
        else{
            Paint p = (Piece.color(piece) == Piece.white) ? Color.WHITE : Color.BLACK;
            Circle c = new Circle(TILESIZE / 2.0, TILESIZE / 2.0, TILESIZE / 3.0, p);
            c.setStroke(borderPaint);
            if (Piece.isKing(piece)) c.setStrokeWidth(3);
            getChildren().add(c);

        }
        if (Piece.isKing(piece)){
            Text t = new Text("K");
            t.setFill(borderPaint);;
            t.setFont(new Font(14));
            getChildren().add(t);
        }
    }

    public void showMove(){
        setStyle("-fx-background-color:e04941");
    }
    public void removeMove(){
        String farge = (y*8 + y+x)%2==0 ? "#b08d5f" : "#dacbb6";
        setStyle("-fx-background-color:" + farge);
    }
}
