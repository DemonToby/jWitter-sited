package org.noear.jwitter;

import org.noear.jwitter.controller.FrmInterceptor;
import org.noear.jwitter.dao.DbUtil;
import org.noear.jwitter.dao.XUtil;
import org.noear.jwitter.controller.AppHandler;
import org.noear.jwitter.controller.ImgHandler;
import org.noear.solon.XApp;
import org.noear.solon.core.XMap;
import org.noear.solon.core.XMethod;

public class App {
    public static void main(String[] args) {
        XMap xarg = XMap.from(args);

        //1.检查参数并尝试实始化数据库
        if (xarg.size() < 4) {
            throw new RuntimeException("Missing necessary parameters（host,db,usr,pwd）");
        } else {
            DbUtil.setDefDb(xarg);
        }

        String sss = xarg.get("sss");

        //2.尝试运行WEB应用
        if (sss == null || sss.indexOf("web") >= 0) {
            runWeb(args);
        }

        //3.尝试运行服务应用
        if (sss == null || sss.indexOf("sev") >= 0) {
            runSev(args);
        }
    }

    private static void runWeb(String[] args) {
        XApp app = XApp.start(App.class, args, (xApp -> {
            xApp.shared.put("XUtil", new XUtil());
            xApp.shared.put("db", DbUtil.db());
            xApp.shared.put("cache", DbUtil.cache);
        }));

        //拉截代理
        app.before("/**", XMethod.GET, FrmInterceptor.g);
        app.before("/**", XMethod.POST, FrmInterceptor.g);

        //图片代理
        app.get(Config.frm_root_img + "**", new ImgHandler());

        //代码代理
        app.all("/", AppHandler.g);
        app.all("/**", AppHandler.g);

        XUtil xUtil = (XUtil)app.shared.get("XUtil");

        xUtil.shared();
    }

    private static void runSev(String[] args) {

    }
}
