package com.example.fluper.larika_user_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fluper.larika_user_app.bean.AddProductModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.customtabs.CustomTabsIntent.KEY_ID;

/**
 * Created by rohit on 30/6/17.
 */

public class AddToCartDataBaseHelper extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "addProductManager";

    // Contacts table name
    private static final String TABLE_PRODUCTS = "products";

    // Contacts Table Columns names
    private static final String KEY_DISH_ID = "dish_id";
    private static final String KEY_VENDOR_ID = "vendor_id";
    private static final String KEY_DISH_NAME = "dish_name";
    private static final String KEY_DISH_TITLE = "dish_title";
    private static final String KEY_DISH_PRICE = "price";
    private static final String KEY_DISH_STOCK = "quantity";
    public AddToCartDataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
//        String CREATE_PRODUCT_TABLE="CREATE TABLE " + TABLE_PRODUCTS + "(" +KEY_DISH_ID + "TEXT,"+KEY_VENDOR_ID + "TEXT,"
//                + KEY_DISH_NAME +"TEXT,"+ KEY_DISH_TITLE + "TEXT,"+ KEY_DISH_PRICE + "TEXT,"+ KEY_DISH_STOCK + "TEXT"+")";
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_DISH_NAME + " TEXT,"
                + KEY_DISH_PRICE + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addProduct(AddProductModel addProductModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_DISH_ID, addProductModel.getDishId());
//        values.put(KEY_VENDOR_ID, addProductModel.getVendorId());
        values.put(KEY_DISH_NAME, addProductModel.getDishName());
        values.put(KEY_DISH_PRICE, addProductModel.getDishPrice());
//        values.put(KEY_DISH_STOCK, addProductModel.getDishStock());
//        values.put(KEY_DISH_TITLE, "");

        // Inserting Row
        db.insert(TABLE_PRODUCTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
//    public AddProductModel getContact(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_PRODUCTS, new String[] { KEY_ID,
//                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        AddProductModel addProductModel = new AddProductModel(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2));
//        // return contact
//        return addProductModel;
//    }



    // Getting All Contacts
    public List<AddProductModel> getAllProducts() {
        List<AddProductModel> addProductModelList = new ArrayList<AddProductModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AddProductModel addProductModel = new AddProductModel();
//                addProductModel.setDishId(cursor.getString(1));
//                addProductModel.setVendorId(cursor.getString(2));
                addProductModel.setDishName(cursor.getString(0));
                addProductModel.setDishPrice(cursor.getString(1));
//                addProductModel.setDishStock(cursor.getString(5));
                // Adding contact to list
                addProductModelList.add(addProductModel);
            } while (cursor.moveToNext());
        }

        // return contact list
        return addProductModelList;
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


    // Updating single contact
    public int updateContact(AddProductModel addProductModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DISH_ID, addProductModel.getDishId());
        values.put(KEY_VENDOR_ID, addProductModel.getVendorId());
        values.put(KEY_DISH_NAME, addProductModel.getDishName());
        values.put(KEY_DISH_PRICE, addProductModel.getDishPrice());
        values.put(KEY_DISH_STOCK, addProductModel.getDishStock());


        // updating row
        return db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(addProductModel.getDishId()) });
    }

    // Deleting single contact
    public void deleteContact(AddProductModel addProductModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_ID + " = ?",
                new String[] { String.valueOf( addProductModel.getDishId()) });
        db.close();
    }
}
