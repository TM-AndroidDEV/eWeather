/* This class use for app to remember the last city that user view the weather 
 * We use SharedPrefeerences to store that
 * */

package biz.teammaster.tmweather;

import android.app.Activity;
import android.content.SharedPreferences;

public class StoreCityPreference {
	SharedPreferences prefs;

	public StoreCityPreference(Activity activity) {
		prefs = activity.getPreferences(Activity.MODE_PRIVATE);
	}

	// If the user has not chosen a city yet, return Sydney as the default city
	String getCity() {
		return prefs.getString("city", "Sydney, AU");
	}

	void setCity(String city) {
		prefs.edit().putString("city", city).commit();
	}
}
