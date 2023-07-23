package checkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import checkers.game.AbstractPlayer;
import checkers.game.HumanPlayer;
import checkers.game.Move;
import checkers.game.PlayerManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class GameController {

    @FXML
    Pane boardPane;

    Tile[] tiles;
    byte[] prevBoard;

    PlayerManager playerManager;

    @FXML
    void initialize(){
        tiles = new Tile[64];
        ObservableList<Node> baneChildren = boardPane.getChildren();
        for (int i = 0; i < tiles.length; i++) {
            Tile t = new Tile(i, 0, 80);
            tiles[i] = t;
            baneChildren.add(t);
            t.setOnMouseClicked(e->klikk(e));
        }
        prevBoard = new byte[64];
        playerManager = new PlayerManager(this);
    }

    private void klikk(MouseEvent e){
        if (e.isAltDown()){
            removeMoves();
        }
        else if (e.getButton().equals(MouseButton.PRIMARY)){
            Tile t = (Tile) e.getSource();
            // System.out.println("Index: " +(t.x + t.y*8));
            // klikk(t.x, t.y);
            // playerManager.humanMove(t.x + t.y*8);
            AbstractPlayer aPlayer = playerManager.getNextPlayer();
            if (aPlayer.isHuman()){
                HumanPlayer humanPlayer = (HumanPlayer) aPlayer;
                humanPlayer.klikk(t.x + t.y*8);
            }
        }
        else if (e.getButton().equals(MouseButton.SECONDARY)){
            showMoves(playerManager.moves);
            // Tile t = (Tile) e.getSource();
            // System.out.println("Index: " +(t.x + t.y*8));
        }
    }


    public void win(boolean whiteWins){
        String navn = (whiteWins) ? "Hvit " : "Sort: ";
        System.out.println(navn + "vant");
    }

    @FXML
    private void switchToMainScreen() throws IOException {
        App.setRoot("mainScreen");
    }

    public void fireBoardUpdate(byte[] board){
        for (int i = 0; i < 64; i++) {
            prevBoard[i] = board[i];
            tiles[i].update(board[i]);
        }
    }

    public void showMoves(List<Move> moves){
        removeMoves();
        for (Move move : moves) {
            tiles[move.getTargetSquare()].showMove();
        }
    }

    public void showMovesForIndex(int index){
        List<Move> show = new ArrayList<>(16);
        for (Move move : playerManager.moves) {
            if (move.getStartSquare() == index) show.add(move);
        }
        showMoves(show);
    }

    public void removeMoves(){
        for (int i = 0; i < 64; i++) {
            tiles[i].removeMove();
        }
    }

    public void undoMove(){
        playerManager.undo();
    }
}