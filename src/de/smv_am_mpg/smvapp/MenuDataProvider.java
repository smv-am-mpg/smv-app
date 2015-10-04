package de.smv_am_mpg.smvapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;

public class MenuDataProvider {
	MenuItem[] menuItems;
	private String apiUrl = "http://www.smv-am-mpg.de/api/";
	private String menuDataUrl = "menuitemproviderhiddenapi/";
	private NavigationView navMenu;
	private Main smvApp;

	// FIXME THIS SMELLS LIKE HELL...
	public MenuDataProvider(Main smvApp) {
		this.navMenu = smvApp.navMenu;
		this.smvApp = smvApp;
	}

	public MenuItem[] getMenuItems() {
		new GetMenuDataTask().execute(apiUrl + menuDataUrl);
		return null;
	}

	public MenuItem getMenuItemById(int id) {
		if (menuItems == null) {
			return new MenuItem("Error", "http://www.smv-am-mpg.de", -1);
		} else {
			for (MenuItem item : menuItems) {
				if (item.id == id) {
					return item;
				}
			}
		}
		return new MenuItem("Errosdr", "http://www.smv-am-mpg.de", -1);
	}

	static class MenuItem {
		// TODO Add more fields from JSON
		private String name;
		private URL url;
		private int id;

		public MenuItem(String name, String url, int id) {
			this.name = name;
			this.id = id;
			try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public String getName() {
			return name;
		}

		public URL getUrl() {
			return url;
		}

		public int getId() {
			return id;
		}

		// FIXME Add this to a custom ArrayAdapter
		public String toString() {
			return name;
		}
	}

	private class GetMenuDataTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// Get String
			// Transform to JSON
			final ArrayList<MenuItem> menuItems = new ArrayList<>();
			System.out.println(result);
			try {
				JSONArray mainPages = new JSONArray(result);
				for (int i = 0; i < mainPages.length(); i++) {
					menuItems.add(new MenuItem(mainPages.getJSONObject(i)
							.getString("title"), mainPages.getJSONObject(i)
							.getString("httpUrl"), mainPages.getJSONObject(i)
							.getInt("id")));
					System.out.println(mainPages.getJSONObject(i).getString(
							"title"));
					System.out.println(mainPages.getJSONObject(i).getString(
							"httpUrl"));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MenuDataProvider.this.menuItems = menuItems
					.toArray(new MenuItem[menuItems.size()]);
			smvApp.runOnUiThread(new Runnable() {
				public void run() {
					Menu menu = navMenu.getMenu();
					menu.clear();
					for (MenuItem item : menuItems) {
						menu.add(Menu.NONE, item.id, Menu.NONE, item.getName());
					}
				}
			});

		}

		private String downloadUrl(String myurl) throws IOException {
			InputStream is = null;

			try {
				URL url = new URL(myurl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();
				Log.d("DEBUG", "The response is: " + response);
				is = conn.getInputStream();

				// Convert the InputStream into a string
				String contentAsString = StreamUtils.streamToString(is);
				return contentAsString;

				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
	}

}
