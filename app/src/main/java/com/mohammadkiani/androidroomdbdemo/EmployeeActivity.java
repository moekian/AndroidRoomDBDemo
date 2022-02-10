package com.mohammadkiani.androidroomdbdemo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class EmployeeActivity<T> extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener{


    private static final String TAG = "MainActivity";
    public static final int ADD_EMPLOYEE_REQUEST_CODE = 1;
    public static final String EMPLOYEE_ID = "employee_id";

    private SwipeHelper swipeHelper;


    // declaration of employeeViewModel
    private EmployeeViewModel employeeViewModel;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter, deptAdapter;

    private Employee deletedEmployee;

    private boolean departmentMenuSelected, gridViewSelected;

    private HashMap<Integer, String> menuList;

    private List<Department> departments = new ArrayList<>();
    private List<Employee> employeesList;

    private int menuItemId;
    private String deptName;

    private List<DepartmentWithEmployees> departmentWithEmployeesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        gridViewSelected = false;

        // instantiating the employeeViewModel
        employeeViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(EmployeeViewModel.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        departmentWithEmployeesList = new ArrayList<>();
        employeesList = new ArrayList<>();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeActivity.this, AddEmployeeActivity.class);
            intent.putExtra(FirstFragment.DEPT_NAME, deptName);
            /*startActivityForResult(intent, ADD_EMPLOYEE_REQUEST_CODE);*/
            // the following approach as startActivityForResult is deprecated
            launcher.launch(intent);

        });

        // using SwipeHelper class
        swipeHelper = new SwipeHelper(this, 300, recyclerView) {
            @Override
            protected void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeUnderlayButton> buffer) {
                buffer.add(new SwipeUnderlayButton(EmployeeActivity.this,
                        "Delete",
                        R.drawable.ic_delete_white,
                        30,
                        50,
                        Color.parseColor("#ff3c30"),
                        SwipeDirection.LEFT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                if (!departmentMenuSelected)
                                    deleteEmployee(position);
                                Log.d(TAG, "onClick: " + "delete click");
                            }
                        }));
                buffer.add(new SwipeUnderlayButton(EmployeeActivity.this,
                        "Update",
                        R.drawable.ic_update_white,
                        30,
                        50,
                        Color.parseColor("#ff9502"),
                        SwipeDirection.LEFT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                displayEmployeeForEditing(position);
                            }
                        }));
                buffer.add(new SwipeUnderlayButton(EmployeeActivity.this,
                        "Dependents",
                        R.drawable.ic_departments,
                        30,
                        50,
                        Color.parseColor("#9370DB"),
                        SwipeDirection.RIGHT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                Log.d(TAG, "onClick: dependents" );
                                //TODO: add another entity (dependents of an employee) and show the list
                            }
                        }));
            }
        };

        if (getIntent().hasExtra(FirstFragment.DEPT_NAME)) {
            deptName = getIntent().getStringExtra(FirstFragment.DEPT_NAME);
            setTitle(deptName);
        }
        employeeViewModel.getEmployeesInDepartment(deptName).observe(this, employees -> {
            employeesList.clear();
            employeesList.addAll(employees);
            updateUI();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewAdapter = new RecyclerViewAdapter(employeesList, this, this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void updateUI() {
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Employees Menu Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.department_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        menuItemId = item.getItemId();
        if (menuItemId == R.id.department_menu_item) {
            finish();
        } else {
            gridViewSelected = !gridViewSelected;
            recyclerViewAdapter.setGridSelected(gridViewSelected);
            if (gridViewSelected) {
                item.setIcon(R.drawable.ic_list);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
            } else {
                item.setIcon(R.drawable.ic_grid);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            }
        }
        return true;
    }

    // the following approach instead of onActivityResult as startActivityForResult is deprecated
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    String name = data.getStringExtra(AddEmployeeActivity.NAME_REPLY);
                    String salary = data.getStringExtra(AddEmployeeActivity.SALARY_REPLY);
                    String department = data.getStringExtra(AddEmployeeActivity.DEPARTMENT_REPLY);
                    String contract = data.getStringExtra(AddEmployeeActivity.CONTRACT_REPLY);
                    // getting the current date
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
                    String joiningDate = sdf.format(cal.getTime());

                    Employee employee = new Employee(name, department, joiningDate, Double.parseDouble(salary), contract);
//                    Department dept = new Department(department, "");
                    departmentMenuSelected = false;
                    employee.setDepartmentName(department);
                    employeeViewModel.insert(employee);
//                    employeeViewModel.insert(dept, employee);
                }
            });

    @Override
    public void onItemClick(int position) {
        if (!departmentMenuSelected)
            displayEmployeeForEditing(position);
    }

    private void displayEmployeeForEditing(int position) {
//        List<Employee> employees = Objects.requireNonNull(employeeViewModel.getDepartmentsWithEmployeesList().getValue()).stream().flatMap(e -> e.employeeList.stream()).collect(Collectors.toList());

        Employee employee = employeesList.get(position);
        Intent intent = new Intent(EmployeeActivity.this, AddEmployeeActivity.class);
        intent.putExtra(EMPLOYEE_ID, employee.getId());
        startActivity(intent);
    }

    private void deleteEmployee(int position) {
        Employee employee = employeesList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeActivity.this);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deletedEmployee = employee;
            employeeViewModel.delete(employee);
            recyclerViewAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, deletedEmployee.getName() + " is deleted!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> employeeViewModel.insert(deletedEmployee)).show();
            Toast.makeText(EmployeeActivity.this, employee.getName() + " deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> recyclerViewAdapter.notifyItemChanged(position));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}














