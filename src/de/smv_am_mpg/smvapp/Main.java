package de.smv_am_mpg.smvapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main extends AppCompatActivity {
	private static final String HOST = "www.smv-am-mpg.de";
	private DrawerLayout mDrawerLayout;
	public NavigationView navMenu;
	private ActionBarDrawerToggle mDrawerToggle;
	private WebView contentWebView;

	private CharSequence initialTitle = "SmV am MPG";
	private MenuDataProvider menuDataProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(initialTitle);
		contentWebView = (WebView) findViewById(R.id.web_view);
		contentWebView.setBackgroundColor(Color.TRANSPARENT);
		// avoids opening links in a separate browser tab and
		// makes it possible to send mails with an installed mailing-app
		contentWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("mailto:")) {
					Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
					startActivity(i);
					return true;
				} else if (Uri.parse(url).getHost().equals(HOST)) {
					contentWebView.loadUrl(PageDataProvider
							.getContentUrlFromPath(Uri.parse(url).getPath()));
					return false;
				} else if (url.startsWith("http://")
						|| url.startsWith("https://")) {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(i);
					return true;
				} else {
					contentWebView.loadUrl(url);
					return false;
				}
			}
		});
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navMenu = (NavigationView) findViewById(R.id.nav_menu);
		navMenu.setNavigationItemSelectedListener(new NavigationItemClickListener());
		// mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		/*
		 * mDrawerList .setAdapter(new
		 * ArrayAdapter<de.smv_am_mpg.smvapp.MenuDataProvider.MenuItem>( this,
		 * android.R.layout.simple_expandable_list_item_1, new
		 * ArrayList<de.smv_am_mpg.smvapp.MenuDataProvider.MenuItem>() { {
		 * add(new de.smv_am_mpg.smvapp.MenuDataProvider.MenuItem( "stest",
		 * "http://www.smv-am-mpg.de")); } }));
		 */

		// mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		menuDataProvider = new MenuDataProvider(this);
		if (isConnectionAvailable()) {
			menuDataProvider.getMenuItems();
		}

		// Fgt den Navigation Drawer zur ActionBar hinzu
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.drawer_close, R.string.drawer_open) {
			public void onDrawerClosed(View view) {
				// getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// getSupportActionBar().setTitle(R.string.app_name);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	// Fgt das Men hinzu / ActionBar Eintrge
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// Versteckt die ActionBar-Eintrge, sobald der Drawer ausgefahren is
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(navMenu);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// ffnet und schliet den Navigation Drawer bei Klick auf den Titel/das
		// Icon in der ActionBar
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(navMenu)) {
				mDrawerLayout.closeDrawer(navMenu);
			} else {
				mDrawerLayout.openDrawer(navMenu);
			}
		}

		// Gibt den ActionBar-Buttons Funktionen
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class NavigationItemClickListener implements
			NavigationView.OnNavigationItemSelectedListener {
		@Override
		public boolean onNavigationItemSelected(MenuItem menuItem) {
			getSupportActionBar().setTitle(menuItem.getTitle());
			System.out.println(menuDataProvider.getMenuItemById(menuItem
					.getItemId()));
			contentWebView.loadUrl(PageDataProvider
					.getContentUrlFromId(menuItem.getItemId()));
			System.out.println(PageDataProvider.getContentUrlFromId(menuItem
					.getItemId()));
			mDrawerLayout.closeDrawer(navMenu);
			return false;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	protected boolean isConnectionAvailable() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

}
