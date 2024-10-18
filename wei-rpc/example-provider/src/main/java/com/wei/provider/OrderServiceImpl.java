package com.wei.provider;

import com.wei.common.service.OrderService;

public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrderMsg() {
        return "返回你，订单信息";
    }
}
