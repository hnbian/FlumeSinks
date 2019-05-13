package org.apache.flume.json;

import org.apache.flume.Event;

import java.util.List;

/**
 * 处理json array 对字段进行提取并修改字段名称
 */
public class JsonArrayExtractRenameProcess implements JsonProcess {
    @Override
    public List<Event> process(Object object, String extract, String renameColumn) {
        return null;
    }
}
