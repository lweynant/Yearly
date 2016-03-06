package com.lweynant.yearly.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.LocalDate;
import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BirthdayListElementView implements IEventListElementView {

    private final View view;
    private TextView nameTextView;
    private TextView dateTextView;
    private final IEventStringResource stringResource;

    public BirthdayListElementView(IStringResources rstring, ViewGroup parent) {
        stringResource = new BirthdayStringResource(rstring);
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.birthday_list_item, parent, false);
        nameTextView = (TextView) view.findViewById(R.id.birthday_list_item_name);
        dateTextView = (TextView) view.findViewById(R.id.birthday_list_item_date);
    }

    @Override
    public void bindEvent(IEvent event) {
        LocalDate eventDate = event.getDate();
        nameTextView.setText(event.getName());
        dateTextView.setText(getDateAsText(event.getDate()));
    }

    private String getDateAsText(LocalDate date) {
        return date.dayOfWeek().getAsText() + " " + date.getDayOfMonth() + " " + date.monthOfYear().getAsText();
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }

    @Override
    public View getView() {
        return view;
    }
}
