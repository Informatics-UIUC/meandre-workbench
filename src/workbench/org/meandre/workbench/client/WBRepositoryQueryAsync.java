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
import org.meandre.workbench.client.beans.WBExecBean;

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
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param location String location url.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void removeLocation(String sid, String location, AsyncCallback cb);

    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param location String location url.
     * @param desc String location description.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void addLocation(String sid, String location, String desc,
                            AsyncCallback cb);

    /**
     * Returns the set of active locations in the repository.
     *
     * @param sid String session id
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void getLocations(String sid, AsyncCallback cb);

    /**
     * Regenerate the repository from all of its locations.  NOTE: this
     * command will delete all unpublished components and flows.
     * @param sid String session id
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void regenerateRepository(String sid, AsyncCallback cb);


    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param flowid String flow uri.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void deleteFlowFromRepository(String sid, String flowid,
                                         AsyncCallback cb);

    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @param flowid String flow uri.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void startInteractiveExecution(String sid,
                                          String execid,
                                          String flowid,
                                          AsyncCallback cb);

    /**
     * Updates status of execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void updateInteractiveExecution(String sid,
                                           String execid,
                                           AsyncCallback cb);

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
    public void login(String userid, String password, String url,
                      AsyncCallback cb);


    /**
     * Returns the set of active components in the repository via
     * callback parameters.
     *
     * @param sid String session id
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void getActiveComponents(String sid, AsyncCallback cb);

    /**
     * Get the active components in the current user's repository that match
     * the search criteria.
     *
     * @param search String The search string for this query.
     * @param sid String session id
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void getActiveComponents(String search, String sid, AsyncCallback cb);

    /**
     * Saves the flow and returns the callback object.
     *
     * @param flow WBFlow flow to save.
     * @param sid String session id
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void saveFlow(WBFlow flow, String sid, AsyncCallback cb);

    /**
     * Returns the set of active flows in the repository via
     * callback parameters.
     *
     * @param sid String session id
     * @param cb AsyncCallback Callback object returned from the server.
     */
    public void getActiveFlows(String sid, AsyncCallback cb);


}
