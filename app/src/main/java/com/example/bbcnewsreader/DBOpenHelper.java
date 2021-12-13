package com.example.bbcnewsreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "NewsDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "NEWS";
    public final static String COL_ID = "id";
    public final static String COL_TITLE = "title";
    public final static String COL_DESCRIPTION = "description";
    public final static String COL_LINK = "link";
    public final static String COL_PUBDATE = "pubDate";


    public DBOpenHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT, "
                + COL_DESCRIPTION + " TEXT, "
                + COL_LINK + " TEXT, "
                + COL_PUBDATE + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
