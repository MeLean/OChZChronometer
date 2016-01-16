package oczcalculator.milen.com.ochzchronometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    //singleton pattern for DB
    private static DBHelper sInstance;

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    static final private String DB_NAME = "OChZDatabase.db";
    static final private int DB_CURRENT_VERSION = 1;
    static final String TABLE_NAME = "tasks";
    static final String TABLE_COLUMN_ID = "_id";
    static final String TABLE_COLUMN_EMPLOYEE_NAME = "employeeName";
    static final String TABLE_COLUMN_TASK_NAME = "taskName";
    static final String TABLE_COLUMN_SECONDS_WORKED = "secondsWorked";
    static final String TABLE_COLUMN_IS_INTERRUPTED = "isInterrupted";
    static final String TABLE_COLUMN_DATE_ADDED = "dateAdded";
    SQLiteDatabase db;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = String.format(
                "create " +
                        "table %s " +
                        "(%s integer primary key autoincrement, " +
                        "%s text not null, " +
                        "%s text not null, " +
                        "%s integer not null, " +
                        "%s integer not null, " +
                        "%s text not null);",

                TABLE_NAME,
                TABLE_COLUMN_ID,
                TABLE_COLUMN_EMPLOYEE_NAME,
                TABLE_COLUMN_TASK_NAME,
                TABLE_COLUMN_SECONDS_WORKED,
                TABLE_COLUMN_IS_INTERRUPTED,
                TABLE_COLUMN_DATE_ADDED
        );

        db.execSQL(sqlCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("drop table if exist '%s'", TABLE_NAME));
        onCreate(db);
    }

    public void open() throws SQLException {
        db = getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public void addTask(TaskEntity task) throws SQLException {

        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_COLUMN_EMPLOYEE_NAME, task.getEmployee());
        contentValues.put(TABLE_COLUMN_TASK_NAME, task.getTaskName());
        contentValues.put(TABLE_COLUMN_SECONDS_WORKED, task.getSecondsWorked());
        contentValues.put(TABLE_COLUMN_IS_INTERRUPTED, (task.isNotInterrupted() ? 1 : 0));
        contentValues.put(TABLE_COLUMN_DATE_ADDED, String.valueOf(task.getDateAdded()));

        this.db.insert(TABLE_NAME, null, contentValues);
        close();
    }

    public ArrayList<TaskEntity> getAllTasks() throws ParseException, SQLException {
        String[] columns = new String[]{
                TABLE_COLUMN_ID,
                TABLE_COLUMN_EMPLOYEE_NAME,
                TABLE_COLUMN_TASK_NAME,
                TABLE_COLUMN_SECONDS_WORKED,
                TABLE_COLUMN_IS_INTERRUPTED,
                TABLE_COLUMN_DATE_ADDED
        };

        open();
        Cursor taskCursor = this.db.query(TABLE_NAME, columns, null, null, null, null, null);
        if (taskCursor == null) {
            return null;
        }

        ArrayList<TaskEntity> tasksFromDB = new ArrayList<>();
        if (taskCursor.moveToFirst()) {
            do {
                String employeeName = taskCursor.getString(taskCursor.getColumnIndex(TABLE_COLUMN_EMPLOYEE_NAME));
                int id = taskCursor.getInt(taskCursor.getColumnIndex(TABLE_COLUMN_ID));
                String taskName = taskCursor.getString(taskCursor.getColumnIndex(TABLE_COLUMN_TASK_NAME));
                long secondsWorked = (long) taskCursor.getInt(taskCursor.getColumnIndex(TABLE_COLUMN_SECONDS_WORKED));
                boolean isNotInterrupted = taskCursor.getInt(taskCursor.getColumnIndex(TABLE_COLUMN_IS_INTERRUPTED)) == 1 ? true : false;
                String dateAdded = taskCursor.getString(taskCursor.getColumnIndex(TABLE_COLUMN_DATE_ADDED));

                tasksFromDB.add(new TaskEntity(employeeName, id, taskName, secondsWorked, isNotInterrupted, dateAdded));
            } while (taskCursor.moveToNext());
        }
        taskCursor.close();
        close();
        return tasksFromDB;
    }

    public void deleteAllTasks() throws SQLException {
        open();
        db.delete(TABLE_NAME, null, null);
        close();
    }
}
