package com.mohammadkiani.androidroomdbdemo.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.DepartmentWithEmployees;
import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.util.EmployeeRoomDatabase;

import java.util.List;

public class EmployeeRepository {
    private EmployeeDao employeeDao;
    private LiveData<List<Employee>> allEmployees;
    private LiveData<List<DepartmentWithEmployees>> departmentsWithEmployeesList;

    public EmployeeRepository(Application application) {
        EmployeeRoomDatabase db = EmployeeRoomDatabase.getInstance(application);
        employeeDao = db.employeeDao();
        allEmployees = employeeDao.getAllEmployees();
        departmentsWithEmployeesList = employeeDao.getDepartmentsWithEmployees();
    }

    public LiveData<List<Employee>> getAllEmployees() {
        return allEmployees;
    }

    public LiveData<Employee> getEmployee(int id) {return employeeDao.getEmployee(id);}

    public LiveData<List<DepartmentWithEmployees>> getDepartmentsWithEmployeesList() {return departmentsWithEmployeesList;}

    public void insert(Employee employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.insert(employee));
    }

    public void insert(Department department, Employee employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.insert(department, employee));
    }

    public long insert(Department department) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.insert(department));
        return department.getId();
    }

    public void update(Employee employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.update(employee));
    }

    public void delete(Employee employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> employeeDao.delete(employee));
    }
}














