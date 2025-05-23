package com.example.prodigy_ad_02;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Toast;
import android.view.View;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ScrollView taskScrollView;
    private ArrayAdapter<String> taskAdapter;
    private TaskDBHelper dbHelper;
    private List<Data> taskData;
    FloatingActionButton fabAddTask;
    RecyclerView recyclerView;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskData=new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        fabAddTask = findViewById(R.id.fab_add_task);
        dbHelper = new TaskDBHelper(this);
        loadTasksFromSQLite(taskData);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, taskData);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Intent intent = new Intent(MainActivity.this, EditTask.class);
                intent.putExtra("task", taskData.get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {

                markTaskAsComplete(position);
                taskData.remove(position);
                Toast.makeText(MainActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onCheckboxClick(int position) {

                markTaskAsComplete(position);
                taskData.remove(position);
                Toast.makeText(MainActivity.this, "Task Completed", Toast.LENGTH_SHORT).show();
                adapter.notifyItemRemoved(position);
            }
        });

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

    }
    public void loadTasksFromSQLite(List<Data> data) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TaskContract.TaskEntry.TABLE_NAME, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String taskName = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK));
            @SuppressLint("Range") String taskDate = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_DATE));
            @SuppressLint("Range") String taskTime = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_TIME));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_CATEGORY));
            @SuppressLint("Range") String priority = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY));
            @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NOTES));

            data.add(new Data(taskName,taskDate,taskTime,category,priority,notes));
        }

        cursor.close();
        db.close();
    }
    public void markTaskAsComplete(int position) {
        String task = taskData.get(position).getName();
        //Toast.makeText(this, task, Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 1);
        db.delete(TaskContract.TaskEntry.TABLE_NAME,
                TaskContract.TaskEntry.COLUMN_TASK + " = ?", new String[]{task});
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
    public class Data{
        String name;
        String date;
        String time;
        String category;
        String priority;
        String notes;
        Data(String name, String date, String time, String category, String priority, String notes) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.category = category;
            this.priority = priority;
            this.notes = notes;
        }
        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }
        public String getCategory() {
            return category;
        }
        public String getPriority() {
            return priority;
        }
        public String getNotes() {
            return notes;
        }
    }

}