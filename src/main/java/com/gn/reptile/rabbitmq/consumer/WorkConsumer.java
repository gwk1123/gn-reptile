package com.gn.reptile.rabbitmq.consumer;

import com.gn.reptile.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class WorkConsumer {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedMessage = new String(delivery.getBody());
            System.out.println("C1接收到消息:" + receivedMessage);
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "C1消费者取消消费接口回调逻辑");
        };
        System.out.println("C1 消费者启动等待消费......");

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
