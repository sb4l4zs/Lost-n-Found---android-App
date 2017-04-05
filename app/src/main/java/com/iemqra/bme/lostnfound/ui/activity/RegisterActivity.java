package com.iemqra.bme.lostnfound.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.api.ApiInterface;
import com.iemqra.bme.lostnfound.event.RegisterEvent;
import com.iemqra.bme.lostnfound.helper.AlertDialogManager;
import com.iemqra.bme.lostnfound.helper.EncryptHelper;
import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
import com.iemqra.bme.lostnfound.helper.SessionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    final AlertDialogManager alert = new AlertDialogManager();
    @BindView(R.id.activity_register_firstname)
    EditText inputFirstName;
    @BindView(R.id.activity_register_lastname)
    EditText inputLastName;
    @BindView(R.id.activity_register_email)
    EditText inputEmail;
    @BindView(R.id.activity_register_password)
    EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private ApiInterface api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.activity_register_btnRegister)
    public void handleOnRegisterButtonClick() {
        String first_name = inputFirstName.getText().toString().trim();
        String last_name = inputLastName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // TODO, field checks...
        if (!first_name.isEmpty() && !last_name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            registerUser(first_name, last_name, email, password);
        } else {
            alert.showAlertDialog(RegisterActivity.this,
                    "Empty fields",
                    "To log in, please fill fields with your user credentials!", false);
        }
    }

    @OnClick(R.id.activity_register_btnLinkToLoginScreen)
    public void handleBackToLoginButtonClick() {
        Intent i = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(i);
        finish();
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

    private void registerUser(final String first_name, final String last_name, final String email, final String password) {
        pDialog.setMessage("Please wait ...");
        showDialog(true);
        String salt = EncryptHelper.generateSalt();
        String encryptedPassword = null;
        try {
            encryptedPassword = EncryptHelper.encryptPassword(password, salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiClient.registerUser(first_name, last_name, email, salt, encryptedPassword);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final RegisterEvent event) {
        showDialog(false);
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
            alert.showAlertDialog(RegisterActivity.this,
                    "No internet connection",
                    "You need wifi or mobile data enabled to use this application!", false);
        } else {
            if (event.getUser() != null) {
                db.addUser(event.getUser().getFirstName(), event.getUser().getLastName(), event.getUser().getEmail(), event.getUser().getUniqueId(), event.getUser().getCreatedAt());
                Toast.makeText(getApplicationContext(), "Successfully registration. You can login now!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(
                        RegisterActivity.this,
                        LoginActivity.class);
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