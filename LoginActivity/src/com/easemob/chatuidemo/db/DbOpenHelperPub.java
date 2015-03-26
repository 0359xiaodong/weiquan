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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 这个DbOpenHelper 是公用的 换了账号也不变化
 * 本来准备按照实体存的 ，发现封住的太乱 ，没法存所以直接存json算了
 *
 */
public class DbOpenHelperPub extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static DbOpenHelperPub instance;
	
	private static final String   NEWS = "CREATE TABLE "
	        + NewsDao.TABLE_NAME + " ("
	        + NewsDao.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + NewsDao.COLUMN_JSON  + " TEXT); ";
	
	private static final String   NOTICE = "CREATE TABLE "
	        + NoticeDao.TABLE_NAME + " ("
	        + NoticeDao.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + NoticeDao.COLUMN_JSON  + " TEXT); ";
	
	private DbOpenHelperPub(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
		
	}
	
	public static DbOpenHelperPub getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelperPub(context.getApplicationContext());
		}
		return instance;
	}

    public  static void reset() {
        instance = null;
    }
	private static String getUserDatabaseName() {
	    
        return  "news.db";
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(NEWS);
		db.execSQL(NOTICE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}
	
}
