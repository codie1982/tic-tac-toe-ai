package com.grnt.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public String HUMAN = "O";
    private String AI = "X";
    private String TIE = "tie";
    private String EMPTY = "-1";
    private String currentPlayer = HUMAN;

    private Button[][] buttons = new Button[3][3];

    private Button buttonReset;
    private String youTurn = HUMAN;
    private int roundCount = 0;
    private int playerOnePoint;
    private int playerTwoPoint;

    private TextView playerTurn;
    private String turnPreText = "Oyuncı Sırası : ";
    private String playerPreText = "Player";
    private String roundCountPreText = "Hamle Numarası";
    private TextView playerOneText;
    private TextView playerTwoText;
    private TextView roundCountText;
    private static int COUNT = 3;
    public String[][] board = new String[3][3];

    public enum scores {
        HUMAN, AI, TIE
    }

    //Oyun Sırası
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        roundCountText = findViewById(R.id.roundCountText);
        roundCountText.setText(roundCountPreText);
        playerOneText = findViewById(R.id.playerOneText);
        playerOneText.setText(playerPreText + " 01 :");
        playerTwoText = findViewById(R.id.playerTwoText);
        playerTwoText.setText(playerPreText + " 02 :");
        playerTurn = findViewById(R.id.turnText);
        playerTurn.setText(turnPreText + "X Oyuncu");
        buttonReset = findViewById(R.id.btnReset);
        roundCount = 0;
        setup();
     /*   Location aiLocation = bestMove(board);
        board[aiLocation.Y][aiLocation.X] = AI;
        updateButtons(board);*/
    }

    @Override
    public void onClick(View v) {
        Button selectedButton = (Button) v;
        if (selectedButton.getText().toString() != "") return;
        String idName = getResources().getResourceName(selectedButton.getId());
        Location location = getLocation(idName);
        //Oyuncu Hamlesi
        board[location.X][location.Y] = HUMAN;
        updateButtons(board);
        currentPlayer = AI;
        playerTurn.setText(turnPreText + "O oyuncu");

        //AI Hamlesi
        Location aiLocation = bestMove(board);
        board[aiLocation.X][aiLocation.Y] = AI;

        currentPlayer = HUMAN;
        playerTurn.setText(turnPreText + "X oyuncu");
        updateButtons(board);
        String winner = checkWinner(board);
        System.out.println("winner " + winner);

        if (winner != null) {
            String winnerText = "";
            if (winner == HUMAN) {
                playerOnePoint++;
                winnerText = "Kazanan 1 Numaralı Oyuncu";
                playerOneText.setText(playerPreText + " 01 :" + playerOnePoint);
            } else  if(winner == AI){
                playerTwoPoint++;
                winnerText = "Kazanan 2 Numaralı Oyuncu";
                playerTwoText.setText(playerPreText + " 02 :" + playerTwoPoint);
            }else{
                winnerText = "Bu Maç Berabere";
            }
            Toast.makeText(getApplicationContext(), winnerText, Toast.LENGTH_SHORT).show();
            reset(false);
        } else {
            roundCount++;
            if (roundCount >= 9) {
                reset(false);
                Toast.makeText(getApplicationContext(), "Berabere", Toast.LENGTH_SHORT).show();
            }
        }
        roundCountText.setText(roundCountPreText + ": " + roundCount);
    }

    void reset(boolean all) {
        roundCount = 0;
        playerTurn.setText(turnPreText + "X Oyuncu");
        currentPlayer = HUMAN;
        if (all) {
            playerOnePoint = 0;
            playerTwoPoint = 0;
        }

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setText("");
                board[i][j] = "";
            }
        }
    }

    public String checkWinner(String[][] board) {

        String winner = null;

        for (int i = 0; i < 3; i++) {
            if (board[0][i] != null)
                if (board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i]) && !board[0][i].equals("")) {
                    winner = board[0][i];
                }
        }
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null)
                if (board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2]) && !board[i][0].equals("")) {
                    winner = board[i][0];
                }
        }
        if (board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]) && !board[0][0].equals("")) {
            winner = board[0][0];
        }
        if (board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0]) && !board[0][2].equals("")) {
            winner = board[0][2];
        }
        int nullCount = 0;
        for (int i = 0; i < COUNT; i++) {
            for (int j = 0; j < COUNT; j++) {
                if (board[i][j] == "") {
                    nullCount++;
                }
            }
        }
        if (nullCount == 0 && winner == null) {
            return TIE;
        } else {
            return winner;
        }

    }

    Location bestMove(String[][] board) {
        Double _bestScore = Double.NEGATIVE_INFINITY;
        Location nLocation = new Location();
        for (int i = 0; i < COUNT; i++) {//Y Ekseni
            for (int j = 0; j < COUNT; j++) {//X Ekseni
                if (board[i][j] == "") {
                    board[i][j] = AI;
                    Double score = minimax(board, false);
                    board[i][j]="";
                    if (score > _bestScore) {
                        _bestScore = score;
                        nLocation.setX(i);
                        nLocation.setY(j);
                    }
                }
            }
        }
        return nLocation;
    }

    Double minimax(String[][] board, boolean isMaximizing) {
        String result = checkWinner(board);
        //System.out.println("depth : " + depth + " result : " + result +" " + "board : " + board);
        if (result != null) return  calcScore(result);
        //System.out.println("checkWinner result : " + result);

        if (isMaximizing) {
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < COUNT; i++) {
                for (int j = 0; j < COUNT; j++) {
                    // Is the spot available?
                    if (board[i][j] == "") {
                        board[i][j] = AI;
                        double score = minimax(board,false);
                        board[i][j] = "";
                        bestScore = max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            double bestScore = Double.POSITIVE_INFINITY;
            for (int i = 0; i < COUNT; i++) {
                for (int j = 0; j < COUNT; j++) {
                    // Is the spot available?
                    if (board[i][j] == "") {
                        board[i][j] = HUMAN;
                        double score = minimax(board,true);
                        board[i][j] = "";
                        bestScore = min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    class Location {
        public int X;
        public int Y;

        Location(){
            return;
        }
        Location(int x, int y) {
            this.X = x;
            this.Y = y;
        }
        void setX(int x){
            this.X = x;
        }
        void setY(int y){
            this.Y = y;
        }

    }

    Location getLocation(String buttonName) {
        String[] nButtonName = buttonName.split("_");
        int X = nButtonName[1].charAt(0) - 48;
        int Y = nButtonName[1].charAt(1) - 48;
        return new Location(X, Y);
    }

    void setup() {
        for (int i = 0; i < COUNT; i++) {
            for (int j = 0; j < COUNT; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setText("");
                buttons[i][j].setOnClickListener(this);
                board[i][j] = "";
            }
        }
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset(true);
            }
        });
    }

    void updateButtons(String[][] board) {
        for (int i = 0; i < COUNT; i++) {//Y Ekseni
            for (int j = 0; j < COUNT; j++) { //X Ekseni
                buttons[i][j].setText(board[i][j]);
            }
        }
    }

    double calcScore(String result) {
        if (result == HUMAN) {
            return -10.0;
        } else if (result == AI) {
            return 10.0;
        } else {
            return 0.0;
        }
    }
}