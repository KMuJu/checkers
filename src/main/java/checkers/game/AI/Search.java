package checkers.game.AI;

import java.util.List;
import java.util.Random;

import checkers.game.Board;
import checkers.game.Move;
import checkers.game.MoveGeneration;
import checkers.game.TranspositionTable;
import checkers.game.util.Bitboard;

public class Search {
    Random random = new Random();

    final int ttSize = 64000;
    final int immediateWinScore = 100000;
    final int positiveInfinity = 9999999;
    final int negativeInfinity = -positiveInfinity;

    
    TranspositionTable tt;
    MoveGeneration moveGeneration;
    Board board;

    Move bestMoveThisIteration;
    int bestEvalThisIteration;
    Move bestMove;
    int bestEval;
    int currentIterativSearchDepth;

    Move invalidMove;
    MoveOrdering moveOrdering;
    AiSettings settings;
    Evalutaions evalutaions;

    boolean clearTTeachMove;
    boolean abortSearch;

    public Search(Board board, AiSettings aiSettings, MoveGeneration moveGeneration){
        this.board = board;
        this.moveGeneration = moveGeneration;
        this.settings = aiSettings;

        moveGeneration = new MoveGeneration();
        tt = new TranspositionTable(board, ttSize);
        moveOrdering = new MoveOrdering(moveGeneration, tt);
        invalidMove = Move.invalidMove;
    }

    public void abortSearch(){
        abortSearch = true;
        System.out.println("Search depth: " + currentIterativSearchDepth);
    }
    

    public void startSearch(){
        bestEvalThisIteration = bestEval = 0;
        bestMoveThisIteration = bestMove = Move.invalidMove;
        tt.setEnabled(settings.useTranspositionTable);

        if (clearTTeachMove){
            tt.clear();
        }

        currentIterativSearchDepth = 0;
        abortSearch = false;
        if (settings.useIterativSearch){
            int targetDepth = (settings.useFixedDepthSearch) ? settings.maxSearchDepth : Integer.MAX_VALUE;
            for (int searchDepth = 1; searchDepth <= targetDepth; searchDepth++) {
                searchMoves(searchDepth, 0, negativeInfinity, positiveInfinity);
                if (abortSearch){
                    break;
                }
                else{
                    currentIterativSearchDepth = searchDepth;
                    bestMove = bestMoveThisIteration;
                    bestEval = bestEvalThisIteration;
                    
                    if (isWinScore(bestEval) && !settings.endlessSearch){
                        break;
                    }
                }
            }
        }
        else {
            searchMoves (settings.maxSearchDepth, 0, negativeInfinity, positiveInfinity);
            bestMove = bestMoveThisIteration;
            bestEval = bestEvalThisIteration;
        }
    }

    private int searchMoves(int depth, int plyFromRoot, int alpha, int beta) {
        if (abortSearch) return 0;

        if (plyFromRoot > 0){

            alpha = Math.max(alpha, -immediateWinScore + plyFromRoot);
            beta = Math.min(beta, immediateWinScore - plyFromRoot);
            if (alpha >= beta){
                return alpha;
            }

        }

        //lookup in transpositiontable
        int ttValue = tt.LookUpEvaltuation(depth, plyFromRoot, alpha, beta);
        if (ttValue != tt.lookUpFailed){
            if (plyFromRoot == 0){
                bestMoveThisIteration = tt.getStoredMove();
                bestEvalThisIteration = tt.getStoredValue();
            }

            return ttValue;
        }

        if (depth == 0){
            int evaluation = quiesenceSearch(alpha, beta);
            return evaluation;
        }

        List<Move> moves = moveGeneration.generate(board);
        if (moves.size() == 0){
            //TODO fix draw
            //assumes win
            return -(immediateWinScore - plyFromRoot);
        }

        moveOrdering.orderMoves(board, moves, settings.useTranspositionTable);

        int evalType = TranspositionTable.UpperBound;
        Move bestMoveInPosition = invalidMove;
        for (int i = 0; i < moves.size(); i++) {
            board.move(moves.get(i), true);
            int eval = -searchMoves(depth-1, plyFromRoot+1, -beta, -alpha);
            board.unmakeMove(moves.get(i), true);
            //move is too good
            if (eval >= beta){
                tt.StoreEvaluation(depth, plyFromRoot, eval, evalType, moves.get(i));
                return beta;
            }
            // new best move
            if (eval > alpha){
                evalType = TranspositionTable.Exact;
                bestMoveInPosition = moves.get(i);
                alpha = eval;
                if (plyFromRoot == 0){
                    bestMoveThisIteration = moves.get(i);
                    bestEvalThisIteration = eval;
                }
            }

        }

        tt.StoreEvaluation(depth, plyFromRoot, alpha, evalType, bestMoveInPosition);

        return alpha;
    }

    private int quiesenceSearch(int alpha, int beta){
        int eval = evalutaions.evaluateBoard(board);
        if (eval >= beta){
            return beta;
        }
        List<Move> moves = moveGeneration.generate(board);
        moveOrdering.orderMoves(board, moves, settings.useTranspositionTable);
        for (int i = 0; i < moves.size(); i++) {
            board.move(moves.get(i), true);
            eval = -quiesenceSearch(-beta, -alpha);
            board.unmakeMove(moves.get(i), true);

            if (eval >= beta){
                return beta;
            }
            if (eval > alpha){
                alpha = eval;
            }
        }

        return alpha;
    }


    public Move getResult(){
        return getResult(false);
    }

    public Move getResult(boolean random){
        if (random) return randomMove();
        abortSearch();
        // System.out.println("best move" + bestMove);
        return bestMove;
    }

    private Move randomMove(){
        List<Move> moveList = moveGeneration.getMoveList();
        if (moveList.size() == 0) return null;
        return moveList.get(random.nextInt(moveList.size()));
    }

    public boolean isWinScore(int score){
        int maxWinDepth = 1000;
        return Math.abs(score) > immediateWinScore - maxWinDepth;
    }
}
