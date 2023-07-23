package checkers.game;

public class PieceList {
    int[] occupiedPieces;
    int[] map;
    int numPieces;

    PieceList(int maxNumberOfPieces){
        occupiedPieces = new int[maxNumberOfPieces];
        map = new int[64];
    }
    
    PieceList(PieceList p1, PieceList p2){
        for (int i = 0; i < p1.size(); i++) {
            add(p1.at(i));
        }
        for (int i = 0; i < p2.size(); i++) {
            add(p2.at(i));
        }
    }

    public void add(int square){
        map[square] = numPieces;
        occupiedPieces[numPieces] = square;
        numPieces ++;
    }

    public void remove(int square){
        int index = map[square];
        occupiedPieces[index] = occupiedPieces[numPieces - 1];
        map[occupiedPieces[index]] = index;
        numPieces --;
    }

    public void move(int start, int target){
        int index = map[start];
        occupiedPieces[index] = target;
        map[target] = index;
    }

    public int size(){
        return numPieces;
    }

    public int at(int i){
        return occupiedPieces[i];
    }
}
