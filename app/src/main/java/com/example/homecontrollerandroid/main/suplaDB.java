package com.example.homecontrollerandroid.main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class suplaDB {

    public static final String KEY_ROW_ID_DEVICES = "_idDevices";
    public static final String KEY_DEVICE = "_device";
    public static final String KEY_LINK = "_link";
    public static final String KEY_BRIGHTNESS = "_brightness";

    public static final String KEY_ROW_ID_SCENES = "_idScene";
    public static final String KEY_SCENE = "_scene";
    public static final String KEY_COMMAND = "_command";
    public static final String KEY_VALUE = "_value";

    private final String DATABASE_NAME = "SuplaDatabase";
    private final String DATA_TABLE_DEVICES = "DevicesTable";
    private final String DATA_TABLE_SCENES = "ScenesTable";
    private final int DATABASE_VERSION = 1;

    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabse;

    public suplaDB(Context context){

        ourContext = context;

    }

    private class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String sqlCode1 = "CREATE TABLE " + DATA_TABLE_DEVICES + " (" +
                    KEY_ROW_ID_DEVICES + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_DEVICE + " TEXT NOT NULL, " + KEY_LINK + " TEXT NOT NULL, " +
                    KEY_BRIGHTNESS + " TEXT NOT NULL);";

            String sqlCode2 = "CREATE TABLE " + DATA_TABLE_SCENES + " (" +
                    KEY_ROW_ID_SCENES + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SCENE + " TEXT NOT NULL, " + KEY_COMMAND + " TEXT NOT NULL, " +
                    KEY_VALUE + " TEXT NOT NULL);";

            sqLiteDatabase.execSQL(sqlCode1);
            sqLiteDatabase.execSQL(sqlCode2);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE_DEVICES);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE_SCENES);
            onCreate(sqLiteDatabase);

        }
    }

    public suplaDB open() throws SQLException{

        ourHelper = new DBHelper(ourContext);
        ourDatabse = ourHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        ourHelper.close();

    }

    public long createDevice(String device, String link, String brightness){

        ContentValues cv = new ContentValues();
        cv.put(KEY_DEVICE, device);
        cv.put(KEY_LINK, link);
        cv.put(KEY_BRIGHTNESS, brightness);
        return ourDatabse.insert(DATA_TABLE_DEVICES,null,cv);

    }

    public long createScene(String name, String commands, String value)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_SCENE, name);
        cv.put(KEY_COMMAND,commands);
        cv.put(KEY_VALUE, value);
        return ourDatabse.insert(DATA_TABLE_SCENES, null, cv);

    }

    public long deleteDevice(String deviceName)
    {
        return ourDatabse.delete(DATA_TABLE_DEVICES, KEY_DEVICE + "=?", new String[]{deviceName});

    }

    public long deleteScene(String scene)
    {
        return ourDatabse.delete(DATA_TABLE_SCENES, KEY_SCENE + "=?", new String[]{scene});

    }

    public String getRowIDFromDevice(String device)
    {
        String[] columns = new String[]{KEY_ROW_ID_DEVICES, KEY_DEVICE};
        Cursor c = ourDatabse.query(DATA_TABLE_DEVICES, columns, KEY_DEVICE + "=?",new String[] {device}, null, null, null);

        c.moveToFirst();

        return c.getString(c.getColumnIndex(KEY_ROW_ID_DEVICES));
    }

    public long updateDevice(String rowId, String device, String link, String brightness)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ROW_ID_DEVICES, rowId);
        cv.put(KEY_DEVICE, device);
        cv.put(KEY_LINK, link);
        cv.put(KEY_BRIGHTNESS, brightness);

        return ourDatabse.update(DATA_TABLE_DEVICES, cv, KEY_ROW_ID_DEVICES + "=?", new String[]{rowId});

    }

    public long updateScene(String rowId, String scene, String commands, String value)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ROW_ID_SCENES, rowId);
        cv.put(KEY_SCENE, scene);
        cv.put(KEY_COMMAND, commands);
        cv.put(KEY_VALUE, value);

        return ourDatabse.update(DATA_TABLE_SCENES, cv, KEY_ROW_ID_SCENES + "=?", new String[]{rowId});

    }

    public ArrayList<String[]> getDevices(){

        ArrayList<String[]> result = new ArrayList<>();

        String[] columns = new String[]{KEY_ROW_ID_DEVICES, KEY_DEVICE, KEY_LINK, KEY_BRIGHTNESS};
        Cursor c = ourDatabse.query(DATA_TABLE_DEVICES, columns, null,null, null, null, null);

        int iRowID = c.getColumnIndex(KEY_ROW_ID_DEVICES);
        int iDevice = c.getColumnIndex(KEY_DEVICE);
        int iLink = c.getColumnIndex(KEY_LINK);
        int iBrightness = c.getColumnIndex(KEY_BRIGHTNESS);

        for (c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            String[] s = {c.getString(iRowID), c.getString(iDevice), c.getString(iLink), c.getString(iBrightness)};
            result.add(s);
        }

        c.close();

        return result;
    }

    public ArrayList<String> getScenes() {

        ArrayList<String> result = new ArrayList<>();

        try {
            String[] columns = new String[]{KEY_SCENE};
            Cursor c = ourDatabse.query(DATA_TABLE_SCENES, columns, null, null, null, null, null);

        int iScene = c.getColumnIndex(KEY_SCENE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String s = c.getString(iScene);
            if(!result.contains(s))
            result.add(s);
        }
            c.close();
    }

        catch (Exception e)
        {
            Toast.makeText(ourContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public ArrayList<String[]> getCommandsForScene(String scene, boolean on, String suplaServerAddress)
    {
        ArrayList<String[]> result = new ArrayList<>(); //0 - command, 1- device, 2-value
        Cursor cScenes = null;
        Cursor cDevices = null;

        String[] columnsScenes = new String[] {KEY_SCENE, KEY_COMMAND, KEY_VALUE};
        String[] columnsDevices = new String[] {KEY_DEVICE,KEY_LINK, KEY_BRIGHTNESS};
        try {
            cScenes = ourDatabse.query(DATA_TABLE_SCENES, columnsScenes, KEY_SCENE + "=?", new String[]{scene}, null, null, null);

            int iScene = cScenes.getColumnIndex(KEY_SCENE);
            int iCommand = cScenes.getColumnIndex(KEY_COMMAND);
            int iValue = cScenes.getColumnIndex(KEY_VALUE);

            for (cScenes.moveToFirst(); !cScenes.isAfterLast(); cScenes.moveToNext()) {
                cScenes.getString(iCommand);
                cDevices = ourDatabse.query(DATA_TABLE_DEVICES, columnsDevices, KEY_DEVICE + "=?", new String[]{cScenes.getString(iCommand)}, null, null, null);
                cDevices.moveToFirst();
                String command = cDevices.getString(cDevices.getColumnIndex(KEY_LINK));

                if ((cScenes.getString(iValue).equals("100")) && on == true && (cDevices.getString(cDevices.getColumnIndex(KEY_BRIGHTNESS)).equals("false")))
                    command = suplaServerAddress + command + "turn-on";

                else if (on == false)
                    command = suplaServerAddress + command + "turn-off";

                else if ((cDevices.getString(cDevices.getColumnIndex(KEY_BRIGHTNESS)).equals("true")) && on == true)
                    command = suplaServerAddress + command + "set-rgbw-parameters?brightness=" + cScenes.getString(iValue);

                result.add(new String[]{command, cScenes.getString(iCommand), cScenes.getString(iValue)});

            }
        }
        catch (Exception e)
        {
            Toast.makeText(ourContext, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        finally
        {
            cDevices.close();
            cScenes.close();
        }


        return result;
    }

}
