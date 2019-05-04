package com.ecg.rl.example;


import com.ecg.rl.util.HandGameUtil;
import com.ecg.rl.model.HandGameModel;

import java.io.IOException;
import java.util.Random;


public class HandGamePartialRandom {

    private static int RETRY_TIMES = 100;
    private static int GAME_NUM = 100;
    private static int counter = 0;
    private static int ALL_counter = 0;
    private static int testTime = 10;
    private static int rockWinTime = 0;
    private static int paperWinTime = 0;

    public static void main(String[] args) throws IOException {
        for (int j = 0; j < RETRY_TIMES; j++) {
            counter = 0;
            rockWinTime = 0;
            paperWinTime = 0;
            Random rand = new Random();
            //get test rate first
            for (int i = 0; i < testTime; i++) {
                HandGameModel model = HandGameUtil.getModelByAction(HandGameUtil.ROCK);
                if (model.getResult() == 1) {
                    rockWinTime ++;
                    counter++;
                }
            }

            for (int i = 0; i < testTime; i++) {
                HandGameModel model = HandGameUtil.getModelByAction(HandGameUtil.PAPER);
                if (model.getResult() == 1) {
                    paperWinTime ++;
                    counter++;
                }
            }

            double rockRate = (double)rockWinTime/testTime;
            double paperRate = (double)paperWinTime/testTime;
            //System.out.println("rock rate is:"+rockRate);
            //System.out.println("paper rate is:"+paperRate);

            for (int i = 0; i < (GAME_NUM - testTime*2); i++) {
                double rdNumber = rand.nextDouble();
                HandGameModel model ;
                if (rdNumber <= rockRate) {
                    model = HandGameUtil.getModelByAction(HandGameUtil.ROCK);
                } else if (rdNumber <= (paperRate+rockRate)) {
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
        System.out.println("Partial Random win percentage: " + (double) ALL_counter * 100 / (RETRY_TIMES * GAME_NUM) + "%");
    }

}
