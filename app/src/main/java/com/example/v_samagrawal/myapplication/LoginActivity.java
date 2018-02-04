package com.example.v_samagrawal.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static int APP_REQUEST_CODE = 1;
    private Button mPhoneLoginBtn;
    private Button mEmailLoginBtn;
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPhoneLoginBtn = (Button) findViewById(R.id.phone_login_button);
        mEmailLoginBtn = (Button) findViewById(R.id.email_login_button);
        mLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        mLoginButton.setReadPermissions("email");

        //login button callback registration
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                launchAccountActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                String toastMessage = error.getMessage();
                Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        mPhoneLoginBtn.setOnClickListener(this);
        mEmailLoginBtn.setOnClickListener(this);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        com.facebook.AccessToken facebookToken = com.facebook.AccessToken.getCurrentAccessToken();

        if (accessToken != null || facebookToken != null) {
            // if previously logged in proceed to the account activity
            launchAccountActivity();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //forward result to the callback manager for login button
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // confirm that the request matches our request code
        if (requestCode == APP_REQUEST_CODE) {
            AccountKitLoginResult accountKitLoginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (accountKitLoginResult.getError() != null) {
                String toastMessage = accountKitLoginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            } else if (accountKitLoginResult.getAccessToken() != null) {
                // on successful login proceed to account screen
                launchAccountActivity();

            }


        }
    }

    private void onLogin(final LoginType loginType) {
        //create intent for the account kit activity
        final Intent intent = new Intent(this, AccountKitActivity.class);

        //configure login type and response type
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN
        );

        final AccountKitConfiguration configuration = configurationBuilder.build();

        // launch the account kit activity
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    private void onPhoneLogin(View view) {

        onLogin(LoginType.PHONE);
    }

    private void onEmailLogin(View view) {
        onLogin(LoginType.EMAIL);
    }

    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_login_button:
                onPhoneLogin(view);
                break;
            case R.id.email_login_button:
                onEmailLogin(view);
                break;
        }
    }
}
