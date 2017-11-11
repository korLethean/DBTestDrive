package com.example.lethean.dbtestdrive;

import android.provider.BaseColumns;

/**
 * Created by letehan on 2017-11-09.
 */

public class UserContract {
    public static final String DB_NAME          = "user.db";
    public static final int DATABASE_VERSION    = 1;
    private static final String TEXT_TYPE       = " TEXT";
    private static final String COMMA_SEP       = ",";


    private UserContract() {}

    public static class Users implements BaseColumns {
        public static final String TABLE_NAME   = "Users";
        public static final String KEY_NAME     = "Name";
        public static final String KEY_PHONE    = "Phone";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                KEY_NAME + TEXT_TYPE + COMMA_SEP +
                KEY_PHONE + TEXT_TYPE +  " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
