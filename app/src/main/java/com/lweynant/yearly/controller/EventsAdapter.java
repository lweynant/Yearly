package com.lweynant.yearly.controller;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.ui.IEventListElementView;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> implements IEventRepoListener {

    private List<IEvent> events = new ArrayList<>();
    private onEventTypeSelectedListener listener;
    private LocalDate sortedFrom = new LocalDate(1900, 1, 1);
    private Subscription subscription;
    private IEventViewFactory viewFactory;
    private String repoId;
    private final List<IEvent> empyList = new ArrayList<>();
    private String currentlyUpdatingRepoModifId;
    //private boolean first = false;


    public EventsAdapter(IEventViewFactory viewFactory) {
        Timber.d("create EventsAdapter instance");
        this.viewFactory = viewFactory;

    }

    public void setListener(EventsAdapter.onEventTypeSelectedListener listener) {
        this.listener = listener;
    }

    public void checkWhetherDataNeedsToBeResorted(LocalDate now, EventRepo repo) {
        Timber.d("checkWhetherDataNeedsToBeResorted");
        if (sortedFrom.isEqual(now) && repo.getModificationId().equals(repoId)) {
            Timber.d("we sorted repo on same day, so nothing to do");
            return;
        } else {
            Timber.d("sort on new date %s and/or id %s", now.toString(), repo.getModificationId());
            onDataSetChanged(repo);
            sortedFrom = now;
            repoId = repo.getModificationId();
        }
    }

    protected void updateDataSet(List<IEvent> events, String modifId) {
        synchronized (this) {
            Timber.d("updateDataSet %s", modifId);
            this.events = events;
            if (subscription != null) subscription.unsubscribe();
            notifyDataSetChanged();
        }
    }

    protected void dataSetUpdateCancelled(String currentlyUpdatingRepoModifId) {
        Timber.d("dataSetUpdateCancelled %s", currentlyUpdatingRepoModifId);
        if (subscription != null) subscription.unsubscribe();
    }
    public void onDetach() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override public void onDataSetChanged(EventRepo repo) {
        synchronized (this) {
            Timber.d("onDataSetChanged - getEvents from repo with modif id: %s", repo.getModificationId());
            if (subscription != null && !subscription.isUnsubscribed()) {
                Timber.d("we allready have a subscription - unsubscribe first.. %s", currentlyUpdatingRepoModifId);
                subscription.unsubscribe();
                dataSetUpdateCancelled(currentlyUpdatingRepoModifId);
            }
            currentlyUpdatingRepoModifId = repo.getModificationId();
            Observable<IEvent> eventsObservable = repo.getEventsSubscribedOnProperScheduler();
            subscription = eventsObservable
                    .toSortedList()
                    .first()
                    //.delay(first ? 5000 : 10, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<IEvent>>() {
                        public List<IEvent> newEvents = empyList;
                        private final String modifId = currentlyUpdatingRepoModifId;

                        @Override
                        public void onCompleted() {
                            Timber.d("onCompleted %s", newEvents.toString());
                            updateDataSet(newEvents, modifId);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "onError");
                            dataSetUpdateCancelled(modifId);
                        }

                        @Override
                        public void onNext(List<IEvent> iEvents) {
                            Timber.d("onNext");
                            newEvents = iEvents;
                        }
                    });
        }
        //first = false;
        Timber.d("end of onDataSetChanged");

    }

    @Override public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IEventListElementView view = viewFactory.getEventListElementView(parent, viewType);
        //View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view, listener);
        return eventViewHolder;
    }

    @Override public void onBindViewHolder(EventViewHolder holder, int position) {
        IEvent event = events.get(position);
        holder.bindEvent(event);
    }

    @Override public int getItemViewType(int position) {
        return viewFactory.getEventListElementViewType(events.get(position));
    }

    @Override public int getItemCount() {
        return events.size();
    }

    public interface onEventTypeSelectedListener {
        public void onSelected(IEvent eventType);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final onEventTypeSelectedListener listener;
        private IEventListElementView eventListElementView;
        private IEvent event;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder

        public EventViewHolder(IEventListElementView itemView, onEventTypeSelectedListener listener) {
            super(itemView.getView());
            itemView.setOnClickListener(this);
            this.listener = listener;
            this.eventListElementView = itemView;
        }

        public IEvent getEvent() {
            return event;
        }

        @Override public void onClick(View v) {
            if (listener != null) {
                listener.onSelected(event);
            }
        }

        public void bindEvent(IEvent event) {
            this.event = event;
            eventListElementView.bindEvent(event);
        }
    }

}
