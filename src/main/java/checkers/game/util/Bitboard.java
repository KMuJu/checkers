package checkers.game.util;

public class Bitboard {

    /**
     * Checks if square is in bitboard by shifting a 1 by the square
     * @param bitboard
     * @param square
     * @return
     */
    public static boolean contains(long bitboard, int square){
        return (bitboard & (1L << square)) != 0;
    }

    /**
     * Would be great to find a way to only have first bit in ray from bitboard without loops
     * @param bitboard
     * @param start
     * @param rayOffset
     * @return
     */
    public static long onlyFirstBitInRay(long bitboard,int start, int rayOffset){
        return 0L;
    }

    public static void print(long bitboard){
        String bitString = Long.toBinaryString(bitboard);
        bitString = String.format("%64s", bitString).replace(' ', '0');
        System.out.println("Value: " + bitboard);
        String padding = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 7; j >= 0; j--){
                System.out.print(bitString.charAt(i*8 + j));
            }
            System.out.println(padding);
            padding += " ";
        }
        System.out.println();
    }

    public static void print(long bitboard, int square){
        bitboard |= 1L << square;
        System.out.println(square);
        print(bitboard);
    }
}
