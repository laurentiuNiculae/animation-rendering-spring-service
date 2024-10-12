package com.lniculae.animation_rendered_spring.executors;

public class TaskStatus {
    public enum StatusKind {
        Unknown,
        Started,
        Successful,
        Unsuccessful
    }
    
    StatusKind status;
    String errMsg;

    public TaskStatus(StatusKind status) {
        this.status = status;
    }

    public TaskStatus(StatusKind status, String errorMsg) {
        this.status = status;
        this.errMsg = errorMsg;
    }

    public StatusKind getStatus() {
        return status;
    }

    public String getError() {
        return errMsg;
    }

    public static TaskStatus UnknownTaskStatus = new TaskStatus(StatusKind.Unknown, "");
}
