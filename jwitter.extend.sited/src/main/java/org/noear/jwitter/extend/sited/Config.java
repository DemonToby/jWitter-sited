package org.noear.jwitter.extend.sited;

import org.noear.solon.XUtil;
import org.noear.solon.core.XMap;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;
import org.noear.weed.cache.LocalCache;

public class Config {
    public static final ICacheServiceEx cache = new LocalCache("data", 60*5);

    private static DbContext _db = null;
    public static DbContext db(){
        return _db;
    }

    public static void setDefDb(XMap map) {
        _db = getDb(map);
    }


    public static DbContext getDb(String host, String schema, String usr, String pwd) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://")
                .append(host)
                .append("/")
                .append(schema)
                .append("?allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true");

        return new DbContext(schema, sb.toString(), usr, pwd,null);
    }

    public static DbContext getDb(XMap map){
        String host = map.get("host");
        String db = map.get("db");
        String usr = map.get("usr");
        String pwd = map.get("pwd");

        if(XUtil.isEmpty(host) || XUtil.isEmpty(db) || XUtil.isEmpty(usr) || XUtil.isEmpty(pwd)){
            throw new RuntimeException("please enter a normal database config");
        }

        return getDb(host,db,usr,pwd);
    }
}
