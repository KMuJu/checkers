package checkers.game;


public class Precompute {
    
    //{white, black}{left, right}
    public static int[][] manOffset = new int[][]{
        new int[] {7, 9},
        new int[] {-9, -7}
    };
    
    //{nw, ne, sw, se}
    public static int[] diagonalOffset = new int[]{
        7, 9,
        -9,-7
    };

    //[index][ray]
    public static int[][] distanceToEdgeByRay;
    
    public static int[][] whiteManMoves;
    public static int[][] blackManMoves;
    public static int[][][] manMoves = new int[][][]{
        whiteManMoves, blackManMoves
    };

    public static int[][] whiteManCaptures;
    public static int[][] blackManCaptures;
    public static int[][][] manCaptures = new int[][][]{
        whiteManCaptures,blackManCaptures
    };
    
    public static int[][][] kingMoves;

    public static long[][] kingRays;
    
    
    /**
     * Computes everything used in the game at the start
     */
    public static void compute(){
        whiteManMoves = new int[64][2];
        blackManMoves = new int[64][2];
        manMoves = new int[][][]{
            whiteManMoves, blackManMoves
        };
        whiteManCaptures = new int[64][2];
        blackManCaptures = new int[64][2];
        manCaptures = new int[][][]{
            whiteManCaptures, blackManCaptures
        };

        distanceToEdgeByRay = new int[64][4];

        kingMoves = new int[64][4][];
        kingRays = new long[64][4];

        for (int index = 0; index < 64; index++) {
            
            int x = index % 8;
            int y = index / 8;
            int north = 7 - y;
            int east = 7 - x;
            int south = y;
            int west = x;
            distanceToEdgeByRay[index][0] = Math.min(north, west);
            distanceToEdgeByRay[index][1] = Math.min(north, east);
            distanceToEdgeByRay[index][2] = Math.min(south, west);
            distanceToEdgeByRay[index][3] = Math.min(south, east);

            for (int colourIndex = 0; colourIndex < 2; colourIndex++) {

                for (int j = 0; j < 2; j++) {
                    //one diagonal move
                    int target = index + manOffset[colourIndex][j];
                    int dx = Math.abs( index%8 - target%8);
                    int dy = Math.abs( index/8 - target/8);
                    if (dx == 1 && dy == 1 && target < 64 && target >= 0){
                        manMoves[colourIndex][index][j] = target;
                    }
                    else{
                        manMoves[colourIndex][index][j] = -1;
                    }

                    //capture
                    target = index + 2*manOffset[colourIndex][j];
                    dx = Math.abs( index%8 - target%8);
                    dy = Math.abs( index/8 - target/8);
                    if (dx == 2 && dy == 2 && target < 64 && target >= 0){
                        manCaptures[colourIndex][index][j] = target;
                    }
                    else{
                        manCaptures[colourIndex][index][j] = -1;
                    }

                }
            }

            for (int rayIndex = 0; rayIndex < 4; rayIndex++) {
                int dist = distanceToEdgeByRay[index][rayIndex];
                kingMoves[index][rayIndex] = new int[dist];
                for (int i = 0; i < dist; i++) {
                    kingMoves[index][rayIndex][i] = index + diagonalOffset[rayIndex]*(i+1);
                    kingRays[index][rayIndex] |= 1L << index + diagonalOffset[rayIndex]*(i+1);
                }
            }

        }
    }
}
