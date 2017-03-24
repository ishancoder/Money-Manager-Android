package com.example.ishan.testapp;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ishan.testapp.data.DataContract;
import com.example.ishan.testapp.data.DataDbHelper;

import java.text.NumberFormat;

public class SearchActivity extends AppCompatActivity {
    private DataDbHelper helper ;
    private SQLiteDatabase db;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setElevation(0);
        helper = new DataDbHelper(this);
        db = helper.getReadableDatabase();
        listView = (ListView) findViewById(R.id.search_list_view);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();

            CheckBox checkBox = (CheckBox) findViewById(R.id.specific_search_check_box);
            checkBox.setChecked(false);
            Cursor cursor = queryAndGetCursor(false, query);
            updateSearchResult(cursor, query);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    updateSearchResult(queryAndGetCursor(b, query), query);
                }
            });
        }
    }

    private void updateSearchResult(Cursor cursor, String queryParam) {
        int amount = 0;

        while (cursor.moveToNext()) {
            amount += cursor.getInt(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_AMOUNT));
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        String amountPaidTo = String.format(getString(R.string.total_amount_string), queryParam, numberFormat.format(amount));

        TextView textView = (TextView) findViewById(R.id.search_total_text_view);
        textView.setText(amountPaidTo);

        cursor.moveToFirst();

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.table_item,
                cursor,
                new String[]{
                        DataContract.DataEntry.COLUMN_GIVENTO,
                        DataContract.DataEntry.COLUMN_GIVENFOR,
                        DataContract.DataEntry.COLUMN_AMOUNT
                },
                new int[]{R.id.givento, R.id.givenfor, R.id.amount},
                0
        );
        listView.setAdapter(simpleCursorAdapter);
    }

    private Cursor queryAndGetCursor(Boolean b, String query) {
        Cursor cursor;
        if (!b) {
            cursor = db.query(
                    true,
                    DataContract.DataEntry.TABLE_NAME,
                    null,
                    DataContract.DataEntry.COLUMN_GIVENTO + " LIKE ?",
                    new String[]{"%" + query + "%"},
                    null,
                    null,
                    null,
                    null
            );
        } else {
            cursor = db.query(
                    true,
                    DataContract.DataEntry.TABLE_NAME,
                    null,
                    DataContract.DataEntry.COLUMN_GIVENTO + " = ?",
                    new String[]{query},
                    null,
                    null,
                    null,
                    null
            );
        }
        return cursor;
    }
}
