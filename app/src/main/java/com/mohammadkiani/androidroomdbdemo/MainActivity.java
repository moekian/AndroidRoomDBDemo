package com.mohammadkiani.androidroomdbdemo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mohammadkiani.androidroomdbdemo.adapter.RecyclerViewAdapter;
import com.mohammadkiani.androidroomdbdemo.helper.SwipeUnderlayButtonClickListener;
import com.mohammadkiani.androidroomdbdemo.helper.SwipeHelper;
import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.DepartmentWithEmployees;
import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnEmployeeClickListener{


    private static final String TAG = "MainActivity";
    public static final int ADD_EMPLOYEE_REQUEST_CODE = 1;
    public static final String EMPLOYEE_ID = "employee_id";

    private SwipeHelper swipeHelper;


    // declaration of employeeViewModel
    private EmployeeViewModel employeeViewModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private Employee deletedEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiating the employeeViewModel
        employeeViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(EmployeeViewModel.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*employeeViewModel.getAllEmployees().observe(this, employees -> {
            // set adapter
            recyclerViewAdapter = new RecyclerViewAdapter(employees, this, this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });*/

        employeeViewModel.getDepartmentsWithEmployeesList().observe(this, departmentsWithEmployees -> {
            List<Employee> employees = new ArrayList<>();
            departmentsWithEmployees.forEach(departmentWithEmployees -> departmentWithEmployees.employeeList.forEach(employee -> employees.add(employee)));
            recyclerViewAdapter = new RecyclerViewAdapter(employees, this, this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
            /*startActivityForResult(intent, ADD_EMPLOYEE_REQUEST_CODE);*/
            // the following approach as startActivityForResult is deprecated
            launcher.launch(intent);

        });

        // using SwipeHelper class
        swipeHelper = new SwipeHelper(this, 300, recyclerView) {
            @Override
            protected void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeUnderlayButton> buffer) {
                buffer.add(new SwipeUnderlayButton(MainActivity.this,
                        "Delete",
                        R.drawable.ic_delete_white,
                        30,
                        50,
                        Color.parseColor("#ff3c30"),
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                deleteEmployee(position);
                                Log.d(TAG, "onClick: " + "delete click");
                            }
                        }));
                buffer.add(new SwipeUnderlayButton(MainActivity.this,
                        "Update",
                        R.drawable.ic_update_white,
                        30,
                        50,
                        Color.parseColor("#ff9502"),
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                displayEmployeeForEditing(position);
                            }
                        }));
            }
        };

        // attach the itemTouchHelper to my recyclerView
        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
    }
/*
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Employee employee = employeeViewModel.getAllEmployees().getValue().get(position);
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deleteEmployee(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
                    intent.putExtra(EMPLOYEE_ID, employee.getId());
                    startActivity(intent);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .setIconHorizontalMargin(1, 1)
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightActionIcon(R.drawable.ic_update_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
*/
    /*
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
*/
    // the following approach instead of onActivityResult as startActivityForResult is deprecated
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    String name = data.getStringExtra(AddEmployeeActivity.NAME_REPLY);
                    String salary = data.getStringExtra(AddEmployeeActivity.SALARY_REPLY);
                    String department = data.getStringExtra(AddEmployeeActivity.DEPARTMENT_REPLY);
                    // getting the current date
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
                    String joiningDate = sdf.format(cal.getTime());

                    Employee employee = new Employee(name, department, joiningDate, Double.parseDouble(salary));
                    Department dept = new Department(department, "");
                    employeeViewModel.insert(dept, employee);
                }
            });

    @Override
    public void onEmployeeClick(int position) {
//        displayEmployeeForEditing(position);
//        Log.d(TAG, "onEmployeeClick: " + position);
        List<DepartmentWithEmployees> departmentWithEmployees = employeeViewModel.getDepartmentsWithEmployeesList().getValue();
        Log.d(TAG, "onEmployeeClick: " + departmentWithEmployees.get(position).department.getName());
    }

    private void displayEmployeeForEditing(int position) {
        Employee employee = employeeViewModel.getAllEmployees().getValue().get(position);
        Intent intent = new Intent(MainActivity.this, AddEmployeeActivity.class);
        intent.putExtra(EMPLOYEE_ID, employee.getId());
        startActivity(intent);
    }

    private void deleteEmployee(int position) {
        Employee employee = employeeViewModel.getAllEmployees().getValue().get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deletedEmployee = employee;
            employeeViewModel.delete(employee);
            Snackbar.make(recyclerView, deletedEmployee.getName() + " is deleted!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> employeeViewModel.insert(deletedEmployee)).show();
            Toast.makeText(MainActivity.this, employee.getName() + " deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> recyclerViewAdapter.notifyDataSetChanged());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}














