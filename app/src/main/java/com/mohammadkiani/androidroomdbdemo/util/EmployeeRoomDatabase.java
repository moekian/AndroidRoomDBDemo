package com.mohammadkiani.androidroomdbdemo.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mohammadkiani.androidroomdbdemo.data.EmployeeDao;
import com.mohammadkiani.androidroomdbdemo.model.Department;
import com.mohammadkiani.androidroomdbdemo.model.Employee;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mohammad Kiani */

@Database(entities = {Employee.class, Department.class}, version = 1, exportSchema = false)
public abstract class EmployeeRoomDatabase extends RoomDatabase {
    public abstract EmployeeDao employeeDao();

    private static volatile EmployeeRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    // executor service helps to do tasks in background thread
    public static final ExecutorService databaseWriteExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static EmployeeRoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (EmployeeRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EmployeeRoomDatabase.class,
                            "employee_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    databaseWriteExecutor.execute(() -> {
                        EmployeeDao employeeDao = INSTANCE.employeeDao();
                        employeeDao.deleteAll();

//                        Employee employee = new Employee("Mo", "cs", "", 1111);
//                        employeeDao.insert(employee);
                    });
                }
            };
}









