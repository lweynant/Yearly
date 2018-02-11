package com.lweynant.yearly;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule  {
    @Provides @PerApp NotificationManager providesNotificationManager(YearlyApp app){
        return (NotificationManager)app.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    @Provides @PerApp NotificationChannels providesNotificationChannels(INotificationChannelFactory channelFactory, NotificationManager nm, IStringResources sr){
        return new NotificationChannels(channelFactory, nm, sr);
    }
    @Provides @PerApp INotificationChannelFactory providesNotificationChannelFactory(){
        return  new INotificationChannelFactory() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public NotificationChannel createChannel(String id, String name, int importance) {
                return new NotificationChannel(id, name, importance);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override public NotificationChannelGroup createGroup(String groupId, String name) {
                return new NotificationChannelGroup(groupId, name);
            }
        };
    }

}
