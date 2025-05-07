package com.unionware.wms.model.bean;

/**
 * @Author : pangming
 * @Time : On 2024/7/18 14:15
 * @Description : DefaultInfoBean 默认值弹窗控制
 */

public class DefaultInfoBean {
    private boolean isDefault;//默认值保存至下次作业
    private String popControl;//"1":仅异常时弹出 "2":固定弹出
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getPopControl() {
        return popControl;
    }

    public void setPopControl(String popControl) {
        this.popControl = popControl;
    }


}
