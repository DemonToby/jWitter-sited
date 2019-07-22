package org.noear.jwitter.extend.sited.controller;

import org.noear.jwitter.extend.sited.Config;
import org.noear.jwitter.extend.sited.dao.Utils;
import org.noear.jwitter.extend.sited.model.SiteModel;
import org.noear.jwitter.extend.sited.utils.Datetime;
import org.noear.jwitter.extend.sited.utils.TextUtils;
import org.noear.snack.ONode;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.weed.DataItem;
import org.noear.weed.DbTableQuery;

import java.net.URLDecoder;
import java.util.List;

/**
 *
 * 插件中心对外统一接口
 *
 * */
@XController
public class ApiController {
    @XMapping("/api4.{ver}/all/{key}?")
    public void all(XContext ctx,Integer ver, String key) throws Exception{
        int app_id = ctx.paramAsInt("appid",0);
        int is_exa = 0;

        if(key!=null) {
            key = URLDecoder.decode(key, "utf-8");
        }

        if (app_id>0) {
            is_exa = Config.db().table("apps")
                    .where("app_id=?", app_id)
                    .limit(1)
                    .caching(Config.cache).usingCache(60 * 1)
                    .select("is_exa")
                    .getValue(0);
        }

        DbTableQuery q = Config.db().table("sites").where(Utils.condition(ctx));

        if (is_exa > 0) {
            q.and("is_exa=1");
        }

        q.and("client_ver <= ?", ver);

        if (TextUtils.isEmpty(key) == false) {
            if (key.startsWith("@")) {
                q.and("author like ?", key.replace("@","") + "%");
            }
            else {
                q.and("title like ?", "%" + key + "%");
            }
        }

        q.orderBy("`index` DESC,update_time DESC")
                .caching(Config.cache).usingCache(60 * 5);

        List<SiteModel> list = q.select("*").getList( SiteModel.class);

        ONode data = new ONode().asArray();
        for (SiteModel s : list) {

            ONode n = data.addNew();
            n.set("url", s.url);
            n.set("author", s.author);
            n.set("type", s.btype);
            n.set("ver", s.ver);
            n.set("title", s.title);
            n.set("src", Utils.addinUrl(s.file));
            n.set("intro", s.intro);
            n.set("logo", s.logo);

            n.set("vip", s.is_vip);
            n.set("del", s.is_del?1:0);
        }

        ctx.outputAsJson(data.toJson());
    }

    @XMapping("/api4.{ver}/als/")
    public void als(XContext ctx,Integer ver) throws Exception{
        List<SiteModel> list = Config.db().table("sites")
                .where(Utils.condition(ctx))
                .and("client_ver <= ?", ver)
                .orderBy("`index` DESC,site_id DESC")
                .select("*")
                .caching(Config.cache).usingCache(60 * 5)
                .getList(SiteModel.class);

        ONode data = new ONode().asArray();
        for (SiteModel s : list) {

            ONode n = data.addNew();
            n.set("url", s.url);
            n.set("type", s.btype);
            n.set("ver", s.ver);
            n.set("del", s.is_del?1:0);
        }

        ctx.outputAsJson(data.toJson());
    }

    @XMapping("/api4.{ver}/chk/{url_}")
    public void chk(XContext ctx,Integer ver,String url_) throws Exception{
        List<SiteModel> list = Config.db().table("sites")
                .where(Utils.condition(ctx))
                .and("client_ver <= ?", ver)
                .and("is_del <> 1")
                .select("*")
                .caching(Config.cache).usingCache(60 * 5)
                .getList(SiteModel.class);

        ONode data = new ONode();
        for (SiteModel s : list) {

            if (TextUtils.isEmpty(url_) == false && Utils.isMatch(url_, s.exp) == false) {
                continue;
            }

            data.set("vip", s.is_vip);
            data.set("ver", s.ver);
            data.set("src", Utils.addinUrl(s.file));
            break;
        }

        ctx.outputAsJson(data.toJson());
    }

    @XMapping("/api4.{ver}/log/")
    public void log(XContext ctx,Integer ver, Integer log) throws Exception {
        try {
            if (log == null) {
                ctx.output("0");
            } else {
                DataItem temp = new DataItem();
                temp.set("log", log);
                temp.set("client_ver", ver);
                temp.set("user_id", ctx.param("u_id"));
                temp.set("appid", ctx.param("appid"));
                temp.set("appver", ctx.param("appver"));
                temp.set("title", ctx.param("sd_title"));
                temp.set("author", ctx.param("sd_author"));
                temp.set("ver", ctx.param("sd_ver"));
                temp.set("url", ctx.param("sd_url"));
                temp.set("url_md5", ctx.param("sd_url_md5"));
                temp.set("log_date", Datetime.Now().toString("yyyyMMdd"));
                temp.set("log_fulltime", Datetime.Now().toString("yyyy-MM-dd HH:mm:ss"));

                Config.db().table("site_logs").insert(temp);

                ctx.output("1");
            }
        } catch (Exception ex) {
            ctx.output(ex.getMessage());
        }
    }
}
