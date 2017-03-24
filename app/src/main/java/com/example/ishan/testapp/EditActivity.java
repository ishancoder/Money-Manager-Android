package com.example.ishan.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ishan.testapp.data.DataContract;
import com.example.ishan.testapp.data.DataDbHelper;

public class EditActivity extends AppCompatActivity {
    Context mContext;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_data_entry);
        this.mContext = this;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int id = getIntent().getIntExtra("id_value_of_clicked_item", 0);
        String results[] = fetchData(id);

        if (results == null) {
            Toast.makeText(this, getString(R.string.could_not_delete_message), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            final EditText givenTo, givenFor, amount;
            final int beforeAmount = Integer.parseInt(results[3]);
            Button editButton = (Button) findViewById(R.id.enter_button);
            editButton.setText(getString(R.string.edit));
            givenTo = (EditText) findViewById(R.id.givento);
            givenFor = (EditText) findViewById(R.id.givenfor);
            amount = (EditText) findViewById(R.id.amount);
            givenTo.setText(results[1]);
            givenFor.setText(results[2]);
            amount.setText(results[3]);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteDatabase db = new DataDbHelper(mContext).getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    int editedAmount = Integer.parseInt(amount.getText().toString());
                    cv.put(DataContract.DataEntry._ID, "" + id);
                    cv.put(DataContract.DataEntry.COLUMN_GIVENTO, givenTo.getText().toString().toLowerCase());
                    cv.put(DataContract.DataEntry.COLUMN_GIVENFOR, givenFor.getText().toString().toLowerCase());
                    cv.put(DataContract.DataEntry.COLUMN_AMOUNT, editedAmount);
                    db.update(
                            DataContract.DataEntry.TABLE_NAME,
                            cv,
                            DataContract.DataEntry._ID + " = ?",
                            new String[]{"" + id}
                    );
                    db.close();
                    int currentAmount = sharedPreferences.getInt(getString(R.string.current_amount), Integer.parseInt(getString(R.string.default_money)));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(getString(R.string.current_amount), currentAmount + (beforeAmount - editedAmount));
                    editor.apply();

                    Toast.makeText(mContext, getString(R.string.data_update_success_message), Toast.LENGTH_SHORT).show();
                    DataFragment.updateView();

                    //Data Updated Close the Current Activity
                    finish();
                }
            });
        }
    }

    private String[] fetchData(int id) {
        String results[] = new String[4];
        SQLiteDatabase db = new DataDbHelper(this).getReadableDatabase();
        Cursor cursor = db.query(
                DataContract.DataEntry.TABLE_NAME,
                null,
                DataContract.DataEntry._ID + " = ?",
                new String[]{"" + id},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            results[0] = "" + cursor.getInt(cursor.getColumnIndex(DataContract.DataEntry._ID));
            results[1] = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_GIVENTO));
            results[2] = cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_GIVENFOR));
            results[3] = "" + cursor.getInt(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_AMOUNT));
        } else {
            return null;
        }

        cursor.close();
        db.close();
        return results;
    }
}
