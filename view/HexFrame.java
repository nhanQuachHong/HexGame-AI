package view;

import javax.swing.JFrame;
import model.HexAI;
import model.HexGame;

public class HexFrame extends JFrame{
      private HexGame game;
    private HexPanel panel;
    private HexAI ai;

    public HexFrame(int n) {
        setTitle("Hex Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 1200);
        setLocationRelativeTo(null);

        game = new HexGame(n);
        ai = new HexAI(game);
       panel = new HexPanel(game, ai);

       add(panel);
        setVisible(true);
    }
}
    

