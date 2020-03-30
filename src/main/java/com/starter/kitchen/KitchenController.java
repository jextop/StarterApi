package com.starter.kitchen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.util.CodeUtil;
import com.common.util.LogUtil;
import com.starter.annotation.AccessLimited;
import com.starter.kitchen.mock.driver.MockDriverSystem;
import com.starter.kitchen.mock.order.MockOrderSystem;
import com.starter.kitchen.service.ActiveMqService;
import com.starter.kitchen.util.RespUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
import java.util.Map;

/**
 * Kitchen controller for admin page. It allows to create test order manually or turn on automatic mode.
 *
 * @author ding
 */
@Api(tags = {"Central Kitchen Order Process"})
@RestController
@RequestMapping("/kitchen")
public class KitchenController {
    @Autowired
    ActiveMqService activeMqService;

    @Autowired
    Queue kitchenOrder;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    MockOrderSystem orderSystem;

    @Autowired
    MockDriverSystem driverSystem;

    @AccessLimited
    @ApiOperation("Switch auto mode: send orders and pickup")
    @PostMapping("/auto")
    public Map<String, Object> auto(@RequestBody String body) {
        // Turn on or off auto mode
        JSONObject params = JSON.parseObject(body);
        orderSystem.setAutoSendOrders(params.getIntValue("order") == 1);
        driverSystem.setAutoPickup(params.getIntValue("pickup") == 1);

        Map<String, Object> ret = RespUtil.ok();
        ret.put("autoOrder", orderSystem.isAutoSendOrders());
        ret.put("autoPickup", driverSystem.isAutoPickup());
        return ret;
    }

    @AccessLimited(count = 100)
    @ApiOperation("Send order")
    @PostMapping()
    public Map<String, Object> cook(@RequestBody String body) {
        LogUtil.info("/cook", body);

        // Send order to kitchen
        Order order = JSON.parseObject(body, Order.class);
        order.setId(CodeUtil.getCode());
        activeMqService.sendMessage(kitchenOrder, order);

        Map<String, Object> ret = RespUtil.ok();
        ret.put("id", order.getId());
        ret.put("name", order.getName());
        return ret;
    }

    @AccessLimited(count = 10)
    @ApiOperation("Shelf info")
    @GetMapping
    public Map<String, Object> info() {
        // Get shelf info from kitchen
        Map<String, Object> ret = RespUtil.ok();
        ret.put("items", kitchenService.getInfo());
        return ret;
    }
}
