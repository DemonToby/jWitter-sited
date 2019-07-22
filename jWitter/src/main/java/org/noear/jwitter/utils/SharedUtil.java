package org.noear.jwitter.utils;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XNote;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class SharedUtil {
    public static List<Map<String, Object>> shared() {
        List<Map<String, Object>> list = new ArrayList<>();

        XApp.global().shared.forEach((k, v) -> {
            Map<String, Object> v1 = new HashMap<>();
            list.add(v1);

            List<Map<String, Object>> methods = new ArrayList<>();

            Class<?> cls = v.getClass();

            v1.put("name", k);
            v1.put("type", v.getClass().getTypeName());
            v1.put("methods", methods);

            if (v instanceof DbContext || v instanceof ICacheServiceEx) {
                return;
            }

            for (Method m : cls.getDeclaredMethods()) {
                if (Modifier.isPublic(m.getModifiers()) == false) {
                    continue;
                }
                Map<String, Object> m1 = new HashMap<>();

                XNote tmp = m.getAnnotation(XNote.class);


                if (tmp != null) {
                    m1.put("note", "/** " + tmp.value() + " */");
                }

                StringBuilder sb = new StringBuilder();
                sb.append(k).append(".");
                sb.append(m.getName());

                sb.append("(");

                for (Parameter p : m.getParameters()) {
                    sb.append(p.getType().getSimpleName())
                            .append(" ")
                            .append(p.getName())
                            .append(",");
                }

                if (m.getParameterCount() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                sb.append(")");

                m1.put("code", sb.toString());
                methods.add(m1);
            }
        });

        Collections.sort(list, (m1, m2) -> m1.get("name").toString().toLowerCase().compareTo(m2.get("name").toString().toLowerCase()));

        return list;
    }
}
