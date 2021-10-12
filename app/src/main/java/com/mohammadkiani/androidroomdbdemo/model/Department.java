package com.mohammadkiani.androidroomdbdemo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "department_table")
public class Department {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "dept_id")
    private long id;

    @NonNull private String name;
    private String location;

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }

    @Ignore
    public Department() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
