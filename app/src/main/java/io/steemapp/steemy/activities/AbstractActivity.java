package io.steemapp.steemy.activities;

import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Bus;

import net.hockeyapp.android.CrashManager;

import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyApplication;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.adapter.CategoryListAdapter;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.models.Category;
import io.steemapp.steemy.models.CategoryList;
import io.steemapp.steemy.network.SteemyAPIService;
import io.steemapp.steemy.transactions.Transaction;
import io.steemapp.steemy.transactions.TransactionBuilder;
import io.steemapp.steemy.transactions.TransactionManager;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * AbstractActivity functions as the base unit for all Activities in Steemy. AbstractActivity handles a static state of the app. This state primarily handles three functions:
 *<p>
 *     Navigation - Toolbar & Category List
 *     Starting/Stopping Services - TransactionManager, AccountManager, EventBus
 *     UI for Login/Out State
 */
public abstract class AbstractActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public abstract int getContentView();
    public abstract void setActivity();
    protected AccountManager mAccountManager;
    protected SteemyAPIService mService;
    protected Bus mEventBus;
    protected TransactionBuilder mTransactor;
    protected TransactionManager mTransactionManager;

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mToggle;
    protected NavigationView mNavigationView;
    protected View mNavHeader;
    protected SteemyTextView mNavUsernameLabel;
    protected SteemyTextView mNavLoginLabel;

    protected RecyclerView mCategoryList;
    protected CategoryListAdapter mCategoryAdapter;
    protected CategoryListAdapter.CategorySelectedListener mCategoryListener;
    protected CategoryList mCategoryData;

    protected boolean mCategoryIsDirty = true;

    protected SteemyGlobals.SORTS mSortMethod = SteemyGlobals.SORTS.TRENDING;
    protected SharedPreferences prefs;

    protected boolean mIsDialogShowing = false;

    public final static int LOGIN_REQUEST_CODE = 1000;
    protected final static int SETTINGS_REQUEST_CODE = 2000;

    protected AppCompatActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        setActivity();
        initializeViews();
        initializeServices();
    }

    private void initializeServices(){
        mEventBus = ((SteemyApplication)getApplication()).mGlobalBus;
        mAccountManager = AccountManager.get(getApplicationContext(), mEventBus);
        mService = SteemyAPIService.getService(getApplicationContext(), mEventBus);
        mTransactor = TransactionBuilder.getInstance(mEventBus, mAccountManager);
        mTransactionManager = TransactionManager.instance(mAccountManager, mEventBus);
        mService.getDynamicGlobalProperties();
        if(SteemyGlobals.areCategoriesInSync()){
            mCategoryData = SteemyGlobals.steemyCategories;
        }else{
            mService.getCategories(mSortMethod.toString(), null, 100);
        }
    }

    private void initializeViews(){
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

//        MenuItem exploreItem = mNavigationView.getMenu().getItem(R.id.nav_explore);

        mNavHeader = mNavigationView.getHeaderView(0);
        mNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAccountManager.isLoggedIn()){
                    startActivity(ProfileActivity.intent(mAccountManager.getCurrentAccount(), activity, true));
                }else
                    startActivityForResult(LogInActivity.loginIntent(activity), LOGIN_REQUEST_CODE);
            }
        });

        mNavUsernameLabel = (SteemyTextView)mNavHeader.findViewById(R.id.nav_username_text);
        mNavLoginLabel = (SteemyTextView)mNavHeader.findViewById(R.id.nav_login_text);

        mCategoryList = (RecyclerView)findViewById(R.id.category_recycler_view);
        mCategoryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mCategoryListener = new CategoryListAdapter.CategorySelectedListener() {
            @Override
            public void itemClicked(Category item) {
                mCategoryData.setCurrent(item);
                if(activity instanceof HomeListActivity) {
                    mService.getDiscussions(mSortMethod.toString(), mCategoryData.getCurrent(), "none", "none", 11);
                    mDrawerLayout.closeDrawers();
                    mCategoryIsDirty = true;
                }else{
                    startActivity(HomeListActivity.intent(activity));
                }
            }
        };

        mCategoryAdapter = new CategoryListAdapter(this, new ArrayList<Category>(), mCategoryListener);
        mCategoryList.setAdapter(mCategoryAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAccountManager.isLoggedIn()){
            setNavHeaderToLoggedIn();
        }else
            resetNavHeader();

        CrashManager.register(this);
    }

    public void setNavHeaderToLoggedIn(){
        mNavLoginLabel.setText("Profile");
        mNavUsernameLabel.setText(mAccountManager.getCurrentAccount().getChromedUsername());
    }

    public void resetNavHeader(){
        mNavLoginLabel.setText("Login Here");
        mNavUsernameLabel.setText("Guest");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_explore) {
            // Handle the camera action
        } else if (id == R.id.nav_manage) {
            startActivityForResult(SettingsActivity.intent(this), SETTINGS_REQUEST_CODE);
        } else if (id == R.id.nav_transfer){
            startActivityForResult(TransferActivity.intent(this), SETTINGS_REQUEST_CODE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
