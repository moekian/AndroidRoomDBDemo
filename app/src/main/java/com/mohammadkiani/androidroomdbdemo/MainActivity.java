package com.mohammadkiani.androidroomdbdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mohammadkiani.androidroomdbdemo.adapter.RecyclerViewAdapter;
import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnEmployeeClickListener{


    private static final String TAG = "MainActivity";
    public static final int ADD_EMPLOYEE_REQUEST_CODE = 1;
    public static final String EMPLOYEE_ID = "employee_id";


    // declaration of employeeViewModel
    private EmployeeViewModel employeeViewModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiating the employeeViewModel
        employeeViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(EmployeeViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeViewModel.getAllEmployees().observe(this, employees -> {
            // set adapter
            recyclerViewAdapter = new RecyclerViewAdapter(employees, this, this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            startActivityForResult(intent, ADD_EMPLOYEE_REQUEST_CODE);
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EMPLOYEE_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: " + data.getStringExtra(AddEmployeeActivity.NAME_REPLY));

            String name = data.getStringExtra(AddEmployeeActivity.NAME_REPLY);
            String salary = data.getStringExtra(AddEmployeeActivity.SALARY_REPLY);
            String department = data.getStringExtra(AddEmployeeActivity.DEPARTMENT_REPLY);
            // getting the current date
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
            String joiningDate = sdf.format(cal.getTime());

            Employee employee = new Employee(name, department, joiningDate, Double.parseDouble(salary));
            employeeViewModel.insert(employee);

        }
    }

    @Override
    public void onEmployeeClick(int position) {
        Log.d(TAG, "onEmployeeClick: " + position);
        Employee employee = employeeViewModel.getAllEmployees().getValue().get(position);
        Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
        intent.putExtra(EMPLOYEE_ID, employee.getId());
        startActivity(intent);
    }
}














