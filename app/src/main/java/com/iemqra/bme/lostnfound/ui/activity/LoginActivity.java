package com.iemqra.bme.lostnfound.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.event.RegisterEvent;
import com.iemqra.bme.lostnfound.helper.AlertDialogManager;
import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
import com.iemqra.bme.lostnfound.helper.SessionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    final AlertDialogManager alert = new AlertDialogManager();
    LoginButton btnFbLogin;
    @BindView(R.id.activity_login_email)
    EditText inputEmail;
    @BindView(R.id.activity_login_password)
    EditText inputPassword;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    AsyncTask<Void, Void, Void> asyncTask = null;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnFbLogin = (LoginButton) findViewById(R.id.activity_login_btnFbLogin);
        btnFbLogin.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                checkFbLogin(currentProfile.getFirstName(), currentProfile.getLastName(), currentProfile.getId());
            }
        };

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Callback registration
        btnFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                try {
                                    Toast.makeText(getApplicationContext(), object.getString("email"), Toast.LENGTH_LONG).show();
                                    Log.d(TAG + "user email ", object.getString("email"));
                                    Log.d(TAG + "profile ", object.getString("public_profile"));
                                    //Log.d(TAG + "profile ", object.getString("public_profile"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.v("LoginActivity", response.toString());
                            }
                        });
            }

            @Override
            public void onCancel() {
                // App code
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }

    @OnClick(R.id.activity_login_btnLogin)
    public void handleLoginButtonClick() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // TODO, field checks...
        if (!email.isEmpty() && !password.isEmpty()) {
            checkLogin(email, password);
        } else {
            alert.showAlertDialog(LoginActivity.this,
                    "Empty fields",
                    "To log in, please fill fields with your user credentials!", false);
        }
    }

    @OnClick(R.id.activity_login_register)
    public void handleToRegistrationButtonClick() {
        Intent i = new Intent(getApplicationContext(),
                RegisterActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void checkLogin(String email, String password) {
        pDialog.setMessage("Please wait ...");
        showDialog(true);
        ApiClient.loginUser(email, password);
    }

    private void checkFbLogin(String email, String password, String fbId) {
        pDialog.setMessage("Please wait ...");
        showDialog(true);
        ApiClient.facebookLogin(email, password, fbId);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final RegisterEvent event) {
        showDialog(false);
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
            alert.showAlertDialog(LoginActivity.this,
                    "No internet connection",
                    "You need wifi or mobile data enabled to use this application!", false);
        } else {
            if (event.getUser() != null) {
                session.setLogin(true);
                db.addUser(event.getUser().getFirstName(), event.getUser().getLastName(), event.getUser().getEmail(), event.getUser().getUniqueId(), event.getUser().getCreatedAt());
                Toast.makeText(getApplicationContext(), "Successful login!", Toast.LENGTH_SHORT).show();

                /**
                 * Get token for GCM
                 */
                asyncTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        String token = null;
                        InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
                        try {
                            token = instanceID.getToken(LoginActivity.this.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ApiClient.addGcmToken(event.getUser().getEmail(), token);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        asyncTask = null;
                    }
                };
                asyncTask.execute(null, null, null);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDialog(boolean req) {
        if (req == true) {
            if (!pDialog.isShowing())
                pDialog.show();
        } else {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}