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
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.IEventType;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsActivityFragment extends Fragment implements IRString, EventsAdapter.onEventTypeSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<IEventType> events;
    private EventsAdapter eventsAdapter;

    public EventsActivityFragment() {
    }

    @Override
    public String getStringFromId(int id)
    {
        return getResources().getString(id);
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
        events = new ArrayList<>(10);
        events.add(new Birthday("Katinka", null, this));
        events.add(new Birthday("Kasper", null, this));
        events.add(new Birthday("Ann", null, this));
        events.add(new Birthday("Ludwig", null, this));
        eventsAdapter = new EventsAdapter(events, this);
        recyclerView.setAdapter(eventsAdapter);
        return view;
    }

    @Override
    public void onSelected(IEventType eventType) {
        Toast.makeText(getContext(), eventType.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
    }
}
