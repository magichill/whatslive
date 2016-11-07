package com.letv.whatslive.web.util.http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Title: httpClient工具类
 * Desc: 对httpClient进行简单封装，方便调用
 * Company: www.gitv.cn
 * Date: 13-7-17 上午1:19
 */
public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final NameValuePair[] EMPTY_NAMEVALUE_PAIRS = new NameValuePair[]{};

    public static String getQuietly(String url) {
        try {
            return get(url);
        } catch (Exception ex) {
            // 捕获异常，但不返回给调用方
        }
        return null;
    }

    public static String get(String url) throws HttpInvokeException {
        return executeMethod(new GetMethod(url));
    }

    public static String get(String url, int redo) throws HttpInvokeException {
        return executeMethod(getHttpClient(1200000, 1200000), new GetMethod(url), redo);
    }

    public static String get(String url, int conTimeout, int soTimeout, int redo) throws HttpInvokeException {
        return executeMethod(getHttpClient(conTimeout, soTimeout), new GetMethod(url), redo);
    }

    public static String get(String url, Map<String, String> parameters) throws HttpInvokeException {
        GetMethod getMethod = new GetMethod(url);
        getMethod.setQueryString(buildNameValuePair(parameters));
        return executeMethod(getMethod);
    }

    public static String getPro(String url, Map<String, String> parameters) throws HttpInvokeException {
        GetMethod getMethod = new GetMethod(url);
        String queryString = getMethod.getQueryString();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(queryString)) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] kv = param.split("=");
                if (kv.length == 2) {
                    parameters.put(kv[0], kv[1]);
                }
            }
        }
        getMethod.setQueryString(buildNameValuePair(parameters));
        return executeMethod(getMethod);
    }

    public static String executeMethod(HttpMethod method) throws HttpInvokeException {
        if (method == null) {
            throw new IllegalArgumentException("method is required");
        }

        long startTime = System.currentTimeMillis();
        HttpClient client = new HttpClient();

        client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
        client.getHttpConnectionManager().getParams().setSoTimeout(120000);

        int statusCode = HttpStatus.SC_OK;
        long elapsedTime = 0;

        try {
            method.setRequestHeader("Connection", "close");
            statusCode = client.executeMethod(method);
            elapsedTime = System.currentTimeMillis() - startTime;

            if (statusCode != HttpStatus.SC_OK) {
                logger.error("调用http请求失败: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + method.getURI() + ",响应码："
                        + statusCode);
            } else {
                logger.info("调用http请求成功: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
            }
            return IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");
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

    public static String executeMethodHeader(HttpMethod method,Map<String,String> headers) throws HttpInvokeException {
        if (method == null) {
            throw new IllegalArgumentException("method is required");
        }

        long startTime = System.currentTimeMillis();
        HttpClient client = new HttpClient();

        client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
        client.getHttpConnectionManager().getParams().setSoTimeout(120000);

        int statusCode = HttpStatus.SC_OK;
        long elapsedTime = 0;

        try {
            method.setRequestHeader("Connection", "close");
            for(Entry<String,String> entry:headers.entrySet()){
                method.addRequestHeader(entry.getKey(),entry.getValue());
            }
            statusCode = client.executeMethod(method);
            elapsedTime = System.currentTimeMillis() - startTime;

            if (statusCode != HttpStatus.SC_OK) {
                logger.error("调用http请求失败: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                logger.error(IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8"));
                throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + method.getURI() + ",响应码："
                        + statusCode);
            } else {
                logger.info("调用http请求成功: " + method.getURI() + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
            }
            return IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");
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

    public static HttpClient getHttpClient(int conTimeout, int soTimeout) {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(conTimeout);
        client.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
        return client;
    }

    /**
     * 有重试机制的执行方法
     *
     * @param client
     * @param method
     * @param redo   1~5之间
     * @return
     * @throws HttpInvokeException
     */
    public static String executeMethod(HttpClient client, HttpMethod method, int redo) throws HttpInvokeException {
        if (client == null || method == null) {
            throw new IllegalArgumentException("client and method is required");
        }
        if (redo < 1 || redo > 5) {
            throw new IllegalArgumentException("redo is between 1 and 5");
        }
        try {
            long startTime = 0;
            long elapsedTime = 0;
            String URI = null;
            int index = 1;
            while (index <= redo) {
                try {
                    logger.info("第" + index + "次调用http请求");
                    URI = method.getURI().getURI();
                    startTime = System.currentTimeMillis();
                    method.setRequestHeader("Connection", "close");
                    int statusCode = client.executeMethod(method);
                    elapsedTime = System.currentTimeMillis() - startTime;
                    if (statusCode == HttpStatus.SC_OK) {
                        logger.info("调用http请求成功: " + URI + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                        return IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");
                    } else {
                        logger.error("调用http请求失败: " + URI + ",耗时：" + elapsedTime + "ms, 响应码: " + statusCode);
                        throw new HttpInvokeException(statusCode, "调用http服务返回响应错误, url: " + URI + ",响应码：" + statusCode);
                    }
                } catch (Exception ex) {
                    index++;
                    logger.info("调用http请求异常: " + URI + ",耗时：" + elapsedTime + "ms, exception:" + ex.getMessage());
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
        }
        throw new HttpInvokeException(499, "调用http服务返回响应错误");
    }


    public static String postQuietly(String url, Map<String, String> parameters) {
        try {
            return post(url, parameters, null, null, null);
        } catch (Exception ex) {
            // ignore exception
        }
        return null;
    }

    public static String postQuietly(String url, Map<String, String> parameters,
                                     String contentType, String charset, String requestBody) {
        try {
            return post(url, parameters, contentType, charset, requestBody);
        } catch (Exception ex) {
            // ignore exception
        }
        return null;
    }

    public static String post(String url, Map<String, String> parameters,
                              String contentType, String charset, String requestBody) throws HttpInvokeException {
        PostMethod post = new PostMethod(url);
        if (requestBody != null) {
            post.setQueryString(buildNameValuePair(parameters));
            try {
                post.setRequestEntity(new StringRequestEntity(requestBody, contentType, charset == null ? "UTF-8"
                        : charset));
            } catch (UnsupportedEncodingException ex) {
                logger.error("", ex);
                throw new HttpInvokeException(ex);
            }
        } else {
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            post.setRequestBody(buildNameValuePair(parameters));
        }
        return executeMethod(post);
    }


    public static String postAndHeader(String url, Map<String, String> parameters,Map<String, String> headers,
                              String contentType, String charset, String requestBody) throws HttpInvokeException {
        PostMethod post = new PostMethod(url);
        if (requestBody != null) {
            post.setQueryString(buildNameValuePair(parameters));
            try {
                post.setRequestEntity(new StringRequestEntity(requestBody, contentType, charset == null ? "UTF-8"
                        : charset));
            } catch (UnsupportedEncodingException ex) {
                logger.error("", ex);
                throw new HttpInvokeException(ex);
            }
        } else {
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            post.setRequestBody(buildNameValuePair(parameters));
        }
        return executeMethodHeader(post,headers);
    }

    private static NameValuePair[] buildNameValuePair(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return EMPTY_NAMEVALUE_PAIRS;
        }
        NameValuePair[] nameValuePairs = new NameValuePair[parameters.size()];

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(parameters.size());
        for (Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairList.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        nameValuePairList.toArray(nameValuePairs);
        return nameValuePairs;
    }

    public static String postMultPartURL(String url,Map<String, String> params,String picUrl) throws Exception{
        byte[] imageBytes = HttpClientUtils.readUrl(picUrl);
        if(null != imageBytes){
            return postMultPartURL(url,params,imageBytes);
        }else{
            return null;
        }
    }

    public static String postMultPartURL(String url,Map<String, String> params,byte[] imageBytes) throws Exception{
        PostMethod postMethod = new PostMethod(url);
            Part[] parts = null;
            if (params == null) {
                parts = new Part[1];
            } else {
                parts = new Part[params.size() + 1];
            }
            if (params != null) {
                int i = 0;
                for (Entry<String, String> entry: params.entrySet()) {
                    parts[i++] = new StringPart(entry.getKey(),
                            entry.getValue());
                }

                parts[parts.length - 1] =new ByteArrayPart(imageBytes,"pic", null);
            }
            postMethod.setRequestEntity(new MultipartRequestEntity(parts,
                    postMethod.getParams()));
            return executeMethod(postMethod);
    }

    public static  String post(String url,Map<String, String> param) throws  Exception{
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        NameValuePair[] nameValuePairs = buildNameValuePair(param);
        method.setRequestBody(nameValuePairs);
        method.releaseConnection();
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(method);
        String response = IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8"); // new String(method.getResponseBodyAsString().getBytes("UTF-8"));
        return response;
    }

    public static byte[] readUrl(String picUrl) throws Exception{
        BufferedInputStream bufferedInputStream = null ;
        byte[] imageBytes = null ;
        try {
            URL purl = new URL(picUrl);
            HttpURLConnection urlconnection = (HttpURLConnection) purl.openConnection();
            urlconnection.connect();
            bufferedInputStream = new BufferedInputStream(urlconnection.getInputStream());
            int len = bufferedInputStream.available();
            imageBytes = new byte[len];
            int r = bufferedInputStream.read(imageBytes);
            if (len != r) {
                imageBytes = null;
                throw new IOException("读取文件不正确");
            }
        }catch (Exception ex){
            throw  ex;
        }finally {
            if(null != bufferedInputStream){
                bufferedInputStream.close();//根据图片url得到图片的字节数组
            }
        }
        return imageBytes;
    }


    private static class ByteArrayPart extends PartBase {
        private byte[] mData;
        private String mName;

        public ByteArrayPart(byte[] data, String name, String type)
                throws IOException {
            super(name, type, "UTF-8", "binary");
            mName = name;
            mData = data;
        }

        protected void sendData(OutputStream out) throws IOException {
            out.write(mData);
        }

        protected long lengthOfData() throws IOException {
            return mData.length;
        }

        protected void sendDispositionHeader(OutputStream out)
                throws IOException {
            super.sendDispositionHeader(out);
            StringBuilder buf = new StringBuilder();
            buf.append("; filename=\"").append(mName).append("\"");
            out.write(buf.toString().getBytes());
        }
    }



}
