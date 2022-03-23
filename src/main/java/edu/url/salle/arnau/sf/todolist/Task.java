package edu.url.salle.arnau.sf.todolist;

import java.util.Date;

public class Task {
    private String sTitle;
    private Date dtDate;
    private boolean bCompleted = false;

    public Task(String title) {
        sTitle = title;
        dtDate = new Date();
    }

    public Task(String title, Date date, boolean completed) {
        sTitle = title;
        dtDate = date;
        bCompleted = completed;
    }

    public String getTitle() {
        return sTitle;
    }

    public Date getDate() {
        return dtDate;
    }

    public boolean isCompleted() {
        return bCompleted;
    }

    public void setCompleted(boolean completed) {
        this.bCompleted = completed;
    }
}
