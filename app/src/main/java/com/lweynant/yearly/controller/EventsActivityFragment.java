package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.ui.IEventNotificationText;
import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventsActivityFragment extends BaseFragment implements EventsAdapter.onEventTypeSelectedListener {

    @Inject EventsAdapter eventsAdapter;
    @Inject IClock clock;
    @Inject EventRepo repo;
    @Inject EventViewFactory viewFactory;
    @Bind(R.id.events_recycler_view) RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    public EventsActivityFragment() {
    }

    @Override
    protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //set the adapter
        YearlyApp app = (YearlyApp) getActivity().getApplication();
        Timber.d("injected component");
        Timber.d("repo: %s", repo.toString());

        eventsAdapter.setListener(this);

        recyclerView.setAdapter(eventsAdapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Timber.d("onSwiped");
                if (direction == ItemTouchHelper.LEFT) {
                    Timber.d("removing event");
                    IEvent event = ((EventsAdapter.EventViewHolder) viewHolder).getEvent();
                    repo.remove(event).commit();
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onDetach() {
        Timber.d("onDetach");
        eventsAdapter.onDetach();
        super.onDetach();
    }

    @Override
    public void onResume() {
        Timber.d("onResume");
        super.onResume();
        repo.addListener(eventsAdapter);

        eventsAdapter.checkWhetherDataNeedsToBeResorted(LocalDate.now(), repo);

    }

    @Override
    public void onPause() {
        Timber.d("onPause");
        super.onPause();
        repo.removeListener(eventsAdapter);
    }

    @Override
    public void onSelected(IEvent eventType) {
        EventViewFactory factory = new EventViewFactory((IStringResources) getActivity().getApplication(), clock);
        IEventNotificationText notifText = factory.getEventNotificationText(eventType);
        String text = notifText.getOneLiner();
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
