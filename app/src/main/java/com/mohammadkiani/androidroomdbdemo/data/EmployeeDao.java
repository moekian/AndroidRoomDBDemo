package com.mohammadkiani.androidroomdbdemo.data;

// DAO - provides API for reading and writing data to/from room db
// tak care of crud

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.DepartmentWithEmployees;
import com.mohammadkiani.androidroomdbdemo.model.Employee;

import java.util.List;

@Dao
public abstract class EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Employee employee);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(Department department);

    @Query("DELETE FROM employee_table")
    public abstract void deleteAll();

    @Query("SELECT * FROM employee_table ORDER BY name ASC")
    public abstract LiveData<List<Employee>> getAllEmployees();

    @Query("SELECT * FROM employee_table WHERE id == :id")
    public abstract LiveData<Employee> getEmployee(long id);

    @Query("SELECT * FROM employee_table WHERE department_name = :department")
    public abstract LiveData<List<Employee>> getEmployeesInDepartment(String department);

    @Update
    public abstract void update(Employee employee);

    @Transaction
    public void updateEmployeeInDepartment(Department department, Employee employee) {
        update(department);
        update(employee);
    }

    @Query("SELECT * FROM department_table WHERE name = :name")
    public abstract Department getDepartment(String name);

    @Query("SELECT * FROM department_table")
    public abstract LiveData<List<Department>> getAllDepartments();

    @Delete
    public abstract void delete(Employee employee);

    @Update
    public abstract void update(Department department);

    @Delete
    public abstract void delete(Department department);

    @Transaction
    @Query("SELECT * FROM department_table")
    public abstract LiveData<List<DepartmentWithEmployees>> getDepartmentsWithEmployees();

    @Transaction
    public void insert(Department department, Employee employee) {
        final long departmentId = insert(department);
        employee.setDepartmentId(departmentId);
        insert(employee);
    }
}
