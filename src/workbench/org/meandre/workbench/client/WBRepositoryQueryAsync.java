package org.meandre.workbench.client;

import java.util.Set;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.meandre.workbench.client.beans.WBCallbackObject;
import org.meandre.workbench.client.beans.WBFlow;

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
public interface WBRepositoryQueryAsync {

        /**
         * Returns the set of active components in the repository via
         * callback parameters.
         *
         * @param cb AsyncCallback Callback object returned from the server.
         */
        public void getActiveComponents(AsyncCallback cb);

        /**
         * Get the active components in the current user's repository that match
         * the search criteria.
         *
         * @param search String The search string for this query.
         * @param cb AsyncCallback Callback object returned from the server.
         */
        public void getActiveComponents(String search, AsyncCallback cb);

        /**
         * Saves the flow and returns the callback object.
         *
         * @param cb AsyncCallback Callback object returned from the server.
         */
        public void saveFlow(WBFlow flow, AsyncCallback cb);

        /**
         * Returns the set of active flows in the repository via
         * callback parameters.
         *
         * @param cb AsyncCallback Callback object returned from the server.
         */
        public void getActiveFlows(AsyncCallback cb);

        /**
         * Check to see if there is a user logged into this session.  Return
         * the user name else null.
         *
         * @param cb AsyncCallback Callback object returned from the server.
         */
        public void getUser(AsyncCallback cb);

}
