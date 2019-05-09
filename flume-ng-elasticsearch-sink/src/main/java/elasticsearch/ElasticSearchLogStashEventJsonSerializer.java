package elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * 支持json格式数据
 * 将event body 中json格式数据解析kv对应elasticsearch 字段与值
 */
public class ElasticSearchLogStashEventJsonSerializer implements
        ElasticSearchEventSerializer  {

    @Override
    public XContentBuilder getContentBuilder(Event event) throws IOException {
        //System.out.println("XContentBuildergetContentBuilder"+new String(event.getBody()));
        XContentBuilder builder = jsonBuilder().startObject();
        appendBody(builder, event);
        appendHeaders(builder, event);
        return builder;
    }

    private void appendBody(XContentBuilder builder, Event event)
            throws IOException, UnsupportedEncodingException {
        byte[] body = event.getBody();
        //ContentBuilderUtil.appendField(builder, "@message", body);
        //System.out.println("ElasticSearchLogStashEventJsonSerializer");
        String bodyString = new String(body);
        JSONObject jb = JSON.parseObject(bodyString);
        for (String key : jb.keySet()) {
            ContentBuilderUtil.appendField(builder,key,jb.getString(key).getBytes());
        }


        /*ContentBuilderUtil.appendField(builder,"testFiled","hello".getBytes());
        ContentBuilderUtil.appendField(builder,"@xiaom","hello".getBytes());
        builder.field("@testFiled2","hello".getBytes());
        System.out.println("appendBody-------------------------");*/
    }

    private void appendHeaders(XContentBuilder builder, Event event)
            throws IOException {
        Map<String, String> headers = Maps.newHashMap(event.getHeaders());

        String timestamp = headers.get("timestamp");
        if (!StringUtils.isBlank(timestamp)
                && StringUtils.isBlank(headers.get("@timestamp"))) {
            long timestampMs = Long.parseLong(timestamp);
            builder.field("@timestamp", new Date(timestampMs));
            //builder.field("@testFiled3","hello".getBytes());
        }

        String source = headers.get("source");
        if (!StringUtils.isBlank(source)
                && StringUtils.isBlank(headers.get("@source"))) {
            ContentBuilderUtil.appendField(builder, "@source",
                    source.getBytes(charset));
        }

        String type = headers.get("type");
        if (!StringUtils.isBlank(type)
                && StringUtils.isBlank(headers.get("@type"))) {
            ContentBuilderUtil.appendField(builder, "@type", type.getBytes(charset));
        }

        String host = headers.get("host");
        if (!StringUtils.isBlank(host)
                && StringUtils.isBlank(headers.get("@source_host"))) {
            ContentBuilderUtil.appendField(builder, "@source_host",
                    host.getBytes(charset));
        }

        String srcPath = headers.get("src_path");
        if (!StringUtils.isBlank(srcPath)
                && StringUtils.isBlank(headers.get("@source_path"))) {
            ContentBuilderUtil.appendField(builder, "@source_path",
                    srcPath.getBytes(charset));
        }

        builder.startObject("@fields");
        for (String key : headers.keySet()) {
            byte[] val = headers.get(key).getBytes(charset);
            ContentBuilderUtil.appendField(builder, key, val);
        }
        builder.endObject();
    }

    @Override
    public void configure(Context context) {
        // NO-OP...
    }

    @Override
    public void configure(ComponentConfiguration conf) {
        // NO-OP...
    }
}
