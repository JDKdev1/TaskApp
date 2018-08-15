package sample.magics.com.myapplication2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.provider.Settings.Secure;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements GoogleMap.OnCameraChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    Spinner usertype;
    Button mBtnRegister;
    ArrayList<String> statusList;
    List<RegisterModel> dueModels;
    List<StateModel> stateModelList;

    String selectedImagePath = "", str_status = "", str_stateid="";
    EditText uname, mobile, email, address1, address2, address3, address4, gstn, web, narration;
    LocationManager locationManager;
    String locationtext;
    private static final String TAG = MainActivity.class.getSimpleName();
    double solat, solon, delat, delon;
    String mAddress;
    private Location mLocation;
    String deviceid="";

    private static final int MY_PERMISSIONS_REQUEST_GPS = 111;
    private static int i = 0;

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

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usertype = (Spinner) findViewById(R.id.usertype);
        mBtnRegister = (Button) findViewById(R.id.reg_sign_up_button);

        if (servicesConnected()) {
            buildGoogleApiClient();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        }
        //setUpGClient();

        uname = (EditText) findViewById(R.id.reg_name_edt);
        mobile = (EditText) findViewById(R.id.reg_email_edt);
        email = (EditText) findViewById(R.id.reg_contact_edt);
        address1 = (EditText) findViewById(R.id.reg_address1_edt);
        address2 = (EditText) findViewById(R.id.reg_address2_edt);
        address3 = (EditText) findViewById(R.id.reg_address3_edt);
        address4 = (EditText) findViewById(R.id.reg_address4_edt);
        gstn = (EditText) findViewById(R.id.GSTN);
        web = (EditText) findViewById(R.id.web);
        narration = (EditText) findViewById(R.id.narration);

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
            deviceid = telephonyManager.getDeviceId();
        }
        deviceid = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        GetState();
        /*statusList = new ArrayList<>();
        statusList.add("Tamil Nadu");
        statusList.add("Andhra Pradesh");
        statusList.add("Telungana");
        statusList.add("Kerala");
        statusList.add("Karnataka");


        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, statusList);
        aa.setDropDownViewResource(R.layout.spinner_item);
        usertype.setAdapter(aa);

        usertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                str_status = statusList.get(position).toLowerCase();
                Toast.makeText(RegisterActivity.this, "Usertype : " + str_status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetails();
                //startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
    }

    private boolean servicesConnected() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return ConnectionResult.SUCCESS == resultCode;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getDetails() {

        // Reset errors.
        uname.setError(null);
        mobile.setError(null);

        address1.setError(null);
        address2.setError(null);

        // Store values at the time of the login attempt.
        String name = uname.getText().toString();
        String phone = mobile.getText().toString();

        String addr1 = address1.getText().toString();
        String addr2 = address2.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Name
        if (TextUtils.isEmpty(name)) {
            uname.setError(getString(R.string.error_field_required));
            focusView = uname;
            cancel = true;
        }


        // Phone Number
        else if (TextUtils.isEmpty(phone)) {
            mobile.setError(getString(R.string.error_field_required));
            focusView = mobile;
            cancel = true;
        }

        // Password
        else if (TextUtils.isEmpty(addr1)) {
            address1.setError(getString(R.string.error_field_required));
            focusView = address1;
            cancel = true;
        }

        // RePassword
        else if (TextUtils.isEmpty(addr2)) {
            address2.setError(getString(R.string.error_field_required));
            focusView = address2;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            UserRegisterTask(name, phone, addr1, addr2);
        }

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isPhoneValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() == 10;
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: " + location.getLatitude() + ", " + location.getLongitude());
        mLocation = location;

        solat = mLocation.getLatitude();
        solon = mLocation.getLongitude();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            System.out.println("address : "+addresses);
           /* loc_address= addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2);
            System.out.println("Location : "+loc_address);*/
        }catch(Exception e)
        {

        }

    }

    private void getFusedLocation() {
       /* try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            try {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(location.getLatitude(),
                                                location.getLongitude()), DEFAULT_ZOOM));

                                getSourceAddress(location);
//                                getDestinationAddress(location);

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        } catch (SecurityException | NullPointerException e) {
            e.printStackTrace();
        }*/
    }

    private void getSourceAddress(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
           /* solat = addresses.get(0).getLatitude();
            System.out.println("solat : "+solat);
            solon = addresses.get(0).getLongitude();*/

            mAddress = address+city+state+country+postalCode;
            // source_address.setText(mAddress);
            Log.v("Address", mAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void GetState() {
        //String URL ="http://api.suninfotechnologies.in/task/GetStates";
        String URL = CommonUtils.STATES;
        final ProgressDialog dialog1 = ProgressDialog.show(RegisterActivity.this, "Sun Info Technologies", "Please Wait...", true);

        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        stateModelList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                StateModel registerModel = new StateModel();
                                registerModel.setId(jsonObject.getString("stateid"));
                                registerModel.setName(jsonObject.getString("statename"));
                                stateModelList.add(registerModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
/*
                        statusList = new ArrayList<>();
                        statusList.add("Tamil Nadu");
                        statusList.add("Andhra Pradesh");
                        statusList.add("Telungana");
                        statusList.add("Kerala");
                        statusList.add("Karnataka");

                        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, statusList);
                        aa.setDropDownViewResource(R.layout.spinner_item);
                        usertype.setAdapter(aa);*/

                        CustomAdapter adapter = new CustomAdapter(RegisterActivity.this, R.layout.spinner_item, stateModelList, getResources());
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        usertype.setAdapter(adapter);

                        usertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                str_status = stateModelList.get(position).getName().toLowerCase();
                                str_stateid = stateModelList.get(position).getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        dialog1.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("in", "Error:" + anError.getErrorCode());
                        dialog1.dismiss();
                    }
                });
    }

    private void UserRegisterTask(String name, String phone, String addr1, String addr2) {
        String URL = CommonUtils.REGISTER + "name="+name +
                "&address1="+addr1 +
                "&address2="+addr2 +
                "&address3="+address3.getText().toString() +
                "&address4="+ address4.getText().toString() +
                "&state="+ str_stateid +
                "&gstin=" + gstn.getText().toString() +
                "&mobileno="+email.getText().toString() +
                "&email="+phone +
                "&web="+ web.getText().toString() +
                "&narration="+ narration.getText().toString() +
                "&latitude=" + solat +
                "&longitude=" + solon +
                "&deviceid="+deviceid;
        /*String URL ="http://api.suninfotechnologies.in/task/UserRegistration?name="+name +
                "&address1="+addr1 +
                "&address2="+addr2 +
                "&address3="+address3.getText().toString() +
                "&address4="+ address4.getText().toString() +
                "&state="+ str_stateid +
                "&gstin=" + gstn.getText().toString() +
                "&mobileno="+email.getText().toString() +
                "&email="+phone +
                "&web="+ web.getText().toString() +
                "&narration="+ narration.getText().toString() +
                "&latitude=" + solat +
                "&longitude=" + solon +
                "&deviceid="+deviceid;*/
        final ProgressDialog dialog1 = ProgressDialog.show(RegisterActivity.this, "Sun Info Technologies", "Please Wait...", true);


        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                SharedPref sharedPref = new SharedPref(getApplicationContext());
                                sharedPref.put("customer_id", jsonObject.getString("customerid"));
                                sharedPref.put("customer_name", jsonObject.getString("name"));
                                sharedPref.put("customer_mobileno", jsonObject.getString("mobileno"));
                            }
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }dialog1.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("in", "Error:" + anError.getErrorCode());
                        dialog1.dismiss();
                    }
                });
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

    private synchronized void setUpGClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        int permissionLocation = ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
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
        int permissionLocation = ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
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
                int permissionLocation = ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
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
                                            .checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(RegisterActivity.this, REQUEST_CHECK_SETTINGS_GPS);
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
        Log.d(TAG,"onStop");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Log.d("GARG", "***** on Stop mGoogleApiClient disconnect ***** ");
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

}


