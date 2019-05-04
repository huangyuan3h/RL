package com.ecg.rl.API.fund.entity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table
public class FundHistory {

    /***
     * eg: 20180101_000001
     */
    @Id
    private String id;

    private String fundCode;

    private double netAssetValue; //单位净值

    private double netValue;//累计净值

    private double rate;// 日增长率

    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public double getNetAssetValue() {
        return netAssetValue;
    }

    public void setNetAssetValue(double netAssetValue) {
        this.netAssetValue = netAssetValue;
    }

    public double getNetValue() {
        return netValue;
    }

    public void setNetValue(double netValue) {
        this.netValue = netValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
