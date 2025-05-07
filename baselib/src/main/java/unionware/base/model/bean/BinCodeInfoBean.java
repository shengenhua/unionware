package unionware.base.model.bean;

import java.util.List;
import java.util.Map;

//仓库扫码用
public class BinCodeInfoBean {
//    private Map<String,Object> options;
//    private List<Map<String,Object>> view;
//    private List<DataInfo> data;



    //   public static class DataInfo{
    private String code;
    private Stock Stock;
    private List<Map<String,Map<String,Object>>> StockLoc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Stock getStock() {
        return Stock;
    }

    public void setStock(Stock stock) {
        this.Stock = stock;
    }

    public List<Map<String, Map<String, Object>>> getStockLoc() {
        return StockLoc;
    }

    public void setStockLoc(List<Map<String, Map<String, Object>>> stockLoc) {
        StockLoc = stockLoc;
    }
    //   }
    public static class Stock{
        private String code;
        private String name;
        private String id;

        private List<Map<String,Object>> FStockFlexItem;
        private boolean isLocManaged;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public List<Map<String, Object>> getFStockFlexItem() {
            return FStockFlexItem;
        }

        public void setFStockFlexItem(List<Map<String, Object>> FStockFlexItem) {
            this.FStockFlexItem = FStockFlexItem;
        }

        public boolean isLocManaged() {
            return isLocManaged;
        }

        public void setLocManaged(boolean locManaged) {
            isLocManaged = locManaged;
        }
    }
}
