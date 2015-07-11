/* Use fragment_weather.xml as its layout */

package biz.teammaster.tmweather;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import biz.teammaster.eweather.R;

public class WeatherFragment extends Fragment {
	Handler handler;

	Typeface weatherFont;
	TextView cityField, updateField, detailsField, currentTemperatureField,
			weatherIcon;

	// We making use of a separate Thread to asynchronous fetch data
	public WeatherFragment() {
		handler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		cityField = (TextView) rootView.findViewById(R.id.city_field);
		updateField = (TextView) rootView.findViewById(R.id.updated_field);
		detailsField = (TextView) rootView.findViewById(R.id.details_field);
		currentTemperatureField = (TextView) rootView
				.findViewById(R.id.current_temperature_field);
		weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);

		weatherIcon.setTypeface(weatherFont);

		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		weatherFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/weather.ttf");
		updateWeatherData(new StoreCityPreference(getActivity()).getCity());

	}

	private void updateWeatherData(final String city) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				final JSONObject json = FetchRemote
						.getJSON(getActivity(), city);
				if (json == null) {
					handler.post(new Runnable() {
						public void run() {
							Toast.makeText(
									getActivity(),
									getActivity().getString(
											R.string.place_not_found),
									Toast.LENGTH_LONG).show();
						}
					});
				} else {
					handler.post(new Runnable() {
						public void run() {
							renderWeather(json);
						}
					});
				}
			}
		}.start();
	}

	private void renderWeather(JSONObject json) {
		try {
			cityField.setText(json.getString("name").toUpperCase(Locale.US)
					+ ", " + json.getJSONObject("sys").getString("country"));

			JSONObject details = json.getJSONArray("weather").getJSONObject(0);
			JSONObject main = json.getJSONObject("main");
			detailsField
					.setText(details.getString("description").toUpperCase(
							Locale.US)
							+ "\n"
							+ "Humidity: "
							+ main.getString("humidity")
							+ "%"
							+ "\n"
							+ "Pressure: "
							+ main.getString("pressure") + " hPa");

			double f;
			f = ((main.getDouble("temp") * 9) / 5) + 32;

			// Unicode character for Celsius is \u00b0
			currentTemperatureField
					.setText(String.format("%.2f", main.getDouble("temp"))
							+ " \u00b0C" + "\n" + String.format("%.2f", f)
							+ " \u00b0F");

			java.text.DateFormat df = SimpleDateFormat.getDateTimeInstance();
			String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
			updateField.setText("Last update: " + updatedOn);

			setWeatherIcon(details.getInt("id"), json.getJSONObject("sys")
					.getLong("sunrise") * 1000, json.getJSONObject("sys")
					.getLong("sunset") * 1000);

		} catch (Exception e) {
			Log.e("eWeather", "One or more fields not found in the JSON data");
		}
	}

	private void setWeatherIcon(int actualId, long sunrise, long sunset) {
		int id = actualId / 100;
		String icon = "";
		if (actualId == 800) {
			long currentTime = System.currentTimeMillis();
			if (currentTime >= sunrise && currentTime < sunset) {
				icon = getActivity().getString(R.string.weather_sunny);
			} else {
				icon = getActivity().getString(R.string.weather_clear_night);
			}
		} else {
			switch (id) {
			case 2:
				icon = getActivity().getString(R.string.weather_thunder);
				break;
			case 3:
				icon = getActivity().getString(R.string.weather_drizzle);
				break;
			case 7:
				icon = getActivity().getString(R.string.weather_foggy);
				break;
			case 8:
				icon = getActivity().getString(R.string.weather_cloudy);
				break;
			case 6:
				icon = getActivity().getString(R.string.weather_snowy);
				break;
			case 5:
				icon = getActivity().getString(R.string.weather_rainy);
				break;
			}
		}
		weatherIcon.setText(icon);
	}

	public void changeCity(String city) {
		updateWeatherData(city);
	}

}
