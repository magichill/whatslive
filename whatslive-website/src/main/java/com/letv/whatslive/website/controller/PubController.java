package com.letv.whatslive.website.controller;

import com.letv.whatslive.website.util.json.JacksonJsonMapper;
import com.letv.whatslive.website.util.json.JsonMapper;
import com.letv.whatslive.website.util.util.UUIDGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuminghua on 14-4-18.
 */
public class PubController {

    private static final Logger logger = LoggerFactory.getLogger(PubController.class);

    public static final String SUCCESS = "A00000";

    public static final String ERROR = "A00001";

    public static final String ERROR_INFO = "A00002";

    protected Map<String, Object> getSuccessMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("rsCode", SUCCESS);
        result.put("rsMsg", "成功");
        return result;
    }

    protected Map<String, Object> getFailMap(String rsMsg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("rsCode", ERROR);
        result.put("rsMsg", rsMsg);
        return result;
    }

    protected Map<String, Object> getFailMap(String rsMsg, Map<String, String> errorInfo) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("rsCode", ERROR_INFO);
        result.put("rsMsg", rsMsg);
        result.put("errorInfo", errorInfo);
        return result;
    }

    protected void setResContent2Json(HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
    }

    protected void setResContent2Text(HttpServletResponse response) {
        response.setContentType("text/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
    }

    protected String map2JsonString(Map<String, Object> map) {
        try {
            return JacksonJsonMapper.getInstance().writeValueAsString(map);
        } catch (IOException e) {
            logger.error("转换JSON失败！", e);
        }
        return "";
    }

    protected void stream2File(InputStream in, String filePath) {
        try {
            File targetFile = new File(filePath);
            if (targetFile.exists()) {
                FileUtils.deleteQuietly(targetFile);
            }
            File tmpFile = new File(targetFile.getAbsolutePath() + "." + UUIDGenerator.uuid());
            FileUtils.copyInputStreamToFile(in, tmpFile);
            tmpFile.renameTo(targetFile);
        } catch (IOException e) {
            logger.error("写入文件失败：" + filePath, e);
        }
    }

    protected void copyFile(File srcFile, File destFile) {
        try {
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            logger.error("复制文件失败！", e);
        }
    }

    protected String getFileExtName(String filename) {
        if (StringUtils.isNotBlank(filename)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot);
            }
        }
        return filename;
    }

    /**
     * 获取客户端IP
     *
     * @param request
     * @return
     */
    protected String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    protected String toJson(Object object) {
        return JsonMapper.toNormalJson(object);
    }

}
