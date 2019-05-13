package org.apache.flume.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.tools.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提取数据并修改列名
 */
public class JsonInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(JsonInterceptor.class);
    //json 格式说明 JSONArray\ JSONObject
    private static String jsonType = null;
    //提取数据说明 "c1>c2>c3>c4,c5,c6,c7
    private static String extract = null;


    /**
     *  Only {@link JsonInterceptor.Builder} can build me
     */
    private JsonInterceptor() { }


    @Override
    public void initialize() {
        // no-op
    }

    /**
     * Modifies events in-place.
     */
    @Override
    public Event intercept(Event event) {
        System.out.println("intercept:" + new String(event.getBody()));
        System.out.println("intercept jsonType:" + jsonType);

        Map<String, String> headers = event.getHeaders();
        return event;
    }

    /**
     * Delegates to {@link #intercept(Event)} in a loop.
     * 调用 {@link #intercept(Event)}  遍历List<Event>
     *
     * @param events
     * @return
     */
    @Override
    public List<Event> intercept(List<Event> events) {
        System.out.println("List intercept jsonType:" + jsonType);
        System.out.println("List intercept extract:" + extract);

        try {
            List<Event> listEvent= new ArrayList<>();

            if("JSONArray".equals(jsonType)){//如果配置类型是json数组
                for (Event event : events) {
                    Map header = event.getHeaders();
                    String str = new String(event.getBody());
                    JSONArray jb = JSON.parseArray(str);

                    for (int i = 0; i < jb.size(); i++) {
                        JSONObject jsonObject = jb.getJSONObject(i);
                        System.out.println("JSONArray JSONObject "+i +" "+ jsonObject.toJSONString());
                        listEvent.add(eventFormat(header,jsonObject));
                    }
                }
            }else if("JSONObject".equals(jsonType)){//如果配置类型是json对象
                for (Event event : events) {
                    Map header = event.getHeaders();
                    String str = new String(event.getBody());

                    JSONObject jsonObject = JSON.parseObject(str);
                    System.out.println("JSONObject "+ jsonObject.toJSONString());
                    listEvent.add(eventFormat(header,jsonObject));
                }
            }
            return listEvent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     *  <p>格式化json 对象 提取出需要的数据</p>
     * @param header 事件头描述
     * @param jsonObject 需要转换的json格式数据
     * @return
     */
    public Event eventFormat(Map<String, String> header, JSONObject jsonObject) {

        System.out.println("intercept jsonType:" + jsonType);
        String [] extractColumnArray = extract.split(">");
        Map renameColumnMap = null;
        Object object = jsonObject;
        for(int i = 0;i<extractColumnArray.length-1;i++){
            object = JSONUtil.getObject(object,extractColumnArray[i]);
        }

        jsonObject = (JSONObject) object;

        return EventBuilder.withBody(jsonObject.toJSONString().getBytes(), header);
    }
    
    @Override
    public void close() {
        // no-op
    }

    /**
     * Builder which builds new instances of the HostInterceptor.
     */
    public static class Builder implements Interceptor.Builder {


        @Override
        public Interceptor build() {
            //return new JsonInterceptor(preserveExisting, useIP, header);
            return new JsonInterceptor();
        }

        @Override
        // 获取配置文件中的的配置
        public void configure(Context context) {

            jsonType = context.getString("jsonType");
            extract = context.getString("extract");
        }

    }


}
