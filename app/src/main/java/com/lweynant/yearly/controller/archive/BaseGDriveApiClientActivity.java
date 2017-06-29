package com.lweynant.yearly.controller.archive;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.lweynant.yearly.controller.BaseActivity;

import timber.log.Timber;

public abstract class BaseGDriveApiClientActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override public void onConnected(@Nullable Bundle bundle) {

    }

    @Override public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended %d", i);
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d("onConnectionFailed");


    }
    protected GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
