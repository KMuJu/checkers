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
    List<Move> tempMoves;
    boolean mustCapture;

    long friendlyBitboard;
    long enemyBitboard;
    
    int promotionRank;
    long promotionMask;

    long opponentAttackMap;

    long[] attackMap;

    int previousColourIndexMoved = -1;
    static boolean samePlayer;

    public void init(){

        whiteToMove = board.whiteToMove;
        colourToMoveIndex = board.colourToMoveIndex;
        opponentColourIndex = 1 - colourToMoveIndex;

        moveList = new ArrayList<>(32);
        mustCapture = false;
        previousColourIndexMoved = board.getColourMovedIndex();
        attackMap = new long[] {0,0};
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

        generateManAttackMap();
        friendlyBitboard = board.getColourPieceMask(colourToMoveIndex);
        enemyBitboard = board.getColourPieceMask(opponentColourIndex);

        if (previousColourIndexMoved == colourToMoveIndex){
            samePlayer = true;
            mustCapture = true;
            // System.out.println("capture");
            int square = board.getLastSquareMovedTo();
            byte movedType = Piece.piece(board.get(square));
            if (movedType == Piece.king){
                generateKingMoves(square);
            }
            else {
                generateManMoves(square);
            }
            if (moveList.size() == 0){
                
            }
            return moveList;
        } else samePlayer = false;

        tempMoves = new ArrayList<>(16);

        
        PieceList kings = board.getPieceList(Piece.king, colourToMoveIndex);
        for (int i = 0; i < kings.size(); i++) {
            generateKingMoves(kings.at(i));
        }

        PieceList manPieces = board.getPieceList(Piece.man, colourToMoveIndex);
        int[] pieceListIndex = new int[manPieces.size()];
        int numIndexesAttack = 0;
        int numIndexesNotAttack = pieceListIndex.length-1;
        for (int i = 0; i < pieceListIndex.length; i++) {
            if (Bitboard.contains(opponentAttackMap, manPieces.at(i))){
                pieceListIndex[numIndexesAttack] = i;
                numIndexesAttack ++;
            }
            else{
                pieceListIndex[numIndexesNotAttack] = i;
                numIndexesNotAttack --;
            }
        }
        promotionRank = (whiteToMove) ? 7 : 0;
        promotionMask = ((long)0b11111111) << promotionRank*8;
        // for (int i = 0; i< manPieces.size(); i++) {
        //     int square = manPieces.at(i);
        //     generateManMoves(square);
        // }
        for (int i : pieceListIndex) {
            int square = manPieces.at(i);
            generateManMoves(square);
        }

        if (!mustCapture) moveList.addAll(tempMoves);
        // previousColourIndexMoved = colourToMoveIndex;
        return moveList;
    }
    
    private void generateManAttackMap() {
        opponentAttackMap = 0;
        // attackMap[opponentColourIndex] = 0;
        PieceList enemyMen = board.getPieceList(Piece.man, opponentColourIndex);
        for (int i = 0; i < enemyMen.size(); i++) {
            int square = enemyMen.at(i);
            opponentAttackMap |= Precompute.manCaptureMask[opponentColourIndex][square];
            attackMap[opponentColourIndex] |= Precompute.manCaptureMask[opponentColourIndex][square];
        }
    }

    private void generateKingMoves(int square){
        for (int rayIndex = 0; rayIndex < 4; rayIndex++) {
            boolean foundEnemy = false;
            int enemyIndex = -1;
            for (int target : Precompute.kingMoves[square][rayIndex]) {
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


    private void generateManMoves(int square){
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

    public List<Move> getMoveList() {
        return moveList;
    }

    public long getFriendlyBitboard() {
        return friendlyBitboard;
    }

    public long getEnemyBitboard() {
        return enemyBitboard;
    }

    public long getOpponentAttackMap() {
        return opponentAttackMap;
    }

    public boolean getSamePlayer(){
        return samePlayer;
    }

    public long getAttackMap(int index){
        return attackMap[index];
    }
}
