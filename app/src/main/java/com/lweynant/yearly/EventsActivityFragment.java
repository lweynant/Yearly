package com.lweynant.yearly;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsActivityFragment extends Fragment {

    public EventsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        return inflater.inflate(R.layout.fragment_events, container, false);
    }
}
