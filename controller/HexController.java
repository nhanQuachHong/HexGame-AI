package controller;

import model.*;
import view.*;

public class HexController {
    private HexGame game;
    private HexAI ai;
    private HexFrame frame;

    public HexController(int n) {
        game = new HexGame(n);
        ai = new HexAI(game);
        frame = new HexFrame(n);
    }

    public void start() {
        // game loop
        // handle user input
        // update view
}
}
