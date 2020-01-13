package com.common.http;

import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public static final String CHARSET_UTF_8 = "utf-8";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=utf-8";
    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

    private static PoolingHttpClientConnectionManager connectionPool;
    private static RequestConfig requestConfig;

    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();

            connectionPool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connectionPool.setMaxTotal(200);
            connectionPool.setDefaultMaxPerRoute(2);

            int socketTimeout = 10000;
            int connectTimeout = 10000;
            int connectionRequestTimeout = 10000;

            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        requestConfig = RequestConfig.custom()
                .setSocketTimeout(50000)
                .setConnectTimeout(50000)
                .setConnectionRequestTimeout(50000)
                .build();
    }

    private static CloseableHttpClient getHttpClient() {
        return HttpClients
                .custom()
                .setConnectionManager(connectionPool)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();
    }

    private static <T> T sendHttpRequest(HttpRequestBase httpRequest, ResponseHandler<T> handler) {
        if (httpRequest == null || handler == null) {
            return null;
        }

        try {
            return getHttpClient().execute(httpRequest, handler);
        } catch (IOException e) {
            LogUtil.error("Error when sendHttpRequest", e.getMessage());
        }
        return null;
    }

    private static String sendHttpPost(HttpPost httpPost) {
        return sendHttpRequest(httpPost, new RespStr());
    }

    private static String sendHttpGet(HttpGet httpGet) {
        return sendHttpRequest(httpGet, new RespStr());
    }

    public static String sendHttpGet(String httpUrl) {
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet);
    }

    public static String sendHttpPost(String httpUrl) {
        HttpPost httpPost = new HttpPost(httpUrl);
        return sendHttpPost(httpPost);
    }

    public static <T> T sendHttpGet(String httpUrl, Map<String, String> headers, ResponseHandler<T> handler) {
        HttpGet httpGet = new HttpGet(httpUrl);
        fillHeaders(httpGet, headers);
        return sendHttpRequest(httpGet, handler);
    }

    public static String sendHttpGet(String httpUrl, Map<String, String> headers) {
        HttpGet httpGet = new HttpGet(httpUrl);
        fillHeaders(httpGet, headers);
        return sendHttpGet(httpGet);
    }

    public static String sendHttpGet(String httpUrl, Map<String, String> headers, Map<String, Object> params) {
        return sendHttpGet(httpUrl, headers, params, new RespStr());
    }

    public static <T> T sendHttpGet(String httpUrl, Map<String, String> headers, Map<String, Object> params, ResponseHandler<T> handler) {
        if (!MapUtils.isEmpty(params)) {
            List<String> paramList = new ArrayList<String>();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                Object value = param.getValue();
                if (value != null) {
                    paramList.add(String.format("%s=%s", param.getKey(), UrlUtil.encode(String.valueOf(value))));
                }
            }

            String paramStr = StrUtil.join(paramList, "&");
            httpUrl = String.format("%s%s%s", httpUrl, httpUrl.indexOf("?") > 0 ? "&" : "?", paramStr);
        }
        return sendHttpGet(httpUrl, headers, handler);
    }

    public static <T> T sendHttpPost(String httpUrl, Map<String, String> headers, Map<String, Object> params, ResponseHandler<T> handler) {
        return sendHttpPost(httpUrl, headers, JsonUtil.toStr(params), handler);
    }

    public static String sendHttpPost(String httpUrl, Map<String, String> headers, Map<String, Object> params) {
        return sendHttpPost(httpUrl, headers, JsonUtil.toStr(params));
    }

    public static String sendHttpPost(String httpUrl, Map<String, String> headers, String jsonStr) {
        return sendHttpPost(httpUrl, headers, jsonStr, new RespStr());
    }

    public static <T> T sendHttpPost(String httpUrl, Map<String, String> headers, String jsonStr, ResponseHandler<T> handler) {
        HttpPost httpPost = new HttpPost(httpUrl);
        fillHeaders(httpPost, headers);

        if (!StrUtil.isEmpty(jsonStr)) {
            try {
                StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_JSON);
                httpPost.setEntity(stringEntity);
            } catch (Exception e) {
                LogUtil.error("Error when sendHttpPost", e.getMessage());
            }
        }
        return sendHttpRequest(httpPost, handler);
    }

    public static String sendHttpPost(String httpUrl, Map<String, String> headers, Map<String, Object> params, File fileObj) {
        return sendHttpPost(httpUrl, headers, params, new ArrayList<File>() {{
            if (fileObj != null) {
                add(fileObj);
            }
        }});
    }

    public static String sendHttpPost(String httpUrl, Map<String, String> headers, Map<String, Object> params, Collection<File> files) {
        return sendHttpPost(httpUrl, headers, params, files, new RespStr());
    }

    public static <T> T sendHttpPost(String httpUrl, Map<String, String> headers, Map<String, Object> params, Collection<File> files, ResponseHandler<T> handler) {
        HttpPost httpPost = new HttpPost(httpUrl);
        fillHeaders(httpPost, headers);

        MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
        if (params != null) {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value != null) {
                    meBuilder.addPart(key, new StringBody(String.valueOf(value), ContentType.TEXT_PLAIN));
                }
            }
        }

        if (files != null) {
            for (Object file : files) {
                if (file instanceof File) {
                    meBuilder.addBinaryBody("file", (File) file);
                } else {
                    LogUtil.error("un-supported file object", file.getClass().getSimpleName());
                }
            }
        }

        HttpEntity reqEntity = meBuilder.build();
        httpPost.setEntity(reqEntity);
        return sendHttpRequest(httpPost, handler);
    }

    public static String sendHttpForm(String httpUrl, Map<String, String> headers, Map<String, Object> params) {
        return sendHttpForm(httpUrl, headers, params, new RespStr());
    }

    public static <T> T sendHttpForm(String httpUrl, Map<String, String> headers, Map<String, Object> params, ResponseHandler<T> handler) {
        HttpPost httpPost = new HttpPost(httpUrl);
        fillHeaders(httpPost, headers);

        if (!MapUtils.isEmpty(params)) {
            try {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    Object value = param.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(value)));
                    }
                }
                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
            } catch (Exception e) {
                LogUtil.error(String.format("Error when sendHttpSubmit: %s", e.getMessage()));
            }
        }
        return sendHttpRequest(httpPost, handler);
    }

    private static void fillHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (request == null || MapUtils.isEmpty(headers)) {
            return;
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
    }

    public static <T> T sendHttpPut(String httpUrl, Map<String, String> headers, Map<String, Object> params, ResponseHandler<T> handler) {
        return sendHttpPut(httpUrl, headers, JsonUtil.toStr(params), handler);
    }

    public static <T> T sendHttpPut(String httpUrl, Map<String, String> headers, String jsonStr, ResponseHandler<T> handler) {
        HttpPut httpPost = new HttpPut(httpUrl);
        fillHeaders(httpPost, headers);

        if (!StrUtil.isEmpty(jsonStr)) {
            try {
                StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_JSON);
                httpPost.setEntity(stringEntity);
            } catch (Exception e) {
                LogUtil.error("Error when sendHttpPut: %s", e.getMessage());
            }
        }
        return sendHttpRequest(httpPost, handler);
    }

    public static <T> T sendHttpDelete(String httpUrl, Map<String, String> headers, ResponseHandler<T> handler) {
        HttpDelete httpDelete = new HttpDelete(httpUrl);
        fillHeaders(httpDelete, headers);
        return sendHttpRequest(httpDelete, handler);
    }
}
