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
     * Regenerate the repository from all of its locations.  NOTE: this
     * command will delete all unpublished components and flows.
     * @param sid String session id
     * @return WBCallbackObject Bean that contains return information.
     */
    public WBCallbackObject regenerateRepository(String sid);

    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param flowid String flow uri.
     * @return WBCallbackObject Bean that contains return information.
     */
    public WBCallbackObject deleteFlowFromRepository(String sid, String flowid);

    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @param flowid String flow uri.
     * @return WBExecBean Bean that contains execution information.
     */
    public WBExecBean startInteractiveExecution(String sid,
                                                String execid,
                                                String flowid);

    /**
     * Updates status of execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @return WBExecBean
     */
    public WBExecBean updateInteractiveExecution(String sid, String execid);

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
     * @param sid String session id
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
     * @return Set Returns set of active components.
     */
    public Set getActiveComponents(String sid);

    /**
     * Get the active components in the current user's repository that match
     * the search criteria.
     *
     * @param search String The search string for this query.
     * @param sid String session id
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
     * @return Set Returns set of active components matching search query.
     */
    public Set getActiveComponents(String search, String sid);

    /**
     * Saves the flow and returns the callback object.
     *
     * @param sid String session id
     * @return WBCallbackObject Returns callback object.
     */
    public WBCallbackObject saveFlow(WBFlow flow, String sid);

    /**
     * Returns the set of active flows in the repository.
     *
     * @param sid String session id
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBFlow>
     * @return Set Returns set of active flows.
     */
    public Set getActiveFlows(String sid);

}
