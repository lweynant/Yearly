package com.lweynant.yearly.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.list_events.ListEventsContract;

public class SeparatorListElementView implements IListElementView {
    private final IStringResources rstring;
    private final View view;
    private final TextView nameTextView;

    public SeparatorListElementView(IStringResources rstring, ViewGroup parent) {
        this.rstring = rstring;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.birthday_list_separator, parent, false);
        nameTextView = (TextView) view.findViewById(R.id.birthday_list_separator_name);

    }
    @Override public void setOnClickListener(View.OnClickListener listener) {

    }

    @Override public View getView() {
        return view;
    }

    @Override public void bindEvent(ListEventsContract.ListItem item) {
        nameTextView.setText(item.getSeparator());
    }
}
