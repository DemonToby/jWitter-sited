package org.noear.jwitter.dao;


import org.noear.jwitter.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class AImageUtil {
    private static String _lock = "";
    private static Map<String, AImageModel> _files = new HashMap<>();

    public static AImageModel get(String path) throws Exception {
        if(TextUtils.isEmpty(path)){
            return null;
        }

        if(_files.containsKey(path)==false){
            synchronized (_lock){
                if(_files.containsKey(path)==false){
                    AImageModel tml =  DbApi.imgGet(path);
                    _files.put(path,tml);
                }
            }
        }

        return _files.get(path);
    }

    public static void remove(String path){
        _files.remove(path);
    }

}
