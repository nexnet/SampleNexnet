package nexnet.com.solution.contact;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.m800.msme.api.M800Client;
import com.m800.msme.api.M800OutgoingCall;
import com.m800.sdk.M800SDK;
import com.m800.sdk.contact.IM800Contact;
import com.m800.sdk.contact.IM800NativeContact;

import java.util.ArrayList;
import java.util.List;

import nexnet.com.solution.R;
import nexnet.com.solution.call.CallScreenActivity;

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
        listViewContacts.setClickable(true);
        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listViewContacts.getItemAtPosition(position);

                String username = o.toString();
                M800Client client = M800SDK.getInstance().getRealtimeClient();

                String carrier = M800SDK.getInstance().getCarrier();
                String jid = username;
                M800OutgoingCall call = client.createCall(username, jid, carrier, null, "");
                if (null != call){
                    call.dial();
                }

                Intent intent = new Intent();
                intent.putExtra(CallScreenActivity.EXTRA_KEY_CALL_ID, call.callID());
                intent.putExtra("phonenumber: ",o.toString());
                intent.setClass(getContext(), CallScreenActivity.class);


                Log.d(DEBUG_TAG,"selected number" + o.toString());
                startActivity(intent);
            }
        });

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
