package com.lweynant.yearly.ui;

import android.view.View;

import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.model.IEvent;

public interface IListElementView {

    void setOnClickListener(View.OnClickListener listener);

    View getView();

    void bindEvent(ListEventsContract.ListItem listItem);
}
