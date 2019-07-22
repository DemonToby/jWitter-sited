package org.noear.jwitter.extend.sited.dao;

import org.noear.jwitter.extend.sited.Config;
import org.noear.jwitter.extend.sited.dao.custom.DdSource;
import org.noear.jwitter.extend.sited.utils.TextUtils;
import org.noear.weed.DataItem;
import org.noear.weed.DbContext;
import org.noear.weed.IDataItem;

public class DbSiteDApi {
    public static String cfgGet(String name) {
        return cfgGet(name,"");
    }

    public static String cfgGet(String name, String def) {
        try {
            return Config.db().table("a_config")
                    .where("`name`=?", name)
                    .select("value")
                    .caching(Config.cache)
                    .cacheTag("cfg_" + name)
                    .getValue(def);
        }catch (Exception ex){
            return def;
        }
    }




    public static String saveSiteD(int puid, DdSource source, String path) throws Exception{
        DbContext db = Config.db();

        boolean exists = db.table("sites").where("guid=?", source.guid).exists();
        int dtype = source.main.dtype();
        int btype = source.main.btype();
        if (btype == 0) btype = dtype;

        if (exists) {
            IDataItem d = new DataItem().set("client_ver", source.engine)
                    .set("ver", source.ver)
                    .set("author", source.author)
                    .set("title", source.title)
                    .set("exp", source.expr)
                    .set("url", source.url)
                    .set("file",path)
                    .set("is_ok", 1)
                    .set("is_vip", source.meta.attrs.getInt("vip", 0))
                    .set("intro", source.intro)
                    .set("dtype", dtype)
                    .set("btype", btype)
                    .set("update_time", "$NOW()");

            if (TextUtils.isEmpty(source.logo) == false) {
                d.set("logo", source.logo);
            }

            db.table("sites").where("guid=?", source.guid).update(d);


        }
        else {
            IDataItem d = new DataItem()
                    .set("guid", source.guid)
                    .set("puid", puid)
                    .set("client_ver", source.engine)
                    .set("ver", source.ver)
                    .set("author", source.author)
                    .set("title", source.title)
                    .set("is_ok", 1)
                    .set("is_vip", source.meta.attrs.getInt("vip", 0))
                    .set("exp", source.expr)
                    .set("url", source.url)
                    .set("intro", source.intro)
                    .set("file", path)
                    .set("dtype", dtype)
                    .set("btype", btype)
                    .set("update_time", "$NOW()");

            if (TextUtils.isEmpty(source.logo) == false) {
                d.set("logo", source.logo);
            }

            db.table("sites").insert(d);
        }

        return path;
    }
}
