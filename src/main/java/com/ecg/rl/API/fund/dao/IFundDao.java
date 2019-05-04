package com.ecg.rl.API.fund.dao;



import com.ecg.rl.API.fund.entity.Fund;

import java.util.List;



public interface IFundDao {
    void saveOrUpdate(Fund fund);
    Fund getFundByCode(String code);
    List<Fund> getFundsByRange(int offset, int step);
    List<Fund> getAllFunds();
}
