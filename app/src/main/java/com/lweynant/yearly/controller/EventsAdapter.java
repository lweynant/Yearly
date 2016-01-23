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
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<IEvent> events = new ArrayList<>();
    private onEventTypeSelectedListener listener;
    private LocalDate sortedFrom = new LocalDate(1900, 1, 1);
    private IEventViewFactory viewFactory;


    public EventsAdapter(IEventViewFactory viewFactory) {
        Timber.d("create EventsAdapter instance");
        this.viewFactory = viewFactory;

    }

    public void setListener(EventsAdapter.onEventTypeSelectedListener listener) {
        this.listener = listener;
    }


    @Override public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IEventListElementView view = viewFactory.getEventListElementView(parent, viewType);
        //FragmentView v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
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

    public void replaceData(List<IEvent> events) {
        synchronized (this) {
            Timber.d("onDataLoaded");
            this.events = events;
            notifyDataSetChanged();
        }
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
