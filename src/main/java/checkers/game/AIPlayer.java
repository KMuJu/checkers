package checkers.game;

import checkers.game.AI.AiSettings;
import checkers.game.AI.Search;
import javafx.animation.AnimationTimer;

public class AIPlayer extends AbstractPlayer{
    
    Search search;
    MoveGeneration moveGeneration;
    AnimationTimer timer;
    AiSettings aiSettings;

    AIPlayer(MoveGeneration moveGeneration, PlayerManager pm, AiSettings aiSettings){
        super(pm);
        this.moveGeneration = moveGeneration;
        this.aiSettings = aiSettings;
        search = new Search(pm.board, aiSettings, moveGeneration, pm);
    }

    public void setTimer(AnimationTimer timer) {
        this.timer = timer;
    }

    public Move chooseMove(){
        //end search
        search.abortSearch();
        return search.getResult(aiSettings.randomSearch);
    }

    public void startSearch(){
        System.out.println("Starter search");
        search.startSearch();
    }

    @Override
    public void canMove() {
        timer.start();
        if (!aiSettings.randomSearch) startSearch();
        //start search
    }

    @Override
    public void afterMove(){    }

    @Override
    public boolean isHuman() {
        return false;
    }

}
