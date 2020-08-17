package com.aston.blindfitness.View;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyHelper extends SQLiteOpenHelper {


    public MyHelper(Context context) {
        super(context, "MyDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS Heart_Rate" + "(day DATETIME DEFAULT (date('now', 'localtime')) PRIMARY KEY," + "rate INTEGER DEFAULT 0)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertData(int valX, int valY){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("xValues", valX);
        contentValues.put("yValues", valY);

        db.insert("myTable", null, contentValues);
    }
}
