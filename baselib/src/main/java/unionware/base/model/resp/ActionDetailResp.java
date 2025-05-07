package unionware.base.model.resp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author : pangming
 * @Time : On 2024/7/26 17:01
 * @Description : ActionDetailResp
 */

public class ActionDetailResp implements Serializable {
    private List<Map<String, Object>> items;
    private Map<String, Object> params;
    private String filter;
    private String schemaId;
    private String formId;
    private String server;//server=remote，使用远端打印服务 server=local，使用PDA便携式打印,server=auto，由系统参数决定
    private int type;
    private String message;
    /**
     * 图片名字
     */
    @SerializedName(value = "name", alternate = {"Name"})
    private String name;
    /**
     * 图片URL
     */
    @SerializedName(value = "uri", alternate = {"Uri"})
    private String uri;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public List<Map<String, Object>> getItems() {
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                for (String key : items.get(i).keySet()) {
                    if (key.equals("id") && items.get(i).get("id") != null && (items.get(i).get("id") instanceof Double || items.get(i).get("id") instanceof Integer)) {
                        if (items.get(i).get("id").toString().contains(".0")) {
                            items.get(i).put("id", Integer.valueOf(items.get(i).get("id").toString().replace(".0", "")));
                        }
                    }
                }

            }
        }
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public Map<String, Object> getParams() {
        for (String key : params.keySet()) {
            if (key.equals("count") && params.get("count") != null && (params.get("count") instanceof Double || params.get("count") instanceof Integer)) {
                if (params.get("count").toString().contains(".0")) {
                    params.put("count", Integer.valueOf(params.get("count").toString().replace(".0", "")));
                }
            }
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }
}
