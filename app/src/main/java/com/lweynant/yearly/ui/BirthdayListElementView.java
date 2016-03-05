package com.lweynant.yearly.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.LocalDate;

public class BirthdayListElementView implements IEventListElementView {

    private final TextView textView;
    private final IEventStringResource stringResource;

    public BirthdayListElementView(IStringResources rstring, ViewGroup parent) {
        stringResource = new BirthdayStringResource(rstring);
        textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void bindEvent(IEvent event) {
        LocalDate eventDate = event.getDate();
        textView.setText(event.getName() + ": " + eventDate.dayOfWeek().getAsText() + " " + eventDate.getDayOfMonth() + " " + eventDate.monthOfYear().getAsText());
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        textView.setOnClickListener(listener);
    }

    @Override
    public View getView() {
        return textView;
    }
}
