package com.example.tictactoe.Model;

import java.util.concurrent.ScheduledExecutorService;

public class AI {
    private static final String TAG = "AI";

    Board board;
    STATE prvPlayer;

    public AI(Board board) {
        this.board = board;
    }

    public static final int SCORE = 100,MAX=100000,MIN=-100000;

    public AiMove getBestMoveAlphaBeta(int prvRow,int prvCol,STATE player,int depth,int alpha,int beta){
        AiMove bestMove = new AiMove();

        if(player==STATE.PLAYER)prvPlayer=STATE.COMPUTER;
        else prvPlayer=STATE.PLAYER;

        if(board.checkWin(prvRow,prvCol,prvPlayer)){
            if(board.ST_ENDGAME==STATE.PLAYER){
                bestMove.score = depth - SCORE;
            }
            else if(board.ST_ENDGAME==STATE.COMPUTER){
                bestMove.score = SCORE - depth;
            }
            else bestMove.score = -depth;
        }
        else if(player == STATE.COMPUTER){
            bestMove.score = alpha;

            forLoop:
            for(int stRow=0;stRow<Board.numRow;++stRow){
                for(int stCol=0;stCol<Board.numCol;++stCol){
                    if(board.ST_BOARD[stRow][stCol]==STATE.NULL){

                        board.ST_BOARD[stRow][stCol]=player;
                        board.numTotalChecked++;

                        int score = getBestMoveAlphaBeta(stRow,stCol,STATE.PLAYER,
                                depth+1,bestMove.score,beta).score;

                        board.numTotalChecked--;
                        board.ST_BOARD[stRow][stCol]=STATE.NULL;

                        if(score>bestMove.score){
                            bestMove.row = stRow;
                            bestMove.col = stCol;
                            bestMove.score = score;
                        }
                        if(bestMove.score>=beta)break forLoop;
                    }
                }
            }
        }
        else{
            bestMove.score = beta;

            forLoop:
            for(int stRow=0;stRow<Board.numRow;++stRow){
                for(int stCol=0;stCol<Board.numCol;++stCol){
                    if(board.ST_BOARD[stRow][stCol]==STATE.NULL){

                        board.ST_BOARD[stRow][stCol]=player;
                        board.numTotalChecked++;

                        int score = getBestMoveAlphaBeta(stRow,stCol,STATE.COMPUTER,
                                depth+1,alpha,bestMove.score).score;

                        board.numTotalChecked--;
                        board.ST_BOARD[stRow][stCol]=STATE.NULL;

                        if(score<bestMove.score){
                            bestMove.row = stRow;
                            bestMove.col = stCol;
                            bestMove.score = score;
                        }
                        if(alpha>=bestMove.score)break forLoop;
                    }
                }
            }
        }
        return bestMove;
    }



}


class AiMove{
    private static final String TAG = "AiMove";

    int row,col,score;

    public AiMove(int row, int col, int score) {
        this.row = row;
        this.col = col;
        this.score = score;
    }

    public AiMove(int score) {
        this.score = score;
    }

    public AiMove() {
    }

}
