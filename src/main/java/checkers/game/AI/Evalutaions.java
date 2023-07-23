package checkers.game.AI;

import checkers.game.util.Piece;

public class Evalutaions {
    public static int kingValue = 2;
    public static int manValue = 1;

    public static int manOnPlayersHalf = 5;
    public static int manOnEnemyHalf = 7;
    // public static int 

    public static int pieceToValue(byte piece){
        return Piece.isKing(piece) ? kingValue : manValue;
    }
}
