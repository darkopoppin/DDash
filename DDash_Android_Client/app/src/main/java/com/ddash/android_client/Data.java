package com.ddash.android_client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Data extends HashMap<String, Object> {

    @Override
    public String toString() {
        String sep = "\n";
        StringBuilder sb = new StringBuilder();
        Iterator iter = this.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            sb.append(pair.toString());
            sb.append(sep);
        }
        return sb.toString();
    }
}
