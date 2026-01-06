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
        for (int[] row : dist) Arrays.fill(row, INF);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        for (int i = 0; i < n; i++) {
            int r = (player == HexGame.RED) ? 0 : i;
            int c = (player == HexGame.RED) ? i : 0;
            
            int cost = (board[r][c] == player) ? 0 : (board[r][c] == HexGame.EMPTY ? 1 : INF);
            
            if (cost != INF) {
                dist[r][c] = cost;
                pq.add(new int[]{r, c, cost});
            }
        }

        int[][] dirs = {{-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}};
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int r = cur[0], c = cur[1], cost = cur[2];

            if (cost > dist[r][c]) continue;
            
            if ((player == HexGame.RED && r == n - 1) || (player == HexGame.BLUE && c == n - 1)) {
                return cost;
            }

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                    int cell = board[nr][nc];
                    int weight = (cell == player) ? 0 : (cell == HexGame.EMPTY ? 1 : INF);
                    
                    if (weight != INF && cost + weight < dist[nr][nc]) {
                        dist[nr][nc] = cost + weight;
                        pq.add(new int[]{nr, nc, dist[nr][nc]});
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

   
}