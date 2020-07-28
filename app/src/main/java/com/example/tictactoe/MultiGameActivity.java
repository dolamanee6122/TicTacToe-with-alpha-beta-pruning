package com.example.tictactoe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tictactoe.Model.Board;
import com.example.tictactoe.Model.STATE;

import androidx.appcompat.app.AppCompatActivity;

public class MultiGameActivity extends AppCompatActivity {

    public static final int NUM_ROW = 3;
    public static final int NUM_COL = 3;
    public static final int BM_WIDTH = 360;
    public static final int BM_HEIGHT = 360;


    //widgets
    private TextView txtPlayer1,txtPlayer2,txtWinner;
    private ImageView imgBoard;
    private Button btnNewGame,btnRestart;

    //variable for drawing chessboard
    private Board board;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private STATE currTurn=STATE.PLAYER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initWidgets();


        imgBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(!board.onTouch(v,event,currTurn))return false;

                    if(currTurn==STATE.PLAYER){
                        txtPlayer1.setBackgroundColor(Color.GRAY);
                        txtPlayer2.setBackgroundColor(Color.RED);
                        currTurn=STATE.COMPUTER;
                    }
                    else{
                        txtPlayer1.setBackgroundColor(Color.GREEN);
                        txtPlayer2.setBackgroundColor(Color.GRAY);
                        currTurn=STATE.PLAYER;
                    }
                    checkWinner();
                    return true;
                }

                return true;

            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWidgets();
            }
        });

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MultiGameActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    void checkWinner(){
        if(board.isGameEnd==true){
            if(board.ST_ENDGAME== STATE.PLAYER)
                txtWinner.setText("PLAYER1 WIN");
            else if(board.ST_ENDGAME==STATE.COMPUTER)
                txtWinner.setText("PLAYER2 WIN");
            else txtWinner.setText("DRAW");
            imgBoard.setEnabled(false);
            txtWinner.setVisibility(View.VISIBLE);
        }
    }

    private void initWidgets(){

        txtPlayer1 = findViewById(R.id.player1);
        txtPlayer2 = findViewById(R.id.player2);
        btnNewGame = findViewById(R.id.btnNewGame);
        btnRestart = findViewById(R.id.btnRestart);
        imgBoard = findViewById(R.id.board);
        txtWinner =findViewById(R.id.txtWinner);


        txtPlayer1.setBackgroundColor(Color.RED);
        imgBoard.setEnabled(true);
        txtWinner.setVisibility(View.GONE);

        bitmap = Bitmap.createBitmap(BM_WIDTH,BM_HEIGHT,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setARGB(255,200,200,200);
        paint.setStrokeWidth(2);

        board=new Board(MultiGameActivity.this,canvas,paint,BM_WIDTH,BM_HEIGHT,NUM_ROW,NUM_COL);

        board.initBoard();
        board.drawBoard();


        imgBoard.setImageBitmap(bitmap);
    }
}
