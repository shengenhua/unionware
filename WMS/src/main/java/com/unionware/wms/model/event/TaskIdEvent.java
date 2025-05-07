package com.unionware.wms.model.event;

import java.io.Serializable;

public class TaskIdEvent implements Serializable {
    private String TaskId;

    public TaskIdEvent(String taskId) {
        TaskId = taskId;
    }

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }
}
