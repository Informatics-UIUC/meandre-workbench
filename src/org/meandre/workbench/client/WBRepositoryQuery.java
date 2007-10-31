package org.seasr.client;

import com.google.gwt.user.client.rpc.RemoteService;
import java.util.Set;
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
public interface WBRepositoryQuery extends RemoteService {

        /**
         * Returns the set of active components in the repository.
         *
         * @gwt.typeArgs <org.seasr.client.beans.WBComponent>
         * @return Set Returns set of active components.
         */
        public Set getActiveComponents();

        /**
         * Get the active components in the current user's repository that match
         * the search criteria.
         *
         * @param search String The search string for this query.
         * @gwt.typeArgs <org.seasr.client.beans.WBComponent>
         * @return Set Returns set of active components matching search query.
         */
        public Set getActiveComponents(String search);

        /**
         * Saves the flow and returns the callback object.
         *
         * @return WBCallbackObject Returns callback object.
         */
        public WBCallbackObject saveFlow(WBFlow flow);

        /**
         * Returns the set of active flows in the repository.
         *
         * @gwt.typeArgs <org.seasr.client.beans.WBFlow>
         * @return Set Returns set of active flows.
         */
        public Set getActiveFlows();

        /**
         * Check to see if there is a user logged into this session.  Return
         * the user name else null.
         *
         * @return String The user name or null.
         */
        public String getUser();

}
