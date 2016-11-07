package com.letv.whatslive.web.util.http;

import com.letv.whatslive.web.util.configuration.PropertyGetter;
import com.letv.whatslive.web.util.util.UUIDGenerator;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-27 下午7:13
 */
public class HttpDownloader {
    private static final Logger logger = LoggerFactory.getLogger(HttpDownloader.class);

    private final static int BUFFER = 1024;

    private final static String QIYI_DISPATCHER_HOST = "data.video.qiyi.com";

    //todo 业务相关，从common模块移出
    private final static String[] QIYI_BAD_VIDEO_DOWNLOAD_HOSTS = PropertyGetter.getStringArray("qiyi.datasync.download.bad.hosts");

    private static MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();

    private static HttpClient client = new HttpClient(httpConnectionManager);

    static {
        //每主机最大连接数和总共最大连接数，通过hosfConfiguration设置host来区分每个主机
        client.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(50);
        client.getHttpConnectionManager().getParams().setMaxTotalConnections(200);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
        client.getHttpConnectionManager().getParams().setSoTimeout(3600000);
        //client.getHttpConnectionManager().getParams().setTcpNoDelay(true);
        //client.getHttpConnectionManager().getParams().setLinger(1000);
        //失败的情况下会进行3次尝试,成功之后不会再尝试
        client.getHttpConnectionManager().getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

    }

    public static void download(String filePath, String url) {
        download(filePath, url, false);
    }

    public static void download(String filePath, String url, boolean go2dispatcher) {
        logger.info("开始下载：" + url + "  至  " + filePath);
        HttpMethod method = new GetMethod(url);
        HttpMethod dispatcherMethod = null;
        long startTime = System.currentTimeMillis();
        //HttpClient client = new HttpClient();
        //client.getHttpConnectionManager().getParams().setConnectionTimeout(120000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(3600000);

        int statusCode = HttpStatus.SC_OK;
        long elapsedTime = 0;
        InputStream in = null;
        try {
            method.addRequestHeader("Connection", "Keep-Alive");
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            statusCode = client.executeMethod(method);

            if (statusCode == HttpStatus.SC_OK) {
                if (go2dispatcher) {
                    logger.info("it is a qiyi bad video host, redirect to dispatcher data.video.qiyi.com");
                    String host = method.getURI().getHost();
                    String newUrl = method.getURI().getURI().replace(host, QIYI_DISPATCHER_HOST);
                    dispatcherMethod = new GetMethod(newUrl);
                    //HttpClient dispatcherClient = new HttpClient();
                    statusCode = client.executeMethod(dispatcherMethod);
                    logger.info("dispatcher to : " + dispatcherMethod.getURI().getURI());
                    if (statusCode == HttpStatus.SC_OK) {
                        logger.info("statusCode = 200, start download from stream, " + dispatcherMethod.getURI());
                        in = dispatcherMethod.getResponseBodyAsStream();
                        downloadStream2File(in, filePath);

                        elapsedTime = System.currentTimeMillis() - startTime;
                        logger.info("调用http下载成功: " + dispatcherMethod.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                        logger.info("结束下载：" + url + "  至  " + filePath);
                    } else {
                        logger.error("调用http下载失败: " + method.getURI() + "响应码: " + statusCode);
                        throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + method.getURI() + ",响应码："
                                + statusCode);
                    }
                } else {
                    logger.info("statusCode = 200, start download from stream, " + method.getURI());
                    in = method.getResponseBodyAsStream();
                    downloadStream2File(in, filePath);

                    elapsedTime = System.currentTimeMillis() - startTime;
                    logger.info("调用http下载成功: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                    logger.info("结束下载：" + url + "  至  " + filePath);
                }
            } else {
                logger.error("调用http下载失败: " + method.getURI() + "响应码: " + statusCode);
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + method.getURI() + ",响应码："
                        + statusCode);
            }
        } catch (Exception ex) {
            try {
                logger.info("调用http下载异常: " + method.getURI() + ",耗时：" + elapsedTime
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
            if (dispatcherMethod != null) {
                dispatcherMethod.releaseConnection();
            }
            IOUtils.closeQuietly(in);
        }
    }

    private static void downloadStream2File(InputStream in, String filePath) throws Exception {
        File targetFile = new File(filePath);
        File tmpFile = new File(targetFile.getAbsolutePath() + "." + UUIDGenerator.uuid());
        FileUtils.copyInputStreamToFile(in, tmpFile);
        tmpFile.renameTo(targetFile);
    }

    public static String getDisDownloadUrl(String url) {

        HttpMethod method = new GetMethod(url);
        int statusCode = HttpStatus.SC_OK;
        try {
            method.setFollowRedirects(false);
            method.addRequestHeader("Connection", "Keep-Alive");
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            statusCode = client.executeMethod(method);
            if (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT
                    || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                logger.info("it is a qiyi bad video host, redirect to dispatcher data.video.qiyi.com");
                //读取新的URL地址
                Header header = method.getResponseHeader("location");
                if (header == null) {
                    logger.error("调用http下载失败: " + url + "响应码: " + statusCode);
                    throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + url + ",响应码："
                            + statusCode);
                }
                String oldURL = header.getValue();
                logger.info("调度前重定向地址:" + oldURL);
                String host = new URL(oldURL).getHost();
                String newURL = oldURL.replace(host, QIYI_DISPATCHER_HOST);
                logger.info("调度后重定向地址:" + newURL);
                return newURL;
            } else {
                logger.error("调用http下载失败: " + url + "响应码: " + statusCode);
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + url + ",响应码：" + statusCode);
            }
        } catch (Exception ex) {
            logger.info("调用http下载异常: " + url + ", exception:" + ex.getMessage());
            if (ex instanceof HttpInvokeException) {
                throw (HttpInvokeException) ex;
            } else {
                throw new HttpInvokeException(statusCode, ex);
            }
        } finally {
            method.releaseConnection();
        }
    }

    public static String getRealDownloadUrl(String dispatcherUrl) {

        HttpMethod method = new GetMethod(dispatcherUrl);
        int statusCode = HttpStatus.SC_OK;
        try {
            method.setFollowRedirects(false);
            method.addRequestHeader("Connection", "Keep-Alive");
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            statusCode = client.executeMethod(method);
            if (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT
                    || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                //读取新的URL地址
                Header header = method.getResponseHeader("location");
                if (header == null) {
                    logger.error("调用http下载失败: " + dispatcherUrl + "响应码: " + statusCode);
                    throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + dispatcherUrl + ",响应码：" + statusCode);
                }
                String newURL = header.getValue();
                logger.info("调度后地址:" + newURL);
                return newURL;
            } else {
                logger.error("调用http下载失败: " + dispatcherUrl + "响应码: " + statusCode);
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + dispatcherUrl + ",响应码：" + statusCode);
            }
        } catch (Exception ex) {
            logger.info("调用http下载异常: " + dispatcherUrl + ", exception:" + ex.getMessage());
            if (ex instanceof HttpInvokeException) {
                throw (HttpInvokeException) ex;
            } else {
                throw new HttpInvokeException(statusCode, ex);
            }
        } finally {
            method.releaseConnection();
        }
    }

    public static InputStream downloadStream(final HttpMethod method, int redo) throws HttpInvokeException {
        if (method == null) {
            throw new IllegalArgumentException("method is required");
        }
        if (redo < 1 || redo > 5) {
            throw new IllegalArgumentException("redo is between 1 and 5");
        }
        int index = 1;
        while (index <= redo) {
            try {
                return downloadStreamOnce(method, index);
            } catch (Exception ex) {
                index++;
                if (index > redo) {
                    // 超出重复次数异常，关闭method连接
                    method.releaseConnection();
                    if (ex instanceof HttpInvokeException) {
                        throw (HttpInvokeException) ex;
                    } else {
                        throw new HttpInvokeException(499, ex);
                    }
                }
            }
        }
        throw new HttpInvokeException(499, "调用http服务返回响应错误");
    }

    /**
     * 注意此方法未关闭InputStream，调用者需要调用关闭方法
     *
     * @param method
     * @return
     */
    public static InputStream downloadStreamOnce(final HttpMethod method, int index) {

        logger.info("开始第" + index + "次下载：" + method.getPath());
        long startTime = 0;
        long elapsedTime = 0;
        String URI = null;
        try {
            method.addRequestHeader("Connection", "Keep-Alive");
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            startTime = System.currentTimeMillis();
            int statusCode = client.executeMethod(method);
            URI = method.getURI().getURI();
            if (statusCode == HttpStatus.SC_OK) {
                elapsedTime = System.currentTimeMillis() - startTime;
                logger.info("调用http下载成功: " + URI + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                return new BufferedInputStream(method.getResponseBodyAsStream()) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        method.releaseConnection();
                        logger.info("下载完成");
                    }
                };
            } else {
                method.releaseConnection();
                elapsedTime = System.currentTimeMillis() - startTime;
                logger.error("调用http下载失败: " + URI + "响应码: " + statusCode);
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + URI + ",响应码：" + statusCode);
            }
        } catch (Exception ex) {
            method.releaseConnection();
            elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("调用http下载异常: " + URI + ",耗时：" + elapsedTime + "ms, exception:" + ex.getMessage());
            if (ex instanceof HttpInvokeException) {
                throw (HttpInvokeException) ex;
            } else {
                throw new HttpInvokeException(499, ex);
            }
        }
    }

    public static String downloadString(String url, boolean go2dispatcher, int redo) throws HttpInvokeException {
        if (redo < 1 || redo > 5) {
            throw new IllegalArgumentException("redo is between 1 and 5");
        }
        HttpMethod method = new GetMethod(url);
        HttpMethod dispatcherMethod = null;
        try {
            long startTime = 0;
            long elapsedTime = 0;
            int index = 1;
            while (index <= redo) {
                try {
                    logger.info("开始第" + index + "次下载：");
                    startTime = System.currentTimeMillis();
                    int statusCode = HttpStatus.SC_OK;
                    method.addRequestHeader("Connection", "Keep-Alive");
                    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
                    statusCode = client.executeMethod(method);
                    if (statusCode == HttpStatus.SC_OK) {
                        if (go2dispatcher) {
                            logger.info("it is a qiyi bad video host, redirect to dispatcher data.video.qiyi.com");
                            String host = method.getURI().getHost();
                            String newUrl = method.getURI().getURI().replace(host, QIYI_DISPATCHER_HOST);
                            dispatcherMethod = new GetMethod(newUrl);
                            statusCode = client.executeMethod(dispatcherMethod);
                            logger.info("dispatcher to : " + dispatcherMethod.getURI().getURI());
                            if (statusCode == HttpStatus.SC_OK) {
                                logger.info("statusCode = 200, start download from stream, " + dispatcherMethod.getURI());
                                elapsedTime = System.currentTimeMillis() - startTime;
                                logger.info("调用http下载成功: " + dispatcherMethod.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                                return IOUtils.toString(dispatcherMethod.getResponseBodyAsStream(), "UTF-8");
                            } else {
                                logger.error("调用http下载失败: " + url + "响应码: " + statusCode);
                                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + url + ",响应码：" + statusCode);
                            }
                        } else {
                            elapsedTime = System.currentTimeMillis() - startTime;
                            logger.info("statusCode = 200, start download from stream, " + url);
                            logger.info("调用http下载成功: " + url + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                            return IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");
                        }
                    } else {
                        elapsedTime = System.currentTimeMillis() - startTime;
                        logger.error("调用http下载失败: " + url + "响应码: " + statusCode);
                        throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + url + ",响应码：" + statusCode);
                    }
                } catch (Exception ex) {
                    index++;
                    elapsedTime = System.currentTimeMillis() - startTime;
                    logger.info("调用http请求异常: " + url + ",耗时：" + elapsedTime + "ms, exception:" + ex.getMessage());
                    if (index > redo) {
                        if (ex instanceof HttpInvokeException) {
                            throw (HttpInvokeException) ex;
                        } else {
                            throw new HttpInvokeException(499, ex);
                        }
                    }
                }
            }
        } finally {
            method.releaseConnection();
            if (dispatcherMethod != null) {
                dispatcherMethod.releaseConnection();
            }
        }
        throw new HttpInvokeException(499, "调用http服务返回响应错误");
    }

    public static void main(String[] args) {

    }

}
