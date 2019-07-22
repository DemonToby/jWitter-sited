package org.noear.jwitter.controller;

import org.noear.jwitter.Config;
import org.noear.jwitter.dao.*;
import org.noear.jwitter.utils.TextUtils;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用代理
 * */
public class AppHandler implements XHandler {

    public static final AppHandler g = new AppHandler();

    @Override
    public void handle(XContext ctx) throws Exception {

        String path = ctx.path();
        String path2 = AFileUtil.path2(path);
        String name = path2.replace("/", "__");

        if (ctx.paramAsInt("_reset", 0) == 1) {
            AFileUtil.remove(path2);
            if(path.endsWith(Config.frm_actoin_suffix)){
                JsxRunner.del(name);
            }else {
                FtlUtil.g().del(name);
            }
        }

        AFileModel file = AFileUtil.get(path2);

        //文件不存在，则404
        if (file.file_id == 0) {
            ctx.status(404);
            return;
        }

        //如果有跳转，则跳转
        if (TextUtils.isEmpty(file.link_to) == false) {
            ctx.redirect(file.link_to);
            return;
        }

        //如果是静态
        if (file.is_staticize) {
            if (file.content == null) {
                ctx.status(404);
            } else {
                AFileStaticHandler.handle(ctx, file);
            }
            return;
        }

        //最后是动态的
        try {
            String text = null;

            if(file.path.endsWith(Config.frm_actoin_suffix)){
                text = JsxUtil.exec(name,file,ctx);
            }else{
                if (TextUtils.isEmpty(file.content_type)) {
                    ctx.contentType("text/html;charset=utf-8");
                }else{
                    ctx.contentType(file.content_type);
                }

                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                text = FtlUtil.g().reander(name, file, model);
            }

            if(text != null) {
                ctx.output(text);
            }

        } catch (Exception ex) {
            PrintStream ps = new PrintStream(ctx.outputStream());
            ex.printStackTrace(ps);
        }
    }
}
