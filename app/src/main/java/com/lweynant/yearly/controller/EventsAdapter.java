package com.lweynant.yearly.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;

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
    private final onEventTypeSelectedListener listener;
    private LocalDate sortedFrom =  new LocalDate(1900, 1, 1);
    private Subscription subscription;


    public void checkWhetherDataNeedsToBeResorted(LocalDate now, EventRepo repo) {
        Timber.d("checkWhetherDataNeedsToBeResorted");
        if (sortedFrom.isEqual(now)) {
            Timber.d("we sorted repo on same day, so nothing to do");
            return;
        }
        else {
            Timber.d("sort on new date %s", now.toString());
            onDataSetChanged(repo);
            sortedFrom = now;
        }
    }

    private void setEvents(List<IEvent> events) {
        Timber.d("setEvents");
        this.events = events;
        notifyDataSetChanged();
    }

    public void onDetach() {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onDataSetChanged(EventRepo repo) {
        Timber.d("onDataSetChanged");
        Observable<IEvent> eventsObservable = repo.getEvents();
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
        subscription = eventsObservable.subscribeOn(Schedulers.io())
                .toSortedList()
                .first()
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

    public interface onEventTypeSelectedListener {
        public void onSelected(IEvent eventType);
    }



    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final onEventTypeSelectedListener listener;
        private TextView textView;
        private IEvent event;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder

        public EventViewHolder(View itemView, EventsAdapter.onEventTypeSelectedListener listener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.listener = listener;
            textView = (TextView)itemView;
        }

        public IEvent getEvent(){
            return event;
        }
        @Override
        public void onClick(View v) {
            listener.onSelected(event);
        }

        public void bindEvent(IEvent event) {
            this.event = event;
            LocalDate eventDate = event.getDate();
            textView.setText(event.getTitle() + ": " + eventDate.dayOfWeek().getAsShortText() + " " + eventDate.getDayOfMonth() + " " + eventDate.monthOfYear().getAsShortText());
        }
    }


    public EventsAdapter(EventsAdapter.onEventTypeSelectedListener listener) {
        this.listener = listener;

    }
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(v, listener);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        IEvent event = events.get(position);
        holder.bindEvent(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

}
