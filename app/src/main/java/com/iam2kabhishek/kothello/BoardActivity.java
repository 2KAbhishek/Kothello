package com.iam2kabhishek.kothello;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iam2kabhishek.kothello.R;

public class BoardActivity extends AppCompatActivity {
    protected static final String KEY_MODE = "mode";
    protected static final int MODE_TWO_USERS = 300;
    protected static int gameMode;
    protected static int playerColor;
    protected Toast toast;
    protected Tile[][] board;
    protected boolean turnBlack;
    private ImageView nowPlaysIV;
    private TextView[] countTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        turnBlack = true; //First turn plays black
        nowPlaysIV = findViewById(R.id.nowPlaysIV);
        playerColor = Tile.BLACK;

        gameMode = MODE_TWO_USERS;

        countTV = new TextView[2];
        countTV[0] = findViewById(R.id.countBlackTV);
        countTV[1] = findViewById(R.id.countWhiteTV);

        board = new Tile[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Tile();
            }
        }
        initializeButtons();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setNeighbors(board, i, j);
            }
        }
        //Initial board has 2 whites and 2 blacks on the center.
        board[3][3].setColor(Tile.WHITE);
        board[3][4].setColor(Tile.BLACK);
        board[4][3].setColor(Tile.BLACK);
        board[4][4].setColor(Tile.WHITE);


        updateCounts();
        addClickListeners();
    }

    @Override
    protected void onPause() {
        if (toast != null) {
            toast.cancel();
        }
        super.onPause();
    }

    protected boolean playsBlack() {
        return turnBlack;
    }

    private int getCurrentColor() {
        if (playsBlack())
            return Tile.BLACK;
        else
            return Tile.WHITE;
    }

    private int getWinner() {
        int[] count = countTiles(board);
        if (count[Tile.WHITE] > count[Tile.BLACK])
            return Tile.WHITE;
        if (count[Tile.WHITE] < count[Tile.BLACK])
            return Tile.BLACK;
        return Tile.GREEN;
    }

    //Calculate and show many tiles of each color exist on the board.
    private void updateCounts() {
        int[] c = countTiles(board);
        countTV[Tile.BLACK].setText(String.format(getResources().getString(R.string.count), c[Tile.BLACK]));
        countTV[Tile.WHITE].setText(String.format(getResources().getString(R.string.count), c[Tile.WHITE]));
    }

    private void nextTurn() {
        updateCounts();
        turnBlack = !turnBlack;
        if (playsBlack())
            nowPlaysIV.setImageResource(R.drawable.black);
        else
            nowPlaysIV.setImageResource(R.drawable.white);
    }

    private int[] countTiles(Tile[][] board) {
        int[] count = new int[3];
        for (Tile[] row : board) {
            for (final Tile tile : row) {
                if (tile.isBlack())
                    count[Tile.BLACK]++;
                else if (tile.isWhite())
                    count[Tile.WHITE]++;
                else
                    count[Tile.GREEN]++;
            }
        }
        return count;
    }

    private void addClickListeners() {
        for (Tile[] row : board) {
            for (final Tile tile : row) {
                if (gameMode == MODE_TWO_USERS) {
                    tile.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean valid;
                            if (toast != null) {
                                toast.cancel();
                            }

                            if (playsBlack())
                                valid = Tile.flipTiles(tile, Tile.BLACK);
                            else
                                valid = Tile.flipTiles(tile, Tile.WHITE);

                            if (valid) {
                                nextTurn();
                                checkAbilityToMove();
                            } else
                                showToast(getResources().getString(R.string.invalid));
                        }
                    });
                } else {
                    showToast("Not implemented yet");
                }
            }
        }
    }

    private void checkAbilityToMove() {
        int ableToMove = Tile.ableToMove(board, getCurrentColor());

        if (ableToMove == Tile.BOARD_FULL) {
            gameOverDialog(Tile.BOARD_FULL);
        } else if (countTiles(board)[getCurrentColor()] == 0) {
            gameOverDialog(getCurrentColor());
        } else if (ableToMove == Tile.CANT_PLAY) {
            if (getCurrentColor() == Tile.BLACK) {
                if (Tile.ableToMove(board, Tile.WHITE) == Tile.CANT_PLAY) {
                    showToast(getResources().getString(R.string.no_moves));
                    gameOverDialog(Tile.CANT_PLAY);
                }
                //Black has no available move. White plays again.
                showToastLong(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.black), getResources().getString(R.string.white)));
            } else {
                if (Tile.ableToMove(board, Tile.BLACK) == Tile.CANT_PLAY) {
                    showToastLong(getResources().getString(R.string.no_moves));
                    gameOverDialog(Tile.CANT_PLAY);
                }
                //White has no available move. Black plays again.
                showToastLong(String.format(getResources().getString(R.string.cant_play), getResources().getString(R.string.white), getResources().getString(R.string.black)));
            }
            nextTurn();
        }
    }

    protected void showToast(String str) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(BoardActivity.this, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void showToastLong(String str) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(BoardActivity.this, str, Toast.LENGTH_LONG);
        toast.show();
    }

    private void gameOverDialog(int why) {
        String title = getResources().getString(R.string.game_over), msg, reason = "";
        int[] count = countTiles(board);
        int winner = getWinner();

        double score = 1000 * count[playerColor] / 64;
        toast.cancel();

        if (why == Tile.CANT_PLAY)
            reason = getResources().getString(R.string.no_moves) + " ";
        else if (why == Tile.WHITE) { //There are no White tiles!
            reason = String.format(getString(R.string.early_victory), getString(R.string.white));
            score = 0;
        } else if (why == Tile.BLACK) { //There are no Black tiles!
            reason = String.format(getString(R.string.early_victory), getString(R.string.black));
            score = 0;
        }

        if (winner == Tile.GREEN) { //TIE
            msg = getResources().getString(R.string.tie) + " " + reason + "\nScore: " + (int) score;
        } else if (gameMode == MODE_TWO_USERS) {
            if (winner == Tile.WHITE)
                msg = reason + getResources().getString(R.string.win_white);
            else
                msg = reason + getResources().getString(R.string.win_black);
        } else {
            if (winner == playerColor) {
                msg = getResources().getString(R.string.you_won) + " " + reason + "\nScore: " + (int) score;
            } else
                msg = getResources().getString(R.string.you_lost) + " " + reason + "\nScore: " + (int) score;

        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        gameReset();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.main_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), ModeActivity.class));
                    }
                })
                .setPositiveButton(getResources().getString(R.string.play_again), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameReset();
                    }
                }).create().show();
    }

    private void gameReset() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void initializeButtons() {
        board[0][0].setButton(findViewById(R.id.tile00));
        board[0][1].setButton(findViewById(R.id.tile01));
        board[0][2].setButton(findViewById(R.id.tile02));
        board[0][3].setButton(findViewById(R.id.tile03));
        board[0][4].setButton(findViewById(R.id.tile04));
        board[0][5].setButton(findViewById(R.id.tile05));
        board[0][6].setButton(findViewById(R.id.tile06));
        board[0][7].setButton(findViewById(R.id.tile07));

        board[1][0].setButton(findViewById(R.id.tile10));
        board[1][1].setButton(findViewById(R.id.tile11));
        board[1][2].setButton(findViewById(R.id.tile12));
        board[1][3].setButton(findViewById(R.id.tile13));
        board[1][4].setButton(findViewById(R.id.tile14));
        board[1][5].setButton(findViewById(R.id.tile15));
        board[1][6].setButton(findViewById(R.id.tile16));
        board[1][7].setButton(findViewById(R.id.tile17));

        board[2][0].setButton(findViewById(R.id.tile20));
        board[2][1].setButton(findViewById(R.id.tile21));
        board[2][2].setButton(findViewById(R.id.tile22));
        board[2][3].setButton(findViewById(R.id.tile23));
        board[2][4].setButton(findViewById(R.id.tile24));
        board[2][5].setButton(findViewById(R.id.tile25));
        board[2][6].setButton(findViewById(R.id.tile26));
        board[2][7].setButton(findViewById(R.id.tile27));

        board[3][0].setButton(findViewById(R.id.tile30));
        board[3][1].setButton(findViewById(R.id.tile31));
        board[3][2].setButton(findViewById(R.id.tile32));
        board[3][3].setButton(findViewById(R.id.tile33));
        board[3][4].setButton(findViewById(R.id.tile34));
        board[3][5].setButton(findViewById(R.id.tile35));
        board[3][6].setButton(findViewById(R.id.tile36));
        board[3][7].setButton(findViewById(R.id.tile37));

        board[4][0].setButton(findViewById(R.id.tile40));
        board[4][1].setButton(findViewById(R.id.tile41));
        board[4][2].setButton(findViewById(R.id.tile42));
        board[4][3].setButton(findViewById(R.id.tile43));
        board[4][4].setButton(findViewById(R.id.tile44));
        board[4][5].setButton(findViewById(R.id.tile45));
        board[4][6].setButton(findViewById(R.id.tile46));
        board[4][7].setButton(findViewById(R.id.tile47));

        board[5][0].setButton(findViewById(R.id.tile50));
        board[5][1].setButton(findViewById(R.id.tile51));
        board[5][2].setButton(findViewById(R.id.tile52));
        board[5][3].setButton(findViewById(R.id.tile53));
        board[5][4].setButton(findViewById(R.id.tile54));
        board[5][5].setButton(findViewById(R.id.tile55));
        board[5][6].setButton(findViewById(R.id.tile56));
        board[5][7].setButton(findViewById(R.id.tile57));

        board[6][0].setButton(findViewById(R.id.tile60));
        board[6][1].setButton(findViewById(R.id.tile61));
        board[6][2].setButton(findViewById(R.id.tile62));
        board[6][3].setButton(findViewById(R.id.tile63));
        board[6][4].setButton(findViewById(R.id.tile64));
        board[6][5].setButton(findViewById(R.id.tile65));
        board[6][6].setButton(findViewById(R.id.tile66));
        board[6][7].setButton(findViewById(R.id.tile67));

        board[7][0].setButton(findViewById(R.id.tile70));
        board[7][1].setButton(findViewById(R.id.tile71));
        board[7][2].setButton(findViewById(R.id.tile72));
        board[7][3].setButton(findViewById(R.id.tile73));
        board[7][4].setButton(findViewById(R.id.tile74));
        board[7][5].setButton(findViewById(R.id.tile75));
        board[7][6].setButton(findViewById(R.id.tile76));
        board[7][7].setButton(findViewById(R.id.tile77));
    }
}