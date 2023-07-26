package checkers.game;

import checkers.game.util.Coord;

public class Move {
    public static final int startMask = 0b0000000111111;
    public static final int targetMask = 0b0111111 << 6;
    public static final int captureIndexMask = 0b111111 << 12;
    public static final int distanceMask = 0b111 << 18;
    public static final int typeMask = 1 << 21;
    public static final int promoteMask = 1 << 22;
    public static final int captureMask = 1 << 23;

    public static Move invalidMove = new Move(0);
    public static Move noMove = new Move(-1);

    int moveValue;
    
    public Move(int start, int target, boolean king){
        int distance = Coord.diagonalDistance(start, target);
        moveValue = start + (target << 6);
        moveValue += distance << 18;
        if (king) moveValue |= typeMask;
        if (distance > 1 && !king) moveValue |= captureMask;
    }

    public Move(int start, int target, boolean king, boolean promotion){
        this(start, target, king);
        if (promotion) moveValue |= promoteMask;
    }

    public Move(int start, int target, int capture, boolean king){
        this(start, target, king);
        moveValue |= captureMask;
        moveValue += capture << 12;
    }

    public Move(int start, int target, int capture, boolean king, boolean promotion){
        this(start, target, king, promotion);
        moveValue |= captureMask;
        moveValue += capture << 12;
    }

    public Move(int moveValue){
        this.moveValue =  moveValue;
    }

    public boolean isNoMove(){
        return moveValue == -1;
    }

    public int getStartSquare(){
        return (moveValue & startMask);
    }
    public int getTargetSquare(){
        return (moveValue & targetMask) >>> 6;
    }

    public int getCapturedSquare(){
        return (moveValue & captureIndexMask) >>> 12;
    }

    public boolean isKing(){
        return ((moveValue & typeMask) >>> 21) == 1;
    }

    public boolean isPromotion(){
        return ((moveValue & promoteMask) >>> 22) == 1;
    }

    public boolean isCapture(){
        return ((moveValue & captureMask) >>> 23) == 1;
    }

    public int distance(){
        return (moveValue & distanceMask) >>> 18;
    }

    public boolean isInvalid(){
        return moveValue == 0;
    }

    // used to find moves with that moves from the same start and target moves in list<Move>
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)){
            return false;
        }
        Move m = (Move) obj;
        return getStartSquare() == m.getStartSquare() && getTargetSquare() == m.getTargetSquare();
    }

    public static boolean sameMove(Move a, Move b){
        return a.moveValue == b.moveValue;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return getStartSquare() + " - " + getTargetSquare();
    }
}
