
package com.f2prateek.dfg.authenticator;

import static android.R.layout.simple_dropdown_item_1line;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.f2prateek.dfg.core.Constants.Http.HEADER_PARSE_APP_ID;
import static com.f2prateek.dfg.core.Constants.Http.HEADER_PARSE_REST_API_KEY;
import static com.f2prateek.dfg.core.Constants.Http.PARSE_APP_ID;
import static com.f2prateek.dfg.core.Constants.Http.PARSE_REST_API_KEY;
import static com.f2prateek.dfg.core.Constants.Http.URL_AUTH;
import static com.github.kevinsawicki.http.HttpRequest.get;
import static com.github.kevinsawicki.http.HttpRequest.post;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.f2prateek.dfg.core.Constants;
import com.f2prateek.dfg.core.User;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.wishlist.Toaster;
import com.f2prateek.dfg.R.id;
import com.f2prateek.dfg.R.layout;
import com.f2prateek.dfg.R.string;
import com.f2prateek.dfg.ui.TextWatcherAdapter;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockAccountAuthenticatorActivity;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import roboguice.util.Strings;

import static com.f2prateek.dfg.core.Constants.Http.USERNAME;
import static com.f2prateek.dfg.core.Constants.Http.PASSWORD;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class BootstrapAuthenticatorActivity extends
        RoboSherlockAccountAuthenticatorActivity {

    /**
     * PARAM_CONFIRMCREDENTIALS
     */
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";

    /**
     * PARAM_PASSWORD
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * PARAM_USERNAME
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";


    private AccountManager accountManager;

    @InjectView(id.et_email)
    private AutoCompleteTextView emailText;

    @InjectView(id.et_password)
    private EditText passwordText;

    @InjectView(id.b_signin)
    private Button signinButton;

    private TextWatcher watcher = validationTextWatcher();

    private RoboAsyncTask<Boolean> authenticationTask;
    private String authToken;
    private String authTokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean confirmCredentials = false;

    private String email;

    private String password;


    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String token;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        accountManager = AccountManager.get(this);
        final Intent intent = getIntent();
        email = intent.getStringExtra(PARAM_USERNAME);
        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        requestNewAccount = email == null;
        confirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,
                false);

        setContentView(layout.login_activity);

        emailText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

        passwordText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signinButton.isEnabled()) {
                    handleLogin(signinButton);
                    return true;
                }
                return false;
            }
        });

        passwordText.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signinButton.isEnabled()) {
                    handleLogin(signinButton);
                    return true;
                }
                return false;
            }
        });

        emailText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);

        TextView signupText = (TextView) findViewById(id.tv_signup);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());
        signupText.setText(Html.fromHtml(getString(string.signup_link)));
    }

    private List<String> userEmailAccounts() {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (Account account : accounts)
            emailAddresses.add(account.name);
        return emailAddresses;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        boolean populated = populated(emailText) && populated(passwordText);
        signinButton.setEnabled(populated);
    }

    private boolean populated(EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.message_signing_in));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (authenticationTask != null)
                    authenticationTask.cancel(true);
            }
        });
        return dialog;
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     *
     * @param view
     */
    public void handleLogin(View view) {
        if (authenticationTask != null)
            return;

        if (requestNewAccount)
            email = emailText.getText().toString();
        password = passwordText.getText().toString();
        showProgress();

        authenticationTask = new RoboAsyncTask<Boolean>(this) {
            public Boolean call() throws Exception {

                final String query = String.format("%s=%s&%s=%s", PARAM_USERNAME, email, PARAM_PASSWORD, password);

                HttpRequest request = get(URL_AUTH + "?" + query)
                        .header(HEADER_PARSE_APP_ID, PARSE_APP_ID)
                        .header(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);


                Log.d("Auth", "response=" + request.code());

                if(request.ok()) {
                    final User model = new Gson().fromJson(Strings.toString(request.buffer()), User.class);
                    token = model.getSessionToken();
                }

                return request.ok();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Throwable cause = e.getCause() != null ? e.getCause() : e;

                String message;
                // A 404 is returned as an Exception with this message
                if ("Received authentication challenge is null".equals(cause
                        .getMessage()))
                    message = getResources().getString(
                            string.message_bad_credentials);
                else
                    message = cause.getMessage();

                Toaster.showLong(BootstrapAuthenticatorActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result
     */
    protected void finishConfirmCredentials(boolean result) {
        final Account account = new Account(email, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.setPassword(account, password);

        final Intent intent = new Intent();
        intent.putExtra(KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        final Account account = new Account(email, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (requestNewAccount)
            accountManager.addAccountExplicitly(account, password, null);
        else
            accountManager.setPassword(account, password);
        final Intent intent = new Intent();
        authToken = token;
        intent.putExtra(KEY_ACCOUNT_NAME, email);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        if (authTokenType != null
                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE))
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(boolean result) {
        if (result)
            if (!confirmCredentials)
                finishLogin();
            else
                finishConfirmCredentials(true);
        else {
            Ln.d("onAuthenticationResult: failed to authenticate");
            if (requestNewAccount)
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        string.message_auth_failed_new_account);
            else
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        string.message_auth_failed);
        }
    }
}
