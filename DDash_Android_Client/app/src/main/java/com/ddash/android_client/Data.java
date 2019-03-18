package com.ddash.android_client;

import android.os.Build;
import android.text.TextUtils;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Data extends HashMap<String, Object> {

    @Override
    public String toString() {
        String sep = "\n\n";
        StringBuffer sb = new StringBuffer();
        Iterator iter = this.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            sb.append(pair.toString());
            sb.append(sep);
        }
        return sb.toString();
    }
}


class GroupMap extends HashMap<String, Object> {
//    private Map<String, Object> map;

    public GroupMap(String group_name) {
//        map = new HashMap<>();
//        map.put("_group_name", group_name);
        this.put("_group_name", group_name);
    }

//    public Object put(String key, Object value) {
//        return map.put(key, value);
//    }
//
//    public void putAll(Map<? extends String,? extends Object> m) {
//        map.putAll(m);
//    }
//
//    @Override
//    public String toString() {
//        return "";
//    }
}


class GroupMapList extends ArrayList<GroupMap> {

//    private List<GroupMap> list;

//    public boolean add(GroupMap e) {
//        return list.add(e);
//    }

//    public String toString() {
//        return TextUtils.join(", ", this);
//    }

    public String[] toFlatStringArray(String sep) {
        List<String> strArr = new ArrayList<>();
//        String[] arr = new String[this.size()];
//        for (int i = 0; i < this.size(); i++) {
//            arr[i] = this.get(i).toString();
//        }
        for (GroupMap map : this) {
            Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();
                strArr.add(entry.getKey() + sep + entry.getValue().toString());
            }
        }
        return strArr.toArray(new String[strArr.size()]);
    }
}