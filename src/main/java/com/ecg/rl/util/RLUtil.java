package com.ecg.rl.util;

public class RLUtil {

    /***
     * Q - learning full value counter
     * @param currentValue
     * @param reward
     * @param rate
     * @param discountRate
     * @param estimateFutureOptimal
     * @return
     */
    public static double getNewValue(double currentValue, double reward, double rate, double discountRate, double estimateFutureOptimal) {
        double discountFactValue = 0;
        if (discountRate != 0 && estimateFutureOptimal != 0) {
            discountFactValue = discountRate * estimateFutureOptimal;
        }
        double newValue = (1 - rate) * currentValue + rate * (reward + discountFactValue);
        return newValue;
    }

    /***
     *  easy counter for q - learning
     * @param currentValue
     * @param reward
     * @param rate
     * @return
     */
    public static double getNewValue(double currentValue, double reward, double rate) {
        return getNewValue(currentValue, reward, rate, 0, 0);
    }

}
