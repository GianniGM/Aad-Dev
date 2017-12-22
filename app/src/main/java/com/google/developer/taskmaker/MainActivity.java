package com.google.developer.taskmaker;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.developer.taskmaker.data.DatabaseContract;
import com.google.developer.taskmaker.data.Task;
import com.google.developer.taskmaker.data.TaskAdapter;
import com.google.developer.taskmaker.data.TaskUpdateService;

import static com.google.developer.taskmaker.data.TaskUpdateService.ACTION_INSERT;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        TaskAdapter.OnItemClickListener,
        View.OnClickListener{

    public static final int INSERTED_RESULT = 0;

    private static final int ADD_ITEM_REQUEST = 666;
    private static final int LOADER = 0;
    private static final int DETAIL_ITEM_REQUEST = 555;
    private TaskAdapter mAdapter;
    private FloatingActionButton mFab;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mAdapter = new TaskAdapter(null);
        mAdapter.setOnItemClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        super.getLoaderManager().initLoader(LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_ITEM_REQUEST: {
                checkInserted(resultCode);
                break;
            }

            case DETAIL_ITEM_REQUEST:

                break;

            //todo
//        getLoaderManager().restartLoader(LOADER,null, this);
//        super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkInserted(int resultCode) {
        String message;
        switch (resultCode) {
            case INSERTED_RESULT:
                message = getString(R.string.tast_added);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    /* Click events in Floating Action Button */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, ADD_ITEM_REQUEST);
    }

    /* Click events in RecyclerView items */
    @Override
    public void onItemClick(View v, int position) {
        Task taskClicked = mAdapter.getItem(position);
        final Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.setData(ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, taskClicked.id));
        startActivity(intent);
    }

    /* Click events on RecyclerView item checkboxes */
    @Override
    public void onItemToggled(boolean active, int position) {
        //TODO: Handle task item checkbox event
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String defaultSort = super.getString(R.string.pref_sortBy_default);
        final String sort = prefs.getString(super.getString(R.string.pref_sortBy_key), defaultSort);
        final String sortOrder = sort.equals(defaultSort) ? DatabaseContract.DEFAULT_SORT : DatabaseContract.DATE_SORT;

        CursorLoader cursorLoader = new CursorLoader(
                this,
                DatabaseContract.CONTENT_URI,
                null,
                null,
                null,
                sortOrder
        );

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mAdapter.swapCursor(cursor);
        if(cursor != null)
             cursor.getCount();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
