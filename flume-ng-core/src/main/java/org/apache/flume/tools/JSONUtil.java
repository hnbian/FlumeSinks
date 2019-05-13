package org.apache.flume.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * json 工具类
 * @date 2019-05-13 16:11:15
 */
public class JSONUtil {
    /**
     * 从复杂json结构中抽取需要的数据
     * @param object
     * @param key
     * @return
     */
    public static Object getObject(Object object,String key){
        if (object instanceof JSONArray) {
            return   ((JSONArray) object).getJSONObject(0).get(key);
        }else{
            Object ob = ((JSONObject) object).get(key);
            if(ob instanceof JSONArray){
                return   ((JSONArray) ob).getJSONObject(0);
            }
            return  ob;
        }
    }

    /**
     * 格式化json数据取出需要的列并且替换列名
     * @param jsonObject
     * @param columns 提取出的列
     * @param columnName 替换的列名
     * @return
     */
    public static JSONObject extractAndRenameColumn(JSONObject jsonObject, String []columns, Map<String,String> columnName){
        JSONObject jb = new JSONObject();

        for(String column:columns){
            String k = columnName.containsKey(column)?columnName.get(column):column;
            jb.put(k,jsonObject.getString(column));
        }
        return jb;
    }


    /**
     *  抽取指定列
     * @param jsonObject
     * @param columns 提取出的列
     * @return
     */
    JSONObject extractColumn(JSONObject jsonObject,String []columns){
        JSONObject jb = new JSONObject();
        for(String column:columns){
            jb.put(column,jsonObject.getString(column));
        }
        return jb;
    }



    /**
     * 修改列名 只修改列名不进行抽取
     * @param jsonObject
     * @param columnName 替换的列名
     * @return
     */
    JSONObject renameColumn(JSONObject jsonObject,Map<String,String> columnName){
        JSONObject jb = new JSONObject();
        for(String column:jb.keySet()){
            String k = columnName.containsKey(column)?columnName.get(column):column;
            jb.put(k,jsonObject.getString(column));
        }
        return jb;
    }

}
