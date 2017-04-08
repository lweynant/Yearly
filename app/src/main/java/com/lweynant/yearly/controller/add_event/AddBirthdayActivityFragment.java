package com.lweynant.yearly.controller.add_event;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.DateSelector;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;




public class AddBirthdayActivityFragment extends BaseFragment implements DateSelector.OnClickListener,
        AddBirthdayContract.FragmentView {


    @Bind(R.id.edit_text_birthday_date) TextInputEditText dateEditText;
    @Bind(R.id.edit_text_first_name) TextInputEditText nameEditText;
    @Bind(R.id.edit_text_lastname) TextInputEditText lastNameEditText;
    private View fragmentView;
    @Inject DateSelector dateSelector;
    @Inject AddBirthdayContract.UserActionsListener userActionsListener;
    private String initialName;
    private String initialLastName;
    private String initialFormatedDate;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private MenuItem saveMenu;
    private boolean saveButtonState = false;

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
        setHasOptionsMenu(true);
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
        return fragmentView;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        Timber.d("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
        userActionsListener.setInputObservables(RxTextView.textChangeEvents(nameEditText).map(e -> e.text()),
                RxTextView.textChangeEvents(lastNameEditText).map(e -> e.text()),
                RxTextView.textChangeEvents(dateEditText).map(e -> e.text()));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Timber.d("onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_add_birthday, menu);
        saveMenu = menu.findItem(R.id.action_save);
        saveMenu.setEnabled(saveButtonState);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Timber.i("save birthday");
            userActionsListener.saveBirthday();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void enableSaveButton(Boolean enabled) {
        Timber.d("enableSaveButton %s", enabled ? "true" : "false");
        if (saveMenu != null) {
            saveMenu.setEnabled(enabled);
        }
        saveButtonState = enabled;
    }


    @Override public void showSavedBirthday(IEvent event) {
        Timber.d("showSavedBirthday");
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        event.archiveTo(bundle);
        resultIntent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
        getActivity().finish();
    }

    @Override public void showNothingSaved() {
        Timber.d("showNothingSaved");
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
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
        this.initialName = name;
        this.initialLastName  = lastName;
        this.initialFormatedDate = formattedDate;
        this.selectedYear = selectedYear;
        this.selectedMonth = selectedMonth;
        this.selectedDay = selectedDay;
    }

    @Override public void showDate(String date) {
        dateEditText.setText(date);
    }


    @Override public boolean onBackPressed() {

        return handleBirthdayModification();
    }

    private boolean handleBirthdayModification() {
        if (userActionsListener.isBirthdayModified()){
            Timber.d("notify user that birthday is NOT saved, give him option to continue editing!!");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.add_birthday_ask_throw_away_modifications);
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.add_birthday_throw_away, new DialogInterface.OnClickListener() {


                @Override public void onClick(DialogInterface dialog, int which) {
                    Timber.d("pressed positive button, throw away changes");
                    userActionsListener.throwAwayModifications();

                }
            });
            builder.setPositiveButton(R.string.add_birthday_ask_throw_away_modifications_cancel, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    Timber.d("pressed negative button, cancel - continue changing");

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        else {
            showNothingSaved();
        }
        return false;
    }

    @Override public boolean onOptionsItemHomePressed() {
        return handleBirthdayModification();
    }

}
