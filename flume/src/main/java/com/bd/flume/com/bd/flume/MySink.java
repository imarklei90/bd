package com.bd.flume.com.bd.flume;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义Sink
 * @author Lei
 * @date 2021/3/19
 */
public class MySink extends AbstractSink implements Configurable {

    private String prefix;
    private String suffix;
    private Logger logger = LoggerFactory.getLogger(MySink.class);


    @Override
    public void configure(Context context) {
        prefix = context.getString("prefix");
        suffix = context.getString("suffix", "END");
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;

        // 获取Channel
        Channel ch = getChannel();
        Transaction tx = ch.getTransaction();

        // 启动TX
        tx.begin();

        try {
            Event event = ch.take();

            if (event != null) {
                // 将Event存储到外部存储或者打印输出
                String body = new String(event.getBody());

                logger.info(prefix + "--" + body + "--" + suffix);
            }
            // 提交事务
            tx.commit();
            status = Status.READY;
        } catch (ChannelException e) {
            e.printStackTrace();

            // 回滚事务
            tx.rollback();
            status = Status.BACKOFF;
        }finally {
            tx.close();
        }

        return status;
    }


}
