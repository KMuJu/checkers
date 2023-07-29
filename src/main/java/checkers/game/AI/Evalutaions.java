package checkers.game.AI;

import checkers.game.Board;
import checkers.game.MoveGeneration;
import checkers.game.PieceList;
import checkers.game.Precompute;
import checkers.game.util.Bitboard;
import checkers.game.util.Piece;

public class Evalutaions {
    public static int kingValue = 200;
    public static int manValue = 100;

    public static int manOnPlayersHalf = 150;
    public static int manOnEnemyHalf = 200;
    public static int canMakeKing = 1050;
    public static int enemyCaptures = 70;
    static int i = 0;

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
        long enemyAttackBitboard = moveGeneration.getAttackMap(1 - colourToMoveIndex);
        // if (enemyAttackBitboard == 0) {
        //     Bitboard.print(moveGeneration.getAttackMap(colourToMoveIndex));
        //     Bitboard.print(board.getColourPieceMask(colourToMoveIndex));
        //     Bitboard.print(board.getColourPieceMask(1 - colourToMoveIndex));
        //     int a = 0;
        // }
        PieceList man = board.getPieceList(Piece.man, colourToMoveIndex);
        score += man.size() * manValue;
        for (int index = 0; index < man.size(); index++) {
            int square = man.at(index);
            int rank = square / 8;
            int pieceValue = 0;
            if ((whiteToMove && rank > 3) || (!whiteToMove && rank < 4)){ // man on enemy half
                score += manOnEnemyHalf;
                pieceValue += manOnEnemyHalf;
            }
            else {  // man on player half
                score += manOnPlayersHalf;
                pieceValue += manOnPlayersHalf;
            }
            if (rank == promotionRank){ //promotion rank
                if ((Precompute.manCaptureMask[colourToMoveIndex][square] & enemyBitboard) == 0){
                    score += canMakeKing;
                    pieceValue += canMakeKing;
                }
            }
            // if (i==0){
            //     Bitboard.print(friendlyBitboard);
            //     Bitboard.print(enemyBitboard);
            //     Bitboard.print(enemyAttackBitboard);
            //     i++;
            // }
            if ((enemyAttackBitboard & 1L << square) != 0){
                score -= enemyCaptures;
                score -= pieceValue;
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


        int perspective = (board.whiteToMove()) ? 1 : -1;
        return score * 500;
    }
}
