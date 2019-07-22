package org.noear.jwitter.controller;

import org.noear.jwitter.dao.AImageHandler;
import org.noear.jwitter.dao.AImageModel;
import org.noear.jwitter.dao.AImageUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * 图片代理
 * */
public class ImgHandler implements XHandler {
    @Override
    public void handle(XContext ctx) throws Exception {
        String path = ctx.path();

        AImageModel file = AImageUtil.get(path);

        //文件不存在，则404
        if (file == null || file.img_id == 0) {
            ctx.status(404);
            return;
        }

        //如果是静态
        if (file.data == null) {
            ctx.status(404);
        } else {
            AImageHandler.handle(ctx, file);
        }
    }
}
