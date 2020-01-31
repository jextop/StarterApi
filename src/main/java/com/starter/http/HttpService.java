package com.starter.http;

import com.common.http.RespStr;
import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HttpService {
    private static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private RequestConfig requestConfig;

    public <T> T sendRequest(HttpRequestBase httpRequest, ResponseHandler<T> handler) {
        httpRequest.setConfig(requestConfig);

        try {
            return httpClient.execute(httpRequest, handler);
        } catch (ClientProtocolException e) {
            LogUtil.error("Error when sendRequest", e.getMessage());
        } catch (IOException e) {
            LogUtil.error("Error when sendRequest", e.getMessage());
        }
        return null;
    }

    public <T> T sendHttpGet(String url, ResponseHandler<T> handler) {
        return sendRequest(new HttpGet(url), handler);
    }

    public String sendHttpGet(String url) {
        return sendHttpGet(url, new RespStr());
    }

    public <T> T sendHttpGet(String url, Map<String, String> headers, ResponseHandler<T> handler) {
        HttpGet httpGet = new HttpGet(url);
        fillHeaders(httpGet, headers);
        return sendRequest(httpGet, handler);
    }

    public String sendHttpGet(String url, Map<String, String> headers) {
        return sendHttpGet(url, headers, new RespStr());
    }

    public <T> T sendHttpPost(String httpUrl, Map<String, String> headers, Map<String, Object> params, ResponseHandler<T> handler) {
        HttpPost httpPost = new HttpPost(httpUrl);
        fillHeaders(httpPost, headers);

        if (!MapUtils.isEmpty(params)) {
            String jsonStr = JsonUtil.toStr(params);
            StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");
            stringEntity.setContentType(CONTENT_TYPE_JSON);
            httpPost.setEntity(stringEntity);
        }
        return sendRequest(httpPost, handler);
    }

    public String sendHttpPost(String httpUrl, Map<String, String> headers, Map<String, Object> params) {
        return sendHttpPost(httpUrl, headers, params, new RespStr());
    }

    public <T> T sendHttpForm(String httpUrl, Map<String, String> headers, Map<String, Object> params, ResponseHandler<T> handler) {
        HttpPost httpPost = new HttpPost(httpUrl);
        fillHeaders(httpPost, headers);

        if (!MapUtils.isEmpty(params)) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> param : params.entrySet()) {
                Object value = param.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(value)));
                }
            }

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
            } catch (UnsupportedEncodingException e) {
                LogUtil.error("Error when setEntity in sendHttpForm", e.getMessage());
            }
        }
        return sendRequest(httpPost, handler);
    }

    private static void fillHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (request == null || MapUtils.isEmpty(headers)) {
            return;
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
    }
}
