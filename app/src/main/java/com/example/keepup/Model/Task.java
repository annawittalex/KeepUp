package com.example.keepup.Model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;


@SuppressLint("ParcelCreator")
public class Task implements Parcelable {

    private int id;
    private String taskName;
    private Date deadline;
    private int status;

    public Task() {
    }

    protected Task(Parcel in) {
        id = in.readInt();
        taskName = in.readString();
        deadline = new Date(in.readLong());
        status = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(taskName);
        dest.writeLong(deadline != null ? deadline.getTime() : -1);
        dest.writeInt(status);
    }
}

