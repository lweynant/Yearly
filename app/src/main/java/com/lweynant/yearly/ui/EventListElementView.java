package com.lweynant.yearly.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.LocalDate;

public class EventListElementView implements IListElementView {
    private final IStringResources rstring;
    private final TextView nameTextView;
    private final TextView dateTextView;
    private final View view;

    public EventListElementView(IStringResources rstring, ViewGroup parent) {
        this.rstring = rstring;
        view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        nameTextView = (TextView) view.findViewById(R.id.event_list_item_name);
        dateTextView = (TextView) view.findViewById(R.id.event_list_item_date);

    }

    @Override public void setOnClickListener(View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }

    @Override public View getView() {
        return view;
    }

    @Override public void bindEvent(ListEventsContract.ListItem listItem) {
        IEvent event = listItem.getEvent();
        LocalDate eventDate = event.getDate();
        nameTextView.setText(event.getName());
        dateTextView.setText(getDateAsText(event.getDate()));

    }
    private String getDateAsText(LocalDate date) {
        return date.dayOfWeek().getAsText() + " " + date.getDayOfMonth() + " " + date.monthOfYear().getAsText();
    }

}
