package nexnet.com.solution.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nexnet.com.solution.R;

public class LogActivity extends Fragment{
    private View view;
    private FloatingActionButton dialButton;
    public LogActivity() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.activity_contact);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_log, container,false);
        dialButton=(FloatingActionButton) view.findViewById(R.id.dialButton);

        dialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), CallPhoneActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        return view;
    }
}