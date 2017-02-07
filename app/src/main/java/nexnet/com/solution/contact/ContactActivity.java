package nexnet.com.solution.contact;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

    private M800ContactsAdapter mM800Adapter;
    private List<IM800Contact> mAdapterData;
    public ContactActivity() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadListData();
    }

    public void loadListData() {
        new GetContactsTask().execute();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_contact, container,false);
        listViewContacts=(ListView) view.findViewById(R.id.listViewContacts);
        mM800Adapter = new M800ContactsAdapter(getContext());
        listViewContacts.setAdapter(mM800Adapter);
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
            mAdapterData = contacts;
            Log.e(DEBUG_TAG, contacts.toString());

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
            //arrayAdapter.notifyDataSetChanged();
        }
    }

    private class M800ContactsAdapter extends ArrayAdapter<IM800Contact> {

        public M800ContactsAdapter(Context context) {
            super(context, R.layout.activity_contact);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.contactlist_item, parent, false);
                holder = new ViewHolder();
                holder.mProfileImageView = (ImageView) convertView.findViewById(R.id.contact_image);
                holder.mNameTextView = (TextView) convertView.findViewById(R.id.contact_name);
                holder.mStatusTextView = (TextView) convertView.findViewById(R.id.contact_number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            IM800Contact contact = getItem(position);
            Glide.with(ContactActivity.this)
                    .load(contact.getUserProfile().getProfileImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mProfileImageView);
            holder.mNameTextView.setText(contact.getUserProfile().getName());
            holder.mStatusTextView.setText(contact.getPhoneNumber());
            Log.d(DEBUG_TAG,"ListView");
            return convertView;
        }

        private class ViewHolder {
            ImageView mProfileImageView;
            TextView mNameTextView;
            TextView mStatusTextView;
        }
    }

}
