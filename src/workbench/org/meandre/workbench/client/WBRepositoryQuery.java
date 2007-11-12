package org.meandre.workbench.client;

import com.google.gwt.user.client.rpc.RemoteService;
import java.util.Set;
import org.meandre.workbench.client.beans.*;


/**
 * <p>Title: Workbench Repository Query Interface</p>
 *
 * <p>Description: An interface to GWT RPC Servlet</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
public interface WBRepositoryQuery extends RemoteService {

    /**
     * Log the user into the application.
     * @param sid String session id
     * @return LoginBean Bean containing login information.
     */
    public WBLoginBean checkSessionID(String sid);


    /**
     * Log the user into the application.
     * @param userid String user's id
     * @param password String user's password
     * @param url String URL of server to connect with.
     * @return LoginBean Bean containing login information.
     */
    public WBLoginBean login(String userid, String password, String url);

    /**
     * Returns the set of active components in the repository.
     *
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
     * @return Set Returns set of active components.
     */
    public Set getActiveComponents();

    /**
     * Get the active components in the current user's repository that match
     * the search criteria.
     *
     * @param search String The search string for this query.
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
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
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBFlow>
     * @return Set Returns set of active flows.
     */
    public Set getActiveFlows();

}
