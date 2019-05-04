package com.ecg.rl.API.fund.model;

public class LearningOptions {

    private String currentFundCode;

    private double money;

    private double fee;

    private int trainTimes;

    private double learningRate;

    public String getCurrentFundCode() {
        return currentFundCode;
    }

    public void setCurrentFundCode(String currentFundCode) {
        this.currentFundCode = currentFundCode;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getTrainTimes() {
        return trainTimes;
    }

    public void setTrainTimes(int trainTimes) {
        this.trainTimes = trainTimes;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
}
