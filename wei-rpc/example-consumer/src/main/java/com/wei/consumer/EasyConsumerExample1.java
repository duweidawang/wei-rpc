package com.wei.consumer;
import com.wei.common.model.User;
import com.wei.common.service.OrderService;
import com.wei.rpc.bootstrap.ConsumerBootstrap;
import com.wei.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample1 {
    public static void main(String[] args) {
        ConsumerBootstrap.init();
        OrderService orderService = ServiceProxyFactory.getProxy(OrderService.class);
        User user = new User();
        user.setName("wei");
        String orderMsg = orderService.getOrderMsg();
        System.out.println(orderMsg);
    }
}
