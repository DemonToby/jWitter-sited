package org.noear.jwitter.dao;

import org.noear.weed.ext.Fun0Ex;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.*;

public class JsxRunner {
    private static JsxEngine _jtSQL = new JsxEngine();

    private static List<String> obj_loaded = Collections.synchronizedList(new ArrayList<>());


    private static void loadFunc(String name, Fun0Ex<String, Exception> code) throws Exception {
        if (isLoaded(name) == false) {
            obj_loaded.add(name);

            _jtSQL.eval(code.run());
        }
    }

    private static boolean isLoaded(String name) {
        return obj_loaded.contains(name);
    }

    public static void del(String name) {
        obj_loaded.remove(name.replace(".","_"));
    }


    private static void tryInitApi(String name,AFileModel api) throws Exception {


        loadFunc(name, () -> JsxUtil.compilerApiAsFun(name, api));
    }


    public static Object runApi(String name,AFileModel api) throws Exception {
        tryInitApi(name,api);

        String code = JsxUtil.compilerRunApi(name);

        return _jtSQL.eval(code);
    }
}
