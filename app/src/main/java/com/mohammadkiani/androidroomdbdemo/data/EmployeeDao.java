package com.mohammadkiani.androidroomdbdemo.data;

// DAO - provides API for reading and writing data to/from room db
// tak care of crud

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mohammadkiani.androidroomdbdemo.model.Employee;

import java.util.List;

@Dao
public interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Employee employee);

    @Query("DELETE FROM employee_table")
    void deleteAll();

    @Query("SELECT * FROM employee_table ORDER BY name ASC")
    LiveData<List<Employee>> getAllEmployee();

    @Query("SELECT * FROM employee_table WHERE id == :id")
    LiveData<Employee> getEmployee(int id);

    @Update
    void update(Employee employee);

    @Delete
    void delete(Employee employee);
}
