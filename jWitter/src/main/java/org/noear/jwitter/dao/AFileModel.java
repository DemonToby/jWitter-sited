package org.noear.jwitter.dao;

import java.util.*;

public class AFileModel {

    /**  */
    public int file_id;
    /** 路径 */
    public String path;
    /** 是否静态 */
    public boolean is_staticize;
    /**  */
    public boolean is_editable;
    /** 连接到 */
    public String link_to;
    /** 分类标签 */
    public String edit_mode;
    /** 内容类型 */
    public String content_type;
    /** 内容 */
    public String content;
    /** 创建时间 */
    public Date create_fulltime;
    /** 更新时间 */
    public Date update_fulltime;

}