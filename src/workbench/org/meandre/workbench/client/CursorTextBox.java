package org.meandre.workbench.client;

/**
 * <p>Title: Cursor Text Box Wrapper</p>
 *
 * <p>Description: A wrapper class for text boxes that fixes a focus bug in
 * firefox browser.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith (adapted from GWT forum)
 * @version 1.0
 */

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusListenerAdapter;

/**
 * For some reason a bug has existed since the first versions of
 * firefox all the way up to late versions of firefox 2:
 * TextBoxes have no cursor when absolutely positioned.
 *
 * This class contains utility methods to optionally wrap them to work
 * around the bug. These methods check if firefox is the browser
 * and in that case work around the issue.
 */
public class CursorTextBox {
        public static void addTextBox(Panel x, TextBoxBase textBox) {
                x.add(wrapTextBox(textBox));
        }

        public static Widget wrapTextBox(TextBoxBase textBox) {
                if ( getUserAgent().equals("gecko") ) {
                        FocusPanel wrapPanel = new FocusPanel();
                        wrapPanel.addFocusListener(new FocusListenerAdapter(){
                            public void onFocus(Widget w){
                                ((TextBoxBase)((FocusPanel)w).getWidget()).setFocus(true);
                            }
                        });
                        DOM.setStyleAttribute(wrapPanel.getElement(), "overflow", "auto");
                        wrapPanel.setWidget(textBox);
                        return wrapPanel;
                } else return textBox;
        }

        /**
         * returns 'opera', 'safari', 'ie6', 'ie7', 'gecko', or 'unknown'.
         */
        public static native String getUserAgent() /*-{
                try {
                        if ( window.opera ) return 'opera';
                        var ua = navigator.userAgent.toLowerCase();
                        if ( ua.indexOf('webkit' ) != -1 ) return 'safari';
                        if ( ua.indexOf('msie 6.0') != -1 ) return 'ie6';
                        if ( ua.indexOf('msie 7.0') != -1 ) return 'ie7';
                        if ( ua.indexOf('gecko') != -1 ) return 'gecko';
                        return 'unknown';
                } catch ( e ) { return 'unknown' }
        }-*/;
}
