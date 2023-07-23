package checkers.game;

import checkers.game.AI.Search;
import javafx.animation.AnimationTimer;

public class AIPlayer extends AbstractPlayer{
    
    Search search;
    MoveGeneration moveGeneration;
    AnimationTimer timer;

    AIPlayer(MoveGeneration moveGeneration, PlayerManager pm){
        super(pm);
        this.moveGeneration = moveGeneration;
        search = new Search(moveGeneration);
    }

    public void setTimer(AnimationTimer timer) {
        this.timer = timer;
    }

    public Move chooseMove(){
        //end search
        return search.searchMoves(true);
    }

    @Override
    public void canMove() {
        timer.start();

        //start search
    }

    @Override
    public void afterMove(){    }

    @Override
    public boolean isHuman() {
        return false;
    }

}
