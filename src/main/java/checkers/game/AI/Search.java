package checkers.game.AI;

import java.util.List;
import java.util.Random;

import checkers.game.Move;
import checkers.game.MoveGeneration;

public class Search {
    Random random = new Random();
    
    MoveGeneration moveGeneration;

    public Search(MoveGeneration moveGeneration){
        this.moveGeneration = moveGeneration;
    }


    public Move searchMoves(){

        return searchMoves();
    }

    public Move searchMoves(boolean random){
        if (random) return randomMove();
        return null;
    }

    private Move randomMove(){
        List<Move> moveList = moveGeneration.getMoveList();
        if (moveList.size() == 0) return null;
        return moveList.get(random.nextInt(moveList.size()));
    }
}
