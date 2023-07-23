package checkers.game;

import java.util.ArrayList;
import java.util.List;

import checkers.game.util.Bitboard;
import checkers.game.util.Piece;

public class MoveGeneration {
    
    // int colourToMove;
    int colourToMoveIndex;
    int opponentColourIndex;
    boolean whiteToMove;

    Board board;
    List<Move> moveList;
    boolean mustCapture;

    public void init(){

        whiteToMove = board.whiteToMove;
        colourToMoveIndex = board.colourToMoveIndex;
        opponentColourIndex = 1 - colourToMoveIndex;

        moveList = new ArrayList<>(32);
        mustCapture = false;

    }

    //TODO check pieces that are more likely to be able to capture
    /**
     * Generates moves for a given board. 
     * Uses a temporary list for moves that does not capture.
     * This is only used if no moves captures
     * If you need to capture this method does to much,
     * because it calculates moves until it finds a piece that must capture and throws away the others
     * Might also need to not throw these moves away in case that it might be benefitial to not capture
     * 
     * If possible should check pieces more likely to be able to capture first.
     * 
     * @param board - board from which to generate moves
     * @return list of moves
     */
    public List<Move> generate(Board board){
        this.board = board;
        init();

        List<Move> tempMoves = new ArrayList<>(16);
        long friendlyBitboard = board.getColourPieceMask(colourToMoveIndex);
        long enemyBitboard = board.getColourPieceMask(opponentColourIndex);

        PieceList manPieces = board.getPieceList(Piece.man, colourToMoveIndex);
        int promotionRank = (whiteToMove) ? 7 : 0;
        long promotionMask = ((long)0b11111111) << promotionRank*8;
        for (int i = 0; i < manPieces.size(); i++) {
            int square = manPieces.at(i);
            for (int dir = 0; dir < 2; dir++) {
                int firstTarget = Precompute.manMoves[colourToMoveIndex][square][dir];
                int secondTarget = Precompute.manCaptures[colourToMoveIndex][square][dir];
                int enemyIndex = -1;
                if (firstTarget == -1) continue;
                if (Bitboard.contains(board.getColourPieceMask(opponentColourIndex), firstTarget)){
                    enemyIndex = firstTarget;
                    if (secondTarget != -1) {
                        if ((board.getAllBitBoards() & 1L << secondTarget)==0){
                            if ((promotionMask & 1L << secondTarget) == 0){
                                moveList.add(new Move(square, secondTarget, enemyIndex, false));
                            }
                            else{
                                moveList.add(new Move(square, secondTarget, enemyIndex, false, true));
                            }
                        }
                    }
                }
                
                if (!mustCapture){
                    if ((board.getAllBitBoards() & 1L << firstTarget) == 0){
                        if ((promotionMask & 1L << firstTarget) == 0){
                            tempMoves.add(new Move(square, firstTarget, false));
                        }
                        else{
                            tempMoves.add(new Move(square, firstTarget, false, true));
                        }
                    }
                    else{
                        if (secondTarget == -1) continue;
                        if (Bitboard.contains(enemyBitboard, firstTarget)){
                            if ((board.getAllBitBoards() & 1L << secondTarget)==0){
                                mustCapture = true;
                            }
                        }
                    }
                }
                
            }
        }

        
        PieceList kings = board.getPieceList(Piece.king, colourToMoveIndex);
        for (int i_ = 0; i_ < kings.size(); i_++) {
            int square = kings.at(i_);
            for (int rayIndex = 0; rayIndex < 4; rayIndex++) {
                boolean foundEnemy = false;
                int enemyIndex = -1;
                for (int target : Precompute.kingMoves[square][rayIndex]) {
                    if (target == 62 || target == 7){
                        System.out.println("square: " + square + ", rayIndex: " + rayIndex + ", target: " + target);
                    }
                    if (Bitboard.contains(friendlyBitboard, target)){
                        break;
                    }
                    if (Bitboard.contains(enemyBitboard, target)){
                        if (foundEnemy) break;
                        foundEnemy = true;
                        enemyIndex = target;
                    }
                    if ((board.getAllBitBoards() & 1L << target) == 0){
                        if (foundEnemy){
                            moveList.add(new Move(square, target, enemyIndex, true));
                        }
                        else if (!mustCapture && !foundEnemy){
                            tempMoves.add(new Move(square, target, true));
                        }
                        if (!mustCapture) mustCapture = foundEnemy;
                    }
                }
            }
        }

        if (!mustCapture) moveList.addAll(tempMoves);

        return moveList;
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    @SuppressWarnings("unused")
    private void generateManMoves() {
        PieceList manPieces = board.getPieceList(Piece.man, colourToMoveIndex);
        int promotionRank = (whiteToMove) ? 7 : 0;
        long promotionMask = ((long)0b11111111) << promotionRank;
        for (int i = 0; i < manPieces.size(); i++) {
            int square = manPieces.at(i);
            for (int dir = 0; dir < 2; dir++) {
                int firstTarget = Precompute.manMoves[colourToMoveIndex][square][dir];
                if (firstTarget == -1) continue;
                if (mustCapture){
                    if (!Bitboard.contains(board.getColourPieceMask(opponentColourIndex), firstTarget)){
                        continue;
                    }
                    int secondTarget = Precompute.manCaptures[colourToMoveIndex][square][dir];
                    if (secondTarget == -1) continue;
                    if ((board.getAllBitBoards() & 1L << secondTarget)==0){
                        if ((promotionMask & 1L << secondTarget) == 0){
                            moveList.add(new Move(square, secondTarget, false));
                        }
                        else{
                            moveList.add(new Move(square, secondTarget, false, true));
                        }
                    }
                }
                else{
                    if ((board.getAllBitBoards() & 1L << firstTarget) == 0){
                        if ((promotionMask & 1L << firstTarget) == 0){
                            moveList.add(new Move(square, firstTarget, false));
                        }
                        else{
                            moveList.add(new Move(square, firstTarget, false, true));
                        }
                    }
                }
                
            }
        }
    }
    @SuppressWarnings("unused")
    private void generateKingMoves() {
        PieceList kings = board.getPieceList(Piece.king, colourToMoveIndex);
        long friendlyBitboard = board.getColourPieceMask(colourToMoveIndex);
        long enemyBitboard = board.getColourPieceMask(opponentColourIndex);
        for (int i_ = 0; i_ < kings.size(); i_++) {
            int square = kings.at(i_);
            for (int rayIndex = 0; rayIndex < 4; rayIndex++) {
                boolean foundEnemy = false;
                for (int target : Precompute.kingMoves[square][rayIndex]) {
                    if (Bitboard.contains(friendlyBitboard, target)){
                        break;
                    }
                    if (Bitboard.contains(enemyBitboard, target)){
                        if (foundEnemy) break;
                        foundEnemy = true;
                    }
                    if ((board.getAllBitBoards() & 1L << target) == 0 && foundEnemy){
                        moveList.add(new Move(square, target, true));
                    }
                }
            }
        }
    }
}
