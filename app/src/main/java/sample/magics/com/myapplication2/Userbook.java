package sample.magics.com.myapplication2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Userbook extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener  {

    TextView username, bookingnum, datentime, Mobilenum;
    EditText service_desc;
    Button booking_btn;
    Spinner servicetype;
    private Button mBtnRegister;
    ArrayList<String> statusList;
    String selectedImagePath="", str_status ="", partyid;
    SharedPref sharedPref;
    SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");

    double lat, lon, delat, delon;
    private Location mLocation;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    private LocationRequest mLocationRequest;
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    public static final int FAST_CEILING_IN_SECONDS = 1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usercall);

        sharedPref = new SharedPref(Userbook.this);

        username = (TextView)findViewById(R.id.username);
        bookingnum = (TextView)findViewById(R.id.bookingnum);
        datentime = (TextView)findViewById(R.id.datentime);
        Mobilenum = (TextView)findViewById(R.id.Mobilenum);
        service_desc = (EditText) findViewById(R.id.service_desc);
        servicetype = (Spinner)findViewById(R.id.servicetype);
        booking_btn = (Button) findViewById(R.id.booking_btn);

        booking_btn.setOnClickListener(this);

        Calendar c = Calendar.getInstance();
        String dateTime = df.format(c.getTime());

        partyid = (sharedPref.getString("partyid"));
        username.setText("Name : " + sharedPref.getString("partyName"));
        bookingnum.setText("Booking no : " + String.valueOf(partyid));
        datentime.setText("Time of Booking : " + dateTime);
        Mobilenum.setText("Mobile number :" + sharedPref.getString("phone_No"));

        GetServiceAPI();

        if (servicesConnected()) {
            buildGoogleApiClient();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        }
    }

    public void GetServiceAPI() {
        String URL = CommonUtils.SERVICE_TYPE;
        final ProgressDialog dialog1 = ProgressDialog.show(Userbook.this, "Sun Info Technologies", "Please Wait...", true);

        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0){
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String data = jsonObject.getString("servicelist");

                                    String[] elements = data.replace(" ","").split(",");
                                    List<String> fixedLenghtList = Arrays.asList(elements);
                                    statusList = new ArrayList<String>(fixedLenghtList);
                                }
                            }else {
                                Toast.makeText(Userbook.this, "Service List is empty!", Toast.LENGTH_SHORT).show();
                            }

                            ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item,statusList);
                            aa.setDropDownViewResource(R.layout.spinner_item);
                            servicetype.setAdapter(aa);

                            servicetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    str_status = statusList.get(position).toLowerCase();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }dialog1.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("in", "Error:" + anError.getErrorCode());
                        Log.e("in", "Error:" + anError.getErrorBody());
                        Log.e("in", "Error:" + anError.getErrorDetail());
                        dialog1.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.booking_btn:
                AddBooking();
                break;
        }
    }

    public void AddBooking() {
        String URL = CommonUtils.ADD_TASK + "partyid=" + partyid +
                "&projectid=" + "1" +
                "&taskdesc=" + service_desc.getText().toString() +
                "&taskdate=" + datentime.getText().toString() +
                "&latitude=" + String.valueOf(lat) +
                "&longitude=" + String.valueOf(lon) +
                "&Createdbyid=" + "7";
        final ProgressDialog dialog1 = ProgressDialog.show(Userbook.this, "Sun Info Technologies", "Please Wait...", true);

        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0){
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    if (jsonObject.getString("result").equals("True")){
                                        Toast.makeText(Userbook.this, "Booking Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Userbook.this, Userbook.class));
                                        finish();
                                    }else {
                                        Toast.makeText(Userbook.this, "Booking Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else {
                                Toast.makeText(Userbook.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }dialog1.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("in", "Error:" + anError.getErrorCode());
                        Log.e("in", "Error:" + anError.getErrorBody());
                        Log.e("in", "Error:" + anError.getErrorDetail());
                        dialog1.dismiss();
                    }
                });
    }

    private boolean servicesConnected() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return ConnectionResult.SUCCESS == resultCode;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("", "onLocationChanged: " + location.getLatitude() + ", " + location.getLongitude());
        mLocation = location;

        if (mLocation!=null){
            lat = mLocation.getLatitude();
            lon = mLocation.getLongitude();
        }
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        try {
            googleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(Userbook.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(Userbook.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            if (googleApiClient != null) {
                googleApiClient.connect();
            }
            getMyLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    public void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(Userbook.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    @SuppressLint("RestrictedApi") LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000000);
                    locationRequest.setFastestInterval(3000000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(Userbook.this, Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(Userbook.this, REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient!=null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        Log.d("","onStop");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Log.d("GARG", "***** on Stop mGoogleApiClient disconnect ***** ");
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

}
