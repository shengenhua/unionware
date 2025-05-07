package unionware.base.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UploadReq {
    var data: DataBean? = null

    class DataBean(
        var fileName: String?,
        @field:SerializedName("SendByte") var imageByte: String,
        @field:SerializedName("IsLast") var isLast: Boolean
    ) : Serializable {
        @SerializedName("FileId")
        var fileId: String? = null
    }

}