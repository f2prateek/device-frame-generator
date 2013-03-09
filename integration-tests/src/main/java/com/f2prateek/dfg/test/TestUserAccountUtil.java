

package com.f2prateek.dfg.test;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;
import static com.f2prateek.dfg.core.Constants.Auth.*;

import com.f2prateek.dfg.tests.R;

import roboguice.util.Ln;

/**
 * Utilities for verifying an account
 */
public class TestUserAccountUtil {

    /**
     * Checks the device has a valid Bootstrap (on parse.com via the example API),
     * account, if not, adds one using the test credentials found in system
     * property 'bootstrap.test.api.key'.
     *
     * The credentials can be passed on the command line like this: mvn
     * -bootstrap.test.api.key=0123456789abcdef0123456789abcdef install
     *
     * @param instrumentation
     *            taken from the test context
     * @return true if valid account credentials are available
     */
    public static boolean ensureValidAccountAvailable(Instrumentation instrumentation) {
        Context c = instrumentation.getContext();
        AccountManager accountManager = AccountManager.get(instrumentation.getTargetContext());

        for (Account account : accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE)) {
            if (accountManager.peekAuthToken(account, AUTHTOKEN_TYPE) != null) {
                Ln.i( "Using existing account : " + account.name);
                return true; // we have a valid account that has successfully authenticated
            }
        }

        String testApiKey = c.getString(R.string.test_account_api_key);
        String truncatedApiKey = testApiKey.substring(0, 4) + "…";

        if (!testApiKey.matches("\\p{XDigit}{32}")) {
            Ln.w("No valid test account credentials in bootstrap.test.api.key : " + truncatedApiKey);
            return false;
        }

        Ln.i("Adding test account using supplied api key credential : " + truncatedApiKey);
        Account account = new Account("test@example.com", BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, null, null); // this test account will not have a valid password
        accountManager.setAuthToken(account, AUTHTOKEN_TYPE, testApiKey);
        return true;
    }
}
