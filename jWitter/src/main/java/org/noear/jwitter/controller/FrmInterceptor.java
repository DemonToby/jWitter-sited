package org.noear.jwitter.controller;

import org.noear.jwitter.dao.*;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * 拉截代理
 * */
public class FrmInterceptor implements XHandler {
    public static final FrmInterceptor g = new FrmInterceptor();

    @Override
    public void handle(XContext ctx) throws Exception {
        String path = ctx.path();
        String path2 = AFileUtil.path3(path);

        if(path2 == null){
            return;
        }

        String name = path2.replace("/", "__");

        AFileModel file = AFileUtil.get(path2);

        if (file.file_id == 0) {
            return;
        }

        JsxUtil.exec(name,file,ctx);
    }
}
