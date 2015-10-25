package com.lweynant.yearly.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.model.IEvent;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private final List<IEvent> events;
    private final onEventTypeSelectedListener listener;

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
            textView.setText(event.getTitle());
        }
    }

    public EventsAdapter(List<IEvent> events, EventsAdapter.onEventTypeSelectedListener listener){
        this.events = events;
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
