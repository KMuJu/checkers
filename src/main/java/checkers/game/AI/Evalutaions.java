package checkers.game.AI;

import checkers.game.Board;
import checkers.game.PieceList;
import checkers.game.util.Piece;

public class Evalutaions {
    public static int kingValue = 2;
    public static int manValue = 1;

    public static int manOnPlayersHalf = 5;
    public static int manOnEnemyHalf = 7;
    public static int canMakeKing = 30;

    public static int pieceToValue(byte piece){
        return Piece.isKing(piece) ? kingValue : manValue;
    }

    public int evaluateBoard(Board board){
        int score = 0;

        int colourToMoveIndex = board.colourToMoveIndex;
        boolean whiteToMove = board.whiteToMove();
        
        long friendlyBitboard = board.getColourPieceMask(colourToMoveIndex);
        long enemyBitboard = board.getColourPieceMask(1 - colourToMoveIndex);

        int promotionrank = (whiteToMove) ? 6 : 1;
        int enemyPromotionRank = (whiteToMove) ? 1 : 6;
        long promotionMask = ((long) 0b11111111) << 8*promotionrank;
        long enemyPromotionMask = ((long) 0b11111111) << 8*enemyPromotionRank;
        
        //TODO make this better
        //enemy can make king or king on last rank
        if ((enemyBitboard & enemyPromotionMask) != 0){
            score -= canMakeKing;
        }
        if ((friendlyBitboard & promotionMask) != 0){
            score += canMakeKing;
        }
        // friendly men
        PieceList man = board.getPieceList(Piece.man, colourToMoveIndex);
        score += man.size() * manValue;
        for (int index = 0; index < man.size(); index++) {
            int square = man.at(index);
            int rank = square / 8;
            if ((whiteToMove && rank > 3) || (!whiteToMove && rank < 4)){
                score += manOnEnemyHalf;
            }
            else {
                score += manOnPlayersHalf;
            }
        }
        // friendly men
        man = board.getPieceList(Piece.man, 1 - colourToMoveIndex);
        score -= man.size() * manValue;
        for (int index = 0; index < man.size(); index++) {
            int square = man.at(index);
            int rank = square / 8;
            if ((!whiteToMove && rank > 3) || (whiteToMove && rank < 4)){
                score -= manOnEnemyHalf;
            }
            else {
                score -= manOnPlayersHalf;
            }
        }

        return 0;
    }
}
