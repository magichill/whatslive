package com.letv.whatslive.web.service.user;

import com.letv.whatslive.model.Device;
import com.letv.whatslive.mongo.dao.DeviceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/8.
 */
@Component
public class DeviceService {

    @Autowired
    private DeviceDAO deviceDAO;

    /**
     *
     * 根据设备ID查询参数获取所有的设备列表
     * @param paramMap
     * @param start
     * @param limit
     * @return
     */
    public List<Device> getDeviceListByIds(Map paramMap, Integer start, Integer limit) {
        return deviceDAO.getDeviceListByParams(paramMap, start, limit);
    }

}
