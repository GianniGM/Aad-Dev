package com.google.developer.taskmaker;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.developer.taskmaker.data.Task;
import com.google.developer.taskmaker.data.TaskUpdateService;
import com.google.developer.taskmaker.reminders.AlarmScheduler;

import java.util.Calendar;

public class TaskDetailActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    private TextView textName;
    private TextView textDate;
    private ImageView preference;
    private DatePickerDialog datePickerDialog = null;

    private Uri taskUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Task must be passed to this activity as a valid provider Uri
        taskUri = getIntent().getData();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reminder:
                if (datePickerDialog == null) {
                    Calendar calendar = Calendar.getInstance();

                    datePickerDialog = new DatePickerDialog(
                            this,
                            this,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    // https://stackoverflow.com/a/23762355/3072570
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                }

                if (!datePickerDialog.isShowing()) {
                    datePickerDialog.show();
                }
                return true;

            case R.id.action_delete:
                new AlertDialog.Builder(this).setTitle(R.string.delete_title)
                        .setMessage(R.string.remove_dialog)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TaskUpdateService.deleteTask(TaskDetailActivity.this, taskUri);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //TODO: Handle date selection from a DatePickerFragment
        final long pickerTime = getMillisTime(day, month, year);
        AlarmScheduler.scheduleAlarm(this, pickerTime, taskUri);
        finish();
    }

    private long getMillisTime(int dd, int mm, int yyyy) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yyyy);
        calendar.set(Calendar.HOUR_OF_DAY, 12);

        calendar.set(Calendar.MONTH, mm);
        calendar.set(Calendar.DAY_OF_MONTH, dd);

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
