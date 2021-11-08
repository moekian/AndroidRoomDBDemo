package com.mohammadkiani.androidroomdbdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddEmployeeActivity extends AppCompatActivity {

    public static final String NAME_REPLY = "name_reply";
    public static final String SALARY_REPLY = "salary_reply";
    public static final String DEPARTMENT_REPLY = "department_reply";
    public static final String CONTRACT_REPLY = "contract_reply";

    private EditText etName, etSalary;
    private Spinner spinnerDept, spinnerContract;

    private boolean isEditing = false;
    private long employeeId = 0;
    private Employee employeeTobeUpdated;

    private EmployeeViewModel employeeViewModel;

    private String deptName;
    private List<Department> departments;

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

        if (getIntent().hasExtra(EmployeeActivity.EMPLOYEE_ID)) {
            employeeId = getIntent().getLongExtra(EmployeeActivity.EMPLOYEE_ID, 0);
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
            employeeViewModel.getAllDepartments().observe(this, departments -> {
                this.departments = departments;
            });
            TextView label = findViewById(R.id.label);
            isEditing = true;
            label.setText(R.string.update_label);
            addUpdateButton.setText(R.string.update_employee_btn_text);
        }
        if (getIntent().hasExtra(FirstFragment.DEPT_NAME)) {
            deptName = getIntent().getStringExtra(FirstFragment.DEPT_NAME);
            String[] departmentArray = getResources().getStringArray(R.array.departments);
            int position = Arrays.asList(departmentArray).indexOf(deptName);
            spinnerDept.setSelection(position);
            spinnerDept.setEnabled(false);
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
            Department dept = new Department();
            Employee employee = new Employee();

            dept.setId(employeeTobeUpdated.getDepartmentId());
            dept.setName(department);
            employee.setId(employeeTobeUpdated.getId());
            employee.setName(name);
            employee.setDepartmentName(department);
            employee.setJoiningDate(employeeTobeUpdated.getJoiningDate());
            employee.setSalary(Double.parseDouble(salary));
            employee.setContract(contract);
            employee.setDepartmentId(employeeTobeUpdated.getDepartmentId());
            if (!departments.stream().map(Department::getName).collect(Collectors.toList()).contains(department)) {
                Department d = new Department(department, null);
                employeeViewModel.insert(d);
                employee.setDepartmentName(department);
                employeeViewModel.update(employee);
            } else
                employeeViewModel.updateEmployeeInDepartment(dept, employee);
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