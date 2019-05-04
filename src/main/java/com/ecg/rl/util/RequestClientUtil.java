package com.ecg.rl.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestClientUtil {
    public static String GET="GET";
    public static String POST="POST";
    public static String PUT="PUT";
    public static String DELETE="DELETE";

    private String accept = "application/json";
    private String method = GET;
    private String urlString;


    public RequestClientUtil(String urlString) throws IOException {
        this.urlString = urlString;
    }

    public RequestClientUtil(String urlString,String method) throws IOException {
        this(urlString);
        this.method= method;
    }

    private void beforeCall(HttpURLConnection conn) throws ProtocolException {
        conn.setRequestMethod(method);
        //conn.setRequestProperty("Accept", accept);
    }

    public String call() throws IOException {
        StringBuilder stringBuilder= new StringBuilder();
        URL url = new URL(urlString);
        String output;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            beforeCall(conn);
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                stringBuilder.append(output);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return stringBuilder.toString();
    }

    public <T extends Object> T callObj(Class<T> classType) throws IOException {
        String json = this.call();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<String, Object>();
        T t = mapper.readValue(json, classType);
        return t;
    }

    public Map callJson() throws IOException {
        String json = this.call();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<String, Object>();
        map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
        return map;
    }

    public String callString() throws IOException {
        String result = this.call();
        return result;
    }
}
