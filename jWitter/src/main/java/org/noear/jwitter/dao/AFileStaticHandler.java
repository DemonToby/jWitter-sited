package org.noear.jwitter.dao;

import org.noear.solon.core.*;
import java.util.Date;

/**
 * 静态文件代理
 * */
public class AFileStaticHandler {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";


    public static void handle(XContext context, AFileModel file) throws Exception {
        String path = context.path();


        context.setHandled(true);

        if (file.update_fulltime == null) {
            file.update_fulltime = app_runtime;
        }

        String modified_since = context.header("If-Modified-Since");
        String modified_now = file.update_fulltime.toString();

        if (modified_since != null) {
            if (modified_since.equals(modified_now)) {
                context.headerSet(CACHE_CONTROL, "max-age=6000");
                context.headerSet(LAST_MODIFIED, modified_now);
                context.status(304);
                return;
            }
        }

        int idx = path.lastIndexOf(".");
        if (idx > 0) {
            String mime = file.content_type;

            if (mime != null) {
                context.headerSet(CACHE_CONTROL, "max-age=6000");
                context.headerSet(LAST_MODIFIED, file.update_fulltime.toString());
                context.contentType(mime);
            }
        }

        context.status(200);
        context.output(file.content);
    }

    private static final Date app_runtime = new Date();
}
