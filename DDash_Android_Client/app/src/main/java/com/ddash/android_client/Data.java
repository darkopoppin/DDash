package com.ddash.android_client;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    public GroupMap(String group_name) {
        super();
        this.put("_group_name", group_name);
    }
}


class GroupMapList extends ArrayList<GroupMap> {

    public String toString() {
        return TextUtils.join(", ", this);
    }
}