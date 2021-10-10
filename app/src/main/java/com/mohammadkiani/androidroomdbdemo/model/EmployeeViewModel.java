package com.mohammadkiani.androidroomdbdemo.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mohammadkiani.androidroomdbdemo.data.EmployeeRepository;

import java.util.List;

public class EmployeeViewModel extends AndroidViewModel {
    private EmployeeRepository repository;
    private final LiveData<List<Employee>> allEmployees;

    public EmployeeViewModel(@NonNull Application application) {
        super(application);

        repository = new EmployeeRepository(application);
        allEmployees = repository.getAllEmployees();
    }

    public LiveData<List<Employee>> getAllEmployees() {return allEmployees;}

    public LiveData<Employee> getEmployee(int id) {return repository.getEmployee(id);}

    public LiveData<List<DepartmentWithEmployees>> getDepartmentsWithEmployeesList() {return repository.getDepartmentsWithEmployeesList();}

    public void insert(Employee employee) {repository.insert(employee);}

    public void insert(Department department, Employee employee) {repository.insert(department, employee);}

    public void update(Employee employee) {repository.update(employee);}

    public void delete(Employee employee) {repository.delete(employee);}
}
