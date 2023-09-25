package com.example.note.Model;

import java.util.List;

public class ResponseClass {

    private boolean status;
    private List<Course> courses;

    public ResponseClass(boolean status, List<Course> courses) {
        this.status = status;
        this.courses = courses;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
