package org.apache.flume.json;

import org.apache.flume.Event;

import java.util.List;

public interface JsonProcess {
    List<Event> process(Object object,String extract,String renameColumn);
}
