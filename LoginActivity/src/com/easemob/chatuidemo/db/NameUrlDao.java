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

import itstudio.instructor.config.MyApplication;
import itstudio.instructor.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.utils.AESCipher;
import com.easemob.util.HanziToPinyin;

import copy.util.NameUrl;

public class NameUrlDao {
	public static final String TABLE_NAME = "nameurl";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_HEAD_URL = "headUrl";
	private static NameUrlDao me = null;
	private DbOpenHelper dbHelper;

	
	private NameUrlDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}


    public static NameUrlDao getInstance() {
        if (me == null) {
            me = new NameUrlDao(MyApplication.applicationContext);
        }
        return me;
    }
	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (User user : contactList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_ID, user.getUsername());
				//values.put(COLUMN_NAME_ID, user.getId());
		        values.put(COLUMN_NAME, user.getName());
				if(user.getNick() != null)
					values.put(COLUMN_HEAD_URL, user.getNick());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
				User user = new User();
				user.setUsername(username);
				user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
				user.setNick(nick);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				
				if (Constant.NEW_FRIENDS_USERNAME.equals(username) || Constant.GROUP_USERNAME.equals(username)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
							.get(0).target.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}
	
	/**
	 * 删除一个用户
	 * @param username
	 */
	public void deleteUser(String username){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{username});
		}
	}
	
	/**
	 * 保存一个用户
	 * @param nameUrl
	 */
	public void saveNameUrl(NameUrl nameUrl){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, nameUrl.getId());
		values.put(COLUMN_NAME, nameUrl.getName());
		values.put(COLUMN_HEAD_URL, nameUrl.getHeadUrl());
		
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
	/**
	 * 查找一个用户
	 * @param user
	 */
	public NameUrl selectNameUrl(String id){
	    SQLiteDatabase db = dbHelper.getReadableDatabase();
	    if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" where id =?",  new String[]{id});
            if(cursor.moveToNext()){  
                NameUrl nameUrl = new NameUrl();
                nameUrl.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                nameUrl.setHeadUrl(cursor.getString(cursor.getColumnIndex(COLUMN_HEAD_URL)));
                nameUrl.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                return nameUrl;  
            }  
            return null;
         }
	    return null;
	}
}
