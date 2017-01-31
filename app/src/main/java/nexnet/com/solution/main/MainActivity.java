package nexnet.com.solution.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import com.m800.sdk.IM800Management;
import com.m800.sdk.IM800SystemUpdateNotification;
import com.m800.sdk.M800Error;
import com.m800.sdk.M800SDK;
import com.m800.sdk.contact.IM800Contact;
import com.m800.sdk.contact.IM800NativeContact;

import java.util.ArrayList;
import java.util.List;

import nexnet.com.solution.R;
import nexnet.com.solution.service.AppM800Class;
import nexnet.com.solution.service.Certificate;
import nexnet.com.solution.service.Utilities;

import static android.R.id.list;
import static com.maaii.type.Platform.android;

public class MainActivity extends AppCompatActivity implements IM800Management.SystemUpdateListener {
    private static final String DEBUG_TAG=MainActivity.class.getSimpleName();
    private volatile M800SDK mM800SDK;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.ic_msg,
            R.drawable.ic_contacts,
            R.drawable.ic_phone
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mM800SDK = M800SDK.getInstance();


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        Log.d(DEBUG_TAG, "Connected: "+ Certificate.getInstance().isConnectedToM800());
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mM800ConnectionReceiver,  new IntentFilter(Certificate.ACTION_M800_CONNECTION_CHANGED));

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
        adapter.addFragment(new LogActivity(), "LOGS");
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
