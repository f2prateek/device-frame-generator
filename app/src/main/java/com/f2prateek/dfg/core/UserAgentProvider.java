package com.f2prateek.dfg.core;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import roboguice.util.Ln;
import roboguice.util.Strings;

public class UserAgentProvider implements Provider<String> {
    @Inject protected Application app;
    @Inject protected PackageInfo info;
    @Inject protected TelephonyManager telephonyManager;

    protected String userAgent;

    private static final String APP_NAME = "Device Frame Generator";

    @Override
    public String get() {
        if( userAgent==null ) {
            synchronized (UserAgentProvider.class) {
                if( userAgent==null ) {
                    userAgent = String.format("%s/%s (Android %s; %s %s / %s %s; %s)",
                            APP_NAME,
                            info.versionName,
                            Build.VERSION.RELEASE,
                            Strings.capitalize(Build.MANUFACTURER),
                            Strings.capitalize(Build.DEVICE),
                            Strings.capitalize(Build.BRAND),
                            Strings.capitalize(Build.MODEL),
                            Strings.capitalize( telephonyManager == null ? "not-found" : telephonyManager.getSimOperatorName())
                    );

                    final ArrayList<String> params = new ArrayList<String>();
                    params.add( "preload=" + ((app.getApplicationInfo().flags& ApplicationInfo.FLAG_SYSTEM)==1) ); // Determine if this app was a preloaded app
                    params.add( "locale=" + Locale.getDefault() );


                    // http://stackoverflow.com/questions/2641111/where-is-android-os-systemproperties
                    try{
                        final Class SystemProperties = app.getClassLoader().loadClass("android.os.SystemProperties");
                        final Method get = SystemProperties.getMethod("get", String.class);
                        params.add( "clientidbase=" + get.invoke(SystemProperties, "ro.com.google.clientidbase"));
                    }catch( Exception ignored ){
                        Ln.d(ignored);
                    }


                    if( params.size()>0 )
                        userAgent += "["+ Strings.join(";", params) +"]";

                }
            }
        }

        return userAgent;
    }
}
