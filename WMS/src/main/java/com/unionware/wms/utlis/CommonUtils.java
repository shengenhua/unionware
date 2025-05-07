package com.unionware.wms.utlis;


import java.util.ArrayList;
import java.util.List;

import unionware.base.model.bean.PropertyBean;

public class CommonUtils {
    public static boolean isInteger(String num) {
        return num.matches("^-?\\d+$");
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static List<PropertyBean> deepClonePropertyBeanList(List<PropertyBean> originalList) {
        List<PropertyBean> clonedList = new ArrayList<>();
        for (PropertyBean element : originalList) {
            PropertyBean clonedElement = element.clone();
            clonedList.add(clonedElement);
        }
        return clonedList;
    }
}
