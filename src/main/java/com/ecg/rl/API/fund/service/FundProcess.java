package com.ecg.rl.API.fund.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecg.rl.API.fund.dao.IFundHistoryDao;
import com.ecg.rl.API.fund.entity.FundHistory;
import com.ecg.rl.API.fund.model.FundAction;
import com.ecg.rl.API.fund.model.LearningOptions;
import com.ecg.rl.util.RLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FundProcess {

    @Autowired
    private IFundService fundService;

    @Autowired
    private IFundHistoryDao fundHistoryDao;

    public static final double defaultInitialValue = 10000;

    private static Map<String, Double> actionMap = new HashMap<>();

    private static final int TRAIN_PERIOD = 365; // one year

    private static final int MAX_KEEP_date = 100; //

    private static final double TRAIN_EXAMPLE_QUANTITY = 10000;

    private static final int trade_day_per_year = 250;

    private static final int variation_rate = 5;// 5%

    private static final int ONE_HUNDRED = 100;

    private static final double optimize_rate = 0.95; //
    /**
     * String currentFundCode;
     *
     * double money;
     *
     * double fee;
     *
     * int trainTimes;
     *
     * double learningRate;
     */
    public double process(LearningOptions options) throws IOException, ParseException {
        fundService.pullAllHistory(options.getCurrentFundCode());
        List<FundHistory> orderedHistory = fundHistoryDao.getFundHistoryByCode(options.getCurrentFundCode());

        // training model
        for (int i = 0; i < options.getTrainTimes(); i++) {
            singleTrain(orderedHistory, options);
        }

        // do real process
        int start_index = orderedHistory.size() - trade_day_per_year;
        boolean hasBuyFund = false;
        int buyIndex = 0;
        double money = options.getMoney();
        for (int i = start_index; i < orderedHistory.size(); i++) {
            String action = chooseAction(orderedHistory, i, hasBuyFund);
            if (action == FundAction.BUY) {
                buyIndex = i;
                hasBuyFund = true;
            }

            if (action == FundAction.SELL || i + 1 == orderedHistory.size()) {
                money = money *(1-options.getFee()*0.01);
                money = money/orderedHistory.get(i).getNetValue() * orderedHistory.get(buyIndex).getNetValue();
            }
        }
        return money;
    }

    private static void singleTrain(List<FundHistory> fundHistories, LearningOptions ops) {
        int startDate = getStartDateIndex(fundHistories);

        boolean hasBuyFund = false;

        int buyIndex = 0;

        for (int i = startDate; i < TRAIN_PERIOD + startDate; i++) {

            String action = chooseAction(fundHistories, i, hasBuyFund);

            //if buy record
            if (action == FundAction.BUY) {
                buyIndex = i;
                hasBuyFund = true;
            }

            //if sell update q value
            //if the end of the period choose sell
            if (action == FundAction.SELL || i + 1 == TRAIN_PERIOD + startDate || i - buyIndex == MAX_KEEP_date) {
                //do update
                if(buyIndex != 0){
                    updateValve(fundHistories, buyIndex, i, ops.getFee(), ops.getLearningRate());
                }
                hasBuyFund = false;
            }
        }

    }

    public List<FundHistory> getAllHistory(String code) throws IOException, ParseException {
        List<FundHistory> fundHistory = fundService.pullAllHistory(code);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(code + ".json"), fundHistory);
        return fundHistory;
    }

    /**
     * String currentFundCode 110003
     *
     * double money 10000
     *
     * double fee 0.1
     *
     * int trainTimes 100
     *
     * double learningRate 0.1
     */
    public double readHistory(String code) throws IOException, ParseException {
        List<FundHistory> orderedHistory = fundHistoryDao.getFundHistoryByCode(code);
        if(orderedHistory.size() == 0) {
            ObjectMapper mapper = new ObjectMapper();
            List<FundHistory> fundHistory = mapper
                    .readValue(new File(code + ".json"), new TypeReference<List<FundHistory>>() {
                    });
            // import to db
            for (FundHistory history : fundHistory) {
                fundHistoryDao.saveOrUpdate(history);
            }

            orderedHistory = fundHistoryDao.getFundHistoryByCode(code);
        }
        // actionMap = new HashMap<>();
        LearningOptions ops = getTestLearningOptions();
        // training model
        for (int i = 0; i < ops.getTrainTimes(); i++) {
            singleTrain(orderedHistory, ops);
        }

        int start_index = orderedHistory.size() - trade_day_per_year;
        boolean hasBuyFund = false;
        int buyIndex = 0;
        double money = ops.getMoney();
        for (int i = start_index; i < orderedHistory.size(); i++) {
            String action = chooseAction(orderedHistory, i, hasBuyFund);
            if (action == FundAction.BUY) {
                buyIndex = i;
                hasBuyFund = true;
            }

            if (action == FundAction.SELL || i + 1 == orderedHistory.size()) {
                money = money *(1-ops.getFee()*0.01);
                money = money/orderedHistory.get(i).getNetValue() * orderedHistory.get(buyIndex).getNetValue();
            }
        }
        System.out.println("the result money is:" + money);
        return money;
    }

    /***
     * when doing sell action, update the value from sell to buy
     *
     */
    private static void updateValve(List<FundHistory> allHistory, int buyIndex, int currentIndex, double fee,
            double learningRate) {
        FundHistory buyHistory = allHistory.get(buyIndex);
        FundHistory sellHistory = allHistory.get(currentIndex);
        double reward = reward(buyHistory, sellHistory, currentIndex - buyIndex, TRAIN_EXAMPLE_QUANTITY,
                fee);
        int day_gap = currentIndex - buyIndex;
        if(buyIndex == 0) {
            System.out.println("buyIndex is 0");
        } else {
            // update buy vale
            updateActionValueByIndex(allHistory, buyIndex, FundAction.BUY, reward * Math.pow(optimize_rate, day_gap) , learningRate);
        }
        // update keep value
        for (int i = buyIndex + 1; i < currentIndex; i++) {
            updateActionValueByIndex(allHistory, i, FundAction.KEEP, reward * Math.pow(optimize_rate, day_gap - (i-buyIndex)), learningRate);
        }

        // update sell value
        updateActionValueByIndex(allHistory, currentIndex, FundAction.SELL, reward * optimize_rate, learningRate);
    }

    /***
     * reward function
     * [netValue(sell)*money*(1-0.001) - netValue (buy)* money]/day_gap
     * @return
     */
    private static double reward(FundHistory buyHistory, FundHistory sellHistory, int gap_gap, double QUANTITY,
            double fee) {
        return (buyHistory.getNetValue() - sellHistory.getNetValue()) * QUANTITY * (1-fee*0.01) / gap_gap;
    }

    private static String chooseAction(List<FundHistory> allHistory, int index, boolean hasBuyFund) {
        if(index == 0){
            System.out.println("index is 0 in choose action");
            return FundAction.KEEP;
        }
        if (hasBuyFund) {
            double sellValue = getActionValueByIndex(allHistory, index, FundAction.SELL);
            double keepValue = getActionValueByIndex(allHistory, index, FundAction.KEEP);
            if(new Random().nextInt(ONE_HUNDRED) > variation_rate){
                return sellValue >= keepValue ? FundAction.SELL : FundAction.KEEP;
            }
            return sellValue >= keepValue ? FundAction.KEEP : FundAction.SELL;
        }

        double buyValue = getActionValueByIndex(allHistory, index, FundAction.BUY);
        double keepValue = getActionValueByIndex(allHistory, index, FundAction.KEEP);
        if(new Random().nextInt(ONE_HUNDRED) > variation_rate) {
            return buyValue >= keepValue ? FundAction.BUY : FundAction.KEEP;
        }
        return buyValue >= keepValue ? FundAction.KEEP : FundAction.BUY;
    }

    private static double getActionValueByKey(String key) {
        double value = actionMap.get(key) == null ? 0 : actionMap.get(key);
        if (value == 0) {
            actionMap.put(key, defaultInitialValue);
            return defaultInitialValue;
        }
        return value;
    }

    private static double getActionValueByIndex(List<FundHistory> allHistory, int index, String action) {
        String key = null;
        try{
            FundHistory today = allHistory.get(index);// get the info of this date
            FundHistory yesterday = allHistory.get(index - 1);
            key = buildActionKey(today.getRate(), yesterday.getRate(), action);
        }catch(Exception e){
            System.out.println("error get action value:" + index);
        }

        return getActionValueByKey(key);
    }

    private static double updateActionValueByIndex(List<FundHistory> allHistory, int index, String action,
            double reward, double learningRate) {
        double currentValue = getActionValueByIndex(allHistory, index, action);
        double newValue = RLUtil.getNewValue(currentValue, reward, learningRate);
        saveToActionMap(allHistory, index, action, newValue);
        return newValue;
    }

    private static void saveToActionMap(List<FundHistory> allHistory, int index, String action, double newValue) {
        FundHistory today = allHistory.get(index);// get the info of this date
        if(index == 0) {
            return;
        }
        FundHistory yesterday = allHistory.get(index - 1);
        String key = buildActionKey(today.getRate(), yesterday.getRate(), action);
        actionMap.put(key, newValue);
    }

    /***
     * build action  rate_yesterday|rate_today|action(buy sell keep)   ---initial value
     */
    private static String buildActionKey(double todayRate, double yesterdayRate, String action) {
        int transferedTodayRate = (int) Math.round(todayRate * 10);
        int transferedYesterdayRate = (int) Math.round(yesterdayRate * 10);
        StringBuilder sb = new StringBuilder();
        return sb.append(transferedTodayRate).append('|').append(transferedYesterdayRate).append('|').append(action)
                .toString();
    }

    private static int getStartDateIndex(List<FundHistory> fundHistories) {
        int max_index = fundHistories.size() - TRAIN_PERIOD - trade_day_per_year; // remove last trade year
        if (max_index <= 0) {
            return 0;
        }
        Random rand = new Random();
        return rand.nextInt(max_index) + 1;
    }

    private static LearningOptions getTestLearningOptions() {
        LearningOptions learningOptions = new LearningOptions();
        learningOptions.setCurrentFundCode("110003");
        learningOptions.setMoney(10000);
        learningOptions.setFee(0.1);
        learningOptions.setTrainTimes(10000);
        learningOptions.setLearningRate(0.1);
        return learningOptions;
    }

}
