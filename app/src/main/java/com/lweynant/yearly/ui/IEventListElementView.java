package com.lweynant.yearly.ui;

import android.view.View;

import com.lweynant.yearly.model.IEvent;

public interface IEventListElementView {

    void setOnClickListener(View.OnClickListener listener);

    View getView();

    void bindEvent(IEvent event);
}
