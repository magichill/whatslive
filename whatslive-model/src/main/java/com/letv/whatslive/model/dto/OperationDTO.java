package com.letv.whatslive.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-9-23.
 */
@Setter
@Getter
public class OperationDTO {

    private Long operationId;
    private String title;
    private String tag;
    private String picture;
    private String url;
    private Integer type;

}
