package nexnet.com.solution.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.m800.sdk.IM800Management;
import com.m800.sdk.IM800SystemUpdateNotification;
import com.m800.sdk.M800SDK;
import com.m800.sdk.user.IM800AccountManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import nexnet.com.solution.R;
import nexnet.com.solution.activitylog.CallLogActivity;
import nexnet.com.solution.chat.IMActivity;
import nexnet.com.solution.contact.ContactActivity;
import nexnet.com.solution.service.Certificate;

import static android.R.attr.name;
import static com.maaii.filetransfer.FileServer.Store.profile;

public class MainActivity extends AppCompatActivity implements IM800Management.SystemUpdateListener {
    private static final String DEBUG_TAG=MainActivity.class.getSimpleName();
    private volatile M800SDK mM800SDK;
    private IM800AccountManager accountManager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private TextView tv;
    private ImageView profile_image;
    private ActionBarDrawerToggle drawerToggle;

    private int[] tabIcons = {
            R.drawable.ic_msg,
            R.drawable.ic_contacts,
            R.drawable.ic_phone
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mM800SDK = M800SDK.getInstance();
        initLayout();

        Log.d(DEBUG_TAG, "Connected: "+ Certificate.getInstance().isConnectedToM800());
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mM800ConnectionReceiver,  new IntentFilter(Certificate.ACTION_M800_CONNECTION_CHANGED));

    }

    private void initLayout(){
        mDrawer = (DrawerLayout) findViewById(R.id.activity_main);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        //CHANGE Account Name & Profile Image
        View header=nvDrawer.getHeaderView(0);
        tv = (TextView)header.findViewById(R.id.textViewAccountName);
        profile_image = (ImageView) header.findViewById(R.id.profile_image);
        accountManager = mM800SDK.getInstance().getAccountManager();
        loadImage(accountManager.getProfileImageUrl(), profile_image);
        tv.setText(accountManager.getName());
        //Action Bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // get menu from navigationView
        Menu menu = nvDrawer.getMenu();
        // find MenuItem you want to change
        MenuItem first = menu.findItem(R.id.nav_first_fragment);
        first.setTitle(mM800SDK.getInstance().getUsername());

        MenuItem second = menu.findItem(R.id.nav_second_fragment);
        second.setTitle(accountManager.getEmailAddress());

        MenuItem third = menu.findItem(R.id.nav_third_fragment);
        third.setTitle(accountManager.getGender() == null ? "" : accountManager.getGender().name());

        MenuItem fourth = menu.findItem(R.id.nav_fourth_fragment);
        fourth.setTitle(accountManager.getBirthday());

        // Setup drawer view
        setupDrawerContent(nvDrawer);

       //AppBar Setup
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //FOR TAB LAYOUT
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void loadImage(String url, ImageView imageView) {
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver mM800ConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //if (intent.getAction().equals(Certificate.ACTION_M800_CONNECTION_CHANGED)) {
            Log.d(DEBUG_TAG, "Connected: "+ Certificate.getInstance().isConnectedToM800());
           // }
        }
    };

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new IMActivity(), "IM");
        adapter.addFragment(new ContactActivity(), "CONTACTS");
        adapter.addFragment(new CallLogActivity(), "LOGS");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public void onSystemUpdateReceived(IM800SystemUpdateNotification im800SystemUpdateNotification) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
           // return mFragmentTitleList.get(position);
            return null;
        }
    }

    @Override
    public void onResume() {
        Log.e(DEBUG_TAG, "onResume of MainActivity");
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mM800ConnectionReceiver, new IntentFilter(Certificate.ACTION_M800_CONNECTION_CHANGED));
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(DEBUG_TAG, "OnPause of MainActivity");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mM800ConnectionReceiver);
        super.onPause();

       // LocalBroadcastManager.getInstance(this).unregisterReceiver(mM800ConnectionReceiver);
      //  mM800SDK.getManagement().setSystemVersionUpdateListener(null);
    }
}
