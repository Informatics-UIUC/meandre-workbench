package org.seasr.meandre.workbench.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class Version {
    // these macros will be replaced by ANT
    private static final String VERSION = "@VERSION@";
    private static final String REVISION = "@REVISION@";
    private static final String BUILD_DATE = "@BUILD_DATE@";

    public static String getVersion() {
        if (VERSION.startsWith("@") && VERSION.endsWith("@"))
            // ANT didn't do its job, return null
            return null;
        else
            return VERSION;
    }

    public static String getRevision() {
        if (REVISION.startsWith("@") && REVISION.endsWith("@"))
            // ANT didn't do its job, return null
            return null;
        else
            return REVISION;
    }

    public static Date getBuildDate() {
        if (BUILD_DATE.startsWith("@") && BUILD_DATE.endsWith("@"))
            // ANT didn't do its job, return null
            return null;
        else
            return DateTimeFormat.getFormat("MMM dd, yyyy h:mm:ssa z").parse(BUILD_DATE);
    }

    public static String getFullVersion() {
        String version = getVersion();
        String revision = getRevision();

        if (version == null) return null;

        return version + ((revision != null) ? "." + revision : "");
    }
}
