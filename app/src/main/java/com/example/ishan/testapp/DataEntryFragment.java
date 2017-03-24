package com.example.ishan.testapp;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishan.testapp.data.DataContract;
import com.example.ishan.testapp.data.DataDbHelper;

import java.text.NumberFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataEntryFragment extends Fragment {


    public DataEntryFragment() {
        // Required empty public constructor
    }

    public static int updateAmount(TextView textView, Resources res, Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        int amount, monthlyBudget = sharedPreferences.getInt(res.getString(R.string.monthly_amount), Integer.parseInt(res.getString(R.string.default_money)));
        amount = sharedPreferences.getInt(res.getString(R.string.current_amount), Integer.parseInt(res.getString(R.string.default_money)));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();


        String availableMoneyText = String.format(res.getString(R.string.available_money_tag), numberFormat.format(amount));
        textView.setText(availableMoneyText);

        if (amount < monthlyBudget * 0.3) {
            textView.setTextColor(Color.RED);
        }

        return amount;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_data_entry, container, false);
        Button button = (Button) rootview.findViewById(R.id.enter_button);

        //Find the text view to show the budget
        final TextView budgetTextview = (TextView) rootview.findViewById(R.id.budget_text_view);

        //Get the Shared Preference instance
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        updateAmount(budgetTextview, getResources(), getActivity());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String givenTo = getStringFromID(R.id.givento, rootview).toLowerCase();
                String givenFor = getStringFromID(R.id.givenfor, rootview).toLowerCase();
                String strAmount = getStringFromID(R.id.amount, rootview);
                if (!givenTo.equals("") && !givenTo.equals("") && !strAmount.equals("")) {

                    int amount = Integer.parseInt(strAmount);

                    try {
                        //Instantiate a DataDbHelper
                        DataDbHelper dataDbHelper = new DataDbHelper(getActivity());

                        //Get a writable database
                        SQLiteDatabase db = dataDbHelper.getWritableDatabase();

                        //Create ContentValues to enter into database
                        ContentValues cv = new ContentValues();
                        cv.put(DataContract.DataEntry.COLUMN_GIVENTO, givenTo);
                        cv.put(DataContract.DataEntry.COLUMN_GIVENFOR, givenFor);
                        cv.put(DataContract.DataEntry.COLUMN_AMOUNT, amount);

                        //Insert hte ContentValues into database;
                        db.insert(DataContract.DataEntry.TABLE_NAME, null, cv);
                        db.close();

                        DataFragment.updateView();

                        //Clear all EditTexts
                        clearEditTexts(rootview);

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        int currentAmount = sharedPreferences.getInt(getString(R.string.current_amount), Integer.parseInt(getString(R.string.default_money)));

                        //Update the amount in sharedPreferences
                        editor.putInt(getString(R.string.current_amount), currentAmount - amount);

                        editor.apply();

                        //Update the amount
                        updateAmount(budgetTextview, getResources(), getActivity());

                        //Make Toast for feedback
                        Toast.makeText(getActivity(), getString(R.string.data_stored_successful_message), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.fields_empty_warning), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAmount((TextView) getActivity().findViewById(R.id.budget_text_view), getResources(), getActivity());
    }

    private String getStringFromID(int id, View rootView) {
        EditText et = (EditText) rootView.findViewById(id);
        return et.getText().toString();
    }

    private void clearEditTexts(View rootView) {
        int ids[] = {R.id.givento, R.id.givenfor, R.id.amount};
        EditText editText;
        for (int id : ids) {
            editText = (EditText) rootView.findViewById(id);
            editText.getText().clear();
        }
    }

}
