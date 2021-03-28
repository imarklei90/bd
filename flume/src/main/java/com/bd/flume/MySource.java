package com.bd.flume;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

/** 自定义Source
 * @author Lei
 * @date 2021/3/19
 */
public class MySource extends AbstractSource implements Configurable, PollableSource {

    private String prefix;
    private String suffix;

    @Override
    public void configure(Context context) {
        // 读取配置信息，并赋值
        prefix = context.getString("prefix");
        suffix = context.getString("suffix", "Test");
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;

        try {
            // 接收数据(自己造数据)
            for (int i = 0; i < 5; i++) {
                SimpleEvent event = new SimpleEvent();
                // 为事件设置值
                event.setBody((prefix + "--" + i + "--" + suffix).getBytes());

                // 将事件传给Channel
                getChannelProcessor().processEvent(event);
                status = Status.READY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = Status.BACKOFF;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }


}
