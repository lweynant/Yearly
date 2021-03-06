package com.lweynant.yearly.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.LocalDate;


public class BirthdayListElementView implements IListElementView {

    private final View view;
    private final ImageView myImageView;
    private TextView nameTextView;
    private TextView dateTextView;
    private final IEventStringResource stringResource;

    public BirthdayListElementView(IStringResources rstring, ViewGroup parent) {
        stringResource = new BirthdayStringResource(rstring);
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.birthday_list_item, parent, false);
        nameTextView = (TextView) view.findViewById(R.id.birthday_list_item_name);
        dateTextView = (TextView) view.findViewById(R.id.birthday_list_item_date);
        myImageView = (ImageView) view.findViewById(R.id.birthday_list_item_image);
    }

    @Override
    public void bindEvent(ListEventsContract.ListItem listItem) {
        IEvent event = listItem.getEvent();
        nameTextView.setText(event.getName());
        dateTextView.setText(getDateAsText(event.getDate()));
        //get first letter of each String item
        String firstLetter = String.valueOf(event.getName().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(event.getName());
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        myImageView.setImageDrawable(drawable);

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
