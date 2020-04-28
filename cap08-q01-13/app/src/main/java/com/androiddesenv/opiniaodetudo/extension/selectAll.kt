package com.androiddesenv.opiniaodetudo.extension

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

fun SQLiteDatabase.selectAll(tableName:String, columns:Array<String>): Cursor {
    return this.query(
        tableName,
        columns,
        null,
        null,
        null,
        null,
        null
    )
}