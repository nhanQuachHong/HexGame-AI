package main;

import model.HexAI;
import model.HexGame;

public class TestDepth {
    public static void main(String[] args) {
    HexGame game = new HexGame(11); 
    HexAI ai = new HexAI(game); 

    System.out.println("=== BẢNG SỐ LIỆU ĐÃ FIX ===");
    System.out.println(String.format("%-10s | %-15s | %-15s", "Depth", "Time (ms)", "Memory (KB)"));
    System.out.println("----------------------------------------------");

    for (int depth = 1; depth <= 7; depth++) {
        
     
        for (int i = 0; i < 6; i++) {
            System.gc();
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        }
        // --------------------------------------------

        long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long startTime = System.currentTimeMillis();

        ai.bestMove(depth); 

        long endTime = System.currentTimeMillis();
        long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long time = endTime - startTime;
        long memUsageBytes = endMem - startMem;
        
        if (memUsageBytes < 0) memUsageBytes = 0; 

        long memUsageKB = memUsageBytes / 1024; 

        System.out.println(String.format("%-10d | %-15d | %-15d", depth, time, memUsageKB));
    }
}
}