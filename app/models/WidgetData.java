package models;

import java.util.HashMap;
import java.util.Map;

public class WidgetData {

    private Map<String,Object> data = new HashMap<String,Object>();


    public void put(java.lang.String key, java.lang.Object arg) {
         data.put(key,arg);
    }

    public java.lang.Object get(java.lang.String key) {
        return data.get(key);
    }

}
