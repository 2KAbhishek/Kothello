package com.iam2kabhishek.kothello;

import android.view.View;
import android.widget.Button;

import com.iam2kabhishek.kothello.R;

class Tile {
    static final int BLACK = 0;
    static final int WHITE = 1;
    static final int GREEN = 2;
    static final int CANT_PLAY = 101;
    static final int BOARD_FULL = 102;
    private static final int CAN_PLAY = 100;
    Tile[] neighbor;
    private Button button;
    private int color;

    Tile() {
        neighbor = new Tile[8];
        button = null;
        color = GREEN;
    }

    static int ableToMove(Tile[][] board, int color) {
        boolean full = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].isEmpty()) {
                    if (full)
                        full = false;
                    //check if a tile can be played on this i,j position:
                    for (int direction = 0; direction < 8; direction++) {
                        if (flipAllowed(board[i][j], color, direction))
                            return CAN_PLAY;
                    }
                }
            }
        }
        if (full)
            return BOARD_FULL;
        return CANT_PLAY;
    }

    static boolean flipTiles(Tile t, int color) {
        Tile examineTile = t;
        boolean flag = false;

        if (t.color != GREEN)
            return false;

        //check
        for (int direction = 0; direction < 8; direction++) {
            if (flipAllowed(t, color, direction)) {
                if (!flag)
                    flag = true;
                do {
                    examineTile = examineTile.neighbor[direction];
                    examineTile.setColor(color);
                } while (examineTile.neighbor[direction].getColor() != color);
                examineTile = t;
            }
        }
        if (flag)
            t.setColor(color);
        return flag;
    }

    static boolean flipAllowed(Tile t, int color, int direction) {
        Tile examineTile = t.neighbor[direction];
        int oppositeColor;

        if (color == WHITE)
            oppositeColor = BLACK;
        else if (color == BLACK)
            oppositeColor = WHITE;
        else
            return false;

        if (!t.isEmpty() || examineTile == null || examineTile.getColor() != oppositeColor)
            return false;

        do {
            examineTile = examineTile.neighbor[direction];
            if (examineTile == null || examineTile.getColor() == GREEN)
                return false;
        } while (examineTile.getColor() == oppositeColor);
        return true;
    }

    Button getButton() {
        return button;
    }

    void setButton(View v) {
        button = (Button) v;
    }

    int getColor() {
        return color;
    }

    void setColor(int color) {
        if (button != null) {
            if (color == WHITE)
                button.setBackgroundResource(R.drawable.white);
            if (color == BLACK)
                button.setBackgroundResource(R.drawable.black);
            if (color == GREEN)
                button.setBackgroundResource(R.drawable.green);
        }
        this.color = color;
    }

    boolean isEmpty() {
        return color == GREEN;
    }

    boolean isWhite() {
        return color == WHITE;
    }

    boolean isBlack() {
        return color == BLACK;
    }

    void setNeighbors(Tile[][] board, int row, int col) {

        // NORTH = neighbor[0]
        if (row == 0)
            neighbor[0] = null;
        else
            neighbor[0] = board[row - 1][col];

        // NORTH-EAST = neighbor[1]
        if (row == 0 || col == 7)
            neighbor[1] = null;
        else
            neighbor[1] = board[row - 1][col + 1];

        // EAST = neighbor[2]
        if (col == 7)
            neighbor[2] = null;
        else
            neighbor[2] = board[row][col + 1];

        // SOUTH-EAST = neighbor[3]
        if (row == 7 || col == 7)
            neighbor[3] = null;
        else
            neighbor[3] = board[row + 1][col + 1];

        // SOUTH = neighbor[4]
        if (row == 7)
            neighbor[4] = null;
        else
            neighbor[4] = board[row + 1][col];

        // SOUTH-WEST = neighbor[5]
        if (row == 7 || col == 0)
            neighbor[5] = null;
        else
            neighbor[5] = board[row + 1][col - 1];

        // WEST = neighbor[6]
        if (col == 0)
            neighbor[6] = null;
        else
            neighbor[6] = board[row][col - 1];

        // NORTH-WEST = neighbor[7]
        if (row == 0 || col == 0)
            neighbor[7] = null;
        else
            neighbor[7] = board[row - 1][col - 1];
    }
}