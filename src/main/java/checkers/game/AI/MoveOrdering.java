package checkers.game.AI;

import java.util.List;

import checkers.game.Board;
import checkers.game.Move;
import checkers.game.MoveGeneration;
import checkers.game.TranspositionTable;
import checkers.game.util.Bitboard;
import checkers.game.util.Piece;

public class MoveOrdering {
    
    final int maxMoveCount = 218;
    int[] moveScores;

    MoveGeneration moveGeneration;
    TranspositionTable tt;
    Move invalidMove;

    public static int capturedPieceMultiplier = 10;
    public static int rankMultiplier = 3;
    public static int enemyCanMakeKing = -25;
    public static int canMakeKing = 25;
    public static int canCapture;

    MoveOrdering(MoveGeneration moveGeneration, TranspositionTable tt){
        moveScores = new int[maxMoveCount];
        this.moveGeneration = moveGeneration;
        this.tt = tt;

        invalidMove = Move.invalidMove;
    }

    public void orderMoves(Board board, List<Move> moves, boolean useTT){
        Move hashMove = invalidMove;
        if (useTT){
            hashMove = tt.getStoredMove();
        }
        for (int i = 0; i < moves.size(); i++) {
            int score = 0;
            int targetSquareRank = moves.get(i).getCapturedSquare() / 8;
            int rankEval = (board.whiteToMove()) ? targetSquareRank : 7 - targetSquareRank;
            byte movePieceType = Piece.piece(board.board[moves.get(i).getStartSquare()]);
            byte capturedPieceType = Piece.piece(board.board[moves.get(i).getCapturedSquare()]);
            boolean isCapture = moves.get(i).isCapture();
            boolean isKing = moves.get(i).isKing();
            if (isCapture){
                score += capturedPieceMultiplier * Evalutaions.pieceToValue(capturedPieceType) - Evalutaions.pieceToValue(movePieceType);
            }
            if (!isKing){
                score += rankEval * rankMultiplier;
            }
            moveScores[i] = score;
            if (hashMove != null && Move.sameMove(hashMove, moves.get(i))){
                score += 25;
            }

            //opponent can make king next move
            long enemyBitboard = board.getColourPieceMask(1-board.colourToMoveIndex);
            int enemyRankCheck = (board.whiteToMove()) ? 6 : 1;
            long rankBitboard = ((long)0b11111111) << (8 * enemyRankCheck);
            if ((enemyBitboard & rankBitboard) != 0){
                score += enemyCanMakeKing;
            }

            //can make king next move
            long friendlyBitboard = board.getColourPieceMask(board.colourToMoveIndex);
            int rankCheck = (board.whiteToMove()) ? 6 : 1;
            rankBitboard = ((long)0b11111111) << (8 * rankCheck);
            if ((friendlyBitboard & rankBitboard) != 0){
                score += canMakeKing;
            }

            //enemy can capture piece
            long enemyCaptureMask = moveGeneration.getOpponentAttackMap();
            if (Bitboard.contains(enemyCaptureMask, moves.get(i).getTargetSquare())){
                score -= canCapture;
            }
        }

        Sort(moves);
    }

    public void Sort(List<Move> moves){
        for (int i = 0; i < moves.size() - 1; i++) {
            for (int j = i+1; j > 0; j--) {
                int swapIndex = j - 1;
                if (moveScores[swapIndex] < moveScores[j]){
                    Move temp = moves.get(j);
                    moves.set(j, moves.get(swapIndex));
                    moves.set(swapIndex, temp);
                    int tempScore = moveScores[j];
                    moveScores[j] = moveScores[swapIndex];
                    moveScores[swapIndex] = tempScore;
                }
            }
        }
    }
}
