

package com.f2prateek.dfg.authenticator;

import static android.accounts.AccountManager.KEY_AUTHTOKEN;

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountsException;
import android.app.Activity;
import android.os.Bundle;

import com.f2prateek.dfg.core.Constants;
import com.google.inject.Inject;

import java.io.IOException;

/**
 * Bridge class that obtains a API key for the currently configured account
 */
public class ApiKeyProvider {

    @Inject private Activity activity;
    @Inject private AccountManager accountManager;

    /**
     * This call blocks, so shouldn't be called on the UI thread
     *
     * @return API key to be used for authorization with a {@link com.f2prateek.dfg.core.BootstrapService} instance
     * @throws AccountsException
     * @throws IOException
     */
    public String getAuthKey() throws AccountsException, IOException {
        AccountManagerFuture<Bundle> accountManagerFuture = accountManager.getAuthTokenByFeatures(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE,
                Constants.Auth.AUTHTOKEN_TYPE, new String[0], activity, null, null, null, null);

        return accountManagerFuture.getResult().getString(KEY_AUTHTOKEN);
    }
}
