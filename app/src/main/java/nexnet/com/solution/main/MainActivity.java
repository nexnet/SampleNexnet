package nexnet.com.solution.main;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import com.m800.sdk.M800Error;
import com.m800.sdk.M800SDK;
import com.m800.sdk.contact.IM800Contact;
import com.m800.sdk.contact.IM800NativeContact;

import java.util.ArrayList;
import java.util.List;

import nexnet.com.solution.R;

import static android.R.id.list;
import static com.maaii.type.Platform.android;

public class MainActivity extends AppCompatActivity {
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



        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        // Initialize M800SDK
      /*  mM800SDK = M800SDK.getInstance();
        if(mM800SDK.hasUserSignedUp())
        {
            mM800SDK.getManagement().connect();
            Log.d(DEBUG_TAG,"Connection State: " + mM800SDK.getManagement().getConnectionState());
        }
        Log.d(DEBUG_TAG,"Connection State njknjknjknj: " + mM800SDK.getManagement().getConnectionState());*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(DEBUG_TAG, "OnPause of MainActivity");
        super.onPause();
    }
}
