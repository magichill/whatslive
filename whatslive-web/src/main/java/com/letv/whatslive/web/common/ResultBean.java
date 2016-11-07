package com.letv.whatslive.web.common;

/**
 * @author pangjie1@letv.com
 * @date 14-11-7 下午5:17
 * @Description
 */
public class ResultBean {

    /**
     * 成功失败标记 true--成功 false--失败
     */
    private boolean flag;

    /**
     * 错误消息 flag=false时，包含错误消息
     */
    private String msg;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean() {
    }

    public ResultBean(boolean flag) {
        this.flag = flag;
    }

    public static ResultBean getTrueInstance() {
        return new ResultBean(true);
    }

    public ResultBean setFalseAndMsg(String msg) {
        this.flag = false;
        this.msg = msg;
        return this;
    }
}

