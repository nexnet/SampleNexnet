package nexnet.com.solution.contact;

/**
 * Created by Ching on 2/7/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.m800.sdk.M800SDK;
import com.m800.sdk.contact.IM800Contact;
import com.m800.sdk.contact.IM800ContactManager;
import com.m800.sdk.contact.IM800NativeContact;
import com.m800.sdk.contact.M800AddContactRequest;

import java.util.ArrayList;
import java.util.List;

import nexnet.com.solution.R;


public abstract class ContactListAbstract extends Fragment implements
        IM800ContactManager.Listener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    protected SwipeRefreshLayout mSwipeLayout;
    protected ListView mListView;

    protected IM800ContactManager mContactManager;
    protected boolean isInPickContactMode;
    protected int mPickContactCount;
    protected String mConstraint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContactManager = M800SDK.getInstance().getContactManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contactlist_item, container, false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mListView = (ListView) rootView.findViewById(android.R.id.list);

        if (isInPickContactMode) {
            if (mPickContactCount == 1) {
                mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            } else if (mPickContactCount > 1) {
                mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            }
            // Disable refresh function in pick contact mode
            mSwipeLayout.setEnabled(false);
        } else {
            // Normal onClick events
            mListView.setOnItemClickListener(this);
            mSwipeLayout.setOnRefreshListener(this);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContactManager.addContactChangeListener(this);
        if (mContactManager.isNativeAddressBookSyncInProgress()) {
            // A contact sync task is running
            startRefreshing();
        } else {
            // No contact sync task running, load data
            loadListData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContactManager.removeContactChangeListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Ignore
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mConstraint = newText;
        filterListData(newText);
        return false;
    }

    @Override
    public void onRefresh() {
        mContactManager.startSyncNativeAddressBook(true);
    }

    @Override
    public void onQueryRosterStart() {
        // Ignore
    }

    @Override
    public void onContactSyncCompleted(boolean hasChange) {
        if (hasChange) {
            loadListData();
        } else {
            if (isAdded() && isVisible()) {
                Toast.makeText(getActivity(), "No change", Toast.LENGTH_SHORT).show();
            }
            stopRefreshing();
        }
    }

    @Override
    public void onContactSyncError(IM800ContactManager.Error error) {
        if (isAdded() && isVisible()) {
            Toast.makeText(getActivity(), error.name(), Toast.LENGTH_SHORT).show();
        }
        stopRefreshing();
    }

    @Override
    public void onNewAddContactRequest(M800AddContactRequest request) {
        // Ignore
    }

    @Override
    public void onAddContactRequestComplete(String jid, M800AddContactRequest.Direction direction, boolean isAccepted) {
        // Ignore
    }

    /**
     * Should stop refreshing when load finished.
     */
    protected abstract void loadListData();

    protected abstract void filterListData(String key);

    protected void startRefreshing() {
        mSwipeLayout.setRefreshing(true);
    }

    protected void stopRefreshing() {
        mSwipeLayout.setRefreshing(false);
    }

}

