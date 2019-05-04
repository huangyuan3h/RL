package com.ecg.rl.example;

import com.ecg.rl.util.HandGameUtil;
import com.ecg.rl.util.RequestClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestClientHelper {
    private static final String url = "http://localhost:8080/hand/choose/";
    private static Map<String, RequestClientUtil> requestClientUtilMap = null;

    public RequestClientHelper() throws IOException {
        if (requestClientUtilMap == null) {
            requestClientUtilMap = new HashMap<>();
            RequestClientUtil rockRequestClient = new RequestClientUtil(url + HandGameUtil.ROCK);
            requestClientUtilMap.put(HandGameUtil.ROCK, rockRequestClient);
            RequestClientUtil paperequestClient = new RequestClientUtil(url + HandGameUtil.PAPER);
            requestClientUtilMap.put(HandGameUtil.PAPER, paperequestClient);
            RequestClientUtil scissorsRequestClient = new RequestClientUtil(url + HandGameUtil.SCISSORS);
            requestClientUtilMap.put(HandGameUtil.SCISSORS, scissorsRequestClient);
        }
    }

    public RequestClientUtil getClinet(String choice) {
        return requestClientUtilMap.get(choice);
    }

}
