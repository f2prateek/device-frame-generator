
package com.f2prateek.dfg.authenticator;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.accounts.AccountManager.KEY_INTENT;
import static com.f2prateek.dfg.authenticator.BootstrapAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.f2prateek.dfg.core.Constants;

class BootstrapAccountAuthenticator extends AbstractAccountAuthenticator {

    private static final String DESCRIPTION_CLIENT = "Bootstrap for Android";

    private final Context context;

    public BootstrapAccountAuthenticator(Context context) {
        super(context);

        this.context = context;
    }

    /*
     * The user has requested to add a new account to the system. We return an intent that will launch our login screen
     * if the user has not logged in yet, otherwise our activity will just pass the user's credentials on to the account
     * manager.
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
            String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(context, BootstrapAuthenticatorActivity.class);
        intent.putExtra(PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    /**
     * This method gets called when the
     * {@link com.f2prateek.dfg.authenticator.ApiKeyProvider#getAuthKey()} methods gets invoked.
     * This happens on a different process, so debugging it can be a beast.
     * @param response
     * @param account
     * @param authTokenType
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
            Bundle options) throws NetworkErrorException {

        Log.d("AccountAuthenticator", "Attempting to get authToken");

        String authToken = AccountManager.get(context).peekAuthToken(account, authTokenType);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACCOUNT_NAME, account.name);
        bundle.putString(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        bundle.putString(KEY_AUTHTOKEN, authToken);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE) ? authTokenType : null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features)
            throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType,
            Bundle options) {
        return null;
    }
}
