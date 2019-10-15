package com.example.lesson39sqliteonupgradedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    String LOG_TAG = "myLogs";

    public DBHelper(Context context) {
        super(context, "staff", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, " --- onCreate database --- ");

//        String[] people_name = {"Иван", "Марья", "Петр", "Антон", "Даша",
//                "Борис", "Костя", "Игорь"};
//        String[] people_positions = {"Программер", "Бухгалтер",
//                "Программер", "Программер", "Бухгалтер", "Директор",
//                "Программер", "Охранник"};

        String[] people_name = {"Иван", "Марья", "Петр", "Антон", "Даша",
                "Борис", "Костя", "Игорь"};
        int[] people_posid = {2, 3, 2, 2, 3, 1, 2, 4};

        // данные для таблицы должностей
        int[] position_id = {1, 2, 3, 4};
        String[] position_name = {"Директор", "Программер", "Бухгалтер",
                "Охранник"};
        int[] position_salary = {15000, 13000, 10000, 8000};

        ContentValues cv = new ContentValues();


        // создаем таблицу должностей
        db.execSQL("create table position ("
                + "id integer primary key,"
                + "name text, salary integer" + ");");

        // заполняем ее
        for (int i = 0; i < position_id.length; i++) {
            cv.clear();
            cv.put("id", position_id[i]);
            cv.put("name", position_name[i]);
            cv.put("salary", position_salary[i]);
            db.insert("position", null, cv);
        }

        // создаем таблицу людей
        db.execSQL("create table people ("
                + "id integer primary key autoincrement,"
                + "name text, posid integer);");

        // заполняем ее
        for (int i = 0; i < people_name.length; i++) {
            cv.clear();
            cv.put("name", people_name[i]);
            cv.put("posid", people_posid[i]);
            db.insert("people", null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, " --- onUpgrade database from " + oldVersion
                + " to " + newVersion + " version --- ");

        if (oldVersion == 1 && newVersion == 2) {
            ContentValues cv = new ContentValues();

            // данные для таблицы должностей
            int[] position_id = {1, 2, 3, 4};
            String[] position_name = {"Директор", "Программер",
                    "Бухгалтер", "Охранник"};
            int[] position_salary = {15000, 13000, 10000, 8000};

            db.beginTransaction();
            try {
                // создаем таблицу должностей
                db.execSQL("create table position ("
                        + "id integer primary key,"
                        + "name text, salary integer);");

                // заполняем ее
                for (int i = 0; i < position_id.length; i++) {
                    cv.clear();
                    cv.put("id", position_id[i]);
                    cv.put("name", position_name[i]);
                    cv.put("salary", position_salary[i]);
                    db.insert("position", null, cv);
                }
                db.execSQL("alter table people add column posid integer;");

                for (int i = 0; i < position_id.length; i++) {
                    cv.clear();
                    cv.put("posid", position_id[i]);
                    db.update("people", cv, "position = ?", new String[]{position_name[i]});
                }
                db.execSQL("create temporary table people_tmp ("
                        + "id integer, name text, position text, posid integer);");

                db.execSQL("insert into people_tmp select id,name,position,posid from people;");
                db.execSQL("drop table people;");

                db.execSQL("create table people ("
                        + "id integer primary key autoincrement,"
                        + "name text, posid integer);");

                db.execSQL("insert into people select id,name,posid from people_tmp;");
                db.execSQL("drop table people_tmp;");

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}
