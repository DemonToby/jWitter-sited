package org.noear.jwitter.dao;

import org.noear.jwitter.utils.*;
import org.noear.solon.annotation.XNote;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMap;
import org.noear.weed.DbContext;

import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.util.*;

public class XUtil {

    /** 生成GUID */
    @XNote("生成GUID")
    public  String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /** 生成数据库上下文 */
    @XNote("生成数据库上下文")
    public  DbContext db(String cfg){
        String[] args = cfg.split(" ");
        return DbUtil.getDb(XMap.from(args));
    }


    /** 编译html */
    @XNote("编译html")
    public String htmlEncode(String str){
        if (str==null){
            return "";
        }else{
            str=str.replaceAll("<", "&lt;");
            str=str.replaceAll(">", "&gt;");
        }
        return str;
    }

    /**
     *
     * ****************************/

    /** 配置获取 */
    @XNote("配置获取")
    public String cfgGet(String name) throws Exception{
        return DbApi.cfgGet(name);
    }

    /** 配置设置 */
    @XNote("配置设置")
    public boolean cfgSet(String name, String value) throws Exception{
        return DbApi.cfgSet(name,value);
    }

    /** 保存图片 */
    @XNote("保存图片")
    public String imgSet(XFile file) throws Exception {
        return imgSet(file,file.extension);
    }

    /** 保存图片（后缀名可自定义） */
    @XNote("保存图片（后缀名可自定义）")
    public String imgSet(XFile file, String extension) throws Exception {
        byte[] data_byte = IOUtils.stream2Byte(file.content);
        String data = Base64Utils.encodeByte(data_byte);
        String path = "/img/" + guid() + "." + extension;

        DbApi.imgSet(path, file.contentType, data);

        return path;
    }

    /** 保存图片（内容，类型，后缀名可自定义） */
    @XNote("保存图片（内容，类型，后缀名可自定义）")
    public String imgSet(String content, String contentType, String extension) throws Exception {
        String data = Base64Utils.encode(content);
        String path = "/img/" + guid() + "." + extension;

        DbApi.imgSet(path, contentType, data);

        return path;
    }

    @XNote("设定图片输出名称")
    public String imgOutName(XContext ctx, String filename) throws Exception{
        String filename2= URLEncoder.encode(filename, "utf-8");

        ctx.headerSet("Content-Disposition","attachment; filename=\""+filename2+"\"");
        return null;
    }

    /** 修改图片 */
    @XNote("修改图片")
    public String imgUpd(String path, String content) throws Exception {
        String data = Base64Utils.encode(content);

        DbApi.imgUpd(path, data);

        return path;
    }

    /** 获取图片内容 */
    @XNote("获取图片内容")
    public String imgGet(String path) throws Exception{
        AImageModel img =  DbApi.imgGet(path);
        return img2String(img.data);
    }

    @XNote("图片内容转为字符串")
    public String img2String(String data){
        if(TextUtils.isEmpty(data)) {
            return "";
        }else {
            return Base64Utils.decode(data);
        }
    }

    /** 新建文件 */
    @XNote("文件新建")
    public boolean fileNew(int fid, XContext ctx) throws Exception{
        return DbApi.fileNew(fid,ctx);
    }

    /** 文件设置内容 */
    @XNote("文件设置内容")
    public boolean fileSet(int fid, String fcontent) throws Exception {
        return DbApi.fileSet(fid,fcontent);
    }


    /**
     *
     * ****************************/
    @XNote("获取共享对象列表")
    public  List<Map<String, Object>> shared() {
        return SharedUtil.shared();
    }


    /** 生成md5码 */
    @XNote("生成md5码")
    public String md5(String str) {
        return EncryptUtils.md5(str);
    }

    /** 生成sha1码 */
    @XNote("生成sha1码")
    public String sha1(String str) {
        return EncryptUtils.sha1(str);
    }


    /** base64 */
    @XNote("BASE64编码")
    public  String base64Encode(String text){
        return Base64Utils.encode(text);
    }
    @XNote("BASE64解码")
    public  String base64Decode(String text){
        return Base64Utils.decode(text);
    }

    /** 生成随机码 */
    @XNote("生成随机码")
    public String codeByRandom(int len){
       return CodeUtils.codeByRandom(len);
    }

    /** 字符码转为图片 */
    @XNote("字符码转为图片")
    public BufferedImage codeToImage(String code) throws Exception {
        return ImageUtils.getValidationImage(code);
    }
}
