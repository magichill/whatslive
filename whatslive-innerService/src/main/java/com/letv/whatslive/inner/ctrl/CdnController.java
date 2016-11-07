package com.letv.whatslive.inner.ctrl;

import com.letv.psp.swift.core.service.MessageContext;
import com.letv.whatslive.inner.Service.CdnPublicService;
import com.letv.whatslive.inner.constants.InnerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangjian7 on 2015/7/17.
 */
@Service("CdnController")
public class CdnController extends BaseController {

    @Autowired
    private CdnPublicService cdnPublicService;


    /**
     *
     * @param msgCtx
     * @return
     */
    public String cdncallback(MessageContext msgCtx) {

        if(!"200".equals(msgCtx.getParameter("status"))){
            logger.error("cdn status is not 200.");
            return "cdn status is not 200.";
        }else{
            String outkey = msgCtx.getParameter("outkey");
            String[] busiAry = outkey.split(InnerConstants.CDN_CODE_SEPARATOR);
            if (busiAry == null || busiAry.length < 2) {
                logger.info("outkey is not standard, it most like 'busType_busId_md5'.");
                return "outkey is not standard, it must like 'busType_busId_md5'.";
            }
            String busiKey = busiAry[0];
            if (InnerConstants.CDN_CODE_USER_PIC.equals(busiAry[0])) {
                //用户头像信息更新
                cdnPublicService.updateUserPic(busiAry[1], msgCtx.getParameter("storeurl"));
            }else if(InnerConstants.CDN_CODE_PROGRAM_PIC.equals(busiAry[0])) {
                //视频封面信息更新
                cdnPublicService.updateProgramPic(busiAry[1], msgCtx.getParameter("storeurl"));
            }else if(InnerConstants.CDN_CODE_ACTIVITY_PIC.equals(busiAry[0])){
                //活动封面信息更新
                cdnPublicService.updateActivityPic(busiAry[1], msgCtx.getParameter("storeurl"));
            }else if(InnerConstants.CDN_CODE_PROGRAM_REPLAY_LOG.equals(busiAry[0])){
                //直播回放日志更新
                cdnPublicService.updateProgramReplayLog(busiAry[1], msgCtx.getParameter("storeurl"));
            }
            logger.info(outkey+" callback success.");
        }
        return "callback success.";
    }
}
