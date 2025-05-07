package unionware.base.model.req;

import java.io.Serializable;

public class TaskIdReq implements Serializable {
    private int taskId;
    private String pageId;
    private String command;
    private String[] Sponsors;

    public TaskIdReq(int taskId) {
        this.taskId = taskId;
    }
    public TaskIdReq(String command,String pageId) {
        this.command = command;
        this.pageId = pageId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    public String[] getSponsors() {
        return Sponsors;
    }

    public void setSponsors(String[] sponsors) {
        Sponsors = sponsors;
    }
}
