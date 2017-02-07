package nexnet.com.solution.contact;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import nexnet.com.solution.R;

import static android.R.attr.resource;
import static nexnet.com.solution.R.id.imageView;

/**
 * Created by Ching on 2/7/2017.
 */

public class ContactListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] contact_name;
    private final Integer[] contact_image;
    public ContactListAdapter(Activity context, String[] contact_name, Integer[] contact_image) {

        super(context, R.layout.contactlist_item, contact_name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.contact_name=contact_name;
        this.contact_image=contact_image;
    }

    public View getView(int position, View view, ViewGroup parent) {


        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.contactlist_item, null,true);

        TextView txcontact_name = (TextView) rowView.findViewById(R.id.contact_name);
        ImageView ivcontact_image = (ImageView) rowView.findViewById(R.id.contact_image);
        TextView tvcontact_number = (TextView) rowView.findViewById(R.id.contact_number);

        txcontact_name.setText("Contact Name");
        ivcontact_image.setImageResource(R.drawable.ic_contact_default);
        tvcontact_number.setText("Phone Number");
        return rowView;

    };
}
