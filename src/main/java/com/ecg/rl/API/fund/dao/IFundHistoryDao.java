package com.ecg.rl.API.fund.dao;



import com.ecg.rl.API.fund.entity.FundHistory;

import java.util.List;


public interface IFundHistoryDao {
    void saveOrUpdate(FundHistory fundHistory);
    List<FundHistory> getFundHistoryByCode(String code);
    FundHistory getFundHistoryById(String id);
}
