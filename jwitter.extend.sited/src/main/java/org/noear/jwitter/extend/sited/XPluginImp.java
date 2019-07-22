package org.noear.jwitter.extend.sited;

import org.noear.jwitter.extend.sited.controller.ApiController;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Config.setDefDb(app.prop().argx());

        Aop.beanLoad(ApiController.class);
        app.shared.put("eSiteD",new eSiteD());
    }
}
