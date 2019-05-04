package com.ecg.rl.util;

import com.ecg.rl.example.RequestClientHelper;
import com.ecg.rl.model.HandGameModel;

import java.io.IOException;

public class HandGameUtil {

    public final static String ROCK = "rock";
    public final static String PAPER = "paper";
    public final static String SCISSORS = "scissors";

    public static void compare(HandGameModel model) {
        if (model.getYourChoice().equalsIgnoreCase(model.getComputerChoice())) {
            model.setResult(0);
            return;
        }

        if (ROCK.equals(model.getYourChoice())) {
            model.setResult(PAPER.equals(model.getComputerChoice()) ? -1 : 1);
            return;
        }

        if (PAPER.equals(model.getYourChoice())) {
            model.setResult(SCISSORS.equals(model.getComputerChoice()) ? -1 : 1);
            return;
        }

        if (SCISSORS.equals(model.getYourChoice())) {
            model.setResult(ROCK.equals(model.getComputerChoice()) ? -1 : 1);
            return;
        }
    }

    public static HandGameModel getModelByAction (String action) throws IOException {
        RequestClientHelper requestClientHelper = new RequestClientHelper();
        RequestClientUtil clientUtil = requestClientHelper.getClinet(action);
        HandGameModel model = clientUtil.callObj(HandGameModel.class);
        return model;
    }


}
