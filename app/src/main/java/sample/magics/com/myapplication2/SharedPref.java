package sample.magics.com.myapplication2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SharedPref {

	Context context;

	public static String MY_PREFS_NAME = "UserApp";


	public SharedPref(Context context) {
		this.context = context;
	}


	public void put(String name, String value) {
		try {
			SharedPreferences.Editor editor = context.getSharedPreferences(
					MY_PREFS_NAME, Activity.MODE_PRIVATE).edit();
			editor.putString(name, value);
			editor.commit();
		} catch(Exception exp) { }
	}

	public void put(String name, Float value) {
		try {
			SharedPreferences.Editor editor = context.getSharedPreferences(
					MY_PREFS_NAME, Activity.MODE_PRIVATE).edit();
			editor.putFloat(name, value);
			editor.commit();
		} catch(Exception exp) {

		}
	}

	public void put(String name, ArrayList<String> value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, Activity.MODE_PRIVATE).edit();
		try {
			Set<String> value1 = new HashSet<String>(value);
			editor.putStringSet(name, value1);
			editor.commit();
		} catch (Exception exp) {}
	}

	public void put(String name, int value) {
		try {
			SharedPreferences.Editor editor = context.getSharedPreferences(
					MY_PREFS_NAME, Activity.MODE_PRIVATE).edit();
			editor.putInt(name, value);
			editor.commit();
		} catch(Exception exp) {

		}
	}

	public String getString(String value) {
		try {
			SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,
					Activity.MODE_PRIVATE);
			return prefs.getString(value, "");
		} catch(Exception exp) {
			return "";
		}
	}

	public boolean Has(String value) {
			SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,
					Activity.MODE_PRIVATE);
			if(prefs.contains(value)) {
				return true;
			} else
				return false;
	}

	public ArrayList<String> getSet(String value) {

		try {
			SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,
					Activity.MODE_PRIVATE);
			ArrayList<String> value1 = new ArrayList<>(prefs.getStringSet(value, new HashSet<String>()));
			return value1;
		} catch(Exception exp) {
			return  new ArrayList<>();
		}
	}

	public int getInt(String value) {
		try {
			SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,
					Activity.MODE_PRIVATE);
			return prefs.getInt(value, -1);
		} catch(Exception exp) {

			return 1;
		}
	}

	public ArrayList<String> getArrayList(String value) {
		try {
			SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,
					Activity.MODE_PRIVATE);
			return new ArrayList<>(prefs.getStringSet(value, new HashSet<String>()));
		} catch(Exception exp) {

			return  new ArrayList<>();
		}
	}

	public void clear() {
		try {
			SharedPreferences.Editor editor = context.getSharedPreferences(
					MY_PREFS_NAME, Activity.MODE_PRIVATE).edit();
			editor.clear();
			editor.commit();
		} catch(Exception exp) {

		}
	}
	public void clearString(String clear) {
		try {
			SharedPreferences.Editor editor = context.getSharedPreferences(
					MY_PREFS_NAME, Activity.MODE_PRIVATE).edit();
			editor.remove(clear);
			editor.commit();
		} catch(Exception exp) {

		}
	}
}
