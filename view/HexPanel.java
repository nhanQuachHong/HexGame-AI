package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.*;

public class HexPanel extends JPanel {
    private HexGame game;
    private HexAI ai;
    private Polygon[][] hexes;
    private int cellSize = 60;

    public HexPanel(HexGame game, HexAI ai) {
        this.game = game;
        this.ai = ai;
        int n = game.getSize();
        hexes = new Polygon[n][n];

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] pos = findCell(e.getX(), e.getY());
                if (pos != null) {
                    int r = pos[0], c = pos[1];
                    if (game.isEmpty(r, c)) {
                        game.place(r, c, game.getCurrent());
                        repaint();
                        if (game.getCurrent() == HexGame.BLUE) {
                            int[] mv = ai.bestMove(3);
                            game.place(mv[0], mv[1], HexGame.BLUE);
                            repaint();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = cellSize, dy = (int)(cellSize * Math.sqrt(3)/2);
        int n = game.getSize();
        int[][] board = game.getBoard();

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int x = c * dx + r * dx/2 + 50;
                int y = r * dy + 50;
                Polygon hex = createHex(x, y, cellSize/2);
                hexes[r][c] = hex;
                if (board[r][c] == HexGame.RED) g2.setColor(Color.RED);
                else if (board[r][c] == HexGame.BLUE) g2.setColor(Color.BLUE);
                else g2.setColor(Color.LIGHT_GRAY);
                g2.fillPolygon(hex);
                g2.setColor(Color.BLACK);
                g2.drawPolygon(hex);
            }
        }
    }

    private Polygon createHex(int x, int y, int r) {
        Polygon p = new Polygon();
        for (int i = 0; i < 6; i++) {
            double ang = Math.PI/3 * i + Math.PI/6;
            int px = (int)(x + r * Math.cos(ang));
            int py = (int)(y + r * Math.sin(ang));
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
}
