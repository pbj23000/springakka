package cj.mf.beans;

/**
 * Created by cjm on 6/13/15.
 */
public class Task {

    private String payload;

    private Integer priority;

    public Task(String payload, Integer priority) {
        this.payload = payload;
        this.priority = priority;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
