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
        int alpha = -INF;
        int beta = INF;

        List<int[]> moves = getOrderedMoves(this.game);

        for (int[] move : moves) {
            int r = move[0];
            int c = move[1];

            HexGame childState = this.game.copy(); 
            childState.place(r, c, HexGame.BLUE);

            int val = minimax(childState, false, depth - 1, alpha, beta);

            if (val > maxVal) {
                maxVal = val;
                bestMove = move;
            }
            if (maxVal > alpha) alpha = maxVal;
        }
        return bestMove;
    }

    public int minimax(HexGame state, boolean maxmin, int depth, int alpha, int beta) {
        int winner = state.checkWinner();
        if (winner == HexGame.BLUE) return WIN_SCORE + depth;
        if (winner == HexGame.RED) return -WIN_SCORE - depth;
        
        if (depth == 0) {
            return heuristic(state);
        }

        List<int[]> moves = getOrderedMoves(state);

        if (maxmin) {
            int maxEval = -INF;
            for (int[] move : moves) {
                HexGame childState = state.copy();
                childState.place(move[0], move[1], HexGame.BLUE);

                int eval = minimax(childState, false, depth - 1, alpha, beta);

                if (eval > maxEval) maxEval = eval;
                if (maxEval > alpha) alpha = maxEval;
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = INF;
            for (int[] move : moves) {
                HexGame childState = state.copy();
                childState.place(move[0], move[1], HexGame.RED);

                int eval = minimax(childState, true, depth - 1, alpha, beta);

                if (eval < minEval) minEval = eval;
                if (minEval < beta) beta = minEval;
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int heuristic(HexGame state) {
        int blueDist = getShortestPath(state, HexGame.BLUE);
        int redDist = getShortestPath(state, HexGame.RED);
        
        if (blueDist == INF) blueDist = 1000;
        if (redDist == INF) redDist = 1000;

        int pathScore = (redDist - blueDist) * 1000; 

        int bluePos = getPositionalScore(state, HexGame.BLUE);
        int redPos = getPositionalScore(state, HexGame.RED);
        
        int posScore = (bluePos - redPos) * 10; 

        return pathScore + posScore;
    }

    private int getShortestPath(HexGame state, int player) {
        int n = state.getSize();
        int[][] board = state.getBoard();
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

    private int getPositionalScore(HexGame state, int player) {
        int score = 0;
        int[][] board = state.getBoard();
        int n = state.getSize();
        int center = n / 2;

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] == player) {
                    int dist = Math.abs(r - center) + Math.abs(c - center);
                    score += (n - dist);
                }
            }
        }
        return score;
    }

    private List<int[]> getOrderedMoves(HexGame state) {
        List<int[]> moves = new ArrayList<>();
        int n = state.getSize();
        int center = n / 2;

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (state.isEmpty(r, c)) {
                    moves.add(new int[]{r, c});
                }
            }
        }

        Collections.sort(moves, (a, b) -> {
            int distA = Math.abs(a[0] - center) + Math.abs(a[1] - center);
            int distB = Math.abs(b[0] - center) + Math.abs(b[1] - center);
            return Integer.compare(distA, distB);
        });

        return moves;
    }

    private static class Node {
        int r, c, cost;
        public Node(int r, int c, int cst) { this.r=r; this.c=c; this.cost=cst; }
    }
}