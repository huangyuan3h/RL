package com.ecg.rl.API.fund.service;

import com.ecg.rl.API.fund.entity.Fund;
import com.ecg.rl.API.fund.entity.FundHistory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


public interface IFundService {
    void pull() throws IOException;
    List<Fund> pullAll() throws IOException;
    List<FundHistory> pullHistory(String code) throws IOException, ParseException;
    List<Fund> getAllFunds() throws IOException;
    List<FundHistory> pullAllHistory(String code) throws IOException, ParseException;
}
