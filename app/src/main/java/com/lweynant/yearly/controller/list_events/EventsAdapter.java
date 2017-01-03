package com.lweynant.yearly.controller.list_events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.IListElementView;
import com.lweynant.yearly.ui.IEventViewFactory;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    public interface EventSelectionListener {
        public void onSelected(IEvent eventType);
    }

    private List<ListEventsContract.ListItem> listItems = new ArrayList<>();
    private EventSelectionListener listener;
    private IEventViewFactory viewFactory;


    public EventsAdapter(IEventViewFactory viewFactory) {
        Timber.d("create EventsAdapter instance");
        this.viewFactory = viewFactory;
    }

    public void setListener(EventSelectionListener listener) {
        this.listener = listener;
    }


    @Override public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IListElementView view = viewFactory.getListElementView(parent, viewType);
        //FragmentView v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view, listener);
        return eventViewHolder;
    }

    @Override public void onBindViewHolder(EventViewHolder holder, int position) {
        ListEventsContract.ListItem listItem = listItems.get(position);
        holder.bindListItem(listItem);
    }

    @Override public int getItemViewType(int position) {
        return viewFactory.getListElementViewType(listItems.get(position));
    }

    @Override public int getItemCount() {
        return listItems.size();
    }

    public void replaceData(List<ListEventsContract.ListItem> listItems) {
        synchronized (this) {
            Timber.d("onDataLoaded");
            this.listItems = listItems;
            notifyDataSetChanged();
        }
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final EventSelectionListener listener;
        private IListElementView eventListElementView;
        private ListEventsContract.ListItem listItem;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder

        public EventViewHolder(IListElementView itemView, EventSelectionListener listener) {
            super(itemView.getView());
            itemView.setOnClickListener(this);
            this.listener = listener;
            this.eventListElementView = itemView;
        }


        @Override public void onClick(View v) {
            if (listener != null) {
                if (listItem.isEvent()) {
                    listener.onSelected(listItem.getEvent());
                }
            }
        }

        public void bindListItem(ListEventsContract.ListItem listItem) {
            this.listItem = listItem;
            eventListElementView.bindEvent(listItem);
        }
    }

}
