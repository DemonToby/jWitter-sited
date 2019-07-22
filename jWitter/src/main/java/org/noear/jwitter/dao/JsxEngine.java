package org.noear.jwitter.dao;


import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.solon.XApp;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JsxEngine {
    private ScriptEngine jsEngine = null;

    public JsxEngine() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        jsEngine = scriptEngineManager.getEngineByName("nashorn");

        XApp.global().shared.forEach((k,v)->{
            jsEngine.put(k, v);
        });

        jsEngine.put("JTAPI", new JTAPI());


        try {
            StringBuilder sb = new StringBuilder();

            sb.append("var XContext = Java.type('org.noear.solon.core.XContext');");

            sb.append("var Datetime = Java.type('org.noear.jwitter.utils.Datetime');");
            sb.append("var Timecount = Java.type('org.noear.jwitter.utils.Timecount');");
            sb.append("var Timespan = Java.type('org.noear.jwitter.utils.Timespan');");

            sb.append("var Jsoup = Java.type('org.jsoup.Jsoup');");



            sb.append("function modelAndView(tml,mod){return JTAPI.modelAndView(tml,mod);};");
            sb.append("function require(path){var c=JTAPI.require(path); return eval(c);}");


            //为JSON.stringify 添加java的时间处理
            sb.append("function stringify_jtime(k,v){if(v&&v.before){return new Datetime(v).toString('yyyy-MM-dd HH:mm:ss')}; if(v&&v.getFullTime){return v.toString('yyyy-MM-dd HH:mm:ss')}; return v};");

            sb.append("function API_RUN(api){var rst=api(XContext.current());if(rst){if(typeof(rst)=='object'){if(rst.addAll||rst.putAll){return JTAPI.serialize_obj(rst)}else{return JSON.stringify(rst,stringify_jtime)}}else{return rst}}else{return null}};");

            eval(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object eval(String code) throws ScriptException {
        return jsEngine.eval(code);
    }

    public Object eval(String code, Bindings map) throws ScriptException {
        return jsEngine.eval(code, map);
    }

    public void load(String jtcode) throws ScriptException {
        jsEngine.eval(jtcode);
    }

    public class JTAPI{
        public String serialize_obj(Object obj) throws Exception {
            if (obj instanceof String) {
                return (String) obj;
            }

            return ONode.serialize(obj, Constants.def);
        }

        public String require(String path) throws Exception{
            String path2 = AFileUtil.path2(path);

            AFileModel file = AFileUtil.get(path2);

            return new StringBuilder()
                    .append("new (function(){")
                    .append(file.content)
                    .append("})();")
                    .toString();
        }

        public Object modelAndView(String path, Object model) throws Exception {
            String path2 = AFileUtil.path2(path);
            String name = path2.replace("/", "__");

            AFileModel file = AFileUtil.get(path2);

            return FtlUtil.g().reander(name, file, model);
        }
    }
}
