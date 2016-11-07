package com.letv.whatslive.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-10-23.
 */
@Setter
@Getter
public class StartVarDTO {

    private Long startTime;
    private Long endTime;
    private Integer showSeconds;
    private String pic;
    private Long updateTime;

    public StartVarDTO(){

    }

    public StartVarDTO(boolean isDefault){
        this.setStartTime(1445601865077l);
        this.setEndTime(1447701865077l);
        this.setShowSeconds(3);
        this.setPic("http://g3.letv.cn/vod/v1/MTE2LzMyLzQ1L2xldHYtaXR2Mi8wL3N0YXRpY2ZpbGUvaW1ncy8yMDE1MDMwMy9mODhiNGExYzMzOWQ3ZDUyNzQ3YjIwYThmNzlmMzFmZC5qcGc=?b=123456&platid=5&splatid=500");
        this.setUpdateTime(1445601865078l);
    }

}
