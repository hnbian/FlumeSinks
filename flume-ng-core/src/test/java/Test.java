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

        String jsonString = "{\"ImageList\":[{\"Image\":{\"FaceList\":[],\"ImageInfo\":{},\"MotorVehicleList\":[{\"AppearTime\":\"\",\"BrandReliability\":75.99385070800781,\"Calling\":-1,\"CarOfVehicle\":\"\",\"CopilotCalling\":-1,\"CopilotSafeBelt\":-1,\"DescOfFrontItem\":\"\",\"DescOfRearItem\":\"\",\"DeviceID\":\"44000000051320000003\",\"Direction\":0,\"DirvingStatusCode\":\"\",\"DisapperaTime\":\"\",\"FilmColor\":\"0\",\"HasPlate\":\"0\",\"HitMarkInfo\":\"\",\"InfoKind\":1,\"IsAltered\":\"0\",\"IsCovered\":\"0\",\"IsDecked\":\"0\",\"IsModified\":false,\"IsSupicious\":\"0\",\"LaneNo\":1,\"LeftTopX\":622,\"LeftTopY\":225,\"MarkTime\":\"\",\"MotorVehicleID\":\"450000000025000000040720181008155403021220202122W\",\"NameOfPassedRoad\":\"\",\"NumOfPassenger\":0,\"PassTime\":\"20181008155403\",\"PlateCharReliability\":\"\",\"PlateClass\":\"0\",\"PlateColor\":\"99\",\"PlateNo\":\"\",\"PlateNoAttach\":\"\",\"PlateReliability\":0.0,\"RearviewMirror\":\"\",\"RightBtmX\":1151,\"RightBtmY\":1088,\"SafetyBelt\":-1,\"SideOfVehicle\":\"\",\"SourceID\":\"\",\"Speed\":0.0,\"StorageUrl1\":\"http://10.3.34.34:80/group1/M00/58/B0/CgMiIlu7DRqAJCR4AAOJNMEPUXY396.jp\",\"StorageUrl2\":\"\",\"StorageUrl3\":\"\",\"StorageUrl4\":\"\",\"StorageUrl5\":\"\",\"Sunvisor\":0,\"TollgateID\":\"\",\"UsingPropertiesCode\":\"\",\"VehicleBodyDesc\":\"\",\"VehicleBrand\":\"福田-风景-2012\",\"VehicleChassis\":\"\",\"VehicleClass\":\"13\",\"VehicleColor\":\"2\",\"VehicleColorDepth\":\"0\",\"VehicleDoor\":\"\",\"VehicleFrontItem\":\"1\",\"VehicleHeight\":0,\"VehicleHood\":\"\",\"VehicleLength\":0,\"VehicleModel\":\"福田\",\"VehicleRearItem\":\"\",\"VehicleRoof\":\"\",\"VehicleShielding\":\"\",\"VehicleStyles\":\"\",\"VehicleTrunk\":\"\",\"VehicleWheel\":\"\",\"VehicleWidth\":0,\"VehicleWindow\":\"\",\"WheelPrintedPattern\":\"\"}],\"PersonList\":[]}}],\"MsgCategory\":\"analysisResult\",\"command\":\"vehicle\",\"moduleCode\":\"45000000002500000004\",\"requestId\":\"8E396D00-050A-48C0-891F-191F335D65F7\",\"taskId\":\"\"}";
        String extract = "ImageList>Image>MotorVehicleList>BrandReliability,CopilotCalling,PlateColor,StorageUrl1";
        String [] formatArray = extract.split(">");
        String columnJSONString = "{\"BrandReliability\": \"c1\",\"CopilotCalling\": \"c2\",\"PlateColor\": \"c3\",\"StorageUrl1\": \"c4\"}";
        Map columnMap = JSON.parseObject(columnJSONString).getInnerMap();

        Object object = JSON.parseObject(jsonString);
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
