package org.meandre.workbench.client;

//==============
// Java Imports
//==============

import java.util.Set;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.meandre.workbench.client.beans.WBFlow;

/**
 * <p>Title: Workbench Repository Query Asynchronous</p>
 *
 * <p>Description: Asynchronous interface for repository API.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public interface WBRepositoryQueryAsync {

        /**
         * Log the user into the application.
         * @param sid String session id
         * @param cb AsyncCallback Callback object returned from the server.
         */
        public void checkSessionID(String sid, AsyncCallback cb);

        /**
          * Log the user into the application.
          * @param userid String user's id
          * @param password String user's password
          * @param url String URL of server to connect with.
          * @param cb AsyncCallback Callback object returned from the server.
          */
         public void login(String userid, String password, String url, AsyncCallback cb);


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


}
