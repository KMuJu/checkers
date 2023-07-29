package checkers.game;

import java.util.Stack;

import checkers.GameController;
import checkers.game.util.Piece;

public class Board {
    public static byte[] baseBoard = new byte[]{
        1, 0, 1, 0, 1, 0, 1, 0,
        0, 1, 0, 1, 0, 1, 0, 1,
        1, 0, 1, 0, 1, 0, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 0, 1, 0, 1, 0, 1, 
        1, 0, 1, 0, 1, 0, 1, 0,
        0, 1, 0, 1, 0, 1, 0, 1, 
    };

    public byte[] board;

    int size = 8;
    int maxNumberOfPieces = (size/2) * 3;

    boolean whiteToMove;
    public int colourToMoveIndex;
    int opponentColour;
    int friendlyColour;
    long whitePieceMask;
    long blackPieceMask;
    long[] pieceMasks;

    PieceList whiteManList;
    PieceList whiteKingList;
    PieceList blackManList;
    PieceList blackKingList;
    PieceList[] allLists;

    GameController gameController;

    //board representation;
    /*1111
     * 1-4 bits - piece captured
     * 5-10 bits - square moved to
     * 11 bit - colour moved index
     */

    int currentGameState;
    Stack<Integer> boardHistory;
    boolean inSearch = false;

    final int pieceCapturedMask = 0b1111;
    final int squaredMovedToMask = 0b1111110000;
    final int colourIndexMovedMask = 1 << 10;

    long ZobristKey;

    //moves since capture
    final int maxPassiveMoves = 80;
    int moveCounter;

    public int getFriendlyColour() {
        return friendlyColour;
    }
    
    public Board(GameController gameController){
        this.gameController = gameController;
        Precompute.compute();
    }
    
    public long getColourPieceMask(int index){
        return pieceMasks[index];
    }

    public long getAllBitBoards(){
        return pieceMasks[0] | pieceMasks[1];
    }

    public byte get(int i){
        return board[i];
    }

    public void move(Move m){
        move(m, false);
    }
    public void move(Move m, boolean inSearch){
        if (m.isInvalid()){

            return;
            // throw new IllegalArgumentException("illegal move");
        }
        if (m.isNoMove()){
            nextToMove();
            return;
        }
        // System.out.println(m);
        this.inSearch = inSearch;
        boolean isCapture = m.isCapture();
        // boolean isKing = m.isKing();
        boolean isPromotion = m.isPromotion();

        int startSquare = m.getStartSquare();
        int targetSquare = m.getTargetSquare();

        int opponentColourIndex = 1 - colourToMoveIndex;

        byte movePieceType = Piece.piece(board[startSquare]);

        if (isCapture){
            int capturedSquare = m.getCapturedSquare();
            byte capturedPieceType = Piece.piece(board[capturedSquare]);
            currentGameState &= ~pieceCapturedMask;
            currentGameState |= board[capturedSquare];
            // System.out.println(currentGameState);
            removeToPieceList(capturedPieceType, opponentColourIndex, capturedSquare);
            // board[capturedSquare] = 0;
        }
        else{
            moveCounter ++;
        }

        if (!isPromotion) {
            // System.out.println("square: " + targetSquare + ", getter: " + getLastSquareMovedTo());
            moveToPieceList(movePieceType, colourToMoveIndex, startSquare, targetSquare);
            // board[targetSquare] = board[startSquare];
        }
        else{
            removeToPieceList(movePieceType, colourToMoveIndex, startSquare);
            addToPieceList(Piece.king, colourToMoveIndex, targetSquare);
            // board[targetSquare] = (byte)(Piece.king + ((whiteToMove) ? Piece.white : Piece.black));
        }
        //add last squared moved to game state
        currentGameState &= ~squaredMovedToMask;
        currentGameState |= targetSquare << 4;

        //add index moved
        currentGameState &= ~colourIndexMovedMask;
        if (colourToMoveIndex == 1) currentGameState |= colourIndexMovedMask;

        boardHistory.push(currentGameState);
        //next move
        if (!isCapture) nextToMove();
        if (!inSearch) fireBoardUpdate();
    }

    public void unmakeMove(Move m){
        unmakeMove(m, false);
    }
    public void unmakeMove(Move m, boolean inSearch){
        this.inSearch = inSearch;
        
        if (m.isNoMove()){
            nextToMove();
            return;
        }
        
        boolean isCapture = m.isCapture();
        boolean isPromotion = m.isPromotion();

        int startSquare = m.getStartSquare();
        int targetSquare = m.getTargetSquare();
        
        int capturedSquare = m.getCapturedSquare();
        byte capturedPiece = (byte)(currentGameState & pieceCapturedMask);

        int colourMovedIndex = getColourMovedIndex();
        int opponentColourIndex = 1 - colourMovedIndex;
        indexToMove(colourMovedIndex);
        
        byte movePieceType = Piece.piece(board[targetSquare]);
        
        if (!isPromotion){
            moveToPieceList(movePieceType, colourToMoveIndex, targetSquare, startSquare);
        }
        else{
            removeToPieceList(movePieceType, colourToMoveIndex, targetSquare);
            addToPieceList(Piece.man, colourToMoveIndex, startSquare);
        }
        if (isCapture){
            addToPieceList(Piece.piece(capturedPiece), opponentColourIndex, capturedSquare);
            moveCounter = 0;
        }
        else{
            moveCounter --;
        }

        boardHistory.pop();
        currentGameState = boardHistory.peek();

        if (!inSearch) fireBoardUpdate();
    }

    /**
     * Makes it so that the right player is supposed to move
     */
    public void nextToMove(){
        whiteToMove = !whiteToMove;
        colourToMoveIndex = 1 - colourToMoveIndex;
        opponentColour = whiteToMove ? Piece.black : Piece.white;
        friendlyColour = whiteToMove ? Piece.white : Piece.black;
    }

    private void indexToMove(int index){
        whiteToMove = index == 0;
        colourToMoveIndex = index;
        opponentColour = whiteToMove ? Piece.black : Piece.white;
        friendlyColour = whiteToMove ? Piece.white : Piece.black;
    }

    public PieceList getPieceList(int type, int colourIndex){
        return allLists[type + colourIndex*3];
    }

    /**
     * Add piece to board by also adding it to PieceList
     * @param type - man or king
     * @param colourIndex - 0 (white) or 1 (black)
     * @param square - index on board (0-63)
     */
    private void addToPieceList(int type, int colourIndex, int square){
        pieceMasks[colourIndex] |= 1L << square;
        getPieceList(type, colourIndex).add(square);
        board[square] = (byte)(type + (colourIndex==0 ? Piece.white : Piece.black));
        ZobristKey ^= Zobrist.piecesArray[type-1][colourIndex][square];
    }

    /**
     * Removes from board and piecelist
     * @param type - man or king
     * @param colourIndex - 0 (white) or 1 (black)
     * @param square - index on board (0-63)
     */
    private void removeToPieceList(int type, int colourIndex, int square){
        pieceMasks[colourIndex] -= 1L << square;
        getPieceList(type, colourIndex).remove(square);
        board[square] = 0;
        ZobristKey ^= Zobrist.piecesArray[type-1][colourIndex][square];
    }

    /**
     * @param type - man or king
     * @param colourIndex - 0 (white) or 1 (black)
     * @param startSquare - move from
     * @param targetSquare - move to
     */
    private void moveToPieceList(int type, int colourIndex, int startSquare, int targetSquare){
        pieceMasks[colourIndex] |= 1L << targetSquare;
        pieceMasks[colourIndex] &= ~(1L << startSquare);
        getPieceList(type, colourIndex).move(startSquare, targetSquare);
        board[targetSquare] = (byte)(type + (colourIndex==0 ? Piece.white : Piece.black));
        board[startSquare] = 0;
        ZobristKey ^= Zobrist.piecesArray[type-1][colourIndex][startSquare];
        ZobristKey ^= Zobrist.piecesArray[type-1][colourIndex][targetSquare];
    }

    public boolean checkMoveCounter(){
        return moveCounter >= maxPassiveMoves;
    }

    public boolean opponentHasNoPieces(){
        PieceList man = getPieceList(Piece.man, 1 - colourToMoveIndex);
        if (man.size() > 0) return false;
        PieceList king = getPieceList(Piece.king, 1 - colourToMoveIndex);
        return king.size() == 0;
    }

    public boolean whiteToMove(){
        return whiteToMove;
    }

    public int getLastSquareMovedTo(){
        return (currentGameState & squaredMovedToMask) >>> 4;
    }

    public int getColourMovedIndex(){
        return (currentGameState >>> 10) & 1;
    }

    /**
     * Initialises new game
     */
    public void init(){


        whiteToMove = false;
        colourToMoveIndex = 1;
        opponentColour = (whiteToMove) ? Piece.black : Piece.white;
        currentGameState = 0;
        moveCounter = 0;
        ZobristKey = 0;

        board = new byte[64];
        boardHistory = new Stack<>();
        boardHistory.push(0);

        whitePieceMask = 0;
        blackPieceMask = 0;
        pieceMasks = new long[]{
            whitePieceMask, blackPieceMask
        };

        whiteManList = new PieceList(maxNumberOfPieces);
        whiteKingList = new PieceList(maxNumberOfPieces);
        blackManList = new PieceList(maxNumberOfPieces);
        blackKingList = new PieceList(maxNumberOfPieces);
        PieceList empty = new PieceList(0);

        allLists = new PieceList[]{
            empty,
            whiteManList,
            whiteKingList,

            empty,
            blackManList,
            blackKingList
        };

        loadBasePosition();
    }

    private void loadBasePosition(){
        for (int i = 0; i < 64; i++) {
            board[i] = baseBoard[i];
            if (Piece.color(board[i]) == 0 && (board[i] & Piece.typeMask) != 0){
                byte colour = (i/8 > 3) ? Piece.black : Piece.white;
                addToPieceList(board[i], (colour/4) - 1, i);
            }
        }
        fireBoardUpdate();
    }

    public void fireBoardUpdate(){
        gameController.fireBoardUpdate(board);
    }

}
