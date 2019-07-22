package org.noear.jwitter.dao;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.noear.jwitter.utils.TextUtils;
import org.noear.solon.XApp;

import java.io.StringWriter;

//后面可以添加新的引擎支持
public class FtlUtil {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
    StringTemplateLoader tmls = new StringTemplateLoader();

    private FtlUtil() {
        cfg.setNumberFormat("#");
        cfg.setDefaultEncoding("utf-8");
        cfg.setTemplateLoader(tmls);

        try {
            cfg.setSharedVaribles(XApp.global().shared);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String lock="";
    private static FtlUtil _g;
    public static FtlUtil g(){
        if(_g == null){
            synchronized (lock){
                if(_g == null){
                    _g  =new FtlUtil();
                }
            }
        }

        return _g;
    }


    private boolean tryInit(String name, AFileModel file) throws Exception {
        if (FtlUtil.g().has(name)) {
            return true;
        }

        if (TextUtils.isEmpty(file.content) == false) {
            FtlUtil.g().put(name, file.content);
            return true;
        } else {
            return false;
        }
    }

    ////////


    public boolean has(String name){
        return tmls.findTemplateSource(name) != null;
    }

    public void del(String name){
        tmls.removeTemplate(name);
    }

    public void put(String name, String template){
        tmls.putTemplate(name, template);
    }

    public String reander(String name, AFileModel file, Object model) throws Exception {
        if(tryInit(name,file)){
            Template tmpl = cfg.getTemplate(name, "utf-8");

            StringWriter writer = new StringWriter();

            tmpl.process(model, writer);

            return writer.toString();
        }else{
            return "";
        }
    }
}
