package com.mohammadkiani.androidroomdbdemo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

// this is our entity in Room db
@Entity(
        tableName = "employee_table",
        foreignKeys = @ForeignKey(entity = Department.class,
        parentColumns = "dept_id",
        childColumns = "department_id",
        onDelete = CASCADE)
)
public class Employee {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "department_id")
    private long departmentId;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "department")
    private String departmentName;

    @ColumnInfo(name = "joining_date")
    private String joiningDate;

    @ColumnInfo(name = "salary")
    private double salary;

    @Ignore
    public Employee() {
    }

    public Employee(@NonNull String name, String departmentName, String joiningDate, double salary) {
        this.name = name;
        this.departmentName = departmentName;
        this.joiningDate = joiningDate;
        this.salary = salary;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }
}
