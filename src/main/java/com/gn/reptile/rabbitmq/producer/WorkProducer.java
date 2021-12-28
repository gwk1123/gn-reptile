package com.gn.reptile.rabbitmq.producer;

import com.gn.reptile.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

/**
 * https://www.cnblogs.com/linhp/p/15219445.html
 */
public class WorkProducer {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel();) {
            // 让消息持久化
            boolean durable = false;
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            //从控制台当中接受信息
            for (int i = 0; i < 10; i++) {
                String message = "生产者发送消息" + i;
                System.out.println(message);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            }
        }
    }


}
