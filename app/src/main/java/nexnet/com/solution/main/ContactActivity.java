package nexnet.com.solution.main;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.nearby.messages.internal.Update;
import com.m800.msme.api.M800Client;
import com.m800.msme.api.M800OutgoingCall;
import com.m800.sdk.M800SDK;
import com.m800.sdk.contact.IM800Contact;
import com.m800.sdk.contact.IM800NativeContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nexnet.com.solution.R;

public class ContactActivity extends Fragment {
    private static final String DEBUG_TAG=ContactActivity.class.getSimpleName();
    private View view;
    private ListView listViewContacts;
    private M800SDK mM800SDK;
    private ArrayList<String> ContactList;
    private ArrayAdapter<String> arrayAdapter;
    public ContactActivity() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetContactsTask().execute();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_contact, container,false);
        ContactList = new ArrayList<String>();
        ContactList.add(" ");

        arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.contact_item,R.id.Itemname,ContactList);

        listViewContacts=(ListView)view.findViewById(R.id.listViewContacts);
        listViewContacts.setAdapter(arrayAdapter);

        //M800Client realtimeClient = M800SDK.getInstance().getRealtimeClient();
      //  M800OutgoingCall call = realtimeClient.createCall("+639104675832", "+639104675832", "maaii.com", null, "");
       // call.dial();
        return view;
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.activity_contact, container, false);
    }

    @Override
    public void onResume() {
        Log.e(DEBUG_TAG, "onResume of ContactFragment");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(DEBUG_TAG, "OnPause of ContactFragment");
        super.onPause();
    }

    class GetContactsTask extends AsyncTask<Void, Void, List<IM800Contact>> {
        @Override
        protected List<IM800Contact> doInBackground(Void... params) {
            return M800SDK.getInstance().getContactManager().getM800Contacts();
        }
        @Override
        protected void onPostExecute(List<IM800Contact> contacts) {
            // Update UI
            ContactList.clear();
            for (IM800Contact contact : contacts) {
                ContactList.add(contact.getUserProfile().getJID());
            }
            arrayAdapter.notifyDataSetChanged();

        }
    }

    class GetNativeContactsTask extends AsyncTask<Void, Void, List<IM800NativeContact>> {
        @Override
        protected List<IM800NativeContact> doInBackground(Void... params) {
            return M800SDK.getInstance().getContactManager().getM800NativeContacts();
        }
        @Override
        protected void onPostExecute(List<IM800NativeContact> contacts) {
            for (IM800NativeContact contact : contacts) {
               // Log.d(DEBUG_TAG,"Contacts Count:" +  contact.getName());
                ContactList.add(contact.getName().toString());
            }
            //contactlist=contacts;
            // Update UI
            arrayAdapter.notifyDataSetChanged();
        }
    }

}
