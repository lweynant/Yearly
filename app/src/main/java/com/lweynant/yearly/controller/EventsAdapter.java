package com.lweynant.yearly.controller;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.ui.IEventListElementView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> implements IEventRepoListener {

    private List<IEvent> events = new ArrayList<>();
    private onEventTypeSelectedListener listener;
    private LocalDate sortedFrom = new LocalDate(1900, 1, 1);
    private Subscription subscription;
    private EventViewFactory viewFactory;
    private String repoId;


    public EventsAdapter(EventViewFactory viewFactory) {
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

    private void setEvents(List<IEvent> events) {
        Timber.d("setEvents");
        this.events = events;
        notifyDataSetChanged();
    }

    public void onDetach() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onDataSetChanged(EventRepo repo) {
        Timber.d("onDataSetChanged");
        Observable<IEvent> eventsObservable = repo.getEvents();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        subscription = eventsObservable.subscribeOn(Schedulers.io())
                .toSortedList()
                .first()
                        //.delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<IEvent>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "onError");
                    }

                    @Override
                    public void onNext(List<IEvent> iEvents) {
                        Timber.d("onNext");
                        setEvents(iEvents);
                    }
                });


    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IEventListElementView view = viewFactory.getEventListElementView(parent, viewType);
        //View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view, listener);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        IEvent event = events.get(position);
        holder.bindEvent(event);
    }

    @Override
    public int getItemViewType(int position) {
        return viewFactory.getEventListElementViewType(events.get(position));
    }

    @Override
    public int getItemCount() {
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

        @Override
        public void onClick(View v) {
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
