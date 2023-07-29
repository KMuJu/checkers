package checkers.game;

import javafx.animation.AnimationTimer;

public class Timer extends AnimationTimer{
    AIPlayer player;
    long start;
    long elapsedTime;
    long time;

    boolean paused;
    boolean wasPaused;

    public Timer(AIPlayer player, long time){
        this.player = player;
        this.time = time;
    }

    @Override
    public void handle(long arg0) {
        if (paused) return;
        elapsedTime = System.nanoTime() - start;
        if (elapsedTime >= time){
            stop();
            finished();
        }
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        super.start();
        if (!paused) {
            start = System.nanoTime();
            wasPaused = true;
        } else wasPaused = false;
        // if (!wasPaused){
        //     // player;
        // }
        paused = false;
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        super.stop();
        elapsedTime = 0;
    }

    public void pause(){
        paused = true;
    }

    public void finished(){
        Move m = player.chooseMove();
        System.out.println("AI move: " + m);
        player.move(m);
    }
    
}
