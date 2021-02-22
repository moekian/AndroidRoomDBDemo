package com.mohammadkiani.androidroomdbdemo.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkiani.androidroomdbdemo.R;
import com.mohammadkiani.androidroomdbdemo.model.Employee;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Employee> employeeList;
    private Context context;

    public RecyclerViewAdapter(List<Employee> employeeList, Context context) {
        this.employeeList = employeeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.name.setText(employee.getName());
        holder.department.setText(employee.getDepartmentName());
        holder.hireDate.setText(employee.getJoiningDate());
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView department;
        private TextView hireDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_row);
            department = itemView.findViewById(R.id.dept_row);
            hireDate = itemView.findViewById(R.id.hire_date_row);

        }
    }
}










