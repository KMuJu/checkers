package checkers.game.AI;

import checkers.game.Board;
import checkers.game.MoveGeneration;
import checkers.game.PieceList;
import checkers.game.Precompute;
import checkers.game.util.Piece;

public class Evalutaions {
    public static int kingValue = 2;
    public static int manValue = 1;

    public static int manOnPlayersHalf = 5;
    public static int manOnEnemyHalf = 7;
    public static int canMakeKing = 30;
    public static int enemyCaptures = 10;

    public static int pieceToValue(byte piece){
        return Piece.isKing(piece) ? kingValue : manValue;
    }

    public int evaluateBoard(Board board, MoveGeneration moveGeneration){
        int score = 0;

        int colourToMoveIndex = board.colourToMoveIndex;
        boolean whiteToMove = board.whiteToMove();
        
        long friendlyBitboard = board.getColourPieceMask(colourToMoveIndex);
        long enemyBitboard = board.getColourPieceMask(1 - colourToMoveIndex);

        int promotionRank = (whiteToMove) ? 6 : 1;
        int enemyPromotionRank = (whiteToMove) ? 1 : 6;
        
        /**
         * checks if man is on enemy half and if it can make king
         */
        // friendly men
        long enemyAttackBitboard = moveGeneration.getOpponentAttackMap();
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
            if (rank == promotionRank){
                if ((Precompute.manCaptureMask[colourToMoveIndex][square] & enemyBitboard) == 0){
                    score += canMakeKing;
                }
            }
            if ((enemyAttackBitboard & 1L << square) != 0){
                score -= enemyCaptures;
            }
        }
        // opponent men
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
            
            if (rank == enemyPromotionRank){
                if ((Precompute.manCaptureMask[1-colourToMoveIndex][square] & friendlyBitboard) == 0){
                    score -= canMakeKing;
                }
            }
        }
        score += board.getPieceList(Piece.king, colourToMoveIndex).size() * kingValue;
        score -= board.getPieceList(Piece.king, 1-colourToMoveIndex).size() * kingValue;


        return score;
    }
}
