package com.example.demo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.demo.modal.Todo
import com.example.demo.modal.User
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class DbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "user"

        private const val TABLE_CONTACTS = "contacts"
        private const val KEY_NAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_EMAIL = "email"

        private const val TABLE_TODOS="todo"
        private const val KEY_ID = "id"
        private const val DESCRIPTION = "description"
        private const val USER_EMAIL = "user_email"
        private const val JOB_NAME = "job"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + DbHelper.TABLE_CONTACTS + "("
                + DbHelper.KEY_EMAIL + " TEXT PRIMARY KEY,"
                + DbHelper.KEY_NAME + " TEXT,"
                + DbHelper.KEY_PASSWORD + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
        /* create table contacts ( email  TEXT PRIMARY KEY , username TEXT ,password TEXT ) */

        val CREATE_TODO_TABLE = ("CREATE TABLE " + DbHelper.TABLE_TODOS + "("
                + DbHelper.KEY_ID + " INTEGER PRIMARY KEY,"
                + DbHelper.DESCRIPTION + " TEXT,"
                + DbHelper.JOB_NAME + " TEXT,"
                + DbHelper.USER_EMAIL + " TEXT" + ")")
        db?.execSQL(CREATE_TODO_TABLE)
        // create table todo (id INTEGER PRIMARY KEY  , description TEXT, job TEXT ,user_email TEXT ) */

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        toDeletetable()
        onCreate(db)
    }

    // Add new User
    fun addUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DbHelper.KEY_NAME, user.username) // Contact Username
        values.put(DbHelper.KEY_EMAIL, user.email)  //  Email address
        values.put(DbHelper.KEY_PASSWORD, user.pasword)  // Password

        // Inserting Row
        db.insert(DbHelper.TABLE_CONTACTS, null, values)
        db.close()
    }

    val usersCount: Int
        get() {
            val countQuery = "SELECT  * FROM ${DbHelper.TABLE_CONTACTS}"
            val db = this.readableDatabase
            val cursor: Cursor = db.rawQuery(countQuery, null)
            return cursor.count
        }

    fun deleteAllUser():Unit {
        val db = this.writableDatabase
        db.execSQL("delete from ${DbHelper.TABLE_CONTACTS}")
    }

    val allUsers: List<User>
        get() {
            val list: MutableList<User> = ArrayList<User>()
            val selectQuery = "SELECT  * FROM ${DbHelper.TABLE_CONTACTS}"
            val db = this.writableDatabase
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            /*
            * table
            *  row -> 1 id ,username,password,email
            *  row -> 2 id ,username,password,email
            *  row -> 3 id ,username,password,email
            * */

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    println("+++++++++++++++++++++++++++++++++++++++++++++")
                    println("cursor pointer at 0 : ${cursor.getString(0)}")
                    println("cursor pointer at 1 : ${cursor.getString(1)}")
                    println("cursor pointer at 2 : ${cursor.getString(2)}")
                    println("+++++++++++++++++++++++++++++++++++++++++++++")

                    val contact = User("1", username= cursor.getString(1), pasword = cursor.getString(2),email = cursor.getString(0) )
                    // Adding contact to list
                    list.add(contact)
                } while (cursor.moveToNext())
            }

            return list
        }
    // get User from contact
    fun getUser(email: String): User? {
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            DbHelper.TABLE_CONTACTS,
            arrayOf(
                DbHelper.KEY_EMAIL,
                DbHelper.KEY_NAME,
                DbHelper.KEY_PASSWORD
            ),
            "${DbHelper.KEY_EMAIL}=?",
            arrayOf(email.toString()),
            null,
            null,
            null,
            null
        )
        if (cursor != null)
            cursor.moveToFirst()
        // return contact
        println("+++++++++++++++++++++++++++++++++++++++++++++")
        println(" GET USER NAME : $email")
        println("+++++++++++++++++++++++++++++++++++++++++++++")
        println("cursor pointer at 0 : ${cursor?.getString(0)}")
        println("cursor pointer at 1 : ${cursor?.getString(1)}")
        println("cursor pointer at 2 : ${cursor?.getString(2)}")
        println("+++++++++++++++++++++++++++++++++++++++++++++")

        val user = cursor?.let {
            User(
                id= "1",
                email = cursor.getString(0),
                username = cursor.getString(1),
                pasword=cursor.getString(2)
            )
        }
        return  user
    }
    // update user
    fun updateUser(user: User): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DbHelper.KEY_NAME, user.username) // Contact Username
        values.put(DbHelper.KEY_PASSWORD, user.pasword)  // Password

        // updating row
        return db.update(
            DbHelper.TABLE_CONTACTS,
            values,
            "${DbHelper.KEY_EMAIL} = ?",
            arrayOf(user.email)
        )
    }

    // Deleting single contact
    fun deleteUser(user: User?) {
        val db = this.writableDatabase
        db.delete(DbHelper.TABLE_CONTACTS, "${DbHelper.KEY_EMAIL} = ?", arrayOf(user?.email))
        Log.d("User Deleted","User Deleted")
        db.close()
    }

    // to delete table contact
    fun toDeletetable() {
        var db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS "+DbHelper.TABLE_CONTACTS)
        //createTable()
    }
    // to delete table Todo
    fun toDeletetodotable() {
        var db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS "+DbHelper.TABLE_TODOS)
    }

    // create table contact


    // code to add the new contact
    fun addTodo(todo: Todo) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DbHelper.DESCRIPTION, todo.description) // Contact Username
        values.put(DbHelper.USER_EMAIL, todo.user_email)  //  Email address
        values.put(DbHelper.JOB_NAME, todo.job)  //  Email address

        // Inserting Row
        db.insert(DbHelper.TABLE_TODOS, null, values)
        db.close()
    }
    val alltodos: List<Todo>
        get() {
            val list: MutableList<Todo> = ArrayList<Todo>()
            // Select All Query
            val selectQuery = "SELECT  * FROM ${DbHelper.TABLE_TODOS}"
            val db = this.writableDatabase
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            /*
            * table
            *  row -> 1 id ,username,password,email
            *  row -> 2 id ,username,password,email
            *  row -> 3 id ,username,password,email
            * */

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    println("+++++++++++++++++++++++++++++++++++++++++++++")
                    println("cursor pointer at 0 : ${cursor.getString(0)}")
                    println("cursor pointer at 1 : ${cursor.getString(1)}")
                    println("cursor pointer at 2 : ${cursor.getString(2)}")
                    println("cursor pointer at 3 : ${cursor.getString(3)}")
                    println("+++++++++++++++++++++++++++++++++++++++++++++")

                    val todo = Todo(id = cursor.getString(0), description= cursor.getString(1),user_email = cursor.getString(3) ,job = cursor.getString(2))
                    // Adding contact to list
                    list.add(todo)
                } while (cursor.moveToNext())
            }

            // return contact list
            return list
        }
        // Get User by email
        fun getTodoByEmail(email: String,offset:String):MutableList<Todo> {

            var list :ArrayList<Todo> = ArrayList()
            val db = this.readableDatabase
            val cursor: Cursor? = db.query(
                DbHelper.TABLE_TODOS,
                arrayOf(
                    DbHelper.KEY_ID,
                    DbHelper.DESCRIPTION,
                    DbHelper.USER_EMAIL,
                    DbHelper.JOB_NAME
                ),
                "${DbHelper.USER_EMAIL}=?",
                arrayOf(email.toString()),
                null,
                null,
                null,
                "$offset,5"
            )

                /*
                * table
                *  row -> 1 id ,username,password,email
                *  row -> 2 id ,username,password,email
                *  row -> 3 id ,username,password,email
                * */

            // looping through all rows and adding to list
            if (cursor!!.moveToFirst()) {
                do {
                    println("+++++++++++++++++++++++++++Filter with email++++++++++++++++++")
                    println("cursor pointer at 0 : ${cursor.getString(0)}")
                    println("cursor pointer at 1 : ${cursor.getString(1)}")
                    println("cursor pointer at 2 : ${cursor.getString(2)}")
                    println("cursor pointer at 3 : ${cursor.getString(3)}")
                    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

                    val todo = Todo(id = cursor.getString(0), description= cursor.getString(1),user_email = cursor.getString(2) ,job = cursor.getString(3))
                    // Adding contact to list
                    list.add(todo)
                } while (cursor.moveToNext())
            }
            return list
        }
    // Deleting single contact
    fun deleteTodo(view:View,todo: Todo?):Boolean {
        val db = this.writableDatabase
        var status = false
        try {
            db.delete(DbHelper.TABLE_TODOS, "${DbHelper.KEY_ID} = ?", arrayOf(todo?.id))
            status = true
        }catch (error:Exception) {
            Snackbar
                .make(view,error.message.toString(), Snackbar.LENGTH_LONG)
                .show()
            status = false
        }finally {
            db.close()
        }
        return status
    }
    fun updateTodo(todo: Todo): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DbHelper.DESCRIPTION, todo.description) // Description
        values.put(DbHelper.JOB_NAME,todo.job)  // job name

        // updating row
        return db.update(
            DbHelper.TABLE_TODOS,
            values,
            "${DbHelper.KEY_ID} = ?",
            arrayOf(todo.id)
        )
    }
}
