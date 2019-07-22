package org.noear.jwitter.dao;

import org.noear.jwitter.Config;

import java.util.HashMap;
import java.util.Map;

public class AFileUtil {
    private static String _lock = "";
    private static Map<String, AFileModel> _files = new HashMap<>();

    /**
     * 生成匹配数据库的路径
     * */
    public static String path2(String path){
        if (path.endsWith("/") == false) {
            if (path.lastIndexOf('/') > path.lastIndexOf('.')) {
                path = path.replaceAll("/[^/]*$", "/*");
            }
        }

        return path;
    }

    /**
     * 生成拉截器的路径
     * */
    public static String path3(String path) {
        String[] temp = path.split("/",-1);

        if (temp.length > 2) {
            return new StringBuilder()
                    .append("/")
                    .append(temp[1])
                    .append("/_")
                    .append(Config.frm_actoin_suffix)
                    .toString();
        } else {
            return null;
        }
    }

    /**
     * 获取缓存的文件
     * */
    public static AFileModel get(String path2) throws Exception {
        if(_files.containsKey(path2)==false){
            synchronized (_lock){
                if(_files.containsKey(path2)==false){
                    AFileModel tml =  DbApi.fileGet(path2);
                    _files.put(path2,tml);
                }
            }
        }

        return _files.get(path2);
    }

    /**
     * 移徐缓存的文件（后面自动重新加载）
     * */
    public static void remove(String path2){
        _files.remove(path2);
    }

}
