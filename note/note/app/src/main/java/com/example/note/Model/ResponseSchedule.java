package com.example.note.Model;

import java.util.List;

public class ResponseSchedule {
    private Boolean status;
    private List<Schedule> schedules;

    public ResponseSchedule(Boolean status, List<Schedule> schedules) {
        this.status = status;
        this.schedules = schedules;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
