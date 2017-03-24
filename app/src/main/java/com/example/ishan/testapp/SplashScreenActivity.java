package com.example.ishan.testapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final EditText editText = (EditText) findViewById(R.id.budget_edit_text);
        final EditText dateEditText = (EditText) findViewById(R.id.date_edit_text);
        Button saveButton = (Button) findViewById(R.id.save_button);




        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetStr = editText.getText().toString();
                if(!budgetStr.equals("")) {
                    int budget = Integer.parseInt(budgetStr);
                    String date = dateEditText.getText().toString();

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Calendar calendar = Calendar.getInstance();

                    if(verifyDate(date)){
                        editor.putInt(getString(R.string.date_value), Integer.parseInt(date));
                        editor.putInt(getString(R.string.monthly_amount), budget);
                        editor.putInt(getString(R.string.current_amount), budget);
                        editor.putInt(getString(R.string.month_value), calendar.get(Calendar.MONTH));
                        editor.apply();
                        finish();
                    } else {
                        Toast.makeText(SplashScreenActivity.this, getString(R.string.not_valid_date_message),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SplashScreenActivity.this, getString(R.string.please_enter_budget_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean verifyDate(String date){
        return !date.equals("") && Integer.parseInt(date) > 0 && Integer.parseInt(date) < 32;
    }
}
