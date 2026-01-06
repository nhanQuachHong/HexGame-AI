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

public HexGame copy() {
    HexGame newGame = new HexGame(this.getSize());
    int[][] newBoard = newGame.getBoard();
    int[][] oldBoard = this.getBoard();
    
    for (int i = 0; i < this.getSize(); i++) {
        for (int j = 0; j < this.getSize(); j++) {
            newBoard[i][j] = oldBoard[i][j];
        }
    }
    return newGame;
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

    public boolean isEmpty(int r, int c) { 
        return board[r][c] == EMPTY; 
    }

    public int checkWinner() {
        if (hasPlayerWon(RED)) return RED;
        if (hasPlayerWon(BLUE)) return BLUE;
        return EMPTY;
    }

    public boolean hasPlayerWon(int player) {
        boolean[][] visited = new boolean[n][n];
        java.util.Deque<int[]> stack = new java.util.ArrayDeque<>();
        int[][] dirs = {{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0}};

        if (player == RED) {
            for (int c = 0; c < n; c++) {
                if (board[0][c] == RED) {
                    visited[0][c] = true;
                    stack.push(new int[]{0, c});
                }
            }
            while (!stack.isEmpty()) {
                int[] p = stack.pop();
                int r = p[0], c = p[1];
                if (r == n - 1) return true;
                for (int[] d : dirs) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < n && nc >= 0 && nc < n && !visited[nr][nc] && board[nr][nc] == RED) {
                        visited[nr][nc] = true;
                        stack.push(new int[]{nr, nc});
                    }
                }
            }
        } else if (player == BLUE) {
            for (int r = 0; r < n; r++) {
                if (board[r][0] == BLUE) {
                    visited[r][0] = true;
                    stack.push(new int[]{r, 0});
                }
            }
            while (!stack.isEmpty()) {
                int[] p = stack.pop();
                int r = p[0], c = p[1];
                if (c == n - 1) return true;
                for (int[] d : dirs) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < n && nc >= 0 && nc < n && !visited[nr][nc] && board[nr][nc] == BLUE) {
                        visited[nr][nc] = true;
                        stack.push(new int[]{nr, nc});
                    }
                }
            }
        }
        return false;
    }
}