package com.gn.reptile.rabbitmq.transactional.consumer;

import com.gn.reptile.utils.RabbitMqUtils;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TransactionalConsumer {

    private static Logger logger = LoggerFactory.getLogger(TransactionalConsumer.class);

    public static void main(String[] args) {
        transactionalTake();
    }

    /**
     * 手动确认模式+事务的用法
     */
    public static void transactionalTake() {

        Connection newConnection = null;
        Channel createChannel = null;
        try {
            newConnection = RabbitMqUtils.getConnection();
            createChannel = newConnection.createChannel();
            /**
             * 声明一个队列。
             * 参数一：队列名称
             * 参数二：是否持久化
             * 参数三：是否排外  如果排外则这个队列只允许有一个消费者
             * 参数四：是否自动删除队列，如果为true表示没有消息也没有消费者连接自动删除队列
             * 参数五：队列的附加属性
             * 注意：
             * 1.声明队列时，如果已经存在则放弃声明，如果不存在则会声明一个新队列；
             * 2.队列名可以任意取值，但需要与消息接收者一致。
             * 3.下面的代码可有可无，一定在发送消息前确认队列名称已经存在RabbitMQ中，否则消息会发送失败。
             */
            createChannel.queueDeclare("myQueue", true, false, false, null);

            /**
             * 开启事务
             * 消费者开启事务后，即使不提交也会获取到消息并且从队列删除。
             * 结论：
             *     如果是手动确认的消费者，开启事物的情况下必须ack之后再commit，否则不会从队列移除
             */
            createChannel.txSelect();

            /**
             * 接收消息。会持续坚挺，不能关闭channel和Connection
             * 参数一：队列名称
             * 参数二：消息是否自动确认，true表示自动确认接收完消息以后会自动将消息从队列移除。否则需要手动ack消息
             * 参数三：消息接收者的标签，用于多个消费者同时监听一个队列时用于确认不同消费者。
             * 参数四：消息接收者
             */
            createChannel.basicConsume("myQueue", false, "", new DefaultConsumer(createChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {

                    // 该消息是否已经被处理过，true表示已经处理过，false表示没有处理过
                    boolean redeliver = envelope.isRedeliver();

                    String string = new String(body, "UTF-8");
                    // 获取消息的编号，根据编号确认消息
                    long deliveryTag = envelope.getDeliveryTag();
                    // 获取当前内部类中的通道
                    Channel channel = this.getChannel();
                    System.out.println("处理消息成功, 消息: " + string + "\t redeliver: " + redeliver);

                    // 手动确认
                    channel.basicAck(deliveryTag, true);

                    // 提交事务
                    channel.txCommit();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (createChannel != null) {
                    // 回滚。如果未异常会提交事务，此时回滚无影响
                    createChannel.txRollback();
                    createChannel.close();
                }
                if (newConnection != null) {
                    newConnection.close();
                }
            } catch (Exception ignore) {
                logger.error("关闭连接异常:{}", ignore);
                // ignore
            }
        }
    }

}
