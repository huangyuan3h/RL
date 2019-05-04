package com.ecg.rl.example.handGameRL;

import com.ecg.rl.util.HandGameUtil;


import java.util.HashMap;
import java.util.Map;

public class ActionManager {
    public static final double defaultInitialValue = 1;
    private Map<String, Double> actionMap = new HashMap<>();
    private String currentHighestAction;

    //step 1
    public void initial() {
        actionMap.put(HandGameUtil.ROCK, defaultInitialValue);
        actionMap.put(HandGameUtil.PAPER, defaultInitialValue);
        actionMap.put(HandGameUtil.SCISSORS, defaultInitialValue);
        currentHighestAction = HandGameUtil.ROCK;
    }

    public String getCurrentHighestAction() {
        return currentHighestAction;
    }

    public double getValueByAction(String action) {
        return actionMap.get(action);
    }

    public void updateValue(String action, Double newValue) {
        double oldValue = actionMap.get(action); // keep old value for comparison
        actionMap.put(action, newValue);

        if (newValue >= oldValue && currentHighestAction.equals(action)) { // means current action is still the highest
            return;
        }

        // update the highest action
        currentHighestAction = actionMap.entrySet().stream().max((a, b) -> a.getValue() > b.getValue() ? 1 : -1).get().getKey();

    }

}
