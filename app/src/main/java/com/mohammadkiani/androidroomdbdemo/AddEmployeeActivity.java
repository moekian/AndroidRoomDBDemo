package com.mohammadkiani.androidroomdbdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

import java.util.Arrays;

public class AddEmployeeActivity extends AppCompatActivity {

    public static final String NAME_REPLY = "name_reply";
    public static final String SALARY_REPLY = "salary_reply";
    public static final String DEPARTMENT_REPLY = "department_reply";
    public static final String CONTRACT_REPLY = "contract_reply";

    private EditText etName, etSalary;
    private Spinner spinnerDept, spinnerContract;

    private boolean isEditing = false;
    private int employeeId = 0;
    private Employee employeeTobeUpdated;

    private EmployeeViewModel employeeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        employeeViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(EmployeeViewModel.class);

        etName = findViewById(R.id.et_name);
        etSalary = findViewById(R.id.et_salary);
        spinnerDept = findViewById(R.id.spinner_dept);
        spinnerContract = findViewById(R.id.spinner_contract);

        Button addUpdateButton = findViewById(R.id.btn_add_employee);

        addUpdateButton.setOnClickListener(v -> {
            addUpdateEmployee();
        });

        if (getIntent().hasExtra(MainActivity.EMPLOYEE_ID)) {
            employeeId = getIntent().getIntExtra(MainActivity.EMPLOYEE_ID, 0);
            Log.d("TAG", "onCreate: " + employeeId);

            employeeViewModel.getEmployee(employeeId).observe(this, employee -> {
                if (employee != null) {
                    etName.setText(employee.getName());
                    etSalary.setText(String.valueOf(employee.getSalary()));
                    String[] departmentArray = getResources().getStringArray(R.array.departments);
                    int position = Arrays.asList(departmentArray).indexOf(employee.getDepartmentName());
                    spinnerDept.setSelection(position);
                    employeeTobeUpdated = employee;
                }
            });
            TextView label = findViewById(R.id.label);
            isEditing = true;
            label.setText(R.string.update_label);
            addUpdateButton.setText(R.string.update_employee_btn_text);
        }
    }

    private void addUpdateEmployee() {
        String name = etName.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String department = spinnerDept.getSelectedItem().toString();
        String contract = spinnerContract.getSelectedItem().toString();
//                spinnerContract.getSelectedItem().toString() == "Full Time" ? Employee.Contract.FULL_TIME : Employee.Contract.PART_TIME;

        if (name.isEmpty()) {
            etName.setError("name field cannot be empty");
            etName.requestFocus();
            return;
        }

        if (salary.isEmpty()) {
            etSalary.setError("salary field cannot be empty");
            etSalary.requestFocus();
            return;
        }

        if (isEditing) {
            Employee employee = new Employee();
            employee.setId(employeeId);
            employee.setName(name);
            employee.setDepartmentName(department);
            employee.setJoiningDate(employeeTobeUpdated.getJoiningDate());
            employee.setSalary(Double.parseDouble(salary));
            employee.setContract(contract);
            employeeViewModel.update(employee);
        } else {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NAME_REPLY, name);
            replyIntent.putExtra(SALARY_REPLY, salary);
            replyIntent.putExtra(DEPARTMENT_REPLY, department);
            replyIntent.putExtra(CONTRACT_REPLY, contract);
            setResult(RESULT_OK, replyIntent);

            Toast.makeText(this, "Employee added", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}