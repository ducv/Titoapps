/* 
 * Copyright 2007 Steven Osborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ducv.fff.chess.utils.filebrowser;

import android.graphics.drawable.Drawable;

/** @author Steven Osborn - http://steven.bitsetters.com */
public class FileInfos implements Comparable<FileInfos> {

	private String mText = "";
	private String mDetails = "";
	private Drawable mIcon;
	private boolean mSelectable = true;

	public FileInfos(String text, String details, Drawable bullet) {
		mIcon = bullet;
		mDetails = details;
		mText = text;
	}

	public boolean isSelectable() {
		return mSelectable;
	}

	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public String getDetails() {
		return this.mDetails;
	}

	public void setDetails(String details) {
		this.mDetails = details;
	}

	public void setIcon(Drawable icon) {
		mIcon = icon;
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public int compareTo(FileInfos other) {
		if (this.mText != null)
			return this.mText.compareTo(other.getText());
		else
			throw new IllegalArgumentException();
	}
}
