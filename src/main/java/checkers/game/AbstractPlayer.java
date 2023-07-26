package checkers.game;

public abstract class AbstractPlayer {

    PlayerManager pm;

    public AbstractPlayer(PlayerManager pm){
        this.pm = pm;
    }

    
    /**
     * Gives playermanager the move to play
     * @param m - chosen move
     */
    public void move(Move m){
        pm.move(m);
        if (m.isCapture()) noMoveAfterCapture();
        afterMove();
    }

    public void noMoveAfterCapture(){
        if (pm.moves.size() == 0){
            pm.move(Move.noMove);
        }
    }

    /**
     * In case the player needs to do something after moving
     */
    public abstract void afterMove();

    public abstract void canMove();

    /**
     * used to check if player is human
     * @return if player is human
     */
    public abstract boolean isHuman();
}
