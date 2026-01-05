package model;

import java.util.*;

public class HexAI {
    private HexGame game;
    private final int INF = 1000000000; 
    private final int WIN_SCORE = 100000; 

    public HexAI(HexGame game) {
        this.game = game;
    }

    public int[] bestMove(int depth) {
        int[] bestMove = null;
        int maxVal = -INF;
        int n = game.getSize();
        int alpha = -INF;
        int beta = INF;

        List<int[]> moves = getOrderedMoves();

        for (int[] move : moves) {
            int r = move[0];
            int c = move[1];
            game.place(r, c, HexGame.BLUE); 
            
            int val = minimax(false, depth - 1, alpha, beta);
            
            game.undo(r, c, HexGame.BLUE); 

            if (val > maxVal) {
                maxVal = val;
                bestMove = move;
            }
            if (maxVal > alpha) alpha = maxVal;
        }
        return bestMove;
    }

    public int minimax(boolean maxmin, int depth, int alpha, int beta) {
        int winner = game.checkWinner();
        if (winner == HexGame.BLUE) return WIN_SCORE + depth; 
        if (winner == HexGame.RED) return -WIN_SCORE - depth;
        if (depth == 0) {
            return heuristic();
        }
        List<int[]> moves = getOrderedMoves();
        if (maxmin == true) { 
            int maxEval = -INF;
            for (int[] move : moves) {
                game.place(move[0], move[1], HexGame.BLUE);
                int eval = minimax(false, depth - 1, alpha, beta);
                game.undo(move[0], move[1], HexGame.BLUE);

                if (eval > maxEval) maxEval = eval;
                if (maxEval > alpha) alpha = maxEval;
                if (beta <= alpha) break; 
            }
            return maxEval;
        } else { 
            int minEval = INF;
            for (int[] move : moves) {
                game.place(move[0], move[1], HexGame.RED);
                int eval = minimax(true, depth - 1, alpha, beta);
                game.undo(move[0], move[1], HexGame.RED);

                if (eval < minEval) minEval = eval;
                if (minEval < beta) beta = minEval;
                if (beta <= alpha) break; 
            }
            return minEval;
        }
    }

    private int heuristic() {
        int blueDist = getShortestPath(HexGame.BLUE);
        int redDist = getShortestPath(HexGame.RED);
        return (redDist - blueDist) * 100;
    }

    private int getShortestPath(int player) {
        int n = game.getSize();
        int[][] board = game.getBoard();
        int[][] dist = new int[n][n];
        for(int[] row : dist) Arrays.fill(row, INF);
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));

        if (player == HexGame.RED) {
            for (int c = 0; c < n; c++) {
                int cost = (board[0][c] == player) ? 0 : (board[0][c] == HexGame.EMPTY ? 1 : INF);
                if (cost != INF) { dist[0][c] = cost; pq.add(new Node(0, c, cost)); }
            }
        } else {
            for (int r = 0; r < n; r++) {
                int cost = (board[r][0] == player) ? 0 : (board[r][0] == HexGame.EMPTY ? 1 : INF);
                if (cost != INF) { dist[r][0] = cost; pq.add(new Node(r, 0, cost)); }
            }
        }
        
        int[][] dirs = {{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0}};
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current.cost > dist[current.r][current.c]) continue;
            
            if (player == HexGame.RED && current.r == n - 1) return current.cost;
            if (player == HexGame.BLUE && current.c == n - 1) return current.cost;

            for (int[] d : dirs) {
                int nr = current.r + d[0], nc = current.c + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                    int moveCost = (board[nr][nc] == player) ? 0 : (board[nr][nc] == HexGame.EMPTY ? 1 : INF);
                    if (moveCost != INF && dist[current.r][current.c] + moveCost < dist[nr][nc]) {
                        dist[nr][nc] = dist[current.r][current.c] + moveCost;
                        pq.add(new Node(nr, nc, dist[nr][nc]));
                    }
                }
            }
        }
        return INF;
    }

    private List<int[]> getOrderedMoves() {
        List<int[]> moves = new ArrayList<>();
        int n = game.getSize();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (game.isEmpty(r, c)) moves.add(new int[]{r, c});
            }
        }
        return moves;
    }

    private static class Node {
        int r, c, cost;
        public Node(int r, int c, int cst) { this.r=r; this.c=c; this.cost=cst; }
    }
}