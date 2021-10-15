package com.mohammadkiani.androidroomdbdemo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private boolean departmentMenuSelected;

    private HashMap<Integer, String> menuList;

    private List<Department> departments = new ArrayList<>();
    private List<Employee> employeesList;

    /*public enum State {
        DATA_INITIAL, DATA_INSERTED, DATA_UPDATED, DATA_DELETED, DATA_CHANGED
    }
    public static State state;*/
//    private int editingEmployeePosition, deletingEmployeePosition;

    private int menuItemId;
    private String deptName;

    private List<DepartmentWithEmployees> departmentWithEmployeesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        menuList = new HashMap<Integer, String>() {{
            put(R.id.technical_menu_item, "Technical");
            put(R.id.support_menu_item, "Support");
            put(R.id.rd_menu_item, "Research and Development");
            put(R.id.marketing_menu_item, "Marketing");
            put(R.id.engineering_menu_item, "Engineering");
            put(R.id.testing_menu_item, "Testing");
            put(R.id.development_menu_item, "Development");
            put(R.id.it_menu_item, "IT");
            put(R.id.human_resource_menu_item, "Human Resource");

        }};

        // instantiating the employeeViewModel
        employeeViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(EmployeeViewModel.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        employeeViewModel.getAllEmployees().observe(this, employees -> {
            // set adapter
//            recyclerViewAdapter = new RecyclerViewAdapter(employees, this, this);
//            recyclerView.setAdapter(recyclerViewAdapter);
//        });

        departmentWithEmployeesList = new ArrayList<>();
        employeesList = new ArrayList<>();

        /*employeeViewModel.getDepartmentsWithEmployeesList().observe(this, departmentsWithEmployees -> {
            Toast.makeText(this, "Data Set changed", Toast.LENGTH_SHORT).show();
            departmentWithEmployeesList = departmentsWithEmployees;
            employeesList.clear();
            employeesList.addAll(departmentsWithEmployees.stream().flatMap(e -> e.employeeList.stream()).collect(Collectors.toList()));
            Log.d(TAG, "onCreate: " + employeesList.size());
            // update UI
            updateUI();
        });*/

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

        // attach the itemTouchHelper to my recyclerView
        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewAdapter = new RecyclerViewAdapter(employeesList, this, this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void updateUI() {
        /*if (departmentMenuSelected) {
            departments.clear();
            departmentWithEmployeesList.stream().map(d -> d.department).collect(Collectors.toList())
                    .stream().collect(Collectors.groupingBy(Department::getName)).values().stream().forEach(element -> departments.add(element.get(0)));
            deptAdapter = new RecyclerViewAdapter(departments, this, this);
            recyclerView.setAdapter(deptAdapter);
            Toast.makeText(this,"Departments Menu Selected", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Employees Menu Selected", Toast.LENGTH_SHORT).show();
        }*/
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
            /*departmentMenuSelected = !departmentMenuSelected;
            updateUI();*/
            finish();
        } else if (menuItemId == R.id.department_list_menu_item) {
            Toast.makeText(this, "Employees by Department", Toast.LENGTH_SHORT).show();
        } else {
            List<Employee> employees = Objects.requireNonNull(employeeViewModel.getDepartmentsWithEmployeesList().getValue()).stream().flatMap(e -> e.employeeList.stream()).collect(Collectors.toList());
            List<Employee> difference = employees.stream()
                    .filter(employee -> employee.getDepartmentName().equals(menuList.get(menuItemId)))
                    .collect(Collectors.toList());
            RecyclerViewAdapter employeeAdapter = new RecyclerViewAdapter(difference, this, this);
            recyclerView.setAdapter(employeeAdapter);
        }
        return true;
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

    /*private void deleteDepartment(int position) {
        employeeViewModel.delete(departments.get(position));
        departments.remove(position);
    }*/
}














