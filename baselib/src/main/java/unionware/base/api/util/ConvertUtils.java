package unionware.base.api.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import unionware.base.model.bean.BarcodeBean;
import unionware.base.model.bean.BillBean;
import unionware.base.model.bean.CommonListBean;
import unionware.base.model.bean.EntityBean;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.bean.ViewBean;
import unionware.base.model.resp.AnalysisResp;

public class ConvertUtils {
    /**
     * data 加载数据 显示对应的 view 值 显示
     * data 里面的数据现在的是
     *
     * @param views
     * @param data
     * @return
     */
    public static List<BillBean> convertViewToList(List<ViewBean> views, List<Map<String, Object>> data) {
        // options 获取需要显示的View
        Map<String, String> options = views.stream()
                .filter(ViewBean::isVisible)
                .collect(Collectors.toMap(ViewBean::getKey, ViewBean::getName));
        List<BillBean> billBeans = new ArrayList<>();
        for (Map<String, Object> map : data) {
            List<CommonListBean> list = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (options.containsKey(key)) {
                    String val = "";
                    if (entry.getValue() != null) {
                        Object value = entry.getValue();
                        if (value == null) {
                            val = "";
                        } else if (value instanceof Number) {
                            val = new BigDecimal(String.valueOf(value)).stripTrailingZeros().toPlainString();
                        } else {
                            val = value.toString();
                        }
                    }
                    if ("code".equals(key)) continue;
                    list.add(new CommonListBean(key, options.get(key), val));
                }
            }
            BillBean bean = null;
            if (map.containsKey("code")) {
                bean = new BillBean(objToStrInt(map.get("id")),
                        objToString(map.get("code")), list);
                if (options.containsKey("code")) {
                    bean.setCodeName(objToString(options.get("code")));
                }
                billBeans.add(bean);
            } else if (map.containsKey("id")) {
                bean = new BillBean(objToStrInt(map.get("id")), list);
                if (options.containsKey("id")) {
                    bean.setCodeName(objToString(options.get("id")));
                } else {
                    bean.setCodeName("编号");
                }
                billBeans.add(bean);
            } else if (map.containsKey("ID")) {
                bean = new BillBean(objToStrInt(map.get("ID")), list);
                if (options.containsKey("ID")) {
                    bean.setCodeName(objToString(options.get("ID")));
                } else {
                    bean.setCodeName("编号");
                }
                billBeans.add(bean);
            } else {
                bean = new BillBean(list);
                billBeans.add(bean);
            }
            if (map.containsKey("barCode")) {
                bean.setBarcode(objToString(map.get("barCode")));
            }
            if (map.containsKey("materialId")) {
                bean.setMaterialId(objToStrInt(map.get("materialId")));
            }
            if (map.containsKey("planId")) {
                bean.setPlanId(objToStrInt(map.get("planId")));
            }
            if (map.containsKey("FPlanId")) {
                bean.setPlanId(objToString(map.get("FPlanId")));
            }

            if (map.containsKey("planNo")) {
                bean.setPlanNo(objToString(map.get("planNo")));
            }
            if (map.containsKey("FPlanNo")) {
                bean.setPlanNo(objToString(map.get("FPlanNo")));
            }

            if (map.containsKey("taskStatus")) {
                bean.setTaskStatus(objToString(map.get("taskStatus")));
            }
            if (map.containsKey("jobId")) {
                bean.setJobId(objToString(map.get("jobId")));
            }
            if (map.containsKey("formId")) {
                bean.setFormId(objToString(map.get("formId")));
            }
            if (map.containsKey("LinkId")) {
                bean.setLinkId(objToString(map.get("LinkId")));
            }
            if (map.containsKey("FBoxCode")) {
                bean.setFBoxCode(objToString(map.get("FBoxCode")));
            }
            if (map.containsKey("creatorId")) {
                bean.setCreatorId(new BigDecimal(map.get("creatorId") == null ? "0" : map.get("creatorId").toString()).stripTrailingZeros().longValue());
            }
            bean.setDataMap(map);
        }
        return billBeans;
    }

    public static String objToString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    public static String objToStrInt(Object obj) {
        String str = objToString(obj);
        try {
            str = new BigDecimal(str).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static List<BillBean> convertViewToListNoCode(List<ViewBean> views, List<Map<String, Object>> data) {
        // options 获取需要显示的View
        Map<String, String> options = views.stream()
                .filter(ViewBean::isVisible)
                .collect(Collectors.toMap(ViewBean::getKey, ViewBean::getName));
        List<BillBean> billBeans = new ArrayList<>();
        for (Map<String, Object> map : data) {
            List<CommonListBean> list = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (options.containsKey(key)) {
                    String val = "";
                    if (entry.getValue() != null) {
                        Object value = entry.getValue();
                        if (value == null) {
                            val = "";
                        } else if (value instanceof Number) {
                            val = new BigDecimal(String.valueOf(value)).stripTrailingZeros().toPlainString();
                        } else {
                            val = value.toString();
                        }
                    }
                    if ("code".equals(key)) continue;
                    list.add(new CommonListBean(key, options.get(key), val));
                }
            }
            BillBean bean = new BillBean(list);
            billBeans.add(bean);
            bean.setDataMap(map);
        }
        return billBeans;
    }


    /**
     * 转化为分录列表
     *
     * @param views
     * @param data
     * @return
     */
    public static List<BillBean> convertViewToRowsList(List<ViewBean> views, List<Map<String, Object>> data) {
        Map<String, String> options = views.stream()
                .filter(ViewBean::isVisible)
                .collect(Collectors.toMap(ViewBean::getKey, ViewBean::getName));
        List<BillBean> billBeans = new ArrayList<>();
        for (Map<String, Object> map : data) {
            List<CommonListBean> list = new ArrayList<>();
            for (Map.Entry entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String val = null == entry.getValue() ? "" : entry.getValue().toString();
                if (!("code".equals(key) || "materialName".equals(key) || "materialNumber".equals(key) || "materialSpec".equals(key)))
                    continue;
                if (options.containsKey(key)) {
                    list.add(new CommonListBean(options.get(key), val));
                }
            }
            BillBean bean = null;
            if (map.containsKey("id")) {
                bean = new BillBean(objToStrInt(map.get("id")), list);
                billBeans.add(bean);
            } else {
                bean = new BillBean(list);
                billBeans.add(bean);
            }
            bean.setDataMap(map);
        }
        return billBeans;
    }

    /**
     * 多个 data 分成list
     * @param views
     * @param data
     * @return
     */
    public static List<List<CommonListBean>> convertMapToListCom(List<ViewBean> views, List<Map<String, Object>> data) {
        List<List<CommonListBean>> list = new ArrayList<>();
        Map<String, String> options = views.stream()
                .filter(ViewBean::isVisible)
                .collect(Collectors.toMap(ViewBean::getKey, ViewBean::getName));

        for (Map<String, Object> map : data) {
            List<CommonListBean> commonListBeanList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String val = null == entry.getValue() ? "" : entry.getValue().toString();
                if (key.contains("qty")) {
                    val = new BigDecimal(val).stripTrailingZeros().toPlainString();
                }
                if (options.containsKey(key)) {
                    commonListBeanList.add(new CommonListBean(options.get(key), val));
                }
            }
            list.add(commonListBeanList);
        }
        return list;
    }

    public static List<CommonListBean> convertMapToList(List<ViewBean> views, List<Map<String, Object>> data) {
        List<CommonListBean> list = new ArrayList<>();
        Map<String, String> options = views.stream()
                .filter(ViewBean::isVisible)
                .collect(Collectors.toMap(ViewBean::getKey, ViewBean::getName));

        for (Map<String, Object> map : data) {
            for (Map.Entry entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String val = null == entry.getValue() ? "" : entry.getValue().toString();
                if (key.contains("qty")) {
                    val = new BigDecimal(val).stripTrailingZeros().toPlainString();
                }
                if (options.containsKey(key)) {
                    list.add(new CommonListBean(options.get(key), val));
                }
            }
        }
        return list;
    }

    public static List<CommonListBean> convertMapToList(List<ViewBean> views, Map<String, Object> map) {
        List<CommonListBean> list = new ArrayList<>();
        Map<String, String> options = views.stream()
                .filter(ViewBean::isVisible)
                .collect(Collectors.toMap(ViewBean::getKey, ViewBean::getName));
        for (Map.Entry entry : map.entrySet()) {
            String key = (String) entry.getKey();
            String val = null == entry.getValue() ? "" : entry.getValue().toString();
            if (options.containsKey(key)) {
                list.add(new CommonListBean(options.get(key), val));
            }

        }
        return list;
    }


    public static List<List<EntityBean>> convertMapToEntityList
            (List<EntityBean> origData, AnalysisResp analysisResp) {
        List<List<EntityBean>> list = new ArrayList<>();
        for (int i = 0; i < analysisResp.getFEntity().size(); i++) {
            Map<String, BarcodeBean> entity = analysisResp.getFEntity().get(i);
            Map<String, BarcodeBean> billHead = analysisResp.getFBillHead().get(0);
            List<EntityBean> innerList = new ArrayList<>();
            for (EntityBean model : origData) {
                // 过滤掉 条码、容器条码字段
                if (!model.isEdit() && ("FLPN".equals(model.getProperty().getKey()) || "FContainer".equals(model.getProperty().getKey())))
                    continue;
                EntityBean entityBean = new EntityBean(new PropertyBean(model.getProperty().getKey(), model.getProperty().getName(), model.getProperty().getParentId()));
                entityBean.setIndex(i);
                String key = model.getProperty().getKey();
                buildEntityItem(entity.containsKey(key) ? entity : billHead, model, entityBean, key);
                innerList.add(entityBean);
            }
            list.add(innerList);
        }
        return list;
    }

    private static void buildEntityItem(Map<String, BarcodeBean> entity, EntityBean
            model, EntityBean entityBean, String key) {
        if (!entity.containsKey(key)) return;
        String type = model.getProperty().getType(); // 字段类型
        String val = "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? entity.get(key).getNumber() : null == entity.get(key).getValue() ? "" : entity.get(key).getValue();
        entityBean.setValue(val);
        entityBean.setKey(key);
        entityBean.setType(model.getProperty().getType());
        entityBean.setTag(model.getProperty().getTag());
        //逐条展示相应数据并支持修改其中定义为可编辑的单据体字段
        entityBean.setEdit("FEntity".equals(model.getProperty().getEntity()) && model.isEdit());
        entityBean.setEnable("FEntity".equals(model.getProperty().getEntity()) && entity.get(key).isEnabled());
    }


    public static List<EntityBean> convertMapToDisplayList
            (List<EntityBean> origData, AnalysisResp analysisResp) {
        List<EntityBean> list = new ArrayList<>();
        for (int i = 0; i < origData.size(); i++) {
            EntityBean model = origData.get(i);
            EntityBean test = new EntityBean(new PropertyBean(model.getProperty().getKey(), model.getProperty().getName()));
            String key = model.getProperty().getKey();
            String type = model.getProperty().getType(); // 字段类型
            Map<String, BarcodeBean> map = "FBillHead".equals(model.getProperty().getEntity()) ? analysisResp.getFBillHead().get(0) : analysisResp.getFEntity().get(0);
            if (map.containsKey(key)) {
                String val = "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
                test.setValue(val);
                list.add(test);
            }
        }
        return list;
    }

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
}

