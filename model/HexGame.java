package model;

public class HexGame {
    public static final int EMPTY = 0, RED = 1, BLUE = 2;
    private int n;
    private int[][] board;
    private int current;

    public HexGame(int n) {
        this.n = n;
        board = new int[n][n];
        current = RED;
    }

    public int getSize() { return n; }
    public int[][] getBoard() { return board; }
    public int getCurrent() { return current; }

    public boolean place(int r, int c, int player) {
        if (board[r][c] != EMPTY) return false;
        board[r][c] = player;
        current = (player == RED) ? BLUE : RED;
        return true;
    }

    public void undo(int r, int c, int prevPlayer) {
        board[r][c] = EMPTY;
        current = prevPlayer;
    }

    public boolean isEmpty(int r, int c) { return board[r][c] == EMPTY; }

    // TODO   : logic check win
}
