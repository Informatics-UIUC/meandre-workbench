package org.seasr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import org.seasr.client.beans.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MyUncaughtExceptionHandler implements
        UncaughtExceptionHandler {

    public void onUncaughtException(Throwable ex) {
        Window.alert("Uncaught Exception\n" +
                     (ex == null ? "null" : ex.toString()));
    }
}
