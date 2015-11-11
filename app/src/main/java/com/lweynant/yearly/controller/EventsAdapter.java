package com.lweynant.yearly.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.LocalDate;

import java.util.List;

import timber.log.Timber;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<IEvent> events;
    private final onEventTypeSelectedListener listener;
    private LocalDate sortedFrom;


    public void checkWhetherDataNeedsToBeResorted(LocalDate now, EventRepo repo) {
        Timber.d("checkWhetherDataNeedsToBeResorted");
        if (sortedFrom.isEqual(now)) {
            Timber.d("we sorted repo on same day, so nothing to do");
            return;
        }
        else {
            Timber.d("sort on new date");
            sortedFrom = now;
            repo.sortFrom(sortedFrom.getMonthOfYear(), sortedFrom.getDayOfMonth());
            events = repo.getEvents();
            notifyDataSetChanged();
        }
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

        @Override
        public void onClick(View v) {
            listener.onSelected(event);
        }

        public void bindEvent(IEvent event) {
            this.event = event;
            textView.setText(event.getTitle() + " at " + event.getDay() + "/" + event.getMonth());
        }
    }

    public EventsAdapter(EventRepo repo, LocalDate now, EventsAdapter.onEventTypeSelectedListener listener) {
        this.sortedFrom = now;
        repo.sortFrom(now.getMonthOfYear(), now.getDayOfMonth());
        this.events = repo.getEvents();
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
