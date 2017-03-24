package com.example.ishan.testapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishan.testapp.data.DataContract;
import com.example.ishan.testapp.data.DataDbHelper;

import java.text.NumberFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    private static Cursor cursor;
    private static SimpleCursorAdapter cursorAdapter = null;

    public DataFragment() {
        // Required empty public constructor
    }

    public static void updateView() {
        if (cursorAdapter != null) {
            cursor.requery();
            cursorAdapter.swapCursor(cursor);
            cursorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_data, container, false);
        DataDbHelper dataDbHelper = new DataDbHelper(getActivity());
        SQLiteDatabase db = dataDbHelper.getReadableDatabase();
        cursor = db.query(DataContract.DataEntry.TABLE_NAME, null, null, null, null, null, null, null);

        cursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.table_item,
                cursor,
                new String[]{
                        DataContract.DataEntry.COLUMN_GIVENTO,
                        DataContract.DataEntry.COLUMN_GIVENFOR,
                        DataContract.DataEntry.COLUMN_AMOUNT
                },
                new int[]{
                        R.id.givento,
                        R.id.givenfor,
                        R.id.amount
                },
                0
        );

        cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(columnIndex == cursor.getColumnIndex(DataContract.DataEntry.COLUMN_AMOUNT)){
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                    TextView textView = (TextView) view;

                    int amount = cursor.getInt(columnIndex);

                    textView.setText(numberFormat.format(amount));
                    return true;
                }
                return false;
            }
        });

        final ListView listView = (ListView) rootView.findViewById(R.id.data_list_view);
        listView.setAdapter(cursorAdapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.inflate(R.menu.context_menu);
                popupMenu.show();

                Cursor cursor = ((SimpleCursorAdapter) adapterView.getAdapter()).getCursor();
                cursor.moveToPosition(i);
                final int id = cursor.getInt(cursor.getColumnIndex(DataContract.DataEntry._ID));
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.context_menu_delete) {
                            deleteDialogBoxShow(id);
                        } else if (menuItem.getItemId() == R.id.content_menu_edit) {
                            Intent intent = new Intent(getActivity(), EditActivity.class);
                            intent.putExtra("id_value_of_clicked_item", id);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
                return true;
            }
        });


        return rootView;
    }

    private void deleteDialogBoxShow(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm));
        builder.setMessage(getString(R.string.are_you_sure_delete_message));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int amountByDeletion = delete(id);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int currentAmount = sharedPreferences.getInt(getString(R.string.current_amount), Integer.parseInt(getString(R.string.default_money)));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.current_amount), currentAmount + amountByDeletion);
                editor.apply();
                DataEntryFragment.updateAmount((TextView)getActivity().findViewById(R.id.budget_text_view), getResources(), getActivity());
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), getString(R.string.deletion_unsuccessful_message), Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    private int delete(final int id){
        SQLiteDatabase db = new DataDbHelper(getActivity()).getWritableDatabase();
        Cursor mcursor = db.query(DataContract.DataEntry.TABLE_NAME, null, DataContract.DataEntry._ID + " = ?", new String[]{""+id},null, null, null);
        mcursor.moveToFirst();
        int amount = mcursor.getInt(mcursor.getColumnIndex(DataContract.DataEntry.COLUMN_AMOUNT));
        mcursor.close();
        db.delete(DataContract.DataEntry.TABLE_NAME, DataContract.DataEntry._ID + " = ?", new String[]{""+id});
        Toast.makeText(getActivity(), getString(R.string.deletion_successful_message), Toast.LENGTH_SHORT).show();
        db.close();
        updateView();
        return amount;
    }
}
