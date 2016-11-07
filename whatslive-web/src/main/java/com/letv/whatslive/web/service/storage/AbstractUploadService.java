package com.letv.whatslive.web.service.storage;

/**
 * 文件上传服务抽象类，声明上传的方法
 * Created by wangjian7 on 2015/8/6.
 */
public abstract class AbstractUploadService {

    /**
     * 生成文件key值方法
     * @param prefix
     * @param fileMd5
     * @param busiKey
     * @return
     */
    public abstract String getKey(String prefix, String fileMd5, String busiKey);

    /**
     * 文件上传
     * @param md5 文件的MD5
     * @param fileSize 文件的长度
     * @param localFileUrl 本地文件地址
     * @param serverFileUrl web服务器上待上传的文件地址
     * @param key 存储key值
     * @return
     */
    public abstract boolean uploadFile(String md5, Long fileSize, String localFileUrl, String serverFileUrl, String key);
}
