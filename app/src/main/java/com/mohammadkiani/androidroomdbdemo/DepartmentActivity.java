package com.mohammadkiani.androidroomdbdemo;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.mohammadkiani.androidroomdbdemo.databinding.ActivityDepartmentBinding;
import com.mohammadkiani.androidroomdbdemo.model.DepartmentWithEmployees;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

public class DepartmentActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityDepartmentBinding binding;

    // declaration of employeeViewModel
    private EmployeeViewModel employeeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiating the employeeViewModel
        employeeViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(EmployeeViewModel.class);

        binding = ActivityDepartmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setTitle("Departments");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_department);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    protected void onResume() {
        super.onResume();
        employeeViewModel.getDepartmentsWithEmployeesList().observe(this, departmentsWithEmployees -> {
            Toast.makeText(this, "Data Set changed", Toast.LENGTH_SHORT).show();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_department);
            assert navHostFragment != null;
            FirstFragment firstFragment = (FirstFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            firstFragment.setDataSource(departmentsWithEmployees);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_department);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}