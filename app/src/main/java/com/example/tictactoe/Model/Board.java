package com.example.tictactoe.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.tictactoe.R;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final String TAG = "Board";

    private Context context;
    private Canvas canvas;
    private Paint paint;
    private int bmWidth, bmHeight;
    public static int numRow, numCol;

    private List<Line> lineList;

    public static int N = 3;
    public static int numTotalChecked;
    public static boolean isGameEnd;
    private int heightBox;


    public static STATE[][]  ST_BOARD;
    public static STATE ST_ENDGAME;
    private static STATE currStatePlayer;

    public static int       prvRow;
    public static int       prvCol;

    public Board(Context context, Canvas canvas, Paint paint, int bmWidth, int bmHeight, int numRow, int numCol) {
        this.context = context;
        this.canvas = canvas;
        this.paint = paint;
        this.bmWidth = bmWidth;
        this.bmHeight = bmHeight;
        this.numRow = numRow;
        this.numCol = numCol;
    }


    public void initBoard(){
        lineList = new ArrayList<>();
        int extra = (int)paint.getStrokeWidth()/2;

        for(int i = 0;i <= numRow;++i){
            int rowStart = i*bmHeight/numRow;
            if(i==0)rowStart+=extra;
            if(i==numRow)rowStart-=extra;
            lineList.add(new Line(new Point(0,rowStart),new Point(bmWidth,rowStart)));
        }

        for(int i = 0;i <= numCol;++i){
            int colStart = i*bmWidth/numCol;
            if(i==0)colStart+=extra;
            if(i==numCol)colStart-=extra;
            lineList.add(new Line(new Point(colStart,0),new Point(colStart,bmHeight)));
        }

        ST_BOARD = new STATE[numRow][numCol];
        for(int i=0;i<numRow;++i){
            for(int j=0;j<numCol;++j)
                ST_BOARD[i][j]=STATE.NULL;
        }

        heightBox = bmHeight/numRow;

        /**
         * initialising the initial player state
         */
        currStatePlayer = STATE.PLAYER;
        numTotalChecked = 0;
        isGameEnd = false;
        ST_ENDGAME = STATE.NULL;

    }

    public void drawBoard(){
        for(Line line:lineList){
            canvas.drawLine(line.getStartPoint().getX(),line.getStartPoint().getY(),
                    line.getEndPoint().getX(),line.getEndPoint().getY(),paint);
        }
    }


    public void BestAiMove(int prvRow,int prvCol,View view){
        AiMove aiMove;
        AI ai = new AI(this);

        aiMove = ai.getBestMoveAlphaBeta(prvRow,prvCol,STATE.COMPUTER,0,AI.MIN,AI.MAX);

        ST_BOARD[aiMove.row][aiMove.col]=STATE.COMPUTER;
        //Toast.makeText(context, "comp" + aiMove.row + " : " + aiMove.col, Toast.LENGTH_SHORT).show();
        drawCheckOnBoard(aiMove.row,aiMove.col,view,STATE.COMPUTER);

        numTotalChecked++;
        isGameEnd = this.checkWin(aiMove.row,aiMove.col,STATE.COMPUTER);
    }

    public boolean onTouch(View v, MotionEvent e,STATE currStatePlayer){

        Point point = new Point((int)e.getX(),(int)e.getY());

        int width = v.getWidth();
        int height = v.getHeight();

        int widthOfBox = width/numCol;
        int heightOfBox = height/numRow;

        int stateCol = point.getX()/heightOfBox;
        int stateRow = point.getY()/widthOfBox;


        if(ST_BOARD[stateRow][stateCol]==STATE.NULL){

            ST_BOARD[stateRow][stateCol]=currStatePlayer;
            prvCol = stateRow;
            prvRow = stateCol;

            ++numTotalChecked;
            //Toast.makeText(context, stateRow +" : " + stateCol, Toast.LENGTH_SHORT).show();

            drawCheckOnBoard(stateRow,stateCol,v,currStatePlayer);
            isGameEnd = checkWin(stateRow,stateCol,currStatePlayer);

            return true;
        }

        return false;
    }

    void drawCheckOnBoard(int stateRow,int stateCol,View view,STATE currStatePlayer){
        Point center = new Point((int)((stateCol+0.5)*bmWidth/numCol),(int)((stateRow+0.5)*bmHeight/numRow));

        Point leftTop = new Point(center.getX()-heightBox/2,center.getY()-heightBox/2);
        Point rightBottom = new Point(center.getX()+heightBox/2,center.getY()+heightBox/2);
        Bitmap touchBox = null;
        if(currStatePlayer==STATE.PLAYER){
            touchBox = BitmapFactory.decodeResource(context.getResources(), R.drawable.o);
        } else if(currStatePlayer == STATE.COMPUTER){
            touchBox = BitmapFactory.decodeResource(context.getResources(),R.drawable.x);
        }

        if(touchBox!=null){
            canvas.drawBitmap(touchBox,new Rect(0,0,touchBox.getWidth(),touchBox.getHeight()),
                    new Rect(leftTop.getX(),leftTop.getY(),rightBottom.getX(),rightBottom.getY()),paint);

            view.invalidate();
        }

    }

    public boolean checkWin(int currRow,int currCol,STATE currStatePlayer){

        if(checkCol(currCol,currStatePlayer) || checkRow(currRow,currStatePlayer)
                || checkDiagonal(currRow,currCol,currStatePlayer)
                || checkOtherDiagonal(currRow,currCol,currStatePlayer)){
            ST_ENDGAME = currStatePlayer;
            return true;
        }
        if(numTotalChecked == (numCol* numRow)){
            ST_ENDGAME = STATE.TIE_VAL;
            return true;
        }
        return false;
    }

    /**
     * checking if the @param currRow Row is win state
     * @param currRow
     * @param currStatePlayer
     * @return
     */
    public boolean checkRow(int currRow,STATE currStatePlayer){
        for(int i=0;i<numCol;++i){
            if(ST_BOARD[currRow][i]!=currStatePlayer)
                return false;
        }
        return true;
    }

    /**
     * checking  if the @param currCol column for victory
     * @param currCol
     * @param currStatePlayer
     * @return
     */
    public boolean checkCol(int currCol,STATE currStatePlayer){
        for(int i=0;i<numRow;++i){
            if(ST_BOARD[i][currCol]!=currStatePlayer)
                return false;
        }
        return true;
    }

    public boolean checkDiagonal(int currRow,int currCol,STATE currStatePlayer){

        int row=currRow,col=currCol,count=0;
        while(row<numRow && col<numCol){
            if(ST_BOARD[row][col]==currStatePlayer)
                ++count;
            ++row;
            ++col;
        }
        row = currRow-1;
        col = currCol-1;
        while(row>=0 && col>=0){
            if(ST_BOARD[row][col]==currStatePlayer)
                ++count;
            --row;
            --col;
        }
//        if(count>=N)
//        Toast.makeText(context, "diag", Toast.LENGTH_SHORT).show();
        return (count>=N);
    }

    public boolean checkOtherDiagonal(int currRow,int currCol,STATE currStatePlayer){

        int row=currRow,col=currCol,count=0;
        while(row<numRow && col>=0){
            if(ST_BOARD[row][col]==currStatePlayer)
                ++count;
            ++row;
            --col;
        }
        row = currRow-1;
        col = currCol+1;
        while(row>=0 && col<numCol){
            if(ST_BOARD[row][col]==currStatePlayer)
                ++count;
            --row;
            ++col;
        }
//        if(count>=N)
//        Toast.makeText(context, "otherDiag", Toast.LENGTH_SHORT).show();
        return (count>=N);
    }

}
