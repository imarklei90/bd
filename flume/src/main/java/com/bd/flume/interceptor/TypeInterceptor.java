package com.bd.flume.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义拦截器
 * @author Lei
 * @date 2021/3/19
 */
public class TypeInterceptor implements Interceptor{

    private List<Event> eventLists;

    @Override
    public void initialize() {
        eventLists = new ArrayList<>();
    }

    // 单个事件拦截
    @Override
    public Event intercept(Event event) {
        Map<String, String> headers = event.getHeaders();
        String body = new String(event.getBody());
        if(body.contains("hello")){
            headers.put("type", "Test");
        }else{
            headers.put("type", "TMP");
        }

        return event;
    }


    // 批量事件拦截
    @Override
    public List<Event> intercept(List<Event> list) {

        // 清空集合
        eventLists.clear();

        for (Event event : list) {
            eventLists.add(intercept(event));
        }

        return eventLists;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder{

        @Override
        public Interceptor build() {
            return new TypeInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
