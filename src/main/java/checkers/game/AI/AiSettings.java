package checkers.game.AI;

public class AiSettings {
    public static final long moveTime = (long)(0.2*Math.pow(10, 9));

    public final int maxSearchDepth = 20;

    public boolean useTranspositionTable;
    public boolean useIterativSearch;
    public boolean useFixedDepthSearch;
    public boolean clearTTeachMove;

    public boolean endlessSearch = false;
    public boolean randomSearch = false;

    public static AiSettings useTtIterativFixedNoClear = new AiSettings(true, true, true, false);
    public static AiSettings useTtNotIterativFixedNoClear = new AiSettings(true, false, true, false);

    public AiSettings(boolean useTranspositionTable, boolean useIterativSearch, boolean useFixedDepthSearch,
            boolean clearTTeachMove) {
        this.useTranspositionTable = useTranspositionTable;
        this.useIterativSearch = useIterativSearch;
        this.useFixedDepthSearch = useFixedDepthSearch;
        this.clearTTeachMove = clearTTeachMove;
    }

    
}
