package com.lweynant.yearly.controller;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.Date;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Calendar;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsActivityFragment extends Fragment implements EventsAdapter.onEventTypeSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private EventsAdapter eventsAdapter;

    public EventsActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.events_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //set the adapter
        EventRepo repo = ((YearlyApp)getActivity().getApplication()).getRepo();
        LocalDate now = LocalDate.now();
        eventsAdapter = new EventsAdapter(repo, now, this);
        recyclerView.setAdapter(eventsAdapter);
        return view;
    }

    @Override
    public void onDetach() {
        Timber.d("onDetach");
        super.onDetach();
    }

    @Override
    public void onResume() {
        Timber.d("onResume");
        super.onResume();
        YearlyApp app = (YearlyApp) getActivity().getApplication();
        eventsAdapter.checkWhetherDataNeedsToBeResorted(LocalDate.now(),app.getRepo());

    }

    @Override
    public void onPause() {
        Timber.d("onPause");
        super.onPause();
    }

    @Override
    public void onSelected(IEvent eventType) {
        LocalDate date = eventType.getDate();
        LocalDate now = LocalDate.now();
        Days d = Days.daysBetween(now, date);
        int days = d.getDays();
        Toast.makeText(getContext(), eventType.getTitle() + " in " + days + " days", Toast.LENGTH_SHORT).show();
    }
}
