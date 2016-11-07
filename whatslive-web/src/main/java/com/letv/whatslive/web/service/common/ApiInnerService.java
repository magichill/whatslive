package com.letv.whatslive.web.service.common;

import com.letv.whatslive.web.service.storage.AbstractUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by wangjian7 on 2015/7/22.
 */
public class ApiInnerService {

    private static final Logger logger = LoggerFactory.getLogger(ApiInnerService.class);

    private AbstractUploadService abstractUploadService;

    public ApiInnerService(AbstractUploadService abstractUploadService){
        this.abstractUploadService = abstractUploadService;
    }

    public AbstractUploadService getAbstractUploadService() {
        return abstractUploadService;

    }

}
