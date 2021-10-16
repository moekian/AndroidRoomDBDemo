package com.mohammadkiani.androidroomdbdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkiani.androidroomdbdemo.adapter.RecyclerViewAdapter;
import com.mohammadkiani.androidroomdbdemo.databinding.FragmentFirstBinding;
import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.DepartmentWithEmployees;
import com.mohammadkiani.androidroomdbdemo.model.Employee;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FirstFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener {

    public static final String DEPT_NAME = "department_name";
    private FragmentFirstBinding binding;

    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<DepartmentWithEmployees> departments = new ArrayList<>();
    // declaration of employeeViewModel
    private EmployeeViewModel employeeViewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // instantiating the employeeViewModel
        employeeViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getActivity().getApplication())
                .create(EmployeeViewModel.class);

        /*recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));*/
        adapter = new RecyclerViewAdapter(departments, getContext(), this);

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        employeeViewModel.getDepartmentsWithEmployeesList().observe(getActivity(), departmentsWithEmployees -> {
            Toast.makeText(getActivity(), "Data Set changed", Toast.LENGTH_SHORT).show();

            departments.clear();
            departments.addAll(departmentsWithEmployees);
            departmentsWithEmployees.forEach(departmentWithEmployees -> {
                if (departmentWithEmployees.getEmployeeListSize() == 0)
                    delete(departmentWithEmployees);
            });
            // update UI
            adapter.notifyDataSetChanged();
        });

        binding.fabDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    private void delete(DepartmentWithEmployees departmentWithEmployees) {
        employeeViewModel.delete(departmentWithEmployees.department);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), EmployeeActivity.class);
        intent.putExtra(DEPT_NAME, departments.get(position).getDepartment().getName()).toString();
        startActivity(intent);
    }
}