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
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.Date;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsActivityFragment extends Fragment implements IRString, EventsAdapter.onEventTypeSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
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
        EventRepo repo = EventRepo.getInstance();
        repo.add(new Birthday("Katinka", 10, Date.MARCH, this));
        repo.add(new Birthday("Kasper", 14, Date.MAY, this));
        repo.add(new Birthday("Ann", 5, Date.MARCH, this));
        repo.add(new Birthday("Ludwig", 8, Date.FEBRUARY, this));
        repo.add(new Birthday("Jinthe", 27, Date.OCTOBER, this));
        repo.add(new Birthday("Lis", 7, Date.NOVEMBER, this));
        repo.add(new Birthday("Caroline", 6, Date.FEBRUARY, this));
        repo.add(new Birthday("Ma", 11, Date.MARCH, this));
        repo.add(new Birthday("Janne", 24, Date.NOVEMBER, this));
        repo.add(new Birthday("Julien", 3, Date.FEBRUARY, this));
        repo.add(new Birthday("Pa", 22, Date.MAY, this));
        repo.add(new Birthday("Josephine", 29, Date.MAY, this));
        repo.add(new Birthday("Joren", 30, Date.MAY, this));
        repo.add(new Birthday("Bjorn", 22, Date.JULY, this));
        Calendar calendar = Calendar.getInstance();
        @Date.Month int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        repo.sortFrom(date, month);
        List<IEvent> events = repo.getEvents();
        eventsAdapter = new EventsAdapter(events, this);
        recyclerView.setAdapter(eventsAdapter);
        return view;
    }

    @Override
    public void onDetach() {
        Timber.d("onDetach");
        super.onDetach();
        EventRepo.deleteInstance();
    }

    @Override
    public void onSelected(IEvent eventType) {
        LocalDate date = new LocalDate(Calendar.getInstance().get(Calendar.YEAR), eventType.getMonth(),eventType.getDay());
        LocalDate now = LocalDate.now();
        if (date.isBefore(now)){
            date = date.plusYears(1);
        }
        Days d = Days.daysBetween(now, date);
        int days = d.getDays();
        Toast.makeText(getContext(), eventType.getTitle() + " in " + days + " days", Toast.LENGTH_SHORT).show();
    }
}
