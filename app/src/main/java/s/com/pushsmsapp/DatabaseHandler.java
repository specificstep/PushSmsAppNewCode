package s.com.pushsmsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.se.omapi.SEService;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MesaageHistoryManager";
    private static final String TABLE_CONTACTS = "MesaageHistory";
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DATE = "date";

    private static final String TABLE_AUTHCONTACTS = "AuthorizeContact";
    private static final String KEY_AUTHID = "id";
    private static final String KEY_AUTHSENDER = "sender";
    private static final String KEY_AUTHMESSAGE = "message";
    private static final String KEY_MSGNOCONTAIN = "msgnocontain";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SENDER + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_DATE + " TEXT" + ")";
        String CREATE_AUTHCONTACTS_TABLE = "CREATE TABLE " + TABLE_AUTHCONTACTS + "("
                + KEY_AUTHID + " INTEGER PRIMARY KEY," + KEY_AUTHSENDER + " TEXT,"
                + KEY_AUTHMESSAGE + " TEXT," + KEY_MSGNOCONTAIN + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_AUTHCONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHCONTACTS);
        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addContact(MesaageHistory contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENDER, contact.getSender()); // Contact Name
        values.put(KEY_MESSAGE, contact.getMessage()); // Contact Phone
        values.put(KEY_STATUS, contact.getStatus()); // Contact Phone
        values.put(KEY_DATE, contact.getDate()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    MesaageHistory getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_SENDER, KEY_MESSAGE, KEY_STATUS, KEY_DATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MesaageHistory contact = new MesaageHistory(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return contact
        return contact;
    }

    // code to get all contacts in a list view
    public List<MesaageHistory> getAllContacts() {
        List<MesaageHistory> contactList = new ArrayList<MesaageHistory>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MesaageHistory contact = new MesaageHistory();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.setSender(cursor.getString(1));
                contact.setMessage(cursor.getString(2));
                contact.setStatus(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateContact(MesaageHistory contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENDER, contact.getSender());
        values.put(KEY_MESSAGE, contact.getMessage());
        values.put(KEY_STATUS, contact.getStatus());
        values.put(KEY_DATE, contact.getDate());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.get_id()) });
    }

    // Deleting single contact
    public void deleteContact(MesaageHistory contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.get_id()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void deleteAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        /*db.delete(TABLE_AUTHCONTACTS, KEY_AUTHID + " = ?",
                new String[] { String.valueOf(contact.get_authid()) });*/
        db.execSQL("delete from "+ TABLE_CONTACTS);
        db.close();
    }

    public void deleteSevenDaysContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        /*db.delete(TABLE_AUTHCONTACTS, KEY_AUTHID + " = ?",
                new String[] { String.valueOf(contact.get_authid()) });*/
        db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + KEY_DATE + " <= date('now','-3 days')");
        db.close();
    }

    public void deleteFifteenDaysContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        /*db.delete(TABLE_AUTHCONTACTS, KEY_AUTHID + " = ?",
                new String[] { String.valueOf(contact.get_authid()) });*/
        db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + KEY_DATE + " <= date('now','-15 days')");
        db.close();
    }



    //Auth queries
    // code to add the new contact
    void addAuthContact(AuthorizeSenderClass contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHSENDER, contact.getAuthsender()); // Contact Name
        values.put(KEY_AUTHMESSAGE, contact.getAuthmessage()); // Contact Phone
        values.put(KEY_MSGNOCONTAIN, contact.getAuthmsgnocontain()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_AUTHCONTACTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    AuthorizeSenderClass getAuthContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_AUTHCONTACTS, new String[] { KEY_AUTHID,
                        KEY_AUTHSENDER, KEY_AUTHMESSAGE, KEY_MSGNOCONTAIN }, KEY_AUTHID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        AuthorizeSenderClass contact = new AuthorizeSenderClass(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return contact;
    }

    // code to get all contacts in a list view
    public List<AuthorizeSenderClass> getAllAuthContacts() {
        List<AuthorizeSenderClass> contactList = new ArrayList<AuthorizeSenderClass>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_AUTHCONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AuthorizeSenderClass contact = new AuthorizeSenderClass();
                contact.set_authid(Integer.parseInt(cursor.getString(0)));
                contact.setAuthsender(cursor.getString(1));
                contact.setAuthmessage(cursor.getString(2));
                contact.setAuthmsgnocontain(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateAuthContact(AuthorizeSenderClass contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHSENDER, contact.getAuthsender());
        values.put(KEY_AUTHMESSAGE, contact.getAuthmessage());
        values.put(KEY_MSGNOCONTAIN, contact.getAuthmsgnocontain());

        // updating row
        return db.update(TABLE_AUTHCONTACTS, values, KEY_AUTHID + " = ?",
                new String[] { String.valueOf(contact.get_authid()) });
    }

    // Deleting single contact
    public void deleteAuthContact(AuthorizeSenderClass contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AUTHCONTACTS, KEY_AUTHID + " = ?",
                new String[] { String.valueOf(contact.get_authid()) });
        db.close();
    }

    // Deleting all contact
    public void deleteAllAuthContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        /*db.delete(TABLE_AUTHCONTACTS, KEY_AUTHID + " = ?",
                new String[] { String.valueOf(contact.get_authid()) });*/
        db.execSQL("delete from "+ TABLE_AUTHCONTACTS);
        db.close();
    }

    // Getting contacts Count
    public int getAuthContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_AUTHCONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }



}
