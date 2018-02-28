package com.example.fluper.larika_user_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fluper.larika_user_app.bean.AddMyProductModel;

import java.util.ArrayList;

/**
 * Created by rohit on 30/6/17.
 */

public class DbHandler {
    private static final String DATABASE_NAME="cart_activity";
    private static final int DATABASE_VERSION = 2;
    private static final String STUDENT_TABLE = "cart";
    private static final String Create_Query = "CREATE TABLE "+STUDENT_TABLE+ "(" +"name"+ " TEXT, "
            +"price"+" TEXT, "
            +"dish_id"+" TEXT, "
            +"vendor_id"+" TEXT, "
            +"quan"+" TEXT, "
            +"dish_stock"+" TEXT);";
    public static final String DROP_QUERY="DROP TABLE "+ STUDENT_TABLE +";";
    private class DbInternal extends SQLiteOpenHelper {
        Context context;

        public DbInternal(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context=context;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Create_Query);

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_QUERY);
            onCreate(db);
        }
        public long getProfilesCount() {
            DbInternal dbInternal=new DbInternal(context);
            SQLiteDatabase db = dbInternal.getReadableDatabase();
            long cnt  = DatabaseUtils.queryNumEntries(db, STUDENT_TABLE);
            db.close();
            return cnt;
        }
    }
    DbInternal dbInternal;
    Context ourcontext;
    SQLiteDatabase database;
    public DbHandler(Context context) {
        this.ourcontext=context;
    }
    public DbHandler open() {
        dbInternal=new DbInternal(ourcontext);
        database=dbInternal.getWritableDatabase();
        return this;
    }

    public boolean insertContact (String dishId,String vendorId,String dish_stock,String name,String price,String quan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("price", price);
        contentValues.put("dish_id", dishId);
        contentValues.put("vendor_id", vendorId);
        contentValues.put("quan", quan);
        contentValues.put("dish_stock", dish_stock);
        database.insert(STUDENT_TABLE, null, contentValues);
        return true;
    }




    public  void close() {
        database.close();
    }




    public ArrayList<AddMyProductModel> getData() {
        ArrayList<AddMyProductModel> cartModelArrayList=new ArrayList<>();
        String[] columns={"name","price","dish_id","vendor_id","quan","dish_stock"};
        Cursor cursor=database.query(STUDENT_TABLE,columns,null,null,null,null,null,null);
        int i_NAME=cursor.getColumnIndex("name");
        int PRICE=cursor.getColumnIndex("price");
        int DISH_ID=cursor.getColumnIndex("dish_id");
        int VENDOR_ID=cursor.getColumnIndex("vendor_id");
        int QUANTITY=cursor.getColumnIndex("quan");
        int DISH_STOCK=cursor.getColumnIndex("dish_stock");
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            AddMyProductModel cartModel=new AddMyProductModel();
            cartModel.setDishName(cursor.getString(i_NAME));
            cartModel.setDishPrice(cursor.getString(PRICE));
            cartModel.setDishId(cursor.getString(DISH_ID));
            cartModel.setVendorId((cursor.getString(VENDOR_ID)));
            cartModel.setQuantity(cursor.getString(QUANTITY));
            cartModel.setDishStock(cursor.getString(DISH_STOCK));
            cartModelArrayList.add(cartModel);
        }
        return cartModelArrayList;
    }
    public long rowCount()
    {
        DbInternal dbInternal=new DbInternal(ourcontext);
        SQLiteDatabase db = dbInternal.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, STUDENT_TABLE);
        db.close();
        return cnt;
    }
    public void deleteARow(String name){
        database.delete(STUDENT_TABLE, "name" + " = ?", new String[] { name });
        database.close();
    }



    public int updateCol(String category_name,String quantity){
        dbInternal=new DbInternal(ourcontext);
        database=dbInternal.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quan",quantity);
        // updating row
        return  database.update(STUDENT_TABLE, values, "name" + " = ?",
                new String[] { category_name });
    }
    public void deleteAll(){
        dbInternal=new DbInternal(ourcontext);
        database=dbInternal.getWritableDatabase();
        database.execSQL(STUDENT_TABLE); //delete all rows in a table
        database.close();




    }
    public void clearCart() {
        dbInternal=new DbInternal(ourcontext);
        database=dbInternal.getWritableDatabase();
        database.execSQL("DELETE FROM " + STUDENT_TABLE);
//        database.execSQL("DELETE FROM " + CART_ADDON_TABLE_NAME);
        database.close();
    }
}
