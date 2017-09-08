package com.pongodev.recipesapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHelperRecipes extends SQLiteOpenHelper {
    // path of database when app installed on device
    private static String DB_PATH = Utils.ARG_DATABASE_PATH;

    // create database name and version
    private final static String DB_NAME = "db_recipes";
    public final static int DB_VERSION = 1;
    public static SQLiteDatabase db;

    private final Context context;

    // create table name and fields
    private final String TABLE_RECIPES = "tbl_recipes";
    private final String RECIPE_ID = "recipe_id";
    private final String RECIPE_NAME = "recipe_name";
    private final String COOK_TIME = "cook_time";
    private final String SERVINGS = "servings";
    private final String SUMMARY = "summary";
    private final String INGREDIENTS = "ingredients";
    private final String STEPS = "steps";
    private final String IMAGE = "image";

    private final String TABLE_CATEGORIES = "tbl_categories";
    private final String CATEGORY_ID = "category_id";
    private final String CATEGORY_NAME = "category_name";


    public DBHelperRecipes(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    // method to create database
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read = null;

        // if database exist delete database and copy the new one
        if(dbExist){
            deleteDataBase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }else{
            db_Read = this.getReadableDatabase();
            db_Read.close();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    // method to delete database
    private void deleteDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        dbFile.delete();
    }

    // method to check database on path
    private boolean checkDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    // method to copy database from app to db path
    private void copyDataBase() throws IOException{

        InputStream myInput = context.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    // method to open database and read it
    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    // close database after it is used
    @Override
    public void close() {
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // method to get all data from database based on keyword
    public ArrayList<ArrayList<Object>> getAllCategoriesData(){
        ArrayList<ArrayList<Object>>  dataArrays= new ArrayList<ArrayList<Object>>();

        Cursor cursor = null;

        try{
            cursor = db.query(
                    TABLE_CATEGORIES,
                    new String[]{CATEGORY_ID, CATEGORY_NAME},
                    null, null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<Object>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));

                    dataArrays.add(dataList);
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return dataArrays;
    }


    // method to get all data from database based on keyword
    public ArrayList<ArrayList<Object>> getAllRecipesData(String id){
        ArrayList<ArrayList<Object>>  dataArrays= new ArrayList<ArrayList<Object>>();

        Cursor cursor = null;

        try{
            cursor = db.query(
                    TABLE_RECIPES,
                    new String[]{RECIPE_ID, RECIPE_NAME, COOK_TIME, SERVINGS, IMAGE},
                    CATEGORY_ID +" = "+id,
                    null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<Object>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));

                    dataArrays.add(dataList);
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return dataArrays;
    }

    // method to get all data from database based on keyword
    public ArrayList<ArrayList<Object>> getRecipesByName(String keyword){
        ArrayList<ArrayList<Object>>  dataArrays= new ArrayList<ArrayList<Object>>();

        Cursor cursor = null;

        try{
            cursor = db.query(
                    TABLE_RECIPES,
                    new String[]{RECIPE_ID, RECIPE_NAME, COOK_TIME, SERVINGS, IMAGE},
                    RECIPE_NAME +" LIKE '%"+keyword+"%'",
                    null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<Object>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));

                    dataArrays.add(dataList);
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return dataArrays;
    }

    // get recipe data based on the id
    public ArrayList<Object> getRecipeDetail(String id){

        ArrayList<Object> rowArray = new ArrayList<Object>();
        Cursor cursor;

        try{
            /*
            cursor = db.query(
                    TABLE_RECIPES,
                    new String[]{RECIPE_ID, RECIPE_NAME, COOK_TIME, SERVINGS, SUMMARY, INGREDIENTS, STEPS, IMAGE},
                    RECIPE_ID + "=" + id,
                    null, null, null, null, null);
                    */


            String query = "SELECT recipe_id, recipe_name, c.category_id, " +
                    "category_name, cook_time, servings, summary, " +
                    "ingredients, steps, image " +
                    "FROM tbl_categories c, tbl_recipes r " +
                    "WHERE r.recipe_id "+" = "+id+" AND r.category_id = c.category_id";

            cursor = db.rawQuery(query, null);


            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    rowArray.add(cursor.getLong(0));
                    rowArray.add(cursor.getString(1));
                    rowArray.add(cursor.getLong(2));
                    rowArray.add(cursor.getString(3));
                    rowArray.add(cursor.getString(4));
                    rowArray.add(cursor.getString(5));
                    rowArray.add(cursor.getString(6));
                    rowArray.add(cursor.getString(7));
                    rowArray.add(cursor.getString(8));
                    rowArray.add(cursor.getString(9));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
        }
        catch (SQLException e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return rowArray;
    }
}
