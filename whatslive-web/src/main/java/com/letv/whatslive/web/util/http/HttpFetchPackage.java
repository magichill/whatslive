package com.letv.whatslive.web.util.http;

import com.letv.whatslive.web.util.util.DateUtils;
import com.letv.whatslive.web.util.util.MD5Util;
import com.letv.whatslive.web.util.util.UUIDGenerator;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title:
 * Desc:抓取SVN路径下客户端安装包工具类
 * User: wangruifeng
 * Date: 14-5-21 下午1:32
 */
public class HttpFetchPackage {
    private static final Logger logger = LoggerFactory.getLogger(HttpFetchPackage.class);

    private final static int BUFFER = 1024;


    /**
     * 调用入口
     *
     * @param url
     * @param username
     * @param pwd
     * @return
     * @throws HttpInvokeException
     */
    public static void get(String filepath, List upUrlLocal, String url, String username, String pwd) throws Exception {

        executeMethod(filepath, upUrlLocal, url, username, pwd);

    }


    /**
     * 调用认证并下载文件
     *
     * @param
     * @throws HttpInvokeException
     * @retur
     */
    public static void executeMethod(String filepath, List upUrlLocal, String url, String username, String pwd) throws Exception {
        String encodeUrl = encode(url, "UTF-8");
        HttpMethod method = new GetMethod(encodeUrl);//URL中有中文
        if (method == null) {
            throw new IllegalArgumentException("method is required");
        }
        String subUrl = subUrl(url);

        long startTime = System.currentTimeMillis();
        HttpClient client = new HttpClient();
        //用户密码认证信息
        client.getState().setCredentials(
                new AuthScope(subUrl, 80, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(username, pwd)
        );
        client.getParams().setAuthenticationPreemptive(true);
        method.setDoAuthentication(true);

        int statusCode = HttpStatus.SC_OK;
        long elapsedTime = 0;
        File file =null;
        try {
            statusCode = client.executeMethod(method);
            elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("返回状态码====" + statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                logger.error("调用http请求失败: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + method.getURI() + ",响应码："
                        + statusCode);
            } else {

                logger.info("statusCode = 200, start download from stream, " + method.getURI());

                long length = Long.valueOf(method.getResponseHeader("Content-Length").getValue());

                file =  stream2File(method.getResponseBodyAsStream(), filepath, upUrlLocal);
                if(file!=null){
                    String reName = reName(file,filepath,upUrlLocal);

                    file.renameTo(new File(reName));
                    long filesize = getFileSizes(new File(reName));
                    if(length!=filesize){
                        throw new HttpInvokeException(statusCode, "调用http服务生成文件, url: " + method.getURI() + ",响应码："
                                + statusCode);
                    }

                }else{
                    throw new HttpInvokeException(statusCode, "调用http服务没有生成文件, url: " + method.getURI() + ",响应码："
                            + statusCode);
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                logger.info("调用http下载成功: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
            }
        } catch (Exception ex) {
            statusCode = 499;
            try {
                logger.info("调用http请求异常: " + method.getURI() + ",耗时：" + elapsedTime
                        + "ms, exception:" + ex.getMessage());
            } catch (URIException uriex) {
                // ignore this exception
            }
            if (ex instanceof HttpInvokeException) {
                throw (HttpInvokeException) ex;
            } else {
                throw new HttpInvokeException(statusCode, ex);
            }
        } finally {
            method.releaseConnection();

        }
    }

    /**
     * 取得文件大小
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) {
        long s=0;
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                s= fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                IOUtils.closeQuietly(fis);
            }
        }
        return s;
    }




    /**
     * 截取URL地址，获取IP地址域名
     *
     * @param url
     * @return
     */
    public static String subUrl(String url) {
        String s = "svn.letv.cn";//默认域名
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher m = p.matcher(url);
        if (m.find()) {
            s = m.group();
            logger.info(s);
        }
        return s;
    }

    /**
     * 下载文件存放到LOCAL目录并且重命名
     *
     * @param
     * @param filePath
     * @throws Exception
     */
    private static String reName(File in, String filePath, List upUrlLocal) throws Exception {

        StringBuffer savePath = new StringBuffer(filePath + upUrlLocal.get(0).toString());
        savePath.append(DateUtils.getToday()).append("/");
        String md5 = MD5Util.fileMd5(in);
        savePath.append(md5).append(".001");
        upUrlLocal.clear();
        upUrlLocal.add(savePath.toString());

        logger.info("savePath=="+savePath.toString());

        return savePath.toString();
    }

    /**
     * 下载文件到本地目录
     *
     * @throws Exception
     */
    private static File stream2File(InputStream in, String filePath, List upUrlLocal) throws Exception {

        File tmpFile = new File(filePath + upUrlLocal.get(0).toString() + DateUtils.getToday() + "/" + UUIDGenerator.uuid());

        FileUtils.copyInputStreamToFile(in, tmpFile);
        return tmpFile;

    }


    /**
     * 替换中文字符串
     *
     * @param str     被替换的字符串
     * @param charset 字符集
     * @return 替换好的
     * @throws UnsupportedEncodingException 不支持的字符集
     */
    public static String encode(String str, String charset) throws UnsupportedEncodingException {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
        }
        m.appendTail(b);
        logger.info(b.toString());
        return b.toString();
    }


    public static void main(String[] args) {

//        String str="http://svn.letv.cn/MobileQA/Publish/Android/Phone/基线/5.3.1/pcode.apk";//是你的字符串
//        try {
//            logger.info(encode(str, "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        File tmpFile = new File("/letv/upload/staticfile/test");
        String reName = "/letv/upload/staticfile/test/2013334/00012121.0001";
        tmpFile.renameTo(new File(reName));
//        try {
//            reName = reName(tmpFile,"/letv/upload/staticfile/","test/");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.info(reName);

    }

}
