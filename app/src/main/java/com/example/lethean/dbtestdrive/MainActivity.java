package com.example.lethean.dbtestdrive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.edit_id)
    EditText editId;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.button_insert)
    Button buttonInsert;
    @BindView(R.id.button_delete)
    Button buttonDelete;
    @BindView(R.id.button_update)
    Button buttonUpdate;
    @BindView(R.id.button_view_all)
    Button buttonViewAll;
    @BindView(R.id.listView)
    ListView listView;

    private Unbinder unbinder;
    private DBHelper helper;
    private final int REQUEST_CODE_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        helper = new DBHelper(this);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        } else {
            getContacts();
        }

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertRecord();
                viewAllToListView();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord();
                viewAllToListView();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecord();
                viewAllToListView();
            }
        });

        buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAllToListView();
            }
        });

        viewAllToListView();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                Toast.makeText(getApplicationContext(), "READ_CONTACTS 접근 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getContacts() {
        String [] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        String selectionClause = ContactsContract.CommonDataKinds.Phone.TYPE + " = ? ";

        String[] selectionArgs = {""+ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE};

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selectionClause,
                selectionArgs,
                null);

        while(cursor.moveToNext()) {
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            if(helper.duplicationCheck(name, phone))
                continue;
            else
                helper.insertUserByMethod(name, phone);
        }
    }

    private void viewAllToListView() {
        Cursor cursor = helper.getAllUsersByMethod();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.item, cursor, new String[]{
                UserContract.Users._ID,
                UserContract.Users.KEY_NAME,
                UserContract.Users.KEY_PHONE},
                new int[]{R.id.text_item_id, R.id.text_item_name, R.id.text_item_phone}, 0);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Adapter adapter = adapterView.getAdapter();

                editId.setText(((Cursor)adapter.getItem(i)).getString(0));
                editName.setText(((Cursor)adapter.getItem(i)).getString(1));
                editPhone.setText(((Cursor)adapter.getItem(i)).getString(2));
            }
        });
    }

    private void updateRecord() {
        long nOfRows = helper.updateUserByMethod(
                editId.getText().toString(),
                editName.getText().toString(),
                editPhone.getText().toString());

        if (nOfRows >0)
            Toast.makeText(this,"Record Updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"No Record Updated", Toast.LENGTH_SHORT).show();
    }

    private void deleteRecord() {
        long nOfRows = helper.deleteUserByMethod(editId.getText().toString());

        if (nOfRows >0)
            Toast.makeText(this,"Record Deleted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"No Record Deleted", Toast.LENGTH_SHORT).show();
    }

    private void insertRecord() {
        String name = editName.getText().toString();
        String phone = editPhone.getText().toString();

        if (helper.duplicationCheck(name, phone)) {
            Toast.makeText(this, "Insert Failed: Duplicated", Toast.LENGTH_SHORT).show();
        } else {
            long nOfRows = helper.insertUserByMethod(name, phone);

            if (nOfRows > 0)
                Toast.makeText(this, nOfRows + " Record Inserted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "No Record Inserted", Toast.LENGTH_SHORT).show();
        }
    }
}
