package com.google.developer.taskmaker;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.developer.taskmaker.data.Task;

public class TaskDetailActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    TextView textName;
    TextView textDate;
    ImageView preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Task must be passed to this activity as a valid provider Uri
        final Uri taskUri = getIntent().getData();

        textName = (TextView) findViewById(R.id.text_name);
        textDate = (TextView) findViewById(R.id.text_due_date);
        preference = (ImageView) findViewById(R.id.priority);

        //TODO: Display attributes of the provided task in the UI
        Cursor cursor = getContentResolver().query(
                taskUri,
                null,
                null,
                null,
                null
        );

        if (null != cursor){
            cursor.moveToFirst();

            Task task = new Task(cursor);
            textName.setText(task.description);
            textDate.setText(R.string.date_not_set);
            preference.setImageResource(R.drawable.ic_not_priority);

            if(task.hasDueDate())
                textDate.setText(DateUtils.getRelativeTimeSpanString(task.dueDateMillis));

            if(task.isPriority)
                preference.setImageResource(R.drawable.ic_priority);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //TODO: Handle date selection from a DatePickerFragment
    }
}
