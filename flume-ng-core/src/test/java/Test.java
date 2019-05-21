import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class Test {
    public static void main(String[] args) {
    }


   static void getDataFromJSONObject(JSONObject jb,String str){
        String [] s = str.split(",");
        for(String key:s){
            System.out.println("key="+key+", value="+jb.getString(key));
        }

    }



    @org.junit.Test
    public void testArray(){

        String extract = "c1>c2>c3>col1,col2,col3";
        String [] formatArray = extract.split(">");
        String columnJSONString = "{\"cc1\": \"c1\",\"cc2\": \"c2\",\"cc3\": \"c3\",\"cc4\": \"c4\"}";
        Map columnMap = JSON.parseObject(columnJSONString).getInnerMap();

        Object object = JSON.parseObject(new String());
        for(int i = 0;i<formatArray.length-1;i++){
            object = getObject(object,formatArray[i]);
        }

        JSONObject jsonObject = (JSONObject) object;

        String[] columns = formatArray[formatArray.length-1].split(",");
        JSONObject jb =  formatJSONObject(jsonObject,columns,columnMap);
        System.out.println(jb.toJSONString());

    }


    /**
     * 从复杂json结构中抽取需要的数据
     * @param object
     * @param key
     * @return
     */
    Object getObject(Object object,String key){
        System.out.println("object instanceof JSONArray=>"+(object instanceof JSONArray));
        System.out.println("object instanceof JSONObject=>"+(object instanceof JSONObject));
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
    JSONObject formatJSONObject(JSONObject jsonObject,String []columns,Map<String,String> columnName){
        JSONObject jb = new JSONObject();

        for(String column:columns){
            String k = columnName.containsKey(column)?columnName.get(column):column;
            jb.put(k,jsonObject.getString(column));

        }
        return jb;
    }
}
