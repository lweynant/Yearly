package com.lweynant.yearly.controller.list_events;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
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
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.ui.IEventViewFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


/**
 * A placeholder fragment containing a simple view.
 */
public class ListEventsActivityFragment extends BaseFragment implements EventsAdapter.EventSelectionListener, ListEventsContract.FragmentView {

    @Inject EventsAdapter eventsAdapter;
    @Inject IClock clock;
    @Inject IEventRepo repo;
    @Inject IEventViewFactory viewFactory;
    @Inject ListEventsContract.UserActionsListener userActionsListener;

    @Bind(R.id.events_recycler_view) RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;

    public static BaseFragment newInstance() {
        return new ListEventsActivityFragment();
    }

    public ListEventsActivityFragment() {
    }

    @Override
    protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.fragment_list_events, container, false);
        ButterKnife.bind(this, view);
        userActionsListener.setFragmentView(this);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //set the adapter
        Timber.d("injected component");
        Timber.d("repo: %s", repo.toString());

        eventsAdapter.setListener(this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(eventsAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

//        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                Timber.d("onSwiped");
//                if (direction == ItemTouchHelper.LEFT) {
//                    Timber.d("removing event");
//                    IEvent event = ((EventsAdapter.EventViewHolder) viewHolder).getEvent();
//                    userActionsListener.removeEvent(event);
//                }
//
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

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
        userActionsListener.loadEvents(false);


    }

    @Override
    public void onPause() {
        Timber.d("onPause");
        super.onPause();
        userActionsListener.cancelLoadingEvents();
    }

    @Override public void onSelected(IEvent event) {
        userActionsListener.openEventDetails(event);
    }


    @Override public void setProgressIndicator(boolean active) {

    }

    @Override public void showListItems(Observable<ListEventsContract.ListItem> items) {
        List<ListEventsContract.ListItem> list = items.toList().toBlocking().single();
        eventsAdapter.replaceData(list);
    }

    @Override public void onBackPressed() {

    }

    @Override public void onOptionsItemHomePressed() {

    }

}
