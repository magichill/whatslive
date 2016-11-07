package com.letv.whatslive.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-8-13.
 */
@Getter
@Setter
public class RemindDTO {

    private Long programId;
    private String pName;
    private String startTime;
    private String pushUrl;
    private String streamId;
}
