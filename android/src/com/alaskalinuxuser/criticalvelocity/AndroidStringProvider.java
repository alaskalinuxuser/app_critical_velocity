package com.alaskalinuxuser.criticalvelocity;

import android.content.Context;

public class AndroidStringProvider implements StringProvider{
	public AndroidStringProvider() {}
	public String getString(int id) {
		return getResources().getString(id);
	}
}
