/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Notice 是大家的
 *
 */
public class NoticeDao {
	public static final String TABLE_NAME = "notice";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_JSON = "noiceJosn";

	private DbOpenHelperPub dbHelper;

	public NoticeDao(Context context) {
		dbHelper = DbOpenHelperPub.getInstance(context);
	}


	public void saveNitice(String  newsJosn) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
            ContentValues values = new ContentValues();
            values.put(COLUMN_JSON, newsJosn);
            db.insert(TABLE_NAME, null, values);
		}
	}

	public String getNitice() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String json = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				json = cursor.getString(cursor.getColumnIndex(COLUMN_JSON));
			}
			cursor.close();
		}
		return json;
	}

}
