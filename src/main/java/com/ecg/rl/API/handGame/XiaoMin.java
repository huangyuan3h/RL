package com.ecg.rl.API.handGame;

import com.ecg.rl.util.HandGameUtil;

import java.util.Random;


/***
 * xiao min will always give 50% for rock, 20% for paper, 30% for scissors
 */
public class XiaoMin {

    //define the percentage of each choice
    private final static double rockBreakPoint = 0.5;
    private final static double paperBreakPoint = 0.3;
    //

    public static String ChooseAction() {
        Random rand = new Random();
        double rdNumber = rand.nextDouble();

        if (rdNumber >= rockBreakPoint) {
            return HandGameUtil.ROCK;
        }

        if(rdNumber >= paperBreakPoint) {
            return HandGameUtil.PAPER;
        }

        return HandGameUtil.SCISSORS;

    }
}
