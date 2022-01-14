package com.mohammadkiani.androidroomdbdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.mohammadkiani.androidroomdbdemo.databinding.FragmentSecondBinding;
import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.EmployeeViewModel;

/**
 * @author Mohammad Kiani */

public class SecondFragment extends Fragment {

    private static final String DEPT_NAME_REPLY = "department_name";
    private static final String LOCATION_REPLY = "location_reply";
    private FragmentSecondBinding binding;

    private Spinner departmentSpinner;
    private EditText locationET;
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

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAddDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.spinnerDept.getSelectedItem().toString();
                String location = binding.etLocation.getText().toString().trim();
                Department department = new Department(name, location);
                employeeViewModel.insert(department);
                getParentFragmentManager().popBackStack();
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}