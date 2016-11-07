package com.letv.whatslive.common.httpclient;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by zoran on 14-9-3.
 */
public class HttpClientUtil {

    private static final String defaultCharset = "utf-8";

    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static  HttpFetchResult requestGet(final String URL) throws Exception {
        final HttpGet req = new HttpGet(URL);
        return doRequest(URL, req);
    }

    public  static HttpFetchResult requestPost(final String URL, final List<BasicNameValuePair> nvps) throws Exception {
        HttpPost req = new HttpPost(URL);
        req.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
        return doRequest(URL, req);
    }

    public  static HttpFetchResult requestPost(final String URL, final List<BasicNameValuePair> nvps,Map<String,String> headers) throws Exception {
        HttpPost req = new HttpPost(URL);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                req.addHeader(entry.getKey(), entry.getValue());
            }
        }
        req.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
        return doRequest(URL, req);
    }
    public  static HttpFetchResult requestPost(final String URL, final String params) throws Exception {
        HttpPost req = new HttpPost(URL);
        req.setEntity(new StringEntity(params,"UTF-8"));
        return doRequest(URL, req);
    }

    public static HttpFetchResult requestPostWithInputStream(String URL, byte[] input) throws Exception {
        HttpPost req = new HttpPost(URL);
        InputStreamEntity reqEntity = new InputStreamEntity(
                    new ByteArrayInputStream(input), -1, ContentType.APPLICATION_OCTET_STREAM);
             reqEntity.setChunked(false);
        req.setEntity(reqEntity);
        return doRequest(URL, req);
    }

    private static  HttpFetchResult doRequest(String url, HttpUriRequest req) throws Exception {
        StatusLine status = null;
        HttpEntity entity = null;
        try {
            try {
                HttpContext localContext = new BasicHttpContext();
                HttpResponse rsp = HttpConnectionManager.getHttpClient().execute(req, localContext);
                entity = rsp.getEntity();
                status = rsp.getStatusLine();
                if (status.getStatusCode() != HttpStatus.SC_OK) {
                    req.abort();
                }
                if (entity != null) {
                    return new HttpFetchResult(rsp, defaultCharset);
                } else {
                    req.abort();
                }
            } catch (IOException ex) {
                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                ex.printStackTrace();
                req.abort();
                throw ex;
            } catch (RuntimeException e) {
                req.abort();
                e.printStackTrace();
                throw  e;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } finally {
            try {
                if (req != null) {
                    req.abort();
                    HttpConnectionManager.releaseConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}