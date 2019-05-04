package com.ecg.rl.API.fund.service;

import com.ecg.rl.API.fund.dao.IFundDao;
import com.ecg.rl.API.fund.dao.IFundHistoryDao;
import com.ecg.rl.API.fund.entity.Fund;
import com.ecg.rl.API.fund.entity.FundHistory;
import com.ecg.rl.util.RequestClientUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FundService implements IFundService {

    @Autowired
    private IFundDao fundDao;

    @Autowired
    private IFundHistoryDao fundHistoryDao;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private String baseUrl = "http://fundgz.1234567.com.cn/js/%s.js";

    private String baseUrlAll = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=&gs=0&sc=dm&st=desc&sd=2016-12-31&ed=2017-12-31&qdii=&tabSubtype=,,,,,&pi=1&pn=%s&dx=1";

    private String historyUrl = "http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code={code}&page={pageNumber}&per={itemNumber}";

    private final int MAX_RECORD = 20;

    @Override
    public void pull() throws IOException {

        int startnumber = 1;
        int endnumber = 770001;
        String result = "";

        for (int i = startnumber; i < endnumber; i++) {
            boolean isExisted = true;
            String code = getCode(i);
            String codeUrl = String.format(baseUrl, code);
            RequestClientUtil requestClientUtil = new RequestClientUtil(codeUrl);

            try {
                result = requestClientUtil.call();
            } catch (Exception e) {
                System.out.println(code + " is not exisited!");
                isExisted = false;
            }
            //save it
            if (isExisted) {
                int starter = result.indexOf('{');
                int ender = result.indexOf('}');
                if (starter != -1 && ender != -1) {
                    String JsonString = result.substring(starter, ender + 1);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = mapper.readValue(JsonString, new TypeReference<Map<String, String>>() {
                    });
                    Fund fund = new Fund();
                    fund.setCode(map.get("fundcode").toString());
                    fund.setName(map.get("name").toString());
                    fundDao.saveOrUpdate(fund);
                    System.out.println(code + " saved!");
                }
            }
        }

    }

    @Override
    public List<Fund> pullAll() throws IOException {
        String length = "3671";
        String codeUrl = String.format(baseUrlAll, length);
        RequestClientUtil requestClientUtil = new RequestClientUtil(codeUrl);
        String result = "";
        List<Fund> funds = new ArrayList<>();
        try {
            result = requestClientUtil.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int starter = result.indexOf('[');
        int ender = result.indexOf(']');
        if (starter != -1 && ender != -1) {
            String JsonString = result.substring(starter, ender + 1);
            ObjectMapper mapper = new ObjectMapper();
            List<String> list = new ArrayList<String>();
            list = mapper.readValue(JsonString, new TypeReference<List<String>>() {
            });

            for (String item : list) {
                Fund fund = new Fund();
                String[] itemArray = item.split(",");
                fund.setCode(itemArray[0].toString());
                fund.setName(itemArray[1].toString());
                fund.setShortCode(itemArray[2].toString());
                fundDao.saveOrUpdate(fund);
                funds.add(fund);
            }
        }
        return funds;
    }

    @Override
    public List<FundHistory> pullHistory(String code) throws IOException, ParseException {
        //get closest data
        String url = historyUrl.replace("{code}", code)
                .replace("{pageNumber}", String.valueOf(1)).replace("{itemNumber}", String.valueOf(MAX_RECORD));
        saveHistoryToDB(code, url);
        return fundHistoryDao.getFundHistoryByCode(code);
    }

    @Override
    public List<Fund> getAllFunds() throws IOException {
        List<Fund> funds = fundDao.getAllFunds();
        return funds;
    }

    @Override
    public List<FundHistory> pullAllHistory(String code) throws IOException, ParseException {
        //get closest data
        int pages = getPages(code);
        for (int i = 1; i <= pages; i++) {
            String url = historyUrl.replace("{code}", code)
                    .replace("{pageNumber}", String.valueOf(i)).replace("{itemNumber}", String.valueOf(MAX_RECORD));
            saveHistoryToDB(code, url);
        }
        return fundHistoryDao.getFundHistoryByCode(code);
    }

    private void saveHistoryToDB(String code, String url) throws IOException, ParseException {
        if (getResultMap(url).isPresent()) {
            Map<String, Object> map = getResultMap(url).get();
            String content = map.get("content").toString();
            List<FundHistory> fundHistories = table2History(content, code);
            // save to db
            for (FundHistory history : fundHistories) {
                fundHistoryDao.saveOrUpdate(history);
            }
        }
    }

    private String getRecorderNumber(String code) throws IOException {
        String url = historyUrl.replace("{code}", code)
                .replace("{pageNumber}", "1").replace("{itemNumber}", "1");
        String records = "";
        if (getResultMap(url).isPresent()) {
            Map<String, Object> map = getResultMap(url).get();
            records = map.get("pages").toString();
        }
        return records;

    }

    private int getPages(String code) throws IOException {
        String url = historyUrl.replace("{code}", code)
                .replace("{pageNumber}", "1").replace("{itemNumber}", String.valueOf(MAX_RECORD));
        int pages = 0;
        if (getResultMap(url).isPresent()) {
            Map<String, Object> map = getResultMap(url).get();
            pages = Integer.parseInt(map.get("pages").toString());
        }
        return pages;
    }

    private Optional<Map<String, Object>> getResultMap(String url) throws IOException {
        RequestClientUtil requestClientUtil = new RequestClientUtil(url);
        String result = "";
        try {
            result = requestClientUtil.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int starter = result.indexOf('{');
        int ender = result.indexOf('}');
        if (starter != -1 && ender != -1) {
            String JsonString = result.substring(starter, ender + 1);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            Map<String, Object> map = new HashMap<String, Object>();
            map = mapper.readValue(JsonString, new TypeReference<Map<String, Object>>() {
            });
            return Optional.of(map);
        }
        return Optional.empty();
    }

    private String getCode(int codeNumber) {
        int fullLength = 6;
        String code = "" + codeNumber;
        int codeLength = code.length();
        int fixedZero = fullLength - codeLength;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fixedZero; i++) {
            sb.append(0);
        }
        sb.append(codeNumber);
        return sb.toString();
    }

    private List<FundHistory> table2History(String records, String code) throws ParseException {
        List<FundHistory> fundHistories = new ArrayList<>();
        int tbodyBegin = records.indexOf("<tbody>");
        int tbodyend = records.indexOf("</tbody>");
        if (tbodyBegin == -1 || tbodyend == -1) {
            return null;
        }
        String tbody = records.substring(tbodyBegin + 7, tbodyend);
        boolean hasNext = true;
        while (hasNext) {
            int trend = tbody.indexOf("</tr>");
            if (trend == -1) {
                break;
            }
            String content = tbody.substring(4, trend);
            tbody = tbody.substring(trend + 5);
            String[] contents = content.split("</td>");

            FundHistory fh = new FundHistory();
            //set date
            String date = contents[0].replace("<td>", "");
            Date d = formatter.parse(date);
            fh.setDate(d);
            //set id
            String id = date.replace("-", "") + "_" + code;
            fh.setId(id);
            //set code
            fh.setFundCode(code);
            //set net assert value
            int before = contents[1].indexOf(">");
            double netAssertValue = Double.parseDouble(contents[1].substring(before + 1));
            fh.setNetAssetValue(netAssertValue);
            //set net value
            int before1 = contents[2].indexOf(">");
            double netValue = Double.parseDouble(contents[2].substring(before1 + 1));
            fh.setNetValue(netValue);
            //set increate rate
            int beforeRate = contents[3].indexOf(">");
            int afterRate = contents[3].indexOf("%");
            if (beforeRate == -1 || afterRate == -1) {
                fh.setRate(0);
            } else {
                double rate = Double.parseDouble(contents[3].substring(beforeRate + 1, afterRate));
                fh.setRate(rate);
            }

            fundHistoryDao.saveOrUpdate(fh);
            fundHistories.add(fh);
        }

        return fundHistories;
    }
}
