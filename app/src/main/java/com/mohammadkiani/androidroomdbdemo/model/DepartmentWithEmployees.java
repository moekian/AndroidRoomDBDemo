package com.mohammadkiani.androidroomdbdemo.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DepartmentWithEmployees {
    @Embedded public Department department;
    @Relation(
            parentColumn = "dept_id",
            entityColumn = "department_id"
    )
    public List<Employee> employeeList;

    public DepartmentWithEmployees(Department department, List<Employee> employeeList) {
        this.department = department;
        this.employeeList = employeeList;
    }

    public Department getDepartment() {
        return department;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }
}
