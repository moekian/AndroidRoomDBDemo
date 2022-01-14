package com.mohammadkiani.androidroomdbdemo.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * @author Mohammad Kiani */

public class DepartmentWithEmployees {
    @Embedded public Department department;
    @Relation(
            parentColumn = "name",
            entityColumn = "department_name"
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
    public int getEmployeeListSize() {return employeeList == null ? 0 : employeeList.size();}
}
