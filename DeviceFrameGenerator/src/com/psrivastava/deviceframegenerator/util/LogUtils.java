
package com.psrivastava.deviceframegenerator.util;

public class LogUtils {

    private static final String LOG_PREFIX = "dfg_";

    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();

    private static final int MAX_LOG_TAG_LENGTH = 23;

    private static final String LOGTAG = makeLogTag(LogUtils.class);

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

}
