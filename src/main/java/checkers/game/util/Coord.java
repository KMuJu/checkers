package checkers.game.util;

public class Coord {
    

    public static int diagonalDistance(int start, int target){
        return Math.abs(start%8 - target%8);
    }
}
