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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

public class Main extends AppCompatActivity {
	private static final String HOST = "www.smv-am-mpg.de";
	private DrawerLayout mDrawerLayout;
	public NavigationView mNavMenu;
	private ActionBarDrawerToggle mDrawerToggle;
	private WebView contentWebView;
	private SwipeRefreshLayout mSwipeRefresh;

	private CharSequence initialTitle = "SmV am MPG";
	private MenuDataProvider menuDataProvider;

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		System.out.println("sdfsdf" + intent.getData());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        contentWebView.saveState(outState);	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParsePush.subscribeInBackground("news");

		// Init Toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(initialTitle);

		// Init WebView
		contentWebView = (WebView) findViewById(R.id.web_view);
		contentWebView.restoreState(savedInstanceState);
		contentWebView.setBackgroundColor(Color.TRANSPARENT);
		// avoids opening links in a separate browser tab and
		// makes it possible to send mails with an installed mailing-app
		contentWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mSwipeRefresh.setRefreshing(false);
			}

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
		if (getIntent().getData() != null) {
			contentWebView.loadUrl(getIntent().getData().toString());
		} else {
			//contentWebView.loadUrl("http://www.smv-am-mpg.de/");
			if (contentWebView.getUrl() == null) {
				contentWebView.loadUrl("http://www.smv-am-mpg.de/");
			}
		}

		// Init SwipeRefresh
		mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		mSwipeRefresh.setColorSchemeResources(R.color.primary_color);
		mSwipeRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				Main.this.contentWebView.reload();

			}
		});

		// Init DrawerLayout aka the menu
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// Add drawer toogle aka the "hamburger" icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.drawer_close, R.string.drawer_open) {
			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mNavMenu = (NavigationView) findViewById(R.id.nav_menu);
		mNavMenu.setNavigationItemSelectedListener(new NavigationItemClickListener());
		// mDrawerList = (ListView) findViewById(R.id.left_drawer);

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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Opens and closes the drawer if you click the home button
		// Pretty useless, because the home button is hidden by the drawer
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mNavMenu)) {
				mDrawerLayout.closeDrawer(mNavMenu);
			} else {
				mDrawerLayout.openDrawer(mNavMenu);
			}
		}

		// Give functionality to the ActionBar items
		switch (item.getItemId()) {
		case R.id.action_share:
			shareText("Schau mal hier: " +contentWebView.getUrl());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class NavigationItemClickListener implements
			NavigationView.OnNavigationItemSelectedListener {
		@Override
		public boolean onNavigationItemSelected(MenuItem menuItem) {
			getSupportActionBar().setTitle(menuItem.getTitle());
			mSwipeRefresh.setRefreshing(true);
			contentWebView.loadUrl(PageDataProvider
					.getContentUrlFromId(menuItem.getItemId()));
			mDrawerLayout.closeDrawer(mNavMenu);
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

	protected void shareText(CharSequence stringToShare) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent,
				getResources().getText(R.string.share_title)));
	}

}
