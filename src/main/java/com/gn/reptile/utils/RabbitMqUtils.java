package com.gn.reptile.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * https://www.cnblogs.com/linhp/p/15219445.html
 */
public class RabbitMqUtils {

    //得到一个连接的 channel
    public static Channel getChannel() throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.3.129");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/vhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        return channel;
    }

    //得到一个连接的 Connection
    public static Connection getConnection() throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("192.168.3.129");
        factory.setHost("192.168.31.132");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/vhost");
        Connection connection = factory.newConnection();
        return connection;
    }

}
