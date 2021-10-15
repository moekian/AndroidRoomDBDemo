package com.mohammadkiani.androidroomdbdemo.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.NO_ACTION;

// this is our entity in Room db
@Entity(
        tableName = "employee_table",
        foreignKeys = @ForeignKey(entity = Department.class,
        parentColumns = "name",
        childColumns = "department_name",
        onDelete = CASCADE,
        onUpdate = NO_ACTION)
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

    @ColumnInfo(name = "department_name")
    private String departmentName;

    @ColumnInfo(name = "joining_date")
    private String joiningDate;

    @ColumnInfo(name = "salary")
    private double salary;

    private String contract;

    @Ignore
    public Employee() {
    }

    public Employee(@NonNull String name, String departmentName, String joiningDate, double salary, String contract) {
        this.name = name;
        this.departmentName = departmentName;
        this.joiningDate = joiningDate;
        this.salary = salary;
        this.contract = contract;
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

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public enum Contract {
        FULL_TIME, PART_TIME
    }
}
