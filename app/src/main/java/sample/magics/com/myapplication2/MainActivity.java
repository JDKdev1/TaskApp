package sample.magics.com.myapplication2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText auth_email_edt;
    Button submit,noacc;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = new SharedPref(getApplicationContext());
        String name = sharedPref.getString("customerid");

        auth_email_edt = (EditText)findViewById(R.id.auth_email_edt);
        submit = (Button)findViewById(R.id.email_sign_in_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!auth_email_edt.getText().toString().isEmpty()){
                    boolean status = CommonUtils.checkValidation(auth_email_edt.getText().toString());
                    LoginAPI(status);
                }
                //startActivity(new Intent(MainActivity.this,Userbook.class));
            }
        });
        noacc = (Button)findViewById(R.id.sign_in_button);
        noacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
    }

    public void LoginAPI(boolean status) {
        String URL = CommonUtils.LOGIN;
        if (status){
            URL = URL + "email=" + auth_email_edt.getText().toString() + "&phone=";
        } else {
            URL = URL + "email=&phone=" + auth_email_edt.getText().toString();
        }

        final ProgressDialog dialog1 = ProgressDialog.show(MainActivity.this, "Sun Info Technologies", "Please Wait...", true);

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
                                    SharedPref sharedPref = new SharedPref(getApplicationContext());
                                    sharedPref.put("user_type", jsonObject.getString("UserType"));
                                    sharedPref.put("partyid", jsonObject.getString("Partyid"));
                                    sharedPref.put("partyName", jsonObject.getString("PartyName"));
                                    sharedPref.put("email_ID", jsonObject.getString("EmailID"));
                                    sharedPref.put("phone_No", jsonObject.getString("Phoneno"));
                                }
                                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,Userbook.class));
                            }else {
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
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
}
