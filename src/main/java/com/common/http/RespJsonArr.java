package com.common.http;

import com.alibaba.fastjson.JSONArray;
import com.common.util.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class RespJsonArr implements ResponseHandler<JSONArray> {
    @Override
    public JSONArray handleResponse(HttpResponse resp) throws ClientProtocolException, IOException {
        HttpEntity entity = resp.getEntity();
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }

        // read content
        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();
        String jsonStr = EntityUtils.toString(entity, charset);

        // parse JSON array
        return JsonUtil.parseArr(jsonStr);
    }
}
