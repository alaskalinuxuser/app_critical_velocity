package com.alaskalinuxuser.criticalvelocity;

import android.content.Context;
import android.content.res.TypedArray;

public class AndroidStringProvider implements StringProvider{
	private Context context;
	public AndroidStringProvider(Context context) {
		this.context = context;
	}
	public String getString(int id) {
		return context.getResources().getString(id);
	}
	public String getString(int id, int index) {
		TypedArray stringArray = context.getResources().obtainTypedArray(id);
		return stringArray.getString(index);
	}
}
