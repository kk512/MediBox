package in.co.medibox.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.medibox.R;

public class Fragment_User_Laboratory extends Fragment {
    public Fragment_User_Laboratory() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_laboratory, container, false);
        return rootView;
    }
}
