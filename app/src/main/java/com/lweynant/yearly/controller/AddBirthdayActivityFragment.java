package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;




public class AddBirthdayActivityFragment extends BaseFragment implements DateSelector.OnClickListener, AddBirthdayContract.View {

    @Bind(R.id.edit_text_birthday_date) EditText dateEditText;
    @Bind(R.id.edit_text_name) EditText nameEditText;
    @Bind(R.id.edit_text_lastname) EditText lastNameEditText;
    private View fragmentView;
    private CompositeSubscription subscription;
    @Inject DateSelector dateSelector;
    @Inject AddBirthdayContract.UserActionsListener userActionsListener;

    public AddBirthdayActivityFragment() {
    }

    @Override
    protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        fragmentView = inflater.inflate(R.layout.fragment_add_birthday, container, false);
        ButterKnife.bind(this, fragmentView);

        dateSelector.prepare(getContext(), this);
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
        userActionsListener.restoreFromInstanceState(this, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        subscription = new CompositeSubscription();
        Observable<CharSequence> nameObservable = RxTextView.textChangeEvents(nameEditText).skip(1)
                .map(e -> e.text());
        Observable<CharSequence> lastNameObservable = RxTextView.textChangeEvents(lastNameEditText).skip(1)
                .map(e -> e.text());
        Observable<Boolean> validName = nameObservable
                .doOnNext(t -> Timber.d("name text field %s", t))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> validDate = RxTextView.textChangeEvents(dateEditText).skip(1)
                .map(e -> e.text())
                .doOnNext(t -> Timber.d("date text field %s", t))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> enableSaveButton = Observable.combineLatest(validName, validDate, (a, b) -> a && b);

        subscription.add(nameObservable
                .subscribe(n -> {
                    userActionsListener.setName(n.toString());
                }));
        subscription.add(lastNameObservable
                .subscribe(n -> {
                    userActionsListener.setLastName(n.toString());
                }));

        subscription.add(enableSaveButton.distinctUntilChanged()
                .subscribe(enabled -> enableSaveButton(enabled)));

    }

    private void enableSaveButton(Boolean enabled) {
        Timber.d("enableSaveButton %s", enabled ? "true" : "false");
        //todo add a save button in the toolbar
    }


    @Override public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
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

    @Override public void showDate(String date) {
        dateEditText.setText(date);
    }
}
