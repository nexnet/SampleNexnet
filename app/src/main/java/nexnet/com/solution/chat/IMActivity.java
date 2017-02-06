package nexnet.com.solution.chat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nexnet.com.solution.R;

public class IMActivity extends Fragment {
    private static final String DEBUG_TAG=IMActivity.class.getSimpleName();
    private View view;
    public IMActivity() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_contact);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_im, container,false);
        return view;
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
}
