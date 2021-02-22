package com.mohammadkiani.androidroomdbdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddEmployeeActivity extends AppCompatActivity {

    public static final String NAME_REPLY = "name_reply";
    public static final String SALARY_REPLY = "salary_reply";
    public static final String DEPARTMENT_REPLY = "department_reply";

    private EditText etName, etSalary;
    private Spinner spinnerDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        etName = findViewById(R.id.et_name);
        etSalary = findViewById(R.id.et_salary);
        spinnerDept = findViewById(R.id.spinner_dept);

        Button addUpdateButton = findViewById(R.id.btn_add_employee);

        addUpdateButton.setOnClickListener(v -> {
            addUpdateEmployee();
        });
    }

    private void addUpdateEmployee() {
        String name = etName.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String department = spinnerDept.getSelectedItem().toString();

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

        Intent replyIntent = new Intent();
        replyIntent.putExtra(NAME_REPLY, name);
        replyIntent.putExtra(SALARY_REPLY, salary);
        replyIntent.putExtra(DEPARTMENT_REPLY, department);
        setResult(RESULT_OK, replyIntent);

        Toast.makeText(this, "Employee added", Toast.LENGTH_SHORT).show();

        finish();
    }
}