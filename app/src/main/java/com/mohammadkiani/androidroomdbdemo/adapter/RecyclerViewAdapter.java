package com.mohammadkiani.androidroomdbdemo.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkiani.androidroomdbdemo.R;
import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.DepartmentWithEmployees;
import com.mohammadkiani.androidroomdbdemo.model.Employee;

import java.util.List;

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter<T>.ViewHolder> {
    private static final String TAG = "Cannot invoke method length() on null object";

    private List<T> tList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean isGridSelected;
    public void setGridSelected(boolean gridSelected) {
        isGridSelected = gridSelected;
    }

    public RecyclerViewAdapter(List<T> tList, Context context, OnItemClickListener onItemClickListener) {
        this.tList = tList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
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
        T t = tList.get(position);
        if (t instanceof Employee) {
            Employee e = (Employee) t;
            holder.name.setText(e.getName());
            holder.department.setText(e.getDepartmentName());
            holder.hireDate.setText(e.getJoiningDate());
            holder.contract.setText(e.getContract());
            holder.department.setVisibility(View.VISIBLE);
            if (isGridSelected) {
                holder.hireDate.setVisibility(View.GONE);
            } else {
                holder.hireDate.setVisibility(View.VISIBLE);
            }
            holder.contract.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.departmentView = false;
            Log.d(TAG, "onBindViewHolder: employee");
        } else if (t instanceof Department) {
            Department d = (Department) t;
            holder.name.setText(d.getName());
            holder.department.setVisibility(View.GONE);
            holder.hireDate.setVisibility(View.GONE);
            holder.contract.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.departmentView = true;
            Log.d(TAG, "onBindViewHolder: department");
        } else if (t instanceof DepartmentWithEmployees) {
            DepartmentWithEmployees dwe = (DepartmentWithEmployees) t;
            Department d = dwe.getDepartment();
            holder.name.setText(d.getName());
            holder.department.setText(String.format("%s Employees", String.valueOf(dwe.getEmployeeListSize())));
            holder.hireDate.setVisibility(View.GONE);
            holder.contract.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.ic_departments);
            holder.departmentView = true;
        }
        Log.d(TAG, "onBindViewHolder: none");
    }

    @Override
    public int getItemCount() {
        return tList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private TextView department;
        private TextView hireDate;
        private TextView contract;
        private ImageView imageView;
        private boolean departmentView = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_row);
            department = itemView.findViewById(R.id.dept_row);
            hireDate = itemView.findViewById(R.id.hire_date_row);
            contract = itemView.findViewById(R.id.contract_row);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);

        }

        public boolean isDepartmentView() {
            return departmentView;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}










