package com.letv.whatslive.web.service.storage.cdn;

import com.letv.whatslive.web.constant.ServiceConstants;
import com.letv.whatslive.web.service.storage.AbstractUploadService;
import com.letv.whatslive.web.util.http.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

/**
 * 乐视CDN文件上传实现类
 * Created by wangjian7 on 2015/8/6.
 */
@Component
public class LetvCdnService extends AbstractUploadService{

    private static final Logger logger = LoggerFactory.getLogger(LetvCdnService.class);

    @Override
    public String getKey(String prefix, String fileMd5, String busiKey) {
        return prefix.replaceAll("/","") + "_" + busiKey + "_" + fileMd5;
    }

    @Override
    public boolean uploadFile(String md5, Long fileSize, String localFileUrl, String serverFileUrl, String key) {

        try {
            if (StringUtils.isBlank(serverFileUrl) || StringUtils.isBlank(key)
                    || StringUtils.isBlank(serverFileUrl) || fileSize <=0) {
                logger.error("分发到CDN参数不正确，请检查");
                return false;
            }
            serverFileUrl = URLEncoder.encode(serverFileUrl, "UTF-8");
            String apiUrl = String.format(ServiceConstants.API_CDN_URL, md5,
                    fileSize, serverFileUrl, key);
            logger.info("分发到CDN接口，请求地址为：" + apiUrl);
            String result = HttpClientUtils.get(apiUrl, 2000, 2000, 1);
            logger.info("返回结果为：" + result);
            return true;
        } catch (Exception e) {
            logger.error("分发到CDN失败，请检查", e);
            return false;
        }
    }
}
