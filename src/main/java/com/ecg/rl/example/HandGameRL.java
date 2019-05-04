package com.ecg.rl.example;


import com.ecg.rl.example.handGameRL.ActionManager;
import com.ecg.rl.model.HandGameModel;
import com.ecg.rl.util.HandGameUtil;
import com.ecg.rl.util.RLUtil;

import java.io.IOException;
import java.text.DecimalFormat;


public class HandGameRL {

    private static int RETRY_TIMES = 10;
    private static int GAME_NUM = 1000;
    private static int counter = 0;
    private static int ALL_counter = 0;
    private static final double rate = 0.1; // learning rate
    private static final double factorRate = 0.1; // factor
    private static double highestQValue = 0; // for gradient model
    private static boolean isGradientEnable = false; //

    public static void main(String[] args) throws IOException {

        for (int j = 0; j < RETRY_TIMES; j++) {
            counter = 0;
            // step 1 At very beginning all the action is the same value. for example 100
            ActionManager actionManager = new ActionManager();
            actionManager.initial();

            for (int i = 0; i < GAME_NUM; i++) {
                // step 2 pick one action get the result (rock for example)
                HandGameModel model = pickHeighestValueAction(actionManager);
                // counter number
                if (model.getResult() == 1) {
                    counter++;
                }
                // step 3 update new value
                updateNewValueByReward(actionManager, model);
            }
            System.out.println("No " + (j + 1) + ". take " + GAME_NUM + " games and win:" + counter);
            //printValue(actionManager);
            ALL_counter = ALL_counter + counter;
        }
        System.out.println("RL win percentage: " + (double) ALL_counter * 100 / (RETRY_TIMES * GAME_NUM) + "%");

    }

    /**
     * @param actionManager
     * @return
     * @throws IOException
     */
    public static HandGameModel pickHeighestValueAction(ActionManager actionManager) throws IOException {
        String currentAction = actionManager.getCurrentHighestAction();
        HandGameModel model = HandGameUtil.getModelByAction(currentAction);
        return model;
    }

    public static void updateNewValueByReward(ActionManager actionManager, HandGameModel model) {
        String currentAction = actionManager.getCurrentHighestAction();
        double currentValue = actionManager.getValueByAction(currentAction);
        double newValue = 0;
        if (isGradientEnable) {
            newValue = RLUtil.getNewValue(currentValue, (double) model.getResult(), rate, factorRate, highestQValue);
            if (newValue > highestQValue) {
                highestQValue = newValue;
            }
        } else {
            newValue = RLUtil.getNewValue(currentValue, (double) model.getResult(), rate);
        }
        //System.out.println("Q value is:" + newValue);
        actionManager.updateValue(currentAction, newValue);
    }

    public static void printValue(ActionManager actionManager) {
        DecimalFormat formater = new DecimalFormat("#0.00");
        System.out.println("rock value:" + formater.format(actionManager.getValueByAction(HandGameUtil.ROCK)) + "; scissors value:"
                + formater.format(actionManager.getValueByAction(HandGameUtil.SCISSORS)) + "; paper value:" + formater.format(actionManager.getValueByAction(HandGameUtil.PAPER)));
    }
}
