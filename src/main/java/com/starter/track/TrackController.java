package com.starter.track;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.http.ParamUtil;
import com.common.http.RespUtil;
import com.common.util.LogUtil;
import com.starter.annotation.AccessLimited;
import com.starter.mq.MqService;
import com.starter.track.mock.MockTrackClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Topic;
import java.util.Map;

/**
 * @author ding
 */
@Api(tags = {"代驾定位系统"})
@RestController
@RequestMapping("/track")
public class TrackController {
    @Autowired
    MqService mqService;

    @Autowired
    Topic trackPosition;

    @Autowired
    MockTrackClient trackClient;

    @AccessLimited
    @ApiOperation("Switch auto mode")
    @PostMapping("/auto")
    public Map<String, Object> auto(@RequestBody String body) {
        // Turn on or off auto mode
        JSONObject params = JSON.parseObject(body);
        trackClient.setAutoSend(params.getIntValue("track") == 1);

        Map<String, Object> ret = RespUtil.ok();
        ret.put("autoSend", trackClient.isAutoSend());
        return ret;
    }

    @AccessLimited(count = 100)
    @ApiOperation("位置信息")
    @PostMapping("/{uid}")
    public Object track(@PathVariable String uid, @RequestBody String body) {
        LogUtil.info("/track", uid, body);

        ParamUtil paramUtil = new ParamUtil(body);
        Map<String, Object> paramMap = paramUtil.getMap();
        paramMap.put("uid", StringUtils.isEmpty(uid) ? "" : uid);
        paramMap.put("time", System.currentTimeMillis());
        mqService.sendMessage(trackPosition, paramMap);

        return RespUtil.ok(String.format("%s: %s",
                StringUtils.isEmpty(uid) ? "" : uid.substring(uid.length() - 2),
                paramUtil.getStr("addr"))
        );
    }

    @AccessLimited(count = 10)
    @ApiOperation("查询信息")
    @GetMapping
    public Object info() {
        LogUtil.info("/track/info");

        Map<String, Object> ret = RespUtil.ok();
        ret.put("items", TrackConsumer.CLIENT_MAP.values());
        return ret;
    }
}
