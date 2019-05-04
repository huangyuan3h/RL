package com.ecg.rl.example;


/*
all play 100 round what is the best way to
 */

import com.ecg.rl.util.HandGameUtil;
import com.ecg.rl.model.HandGameModel;

import java.io.IOException;
import java.util.Random;

/**
 * count top 100 return value and give percentage and playGames
 */
public class HandGameAllRandom {

    private static int RETRY_TIMES = 100;
    private static int GAME_NUM = 100;
    private static int counter = 0;
    private static int ALL_counter = 0;

    private final static double rockBreakPoint = 0.33333333333;
    private final static double paperBreakPoint = 0.66666666666;

    public static void main(String[] args) throws IOException {
        for (int j = 0; j < RETRY_TIMES; j++) {
            counter = 0;
            Random rand = new Random();
            HandGameModel model;
            for (int i = 0; i < GAME_NUM; i++) {
                double rdNumber = rand.nextDouble();
                if (rdNumber <= rockBreakPoint) {
                    model = HandGameUtil.getModelByAction(HandGameUtil.ROCK);
                } else if (rdNumber <= paperBreakPoint) {
                    model = HandGameUtil.getModelByAction(HandGameUtil.PAPER);
                } else {
                    model = HandGameUtil.getModelByAction(HandGameUtil.SCISSORS);
                }
                if (model.getResult() == 1) {
                    counter++;
                }
            }
            ALL_counter = ALL_counter + counter;
            System.out.println("No " + (j + 1) + ". take " + GAME_NUM + " games and win:" + counter);
        }
        System.out.println("All Random win percentage: " + (double) ALL_counter * 100 / (RETRY_TIMES * GAME_NUM) + "%");
    }
}
