package checkers.game;

public class TranspositionTable {
    public final int lookUpFailed = Integer.MIN_VALUE;

    
    final int Exact = 0;
    final int LowerBound = 1;
    final int UpperBound = 2;
    

    Entry[] entries;

    public long size;
    boolean enabled = true;
    Board board;

    TranspositionTable(Board board, int size){
        this.board = board;
        this.size = (long) size;
        entries = new Entry[size];
    }

    public void clear(){
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new Entry();
        }
    }
    
    public int getIndex(){
        return (int) (board.ZobristKey % size);
    }

    public Move getStoredMove(){
        return entries[getIndex()].move;
    }

    public int LookUpEvaltuation(int depth, int plyFrom, int alpha, int beta){
        if (!enabled) return lookUpFailed;

        Entry entry = entries[getIndex()];
        if (entry.key == board.ZobristKey){
            if (entry.depth >= depth){
                int value = entry.value;
                if (entry.nodeType == Exact){
                    return value;
                }
                if (entry.nodeType == UpperBound && value <= alpha){
                    return value;
                }
                if (entry.nodeType == LowerBound && value >= beta){
                    return value;
                }
            }
        }
        return lookUpFailed;
    }
    
    public void StoreEvaluation(int depth, int numPlySearched, int eval, int evalType, Move move){
        if (!enabled){
            return;
        }

        Entry entry = new Entry(board.ZobristKey, CorrectScore(eval, numPlySearched), (byte) depth, (byte) evalType, move);
        entries[getIndex()] = entry;
    }

    private int CorrectScore(int eval, int numPlySearched) {
        return eval;
    }

    public class Entry {
        long key;
        int value;
        Move move;
        byte depth;
        byte nodeType;

        public Entry(long key, int value, byte depth, byte nodeType, Move move) {
            this.key = key;
            this.value = value;
            this.move = move;
            this.depth = depth;
            this.nodeType = nodeType;
        }

        public Entry() {
        }
    }
}
