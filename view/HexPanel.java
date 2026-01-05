package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.*;

public class HexPanel extends JPanel {
    private HexGame game;
    private HexAI ai;
    private Polygon[][] hexes;
    private int cellSize = 60; // Kích thước như file gốc

    private final int OFFSET_X = 80; // Vị trí như file gốc
    private final int OFFSET_Y = 80;

    private boolean isThinking = false; // Biến khóa chuột khi máy đang tính

    public HexPanel(HexGame game, HexAI ai) {
        this.game = game;
        this.ai = ai;
        int n = game.getSize();
        hexes = new Polygon[n][n];

        setBackground(new Color(245, 245, 245));

        // --- GIỮ LẠI LOGIC XỬ LÝ CLICK ĐỂ KHÔNG BỊ ĐƠ ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (game.checkWinner() != HexGame.EMPTY || isThinking || game.getCurrent() == HexGame.BLUE) return;

                int[] pos = findCell(e.getX(), e.getY());
                if (pos != null) {
                    int r = pos[0], c = pos[1];
                    
                    if (game.place(r, c, HexGame.RED)) {
                        repaint(); 

                        if (checkGameOver()) return;
                        performAiMove();
                    }
                }
            }
        });
    }

    private void performAiMove() {
        isThinking = true;
        new Thread(() -> {
            try {
                Thread.sleep(500); 
                int[] mv = ai.bestMove(3); 
                
                SwingUtilities.invokeLater(() -> {
                    if (mv != null) {
                        game.place(mv[0], mv[1], HexGame.BLUE);
                        repaint();
                        checkGameOver();
                    }
                    isThinking = false;
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                isThinking = false;
            }
        }).start();
    }

    private boolean checkGameOver() {
        int winner = game.checkWinner();
        if (winner != HexGame.EMPTY) {
            String msg = (winner == HexGame.RED) ? "RED WINS!" : "BLUE WINS!";
            JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // --- KHÔI PHỤC CÔNG THỨC VẼ GỐC CỦA BẠN ---
        int dx = cellSize;
        int dy = (int)(cellSize * Math.sqrt(3)/2);
        int n = game.getSize();
        int[][] board = game.getBoard();

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                // Công thức xếp nghiêng gốc
                int x = c * dx + r * dx/2 + OFFSET_X;
                int y = r * dy + OFFSET_Y;
                
                Polygon hex = createHex(x, y, cellSize/2);
                hexes[r][c] = hex;

                if (board[r][c] == HexGame.RED) {
                    g2.setColor(Color.RED);
                    g2.fillPolygon(hex);
                } else if (board[r][c] == HexGame.BLUE) {
                    g2.setColor(Color.BLUE);
                    g2.fillPolygon(hex);
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fillPolygon(hex);
                }

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawPolygon(hex);
            }
        }

        drawBorders(g2);
    }

    // --- KHÔI PHỤC HÌNH LỤC GIÁC NHỌN ĐẦU GỐC ---
    private Polygon createHex(int x, int y, int r) {
        Polygon p = new Polygon();
        for (int i = 0; i < 6; i++) {
            // Góc lệch PI/6 để tạo đỉnh nhọn
            double ang = Math.PI/3 * i + Math.PI/6; 
            int px = (int)Math.round(x + r * Math.cos(ang));
            int py = (int)Math.round(y + r * Math.sin(ang));
            p.addPoint(px, py);
        }
        return p;
    }

    private int[] findCell(int mx, int my) {
        int n = game.getSize();
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                if (hexes[r][c] != null && hexes[r][c].contains(mx, my))
                    return new int[]{r, c};
        return null;
    }

    // --- KHÔI PHỤC LOGIC VẼ VIỀN GỐC ---
    private void drawBorders(Graphics2D g2) {
        int n = game.getSize();
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Viền ĐỎ (Trên - Dưới)
        g2.setColor(new Color(220, 40, 40)); 
        for (int c = 0; c < n; c++) {
            Polygon topHex = hexes[0][c];
            if (topHex != null) {
                // Các chỉ số đỉnh này khớp với lục giác nhọn đầu
                g2.drawLine(topHex.xpoints[3], topHex.ypoints[3], 
                           topHex.xpoints[4], topHex.ypoints[4]);
                g2.drawLine(topHex.xpoints[4], topHex.ypoints[4], 
                           topHex.xpoints[5], topHex.ypoints[5]);
            }

            Polygon botHex = hexes[n - 1][c];
            if (botHex != null) {
                g2.drawLine(botHex.xpoints[0], botHex.ypoints[0], 
                           botHex.xpoints[1], botHex.ypoints[1]);
                g2.drawLine(botHex.xpoints[1], botHex.ypoints[1], 
                           botHex.xpoints[2], botHex.ypoints[2]);
            }
        }

        // Viền XANH (Trái - Phải)
        g2.setColor(new Color(50, 50, 220)); 
        for (int r = 0; r < n; r++) {
            Polygon leftHex = hexes[r][0];
            if (leftHex != null) {
                g2.drawLine(leftHex.xpoints[2], leftHex.ypoints[2], 
                           leftHex.xpoints[3], leftHex.ypoints[3]);
                g2.drawLine(leftHex.xpoints[1], leftHex.ypoints[1], 
                           leftHex.xpoints[2], leftHex.ypoints[2]);
            }

            Polygon rightHex = hexes[r][n - 1];
            if (rightHex != null) {
                g2.drawLine(rightHex.xpoints[5], rightHex.ypoints[5], 
                           rightHex.xpoints[0], rightHex.ypoints[0]);
                g2.drawLine(rightHex.xpoints[4], rightHex.ypoints[4], 
                           rightHex.xpoints[5], rightHex.ypoints[5]);
            }
        }
        g2.setStroke(new BasicStroke(1.0f));
    }
}