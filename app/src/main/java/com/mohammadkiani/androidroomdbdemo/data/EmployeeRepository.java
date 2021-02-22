package com.mohammadkiani.androidroomdbdemo.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.util.EmployeeRoomDatabase;

import java.util.List;

public class EmployeeRepository {
    private EmployeeDao employeeDao;
    private LiveData<List<Employee>> allEmployees;

    public EmployeeRepository(Application application) {
        EmployeeRoomDatabase db = EmployeeRoomDatabase.getInstance(application);
        employeeDao = db.employeeDao();
        allEmployees = employeeDao.getAllEmployees();
    }

    public LiveData<List<Employee>> getAllEmployees() {
        return allEmployees;
    }

    public LiveData<Employee> getEmployee(int id) {return employeeDao.getEmployee(id);}

    public void insert(Employee employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.insert(employee));
    }

    public void update(Employee employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.update(employee));
    }

    public void delete(Employee employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.delete(employee));
    }
}














