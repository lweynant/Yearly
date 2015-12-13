package com.lweynant.yearly.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lweynant.yearly.IRString;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUUID;

import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class EventRepo {
    private IClock clock = null;
    private IUUID iuuid = null;
    private IRString rstring = null;
    private Context context = null;
    private List<IEvent> cachedEvents = null;

    public EventRepo(IClock clock, IUUID iuuid, IRString rstring, Context context) {
        this.clock = clock;
        this.iuuid = iuuid;
        this.rstring = rstring;
        this.context = context;
    }
    public EventRepo(){

    }

    public EventRepo add(IEvent event) {
        if (cachedEvents == null){
            cachedEvents = new ArrayList<>();
        }
        cachedEvents.add(event);
        return this;
    }


    public Observable<IEvent> getEvents() {
        if (cachedEvents != null || context == null){
            return getEventsFromCache();
        }
        else {
            return getEventsFromFile();
        }
    }
    private Observable<IEvent> getEventsFromFile() {
        Timber.d("getEventsFromFile");
        Observable<IEvent> observable = Observable.create(new Observable.OnSubscribe<IEvent>() {
            @Override
            public void call(Subscriber<? super IEvent> subscriber) {
                cachedEvents = null;
                ArrayList<IEvent> cache = new ArrayList<IEvent>();
                try {
                    String filename = "events.json";
                    BufferedReader in = null;
                    try {
                        GsonBuilder builder = new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .registerTypeAdapter(Birthday.class, new BirthdayInstanceCreator(clock, iuuid, rstring));
                        in = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(in).getAsJsonObject();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("events");
                        for (int i = 0; i < jsonArray.size(); i++){
                            JsonObject eventObj = jsonArray.get(i).getAsJsonObject();
                            Gson gson = builder.create();
                            Event event = gson.fromJson(eventObj, Birthday.class);
                            if (!subscriber.isUnsubscribed()){
                                Timber.d("calling onNext for %s", event.toString());
                                cache.add(event);
                                subscriber.onNext(event);
                            }
                            else {
                                break;
                            }
                        }
                        Timber.d("calling onCompleted");
                        subscriber.onCompleted();
                        cachedEvents = cache;
                    } catch (FileNotFoundException e) {
                        Timber.d("file not found, so we assume we have empty list");
                        subscriber.onCompleted();
                    }
                    finally {
                        if (in != null){
                            in.close();
                        }
                    }

                }
                catch (Throwable t){
                    subscriber.onError(t);
                }
            }
        });
        return observable;
    }

    private Observable<IEvent> getEventsFromCache() {
        Timber.d("getEventsFromCache");
        Observable<IEvent> observable = Observable.create(new Observable.OnSubscribe<IEvent>() {
            @Override
            public void call(Subscriber<? super IEvent> subscriber) {
                try {
                    if (cachedEvents != null) {
                        for (IEvent event : cachedEvents) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(event);
                            } else {
                                break;
                            }
                        }
                    }
                    subscriber.onCompleted();

                }
                catch (Throwable t){
                    subscriber.onError(t);
                }
            }
        });
        return observable;
    }

    public Observable<TimeBeforeNotification> timeBeforeFirstUpcomingEvent(final LocalDate from) {
        Observable<TimeBeforeNotification> time = getEvents()
                .map(event -> Event.timeBeforeNotification(from, event))
                .reduce((currentMin, x) -> TimeBeforeNotification.min(currentMin, x));
        return time;
    }
}
