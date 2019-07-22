package org.noear.jwitter.dao;

import org.noear.solon.XUtil;
import org.noear.solon.core.XMap;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;
import org.noear.weed.cache.LocalCache;

//-host=mysql.dev.zmapi.cn:3306 -db=jpress -usr=root -pwd=gXWTCL18

//-host=121.41.104.216:3307 -db=sited2 -usr=noear -pwd=rd011077

public class DbUtil {
    /** 说明：
     *
     * 1.找个数据库，建个a-file表
     * 2.为启动时配一下连接信息
     *
     * CREATE TABLE `a_file` (
     *   `file_id` int(11) NOT NULL AUTO_INCREMENT,
     *   `path` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路径',
     *   `tag` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分类标签',
     *   `is_static` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否静态',
     *   `link_to` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '连接到',
     *   `content_type` varchar(99) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容类型',
     *   `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
     *   `create_fulltime` datetime DEFAULT NULL COMMENT '创建时间',
     *   `update_fulltime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     *   PRIMARY KEY (`file_id`),
     *   UNIQUE KEY `IX_key` (`path`) USING HASH
     * ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     * */
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
