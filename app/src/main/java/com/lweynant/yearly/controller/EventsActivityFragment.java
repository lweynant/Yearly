package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lweynant.yearly.AlarmGenerator;
import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.model.TimeBeforeNotification;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.ui.IEventNotificationText;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.EventRepoSerializerToFileDecorator;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventsActivityFragment extends BaseFragment implements EventsAdapter.onEventTypeSelectedListener, IEventRepoListener {

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
        YearlyApp app = (YearlyApp) getActivity().getApplication();
        EventViewFactory viewFactory = new EventViewFactory(app, new Clock());

        eventsAdapter = new EventsAdapter(viewFactory, this);

        recyclerView.setAdapter(eventsAdapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Timber.d("onSwiped");
                if (direction == ItemTouchHelper.LEFT){
                    Timber.d("removing event");
                    IEvent event = ((EventsAdapter.EventViewHolder)viewHolder).getEvent();
                    getRepo().remove(event);
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private EventRepo getRepo() {
        return ((YearlyApp)getActivity().getApplication()).getRepo();
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
        EventRepo repo = getRepo();
        repo.addListener(eventsAdapter);

        eventsAdapter.checkWhetherDataNeedsToBeResorted(LocalDate.now(), repo);
        repo.addListener(this);

    }

    @Override
    public void onPause() {
        Timber.d("onPause");
        super.onPause();
        EventRepo repo = getRepo();
        repo.removeListener(eventsAdapter);
        repo.removeListener(this);
    }

    @Override
    public void onSelected(IEvent eventType) {
        EventViewFactory factory = new EventViewFactory((IRString) getActivity().getApplication(), new Clock());
        IEventNotificationText notifText = factory.getEventNotificationText(eventType);
        String text = notifText.getOneLiner();
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataSetChanged(EventRepo repo) {
        Timber.d("onDataSetChanged");
        YearlyApp app = (YearlyApp) getActivity().getApplication();
        Observable<IEvent> events = repo.getEvents();
        Timber.i("archive");
        events.subscribeOn(Schedulers.io())
                .subscribe(new EventRepoSerializerToFileDecorator(app.getRepoAccessor(), new EventRepoSerializer(new Clock())));
        Timber.i("set next event");
        LocalDate now = LocalDate.now();
        Observable<TimeBeforeNotification> nextAlarmObservable = repo.timeBeforeFirstUpcomingEvent(now);
        nextAlarmObservable.subscribeOn(Schedulers.io())
                .subscribe(new AlarmGenerator(getContext(), now));


    }
}
