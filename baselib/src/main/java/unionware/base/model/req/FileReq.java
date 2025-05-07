package unionware.base.model.req;

import java.io.Serializable;

public class FileReq implements Serializable {
    private String fileId;

    public FileReq(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
