package com.gn.reptile.rabbitmq.confirm.producer;

import com.gn.reptile.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * https://blog.csdn.net/u012988901/article/details/88778966?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link&utm_relevant_index=2
 */
public class ConfirmProducer {


    /**
     * 普通confirm模式
     */
    public static void confirmSend_ordinary(){

         String exchangeName = "confirmExchange";
         String queueName = "confirmQueue";
         String routingKey = "confirmRoutingKey";
         String bindingKey = "confirmRoutingKey";
         int count = 5;

        Channel channel = null;
        try {
            Connection connection = RabbitMqUtils.getConnection();
            channel = connection.createChannel();
            //创建exchange
            channel.exchangeDeclare(exchangeName, "direct", true, false, null);
            //创建队列
            channel.queueDeclare(queueName, true, false, false, null);
            //绑定exchange和queue
            channel.queueBind(queueName, exchangeName, bindingKey);
            channel.confirmSelect();
            //发送持久化消息
                //第一个参数是exchangeName(默认情况下代理服务器端是存在一个""名字的exchange的,
                //因此如果不创建exchange的话我们可以直接将该参数设置成"",如果创建了exchange的话
                //我们需要将该参数设置成创建的exchange的名字),第二个参数是路由键
                channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_BASIC, ("消息").getBytes());
            if(!channel.waitForConfirms()){
                System.out.println("消息发送失败");
                //进行重发等操作
            }
            System.out.println("消息发送成功");

            final long start = System.currentTimeMillis();
            System.out.println("执行waitForConfirmsOrDie耗费时间: "+(System.currentTimeMillis()-start)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 批量confirm模式
     */
    public static void confirmSend_batch(){

        String exchangeName = "confirmExchange";
        String queueName = "confirmQueue";
        String routingKey = "confirmRoutingKey";
        String bindingKey = "confirmRoutingKey";
        int count = 100;

        Channel channel = null;
        try {
            Connection connection = RabbitMqUtils.getConnection();
            channel = connection.createChannel();
            //创建exchange
            channel.exchangeDeclare(exchangeName, "direct", true, false, null);
            //创建队列
            channel.queueDeclare(queueName, true, false, false, null);
            //绑定exchange和queue
            channel.queueBind(queueName, exchangeName, bindingKey);
            channel.confirmSelect();
            //发送持久化消息
            for(int i = 0;i < count;i++)
            {
                //第一个参数是exchangeName(默认情况下代理服务器端是存在一个""名字的exchange的,
                //因此如果不创建exchange的话我们可以直接将该参数设置成"",如果创建了exchange的话
                //我们需要将该参数设置成创建的exchange的名字),第二个参数是路由键
                channel.basicPublish(exchangeName, routingKey,MessageProperties.PERSISTENT_BASIC, ("第"+(i+1)+"条消息").getBytes());
            }
            if(!channel.waitForConfirms()){
                System.out.println("消息发送失败");
                //进行重发等操作
            }
            System.out.println("消息发送成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 生产者confirm模式:异步confirm
     */
    public static void confirmSend_asyn() throws Exception{

        String EXCHANGE_NAME = "fanout_exchange";
        //TreeSet是有序集合,元素使用其自然顺序进行排序,拥有存储需要confirm确认的消息序号
        SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

        // 获取到连接以及mq通道
        Connection connection = RabbitMqUtils.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout",true);
        //声明队列
        channel.queueDeclare("test_queue", true, false, false, null);
        //绑定
        channel.queueBind("test_queue", EXCHANGE_NAME, "");

        channel.confirmSelect();//将信道置为confirm模式
        channel.addConfirmListener(new ConfirmListener() {

            public void handleNack(long deliveryTag, boolean multiple)
                    throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }

            public void handleAck(long deliveryTag, boolean multiple)
                    throws IOException {
                //confirmSet.headSet(n)方法返回当前集合中小于n的集合
                if (multiple) {
                    //批量确认:将集合中小于等于当前序号deliveryTag元素的集合清除，表示这批序号的消息都已经被ack了
                    System.out.println("ack批量确认,deliveryTag:"+deliveryTag+",multiple:"+multiple+",当次确认消息序号集合:"+confirmSet.headSet(deliveryTag + 1));
                    confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                    //单条确认:将当前的deliveryTag从集合中移除
                    System.out.println("ack单条确认,deliveryTag:"+deliveryTag+",multiple:"+multiple+",当次确认消息序号:"+deliveryTag);
                    confirmSet.remove(deliveryTag);
                }
                //需要重发消息
            }
        });

        for(int i=0;i<30;i++){
            String message = "异步confirm消息"+i;
            //得到下次发送消息的序号
            long nextPublishSeqNo = channel.getNextPublishSeqNo();
            channel.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            //将序号存入集合中
            confirmSet.add(nextPublishSeqNo);
        }
        //关闭通道和连接
//        channel.close();
//        connection.close();


    }


}
