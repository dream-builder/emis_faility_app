package org.sci.rhis.fwc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import org.sci.rhis.utilities.Constants;


public class ClientIntroFragment extends Fragment {

    View fragmentView;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_client_intro,
                container, false);
        return fragmentView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("ClientIntro", "OnActivityResult");
    }
}

