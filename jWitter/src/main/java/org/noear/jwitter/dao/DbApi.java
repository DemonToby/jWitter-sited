package org.noear.jwitter.dao;

import org.noear.solon.core.XContext;
import org.noear.weed.DbContext;
import org.noear.weed.DbTableQuery;

public class DbApi {
    private static DbContext db(){
        return DbUtil.db();
    }

    /** 新建文件 */
    public static boolean fileNew(int fid, XContext ctx) throws Exception {
        DbTableQuery qr = db().table("a_file")
                .set("path", ctx.param("path", ""))
                .set("tag", ctx.param("tag", ""))
                .set("is_staticize", ctx.paramAsInt("is_staticize", 0))
                .set("is_editable", ctx.paramAsInt("is_editable", 0))
                .set("link_to", ctx.param("link_to", ""))
                .set("edit_mode", ctx.param("edit_mode", ""))
                .set("content_type", ctx.param("content_type", ""));

        if (fid > 0) {
            return qr.where("file_id=?", fid)
                    .update() > 0;
        } else {
            return qr.insert() > 0;
        }
    }

    public static AFileModel fileGet(String path) throws Exception {
        return db().table("a_file")
                .where("path=?", path)
                .select("*")
                .getItem(AFileModel.class);
    }

    public static boolean fileSet(int fid, String fcontent) throws Exception {
        if(fid<1){
            return false;
        }

        if(fcontent == null){
            return false;
        }

        AFileModel fm = DbUtil.db().table("a_file")
                .where("file_id=?", fid)
                .select("*")
                .getItem(AFileModel.class);


        if(fm.is_editable==false){
            return false;
        }

        DbUtil.db().table("a_file")
                .set("content", fcontent)
                .where("file_id=?", fid)
                .update();

        String path2 = fm.path;
        String name = path2.replace("/", "__");

        AFileUtil.remove(path2);
        FtlUtil.g().del(name);
        JsxRunner.del(name);

        return true;
    }

    public static String cfgGet(String name) {
        return cfgGet(name,"");
    }

    public static String cfgGet(String name, String def) {
        try {
            return db().table("a_config")
                    .where("`name`=?", name)
                    .select("value")
                    .caching(DbUtil.cache)
                    .cacheTag("cfg_" + name)
                    .getValue(def);
        }catch (Exception ex){
            return def;
        }
    }

    public static boolean cfgSet(String name, String value) throws Exception{
        boolean is_ok =false;
        if(db().table("a_config") .where("`name`=?",name).exists()){
            is_ok = db().table("a_config").set("value",value)
                    .where("`name`=?",name)
                    .update()>0;
        }else{
            is_ok = db().table("a_config")
                    .set("name",name)
                    .set("value",value)
                    .insert() > 0;
        }

        DbUtil.cache.clear("cfg_"+name);

        return is_ok;
    }


    public static AImageModel imgGet(String path) throws Exception{
        return db().table("a_image")
                .where("`path`=?",path)
                .select("*")
                .caching(DbUtil.cache)
                .getItem(AImageModel.class);
    }

    public static boolean imgSet(String path,String content_type, String data) throws Exception{
        boolean is_ok =false;
        if(db().table("a_image") .where("`path`=?",path).exists()){
            is_ok = db().table("a_image")
                    .set("`content_type`",content_type)
                    .set("`data`",data)
                    .where("`path`=?",path)
                    .update()>0;
        }else {
            is_ok = db().table("a_image")
                    .set("`path`", path)
                    .set("`content_type`", content_type)
                    .set("`data`", data)
                    .insert() > 0;
        }

        return is_ok;
    }

    public static boolean imgUpd(String path, String data) throws Exception {
        boolean is_ok = db().table("a_image")
                .set("`data`",data)
                .where("`path`=?",path)
                .update()>0;

        return is_ok;
    }
}
