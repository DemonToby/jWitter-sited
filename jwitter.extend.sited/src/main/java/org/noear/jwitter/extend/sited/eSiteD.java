package org.noear.jwitter.extend.sited;

import org.noear.jwitter.extend.sited.dao.DbSiteDApi;
import org.noear.jwitter.extend.sited.dao.Utils;
import org.noear.jwitter.extend.sited.utils.*;
import org.noear.jwitter.extend.sited.dao.custom.DdApi;
import org.noear.jwitter.extend.sited.dao.custom.DdSource;
import org.noear.solon.annotation.XNote;
import org.noear.solon.core.XFile;
import org.noear.weed.DbTableQuery;

import java.util.List;
import java.util.Map;

public class eSiteD {

    @XNote("url转为data形式")
    public  String urlData(String uri){
        return Base64Util.encode(Utils.addinUrl(uri));
    }

    @XNote("查找插件（tag,key）")
    public List<Map<String, Object>> findAddins(int tag, String key) throws Exception{
        StringBuilder sb = new StringBuilder();

        DbTableQuery q = Config.db().table("sites").where("is_ok = 1 AND is_vip = 0 AND is_del = 0");
        if (TextUtils.isEmpty(key) == false) {
            q.and("(title like ? OR author like ? )", "%" + key + "%", key + "%");
        }

        if (tag>0) {
            switch (tag) {
                case 0:
                case 1:
                    q.and("(btype=1)"); break;
                case 2:
                    q.and("(btype=2)"); break;
                case 3:
                    q.and("(btype=3 or btype=7)"); break;
                case 6:
                    q.and("(btype=6)"); break;
                default:
                    q.and("(btype=4 or btype=5 or btype=8)"); break;
            }
        }

        q.orderBy("`index` DESC,update_time DESC");

        return q.caching(Config.cache).select("*").getMapList();
    }

    @XNote("插件源码解密")
    public String codeDecode(String xml){
        if (xml.startsWith("sited::")) {
            int start = xml.indexOf("::") + 2;
            int end = xml.lastIndexOf("::");
            String txt = xml.substring(start, end);
            String key = xml.substring(end + 2);
            xml = DdApi.unsuan(txt, key);
        }

        return xml;
    }

    @XNote("插件源码加密")
    public String codeEncode(String xml) throws Exception{
        if (xml.startsWith("sited::")) {
            return xml;
        }else {
            String key2 = "TIME" + Datetime.Now().toString("yyMMddHHMMsss");

            String txt = Utils.addinEncode(xml, key2);
            String txt2 = "sited::" + txt + "::" + key2;

            return txt2;
        }
    }

    @XNote("设置插件源码")
    public String sourceSet(String puid,XFile file,String path) throws Exception {
        String xml = IOUtil.stream2String(file.content);
        return sourceSet(puid, path, xml);
    }

    @XNote("设置插件源码")
    public String sourceSet(String puid, String path, String content)throws Exception {
        DdSource sd = null;

        try {
            sd = new DdSource(content);
        } catch (Exception ex) {
            throw new Exception("插件格式解析出错::" + ex.toString());
        }

        if (sd.engine == 0) {
            throw new Exception("engine引擎版本号错误");
        }

        if (TextUtils.isEmpty(sd.author)) {
            throw new Exception("author格式错误");
        }

        if (TextUtils.isEmpty(sd.intro) || sd.intro.length() < 10) {
            throw new Exception("intro内容太少");
        }

        int puid2 = Integer.parseInt(puid);

        sd.guid = puid + "_" + EncryptUtil.md5(sd.url);

        return DbSiteDApi.saveSiteD(puid2, sd, path);
    }
}
