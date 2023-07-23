package checkers.game.util;

public class Piece {

    static public byte empty = 0;       //0
    static public byte man = 0b01;      //1
    static public byte king = 0b10;     //2

    static public byte white = 0b100;   //4
    static public byte black = 0b1000;  //8

    static public byte typeMask = 0b11;

    static public byte colourMask = 0b100;
    
    public static boolean isKing(byte piece){
        return (piece & typeMask) == king;
    }

    public static int color(byte piece){
        return (piece & colourMask);
    }
    public static int colorIndex(byte piece){
        return ((piece & colourMask) >> 2);
    }

    public static byte piece(byte piece){
        return (byte)(piece & typeMask);
    }
}
