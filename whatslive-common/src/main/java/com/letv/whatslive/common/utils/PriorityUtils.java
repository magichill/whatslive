package com.letv.whatslive.common.utils;

/**
 * 优先级工具类，用于根据直播录播的状态生成优先级
 * Created by wangjian7 on 2015/10/29.
 */
public class PriorityUtils {

    private static final int LOAD_FACTOR = 100;

    /**
     * 根据pType、是否优先、开始时间计算优先级的值
     * @param pType
     * @param first
     * @param startTime
     * @return
     */
    public static Long getPriority(int pType, boolean first, long startTime){
        if(first){
            startTime = System.currentTimeMillis();
        }
        return getRealFactor(pType)+startTime/1000;
    }

    /**
     * 根据pType，priority判断是否是优先视频
     * @param pType
     * @param priority
     * @return
     */
    public static boolean isFirst(int pType, long startTime, long priority){
        return priority>(startTime/1000+getRealFactor(pType))?true:false;
    }

    /**
     * 根据直播的状态转换修改优先级
     * @param lastPType 上一个状态
     * @param nowPType 当前状态
     * @param lastPriority 上一个优先级
     * @return
     */
    public static Long changePriorityByPType(int lastPType, int nowPType, long lastPriority){
        return lastPriority + (getRealFactor(nowPType) - getRealFactor(lastPType));
    }

    private static Long getRealFactor(int pType){
        int factor = LOAD_FACTOR;
        if(pType==1){
            factor = LOAD_FACTOR - 10;
        }else if(pType==2){
            factor = LOAD_FACTOR - 40;
        }else if(pType==3){
            factor = LOAD_FACTOR - 70;
        }else{
            factor = 0;
        }
        return factor*1000000000L;
    }

}
