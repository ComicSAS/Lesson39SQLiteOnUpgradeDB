package com.example.lesson39sqliteonupgradedb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    DBHelper dbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DBHelper(this);
        db = dbh.getWritableDatabase();
        Log.d(LOG_TAG, " --- Staff db v." + db.getVersion() + " --- ");
        writeStaff(db);
        dbh.close();
    }

    // запрос данных и вывод в лог
    private void writeStaff(SQLiteDatabase db) {
        Cursor c = db.rawQuery("select * from people", null);
        logCursor(c, "Table people");
        c.close();

        c = db.rawQuery("select * from position", null);
        logCursor(c, "Table position");
        c.close();

        String sqlQuery = "select PL.name as Name, PS.name as Position, salary as Salary "
                + "from people as PL "
                + "inner join position as PS "
                + "on PL.posid = PS.id";
        c = db.rawQuery(sqlQuery, null);
        logCursor(c, "inner join");
        c.close();
    }

    // вывод в лог данных из курсора
    private void logCursor(Cursor c, String title) {
        if (c != null) {
            if (c.moveToFirst()) {
                Log.d(LOG_TAG, title + ". " + c.getCount() + " rows");
                StringBuilder sb = new StringBuilder();
                do {
                    sb.setLength(0);
                    for (String s : c.getColumnNames())
                        sb.append(s + " = "
                                + c.getString(c.getColumnIndex(s)) + "; ");
                    Log.d(LOG_TAG, sb.toString());
                } while (c.moveToNext());
            }
        } else
            Log.d(LOG_TAG, title + " Cursor is null");
    }
}
