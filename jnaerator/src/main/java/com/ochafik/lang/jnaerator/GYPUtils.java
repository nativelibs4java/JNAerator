/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ochafik
 */
public class GYPUtils {

    public static Map<String, Object> map() {
        return new HashMap<String, Object>();
    }

    public static String toGYP(Object o) {
        StringBuilder out = new StringBuilder();
        toGYP(o, out, "");
        return out.toString();
    }

    static void toGYP(Object o, StringBuilder out, String indent) {
        if (o instanceof Map) {
            toGYP((Map) o, out, indent);
        } else if (o instanceof String) {
            out.append('\'').append(o).append('\'');
        } else if (o instanceof Number) {
            out.append(o);
        } else if (o instanceof List) {
            toGYP((List) o, out, indent);
        } else {
            throw new RuntimeException("Type not supported: " + o.getClass().getName() + " (" + o + ")");
        }
    }

    private static void toGYP(Map<String, Object> map, StringBuilder out, String indent) {
        String indent2 = indent + "  ";
        out.append("{\n");
        for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> e = it.next();
            out.append(indent2).append('\'').append(e.getKey()).append("': ");
            toGYP(e.getValue(), out, indent2);
            if (it.hasNext()) {
                out.append(",\n");
            }
        }
        out.append('\n').append(indent).append('}');
    }

    private static void toGYP(List<Object> list, StringBuilder out, String indent) {
        String indent2 = indent + "  ";
        out.append("[\n");
        for (Iterator<Object> it = list.iterator(); it.hasNext();) {
            out.append(indent2);
            toGYP(it.next(), out, indent2);
            if (it.hasNext()) {
                out.append(",\n");
            }
        }
        out.append('\n').append(indent).append(']');
    }
}
