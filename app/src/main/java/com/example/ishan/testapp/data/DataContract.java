package com.example.ishan.testapp.data;

import android.provider.BaseColumns;

/**
 * Created by Ishan on 7/1/2016.
 */
public class DataContract {
    public static final class DataEntry implements BaseColumns{
        public static String TABLE_NAME = "datattable";

        public static String COLUMN_GIVENTO = "givento";

        public static String COLUMN_GIVENFOR = "givenfor";

        public static String COLUMN_AMOUNT = "amount";
    }
}
