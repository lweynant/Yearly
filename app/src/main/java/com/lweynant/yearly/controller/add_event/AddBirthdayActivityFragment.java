package com.lweynant.yearly.controller.add_event;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.DateSelector;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.lweynant.yearly.controller.add_event.AddBirthdayContract.REQUEST_IMAGE;


public class AddBirthdayActivityFragment extends BaseFragment implements DateSelector.OnClickListener,
        AddBirthdayContract.FragmentView {


    @Bind(R.id.edit_text_birthday_date) EditText dateEditText;
    @Bind(R.id.edit_text_first_name) EditText nameEditText;
    @Bind(R.id.edit_text_lastname) EditText lastNameEditText;
    @Bind(R.id.image_button) ImageButton imageButton;
    private View fragmentView;
    @Inject DateSelector dateSelector;
    @Inject AddBirthdayContract.UserActionsListener userActionsListener;
    private String initialName;
    private String initialLastName;
    private String initialFormatedDate;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    public static AddBirthdayActivityFragment newInstance(Bundle args) {
        AddBirthdayActivityFragment fragment = new AddBirthdayActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public AddBirthdayActivityFragment() {
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        Bundle args = savedInstanceState != null? savedInstanceState: getArguments();
        userActionsListener.initialize(this, args);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        fragmentView = inflater.inflate(R.layout.fragment_add_birthday, container, false);
        ButterKnife.bind(this, fragmentView);

        if (initialFormatedDate != null) {
            dateEditText.setText(initialFormatedDate);
            nameEditText.setText(initialName);
            lastNameEditText.setText(initialLastName);
        }
        //noinspection ResourceType
        dateSelector.prepare(getContext(), this, selectedYear, selectedMonth, selectedDay);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentView.requestFocus();
                dateSelector.show();
            }
        });
        //prototyping camera
        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override public void onClick(View v) {
                userActionsListener.takePicture();
            }
        });

        return fragmentView;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        Timber.d("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        userActionsListener.setInputObservables(RxTextView.textChangeEvents(nameEditText).skip(1).map(e -> e.text()),
                RxTextView.textChangeEvents(lastNameEditText).skip(1).map(e -> e.text()),
                RxTextView.textChangeEvents(dateEditText).skip(1).map(e -> e.text()));

    }

    @Override public void onResume() {
        Timber.d("onResume");
        super.onResume();

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult");
        if (requestCode == AddBirthdayContract.REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Timber.d("set image");
                userActionsListener.setPicture();
            }
            else {
                userActionsListener.clearPicture();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override public void enableSaveButton(Boolean enabled) {
        Timber.d("enableSaveButton %s", enabled ? "true" : "false");
        //todo added a save button in the toolbar
    }

    @Override public void showSavedBirthday(IEvent event) {
        Timber.d("showSavedBirthday %s", event.toString());
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        event.archiveTo(bundle);
        resultIntent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override public void showNothingSaved() {
        Timber.d("showNothingSaved");
        getActivity().setResult(Activity.RESULT_CANCELED);
    }

    @Override public void showPicture(File picture) {
        Timber.d("showPicture");
        Picasso.with(getContext()).load(picture).centerCrop().fit().into(imageButton);
    }

    @Override public void takePicture(File pictureFile) {
        Timber.d("takePicture in file %s", pictureFile.toString());
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(pictureFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(captureImage, REQUEST_IMAGE);
    }


    @Override public void onSaveInstanceState(Bundle outState) {
        Timber.d("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        userActionsListener.saveInstanceState(outState);
    }

    @Override public void onPositiveClick(int year, @Date.Month int month, int day) {
        userActionsListener.setDate(year, month, day);
    }

    @Override public void onPositiveClick(@Date.Month int month, int day) {
        userActionsListener.setDate(month, day);
    }

    @Override public void onNegativeClick() {

    }

    @Override
    public void initialize(String name, String lastName, String formattedDate, int selectedYear, @Date.Month int selectedMonth, int selectedDay) {
        Timber.d("initialize");
        this.initialName = name;
        this.initialLastName  = lastName;
        this.initialFormatedDate = formattedDate;
        this.selectedYear = selectedYear;
        this.selectedMonth = selectedMonth;
        this.selectedDay = selectedDay;
    }

    @Override public void showDate(String date) {
        Timber.d("showDate");
        dateEditText.setText(date);
    }

    @Override public void onBackPressed() {
        Timber.d("onBackPressed");
        userActionsListener.saveBirthday();
    }

    @Override public void onOptionsItemHomePressed() {
        Timber.d("onOptionsItemHomePressed");
        userActionsListener.saveBirthday();
    }

}
