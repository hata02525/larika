package com.example.fluper.larika_user_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fluper.larika_user_app.bean.AddProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by rohit on 30/6/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(dishId text,vendorid text,name text,price text,quantity text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact (String dishId,String vendorId,String quantity,String name,String price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("price", price);
        contentValues.put("dishId", dishId);
        contentValues.put("vendorid", vendorId);
        contentValues.put("quantity", quantity);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (String name,String price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("place", price);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public List<AddProductModel> getAllCotacts() {
        ArrayList<AddProductModel> addProductModelList = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns={"name","price","dishId","vendorid","quantity"};

        Cursor cursor =  db.rawQuery( "select * from contacts", null );
        if (cursor.moveToFirst()) {
            do {
                AddProductModel addProductModel = new AddProductModel();
//                addProductModel.setDishId(cursor.getString(1));
//                addProductModel.setVendorId(cursor.getString(2));
                addProductModel.setDishName(cursor.getString(0));
                addProductModel.setDishPrice(cursor.getString(1));
                addProductModel.setDishId(cursor.getString(2));
//                addProductModel.setVendorId(cursor.getString(3));
//                addProductModel.setDishStock(cursor.getString(4));
//                addProductModel.setDishStock(cursor.getString(5));
                // Adding contact to list
                addProductModelList.add(addProductModel);
            } while (cursor.moveToNext());
        }

        return addProductModelList;
    }
}