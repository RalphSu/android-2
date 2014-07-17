package com.mixmusic.adapter;

import android.graphics.drawable.Drawable;

public class MoreItem{
		public Drawable icon;
		public String text;
		public int id;
		
		public MoreItem() {
		}
		
		public MoreItem(Drawable icon,String text, int id) {
			this.icon=icon;
			this.text=text;
			this.id=id;
		}
	}
