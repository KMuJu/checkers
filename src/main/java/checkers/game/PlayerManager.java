package checkers.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import checkers.GameController;
import checkers.game.AI.AiSettings;

public class PlayerManager {
    
    public boolean whiteToMove;
    public int colourToMoveIndex;

    //{white, black} if player is human
    public boolean[] playerIsHuman = new boolean[]{false, true};
    boolean choosingMove = false;
    int start, target;

    public List<Move> moves;
    public Stack<Move> movesDone;
    public MoveGeneration moveGeneration;
    public Board board;
    public GameController gameController;

    //players {white, black}
    AbstractPlayer[] players;

    public PlayerManager(GameController gc){
        gameController = gc;
        moveGeneration = new MoveGeneration();
        board = new Board(gc);
        board.init();

        moves = new ArrayList<>(32);
        movesDone = new Stack<>();
        generateMoves();
        colourToMoveIndex = board.colourToMoveIndex;

        AiSettings aiSettings = AiSettings.useTtIterativFixedNoClear;

        players = new AbstractPlayer[2];
        //add players to array based on if player is human from boolean array
        for (int i = 0; i < 2; i++) {
            if (playerIsHuman[i]){
                players[i] = new HumanPlayer(this);
            }
            else{
                AIPlayer player = new AIPlayer(moveGeneration, this, aiSettings);
                player.setTimer(new Timer(player, AiSettings.moveTime));
                players[i] = player;
            }
        }
        //first player can move
        players[1].canMove();
    }


    /**
     * Used to check if next player is human
     * @return The player that is supposed to move next
     */
    public AbstractPlayer getNextPlayer(){
        return players[colourToMoveIndex];
    }

    private void checkIfWin(){
        // if opponents can't move -> win
        // if opponent has no pieces -> win
        // if moveCounter >= maxCounts -> draw TODO: implement draw
        if ((moves.size() == 0 && !moveGeneration.samePlayer) || board.checkMoveCounter() || board.opponentHasNoPieces()){
            gameController.win(board.whiteToMove());
            for (int i = 0; i < 2; i++) {
                if (!playerIsHuman[i]){
                    ((AIPlayer) players[i]).timer.stop();
                }
            }
        }
    }

    /**
     * Assumes that the move is already legal
     * @param m - move that the board is going do
     */
    public void move(Move m){
        //moves
        if (m == null) return;
        board.move(m);
        generateMoves();
        colourToMoveIndex = board.colourToMoveIndex;
        movesDone.push(m);
        getNextPlayer().canMove();
    }

    /**
     * Generates moves for the next player
     * and checks if the player that made the move wins
     */
    private void generateMoves() {
        // Long start = System.nanoTime();
        moves = moveGeneration.generate(board);
        // System.out.println((System.nanoTime() - start) / Math.pow(10,9));
        checkIfWin();
        if (moveGeneration.samePlayer && moves.size()==0){
            board.nextToMove();
            generateMoves();
        }
    }

    //TODO: fix player behavior,(Stop next player, get previous player to play)
    /**
     * Undos the move
     */
    
    public void undo() {
        board.unmakeMove(movesDone.pop());
        generateMoves();
    }

    public void showMovesForIndex(int square){
        gameController.showMovesForIndex(square);
    }
}
