package unionware.base.model.resp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FileResp : Serializable {
    var data: String? = null

    @SerializedName("FileId")
    var id: String? = null

    @SerializedName("Message")
    var message: String? = null
}