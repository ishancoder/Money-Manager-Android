package com.example.ishan.testapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ishan on 7/1/2016.
 */
public class DataDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "data.db";
    private static final String QUERY_CREATE_TABLE = "CREATE TABLE " + DataContract.DataEntry.TABLE_NAME + " ("
            + DataContract.DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataContract.DataEntry.COLUMN_GIVENTO + " TEXT, "
            + DataContract.DataEntry.COLUMN_GIVENFOR + " TEXT, "
            + DataContract.DataEntry.COLUMN_AMOUNT + " INTEGER);";

    public DataDbHelper(Context mContext){
        super(mContext, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.DataEntry.TABLE_NAME + ";");
        onCreate(sqLiteDatabase);
    }
}
