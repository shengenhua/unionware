package unionware.base.model.resp;


import unionware.base.model.bean.ViewBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CommonListDataResp<T> implements Serializable {
    private List<T> data;

    private List<ViewBean> view;
    private Map<String, Object> options;

    public List<ViewBean> getView() {
        return view;
    }

    public void setView(List<ViewBean> view) {
        this.view = view;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }
}
