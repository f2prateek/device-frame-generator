package com.f2prateek.dfg.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.util.Log;

import com.f2prateek.dfg.core.Constants;
import com.google.inject.Inject;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import roboguice.inject.ContextSingleton;
import roboguice.util.RoboAsyncTask;

@ContextSingleton
public class LogoutService {

    @Inject protected Context context;
    @Inject protected AccountManager accountManager;



    public void logout(final Runnable onSuccess) {

        new LogoutTask(context, onSuccess).execute();
    }

    private static class LogoutTask extends RoboAsyncTask<Boolean> {

        private Runnable onSuccess;

        protected LogoutTask(Context context, Runnable onSuccess) {
            super(context);
            this.onSuccess = onSuccess;
        }

        @Override
        public Boolean call() throws Exception {

            final Account[] accounts = AccountManager.get(context).getAccountsByType(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
            if(accounts.length > 0) {
                AccountManagerFuture<Boolean> removeAccountFuture = AccountManager.get(context).removeAccount
                        (accounts[0], null, null);
                if(removeAccountFuture.getResult() == true) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        @Override
        protected void onSuccess(Boolean accountWasRemoved) throws Exception {
            super.onSuccess(accountWasRemoved);

            Log.d("LOGOUT_SERVICE", "Logout succeeded:" + accountWasRemoved);
            onSuccess.run();

        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("LOGOUT_SERVICE", "Logout failed.", e.getCause());
        }
    }
}
