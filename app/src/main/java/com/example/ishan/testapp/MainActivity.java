package com.example.ishan.testapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishan.testapp.data.DataContract;
import com.example.ishan.testapp.data.DataDbHelper;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String firstRunIdentifier = getString(R.string.first_run);

        if (sharedPreferences.getBoolean(firstRunIdentifier, true)) {
            //Get an editor object
            SharedPreferences.Editor editor = sharedPreferences.edit();

            //Update the value so in next run splash screen does not appear
            editor.putBoolean(firstRunIdentifier, false);
            editor.apply();

            //Start the splash screen activity
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        Log.d(this.getClass().toString(), "" + month);

        if(sharedPreferences.getInt(getString(R.string.date_value), 0) >= day &&
                sharedPreferences.getInt(getString(R.string.month_value), 0) != month){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.month_value), month);
            editor.apply();
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setTitle(getString(R.string.confirm));
            mBuilder.setMessage(getString(R.string.reset_budget_message));
            mBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SQLiteDatabase db = new DataDbHelper(MainActivity.this).getWritableDatabase();
                    db.delete(DataContract.DataEntry.TABLE_NAME, null, null);
                    db.close();
                    fillAmount();
                    Toast.makeText(MainActivity.this, getString(R.string.resetted_successfully), Toast.LENGTH_SHORT).show();
                    DataFragment.updateView();
                }
            });

            mBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, getString(R.string.resetted_unsuccessful), Toast.LENGTH_SHORT).show();
                }
            });

            mBuilder.create().show();
        }

        setContentView(R.layout.activity_main);
        //Set the elevation of the tabs to 0
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        //Find the view pager by id
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        //Find the tab layout by id
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        //Instantiating a new NaviagtionAdapter
        NavigationAdapter navigationAdapter = new NavigationAdapter(getSupportFragmentManager(), this);

        //Add our navigation adapter to the view pager
        viewPager.setAdapter(navigationAdapter);

        //Set the tab layout with the view pager
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_item).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.fill_money) {
            if (!fillAmount()) {
                Toast.makeText(this, getString(R.string.money_already_full_message), Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.budget_resetter) {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean fillAmount(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentAmount = sharedPreferences.getInt(getString(R.string.current_amount), Integer.parseInt(getString(R.string.default_money)));
        int monthyAmount = sharedPreferences.getInt(getString(R.string.monthly_amount), Integer.parseInt(getString(R.string.default_money)));
        if (currentAmount < monthyAmount) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.current_amount), monthyAmount);
            editor.apply();
            TextView textView = (TextView) findViewById(R.id.budget_text_view);
            textView.setText(getString(R.string.available_money_tag) + monthyAmount);
            textView.setTextColor(Color.BLACK);
            return true;
        }
        return false;
    }
}
