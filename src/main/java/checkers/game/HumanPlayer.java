package checkers.game;

import java.util.List;

import checkers.game.util.Piece;

public class HumanPlayer extends AbstractPlayer {

    boolean canMove;

    int start, target;
    boolean choosingMove;

    public HumanPlayer(PlayerManager pm) {
        super(pm);
        //TODO Auto-generated constructor stub
    }

    //TODO: make clicks feel better
    /**
     * First klikk is where it is supposed to move from and second is where the move is targeting
     * @param square - where a click happened
     */
    public void klikk(int square){
        if (!choosingMove){
            start = square;
            choosingMove = true;
            pm.showMovesForIndex(square);
            return;
        }
        List<Move> moves = pm.moves;

        target = square;
        Move m = new Move(start, target, false);
        int index = moves.indexOf(m);
        choosingMove = false;
        pm.gameController.removeMoves();
        if (index >= 0){
            move(moves.get(index));
        }
        else{
            if (Piece.color(pm.board.get(target)) == pm.board.getFriendlyColour()){
                klikk(square);
            }
        }
    }

    @Override
    public void canMove() {
        canMove = true;
    }

    @Override
    public void afterMove(){
        canMove = false;
    }

    @Override
    public boolean isHuman() {
        return true;
    }
    
}
