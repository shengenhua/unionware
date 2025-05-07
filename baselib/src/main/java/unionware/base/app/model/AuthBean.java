package unionware.base.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AuthBean implements Serializable {


    @SerializedName("Product")
    private ProductDTO product;
    @SerializedName("Customer")
    private CustomerDTO customer;
    @SerializedName("Params")
    private ParamsDTO params;
    @SerializedName("Machines")
    private List<String> machines;

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public ParamsDTO getParams() {
        return params;
    }

    public void setParams(ParamsDTO params) {
        this.params = params;
    }

    public List<String> getMachines() {
        return machines;
    }

    public void setMachines(List<String> machines) {
        this.machines = machines;
    }

    public static class ProductDTO {
        @SerializedName("Id")
        private String id;
        @SerializedName("Code")
        private String code;
        @SerializedName("Name")
        private List<NameDTO> name;
        @SerializedName("Tactics")
        private List<TacticsDTO> tactics;

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

        public List<NameDTO> getName() {
            return name;
        }

        public void setName(List<NameDTO> name) {
            this.name = name;
        }

        public List<TacticsDTO> getTactics() {
            return tactics;
        }

        public void setTactics(List<TacticsDTO> tactics) {
            this.tactics = tactics;
        }

        public static class NameDTO {
            @SerializedName("Key")
            private Integer key;
            @SerializedName("Value")
            private String value;

            public Integer getKey() {
                return key;
            }

            public void setKey(Integer key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class TacticsDTO {
            @SerializedName("Id")
            private String id;
            @SerializedName("Param")
            private String param;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getParam() {
                return param;
            }

            public void setParam(String param) {
                this.param = param;
            }
        }
    }

    public static class CustomerDTO {
        @SerializedName("Id")
        private String id;
        @SerializedName("Code")
        private String code;
        @SerializedName("Name")
        private List<NameDTO> name;

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

        public List<NameDTO> getName() {
            return name;
        }

        public void setName(List<NameDTO> name) {
            this.name = name;
        }

        public static class NameDTO {
            @SerializedName("Key")
            private Integer key;
            @SerializedName("Value")
            private String value;

            public Integer getKey() {
                return key;
            }

            public void setKey(Integer key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }

    public static class ParamsDTO {
        @SerializedName("Requester")
        private String requester;
        @SerializedName("RequestTime")
        private String requestTime;
        @SerializedName("Creator")
        private String creator;
        @SerializedName("CreateTime")
        private String createTime;

        public String getRequester() {
            return requester;
        }

        public void setRequester(String requester) {
            this.requester = requester;
        }

        public String getRequestTime() {
            return requestTime;
        }

        public void setRequestTime(String requestTime) {
            this.requestTime = requestTime;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
