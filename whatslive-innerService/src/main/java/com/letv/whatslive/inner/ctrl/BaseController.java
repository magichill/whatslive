package com.letv.whatslive.inner.ctrl;

import com.letv.psp.swift.httpd.service.ContentTypeResolver;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class BaseController implements ContentTypeResolver {

    protected final Logger logger = Logger.getLogger(this.getClass());

    private static final Map<String, String> contentTypeMapper = new HashMap<String, String>();

    @Override
    public String getContentType(String method) {
        return contentTypeMapper.get(method);
    }

    @Override
    public String getDefaultContentType() {
        return "application/json; charset=UTF-8";
    }

}
