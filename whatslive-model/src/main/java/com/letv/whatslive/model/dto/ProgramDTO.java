package com.letv.whatslive.model.dto;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.convert.UserConvert;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by gaoshan on 15-7-13.
 */
@Getter
@Setter
public class ProgramDTO {

    private long id;
    private String pName;
    private String pDesc;
    private int pType;  //节目类型 1:直播 2:录播
    private String picture;
    private UserDTO user;  //发布人
    private String streamId;
    private String startTime;
    private String playTime;
    private long watchNum;
    private long likeNum;
    private long commentNum;
    private Integer isSelf;  //判断是否是当前用户发起
    private Integer isReport; //直播被当前用户举报状态 1：已举报 0：未举报
    private Integer status;  //0：未预约 1：已预约
    private LiveInfo liveInfo;  //轮播台信息
    private ReplayInfo replayInfo; //录播节目信息
    private Integer from;
    private Integer isFocus;  //当前用户对直播发布人的关系
    private String shareUrl;  //直播分享地址
    private Integer ugcStatus;  //转码状态  -1正在转码，1转码完成
    private String actionLogUrl; //回放日志文件地址


    public ProgramDTO(Program program,UserDTO user){
        this.setUser(user);
        this.setPName(program.getPName());
        this.setLiveInfo(new LiveInfo(ObjectUtils.toString(program.getLiveId())));
        this.setPDesc(program.getPDesc());
        this.setPType(program.getPType());
        this.setPicture(program.getPicture());
        this.setStartTime(DateUtils.long2StrDate(program.getStartTime()));
        this.setPlayTime(DateUtils.continueSecTime(program.getStartTime()));
        this.setLikeNum(program.getLikeNum());
        this.setWatchNum(program.getWatchNum());
        this.setCommentNum(program.getCommentNum());
        this.setShareUrl(ProgramConstants.SHARE_URL+program.getId());
        this.setFrom(program.getFrom());
        this.setUgcStatus(program.getStatus());
    }

    public ProgramDTO(Program program){
        if(program.getPType()==4){
            this.setPName(program.getPName());
            this.setPicture(program.getPicture());
            this.setPType(program.getPType());
        }else {
            this.setId(program.getId());
            User user = UserConvert.castDBObjectToUser(program.getUserRef().fetch());
            if (user != null) {
                UserDTO dto = new UserDTO(user);
                this.setUser(dto);
            }
            this.setPName(program.getPName());
            this.setPDesc(program.getPDesc());
            this.setPType(program.getPType());
            this.setPicture(program.getPicture());
            if (StringUtils.isNotBlank(program.getLiveId())) {
                this.setLiveInfo(new LiveInfo(ObjectUtils.toString(program.getLiveId())));
            } else if (program.getPType() == 3) {
                if (StringUtils.isNotBlank(program.getVuid())) {
                    this.setReplayInfo(new ReplayInfo(ProgramConstants.UUID, program.getVuid()));
                }
            } else {
                this.setStreamId(program.getActivityId());
            }
            this.setStartTime(ObjectUtils.toString(program.getStartTime()));
            if (program.getPType() == 1 || program.getPType() == 2) {
                this.setPlayTime(DateUtils.continueSecTime(program.getStartTime()));
            }
            if (program.getPType() == 3) {
                this.setPlayTime(DateUtils.continueSecTime(program.getStartTime(), program.getEndTime()));
                this.setActionLogUrl(program.getActionLogUrl());
            }
            this.setShareUrl(ProgramConstants.SHARE_URL + program.getId());
            this.setLikeNum(program.getLikeNum());
            this.setWatchNum(program.getWatchNum());
            this.setCommentNum(program.getCommentNum());
            this.setFrom(program.getFrom());
            this.setUgcStatus(program.getStatus());
        }
    }

    @Getter
    @Setter
    class LiveInfo {
        private String liveId;
        private Integer isLetv;
        private Integer isHls;

        public LiveInfo(String liveId){
            this.setIsHls(1);
            this.setIsLetv(1);
            this.setLiveId(liveId);
        }
    }

    @Getter
    @Setter
    class ReplayInfo {
        private String uuid;
        private String vuid;

        public ReplayInfo(String uuid,String vuid){
            this.setUuid(uuid);
            this.setVuid(vuid);
        }
    }

}
