package com.mohammadkiani.androidroomdbdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mohammadkiani.androidroomdbdemo.adapter.RecyclerViewAdapter;
import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnEmployeeClickListener{


    private static final String TAG = "MainActivity";
    public static final int ADD_EMPLOYEE_REQUEST_CODE = 1;
    public static final String EMPLOYEE_ID = "employee_id";


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

        // attach the itemTouchHelper to my recyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Are you sure?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        deletedEmployee = employee;
                        employeeViewModel.delete(employee);
                        Snackbar.make(recyclerView, deletedEmployee.getName() + " is deleted!", Snackbar.LENGTH_LONG)
                                .setAction("Undo", v -> employeeViewModel.insert(deletedEmployee)).show();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> recyclerViewAdapter.notifyDataSetChanged());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
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
                    .addSwipeRightActionIcon(R.drawable.ic_update)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

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














