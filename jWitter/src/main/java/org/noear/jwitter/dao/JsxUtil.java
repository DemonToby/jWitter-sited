package org.noear.jwitter.dao;

import org.noear.jwitter.utils.IPUtils;
import org.noear.jwitter.utils.TextUtils;
import org.noear.snack.ONode;
import org.noear.solon.core.XContext;

public class JsxUtil {

    public static String exec(String name, AFileModel api, XContext context) throws Exception {
        name = name.replace(".","_");
        Object rst = null;


        try {
            rst = JsxRunner.runApi(name,api);

            if(rst == null){
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ONode args = JsxUtil.buildArgs(context);
            args.set("ip", IPUtils.getIP(context));
            throw ex;
        }

        String call = context.param("callback");
        String temp = rst.toString().trim();

        if (temp.startsWith("::") && temp.indexOf("://") > 0) {
            context.redirect(temp.replace("::", ""));
        } else if (TextUtils.isEmpty(call) == false) {
            StringBuilder sb = new StringBuilder();
            sb.append(call).append("(").append(temp).append(");");
        }

        if(temp.startsWith("<")){
            context.contentType("text/html;charset=utf-8");
        }

        if(temp.startsWith("{")){
            context.contentType("application/json;charset=utf-8");
        }


        return temp;
    }

    //////////////////////////////////////////////////////////////////

    //::util

    private static ONode buildArgs(XContext context) {
        ONode args = new ONode().asObject();
        context.paramMap().forEach((k,v)->{
            args.set(k, v);
        });

        return args;
    }

    //////////////////////////////////////////////////////////////////


    public static String compilerApiAsFun(String name,AFileModel api){
        StringBuilder sb = new StringBuilder();

        sb.append("this.API_").append(name).append(" = function(ctx){");
        sb.append("\r\n\r\n");
        sb.append(api.content);
        sb.append("\r\n\r\n};");

        return sb.toString();
    }

    public static String compilerRunApi(String name){

        StringBuilder sb = new StringBuilder();
        sb.append("API_RUN(");
        sb.append("this.API_").append(name);
        sb.append(");");

        return sb.toString();
    }
}
