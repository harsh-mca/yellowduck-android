/* 
 *
 * Copyright (C) 2013 The PlayTractor Team (support@playtractor.com)
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <http://www.gnu.org/licenses/>.
 */


package name.w.yellowduck;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SysHelper {
	private static int totalActivities;
	private static boolean fullVersion=false;
	private static int bgMusicEnabled=-1;

	public static int getTotalActivities() {
		return totalActivities;
	}

	public static void setTotalActivities(int totalActivities) {
		SysHelper.totalActivities = totalActivities;
	}

	public static boolean isFullVersion() {
		return fullVersion;
	}

	public static void setFullVersion(boolean b) {
		fullVersion=b;
	}

	public static int getIntProfileValue(String key) {
        SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(YDConfiguration.context);
        return sharedPref.getInt(key, 0);
	}

	public static void setIntProfileValue(String key, int value) {
        SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(YDConfiguration.context);
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
	}
	
	public static boolean isBgMusicEnabled() {
		if (bgMusicEnabled < 0) {
			bgMusicEnabled=getIntProfileValue("BgMusic");
		}
		return bgMusicEnabled > 0;
	}
	public static void setBgMusicEnabled(boolean b) {
		bgMusicEnabled=b?100:0;
		setIntProfileValue("BgMusic", bgMusicEnabled);
	}
}
