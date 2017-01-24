package nexnet.com.solution.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nexnet.com.solution.R;

public class CallPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText callPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_phone);

        callPhoneNumber = (EditText) findViewById(R.id.callPhoneNumber);

        TextView key_0 = (TextView) findViewById(R.id.key_0);
        key_0.setOnClickListener(this);
        TextView key_1 = (TextView) findViewById(R.id.key_1);
        key_1.setOnClickListener(this);
        TextView key_2 = (TextView) findViewById(R.id.key_2);
        key_2.setOnClickListener(this);
        TextView key_3 = (TextView) findViewById(R.id.key_3);
        key_3.setOnClickListener(this);
        TextView key_4 = (TextView) findViewById(R.id.key_4);
        key_4.setOnClickListener(this);
        TextView key_5 = (TextView) findViewById(R.id.key_5);
        key_5.setOnClickListener(this);
        TextView key_6 = (TextView) findViewById(R.id.key_6);
        key_6.setOnClickListener(this);
        TextView key_7 = (TextView) findViewById(R.id.key_7);
        key_7.setOnClickListener(this);
        TextView key_8 = (TextView) findViewById(R.id.key_8);
        key_8.setOnClickListener(this);
        TextView key_9 = (TextView) findViewById(R.id.key_9);
        key_9.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        callPhoneNumber.append(((TextView) v).getText());
    }
}
