package com.ecg.rl.API.fund.controller;

import com.ecg.rl.API.fund.entity.Fund;
import com.ecg.rl.API.fund.entity.FundHistory;
import com.ecg.rl.API.fund.model.LearningOptions;
import com.ecg.rl.API.fund.service.FundProcess;
import com.ecg.rl.API.fund.service.IFundService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("fund")
public class FundController {

    @Autowired
    private IFundService fundService;
    @Autowired
    private FundProcess fundProcess;

    @GetMapping("pull")
    public ResponseEntity<List<Fund>> pull() throws IOException {
        List<Fund> funds = fundService.pullAll();
        return new ResponseEntity<List<Fund>>(funds, HttpStatus.OK);
    }

    @GetMapping("pullHistory/{id}")
    public ResponseEntity<List<FundHistory>> pullHistory(@PathVariable String id) throws IOException, ParseException {
        List<FundHistory> fundHistories = fundService.pullHistory(id);
        return new ResponseEntity<List<FundHistory>>(fundHistories, HttpStatus.OK);
    }

    @GetMapping("getAllFunds")
    public ResponseEntity<List<Fund>> getAllFunds() throws IOException {
        List<Fund> funds = fundService.pullAll();
        return new ResponseEntity<List<Fund>>(funds, HttpStatus.OK);
    }

    @PostMapping("processFundLearning")
    public ResponseEntity<Double> processFundLearning(@RequestBody LearningOptions learningOptions) throws IOException, ParseException {
        double result = fundProcess.process(learningOptions);
        return new ResponseEntity<Double>(result, HttpStatus.OK);
    }

    @GetMapping("test")
    public ResponseEntity<Double> test() throws IOException, ParseException {
        Double result = fundProcess.readHistory("110003");
        return new ResponseEntity<Double>(result, HttpStatus.OK);
    }
}
