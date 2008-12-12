/**
 * University of Illinois/NCSA
 * Open Source License
 *
 * Copyright (c) 2008, Board of Trustees-University of Illinois.
 * All rights reserved.
 *
 * Developed by:
 *
 * Automated Learning Group
 * National Center for Supercomputing Applications
 * http://www.seasr.org
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimers.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimers in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the names of Automated Learning Group, The National Center for
 *    Supercomputing Applications, or University of Illinois, nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this Software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 */

package org.seasr.meandre.workbench.client.rpc;

import java.util.Map;
import java.util.Set;

import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBLocation;
import org.seasr.meandre.workbench.client.beans.session.WBSession;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Defines the AJAX service interface for working with the Meandre server
 *
 * @author Boris Capitanu
 *
 */
public interface IRepositoryAsync {

    /**
     * Returns the session object for the current session
     *
     * @return The session object for the current session
     * @throws SessionExpiredException Thrown if the user's session has expired
     */
    public void getSession(AsyncCallback<WBSession> callback);

    /**
     * Performs authentication against a Meandre server
     *
     * @param userName The user name
     * @param password The user's password
     * @param hostName The host name where the Meandre server is running at
     * @param port The port number where the Meandre server is reachable at
     * @return The session object for the authenticated user
     * @throws LoginFailedException Thrown if a problem occurred while authenticating the user's credentials
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void login(String userName, String password, String hostName, int port, AsyncCallback<WBSession> callback);

    /**
     * Logs out the current user
     *
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     */
    public void logout(AsyncCallback<Boolean> callback);

    /**
     * Clears the cached repository state from the Workbench server
     *
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     */
    public void clearCache(AsyncCallback<Boolean> callback);

    ///////////////
    // Locations //
    ///////////////

    /**
     * Retrieves the set of locations registered on the server
     *
     * @return The set of locations
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveLocations(AsyncCallback<Set<WBLocation>> callback);

    /**
     * Adds a new location
     *
     * @param locationURL The location url
     * @param description The location description
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void addLocation(String locationURL, String description, AsyncCallback<Boolean> callback);

    /**
     * Removes a location
     *
     * @param url The url for the location to be removed
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void removeLocation(String url, AsyncCallback<Boolean> callback);

    ////////////////
    // Repository //
    ////////////////

    /**
     * Regenerates the repository
     *
     * @return true if the operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void regenerate(AsyncCallback<Boolean> callback);

    /**
     * Retrieves all component urls known to the Meandre server
     *
     * @return The set of component urls
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveComponentUrls(AsyncCallback<Set<String>> callback);

    /**
     * Retrieves the component description object for the specified component
     *
     * @param componentURL The component id
     * @return The object describing the specified component
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveComponentDescriptor(String componentURL, AsyncCallback<WBExecutableComponentDescription> callback);

    /**
     * Retrieves all component descriptors
     *
     * @return The set of all component descriptors
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveComponentDescriptors(AsyncCallback<Set<WBExecutableComponentDescription>> callback);

    /**
     * Retrieves all flow urls known to the Meandre server
     *
     * @return The list of flow urls
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveFlowUrls(AsyncCallback<Set<String>> callback);

    /**
     * Retrieves the flow descriptor for the specified flow
     *
     * @param flowURL The flow id
     * @return The descriptor object for the specified flow
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveFlowDescriptor(String flowURL, AsyncCallback<WBFlowDescription> callback);

    /**
     * Retrieves all flow descriptors
     *
     * @return The set of all flow descriptors
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveFlowDescriptors(AsyncCallback<Set<WBFlowDescription>> callback);

    /**
     * Retrieves all tags known to the Meandre server
     *
     * @return The set of all tags
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveAllTags(AsyncCallback<Set<String>> callback);

    /**
     * Retrieves all component tags known to the Meandre server
     *
     * @return The set of all component tags
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveComponentTags(AsyncCallback<Set<String>> callback);

    /**
     * Retrieves all flow tags known to the Meandre server
     *
     * @return The set of all flow tags
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveFlowTags(AsyncCallback<Set<String>> callback);

    /**
     * Retrieves components by tag
     *
     * @param tag The tag to search for
     * @return The set of components containing the specified tag
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveComponentsByTag(String tag, AsyncCallback<Set<String>> callback);

    /**
     * Retrieves flows by tag
     *
     * @param tag The tag to search for
     * @return The set of flows containing the specified tag
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveFlowsByTag(String tag, AsyncCallback<Set<String>> callback);

    /**
     * Retrieves component ids (urls) that match a specific query
     *
     * @param query The query
     * @return The set of all component ids matching the specified query
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveComponentUrlsByQuery(String query, AsyncCallback<Set<String>> callback);

    /**
     * Retrieves flow ids (urls) that match a specific query
     * @param query The query
     * @return The set of all flow ids matching the specified query
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveFlowUrlsByQuery(String query, AsyncCallback<Set<String>> callback);

    /**
     * Uploads (saves) a flow to the Meandre server
     *
     * @param flow The flow descriptor
     * @param overwrite true to overwrite, false to ignore the upload request if the flow already exists on the server
     * @return The uploaded flow descriptor
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     * @throws CorruptedFlowException Thrown if the repository state on the server is corrupted
     */
    // TODO: change return type to boolean once refactoring is done
    public void uploadFlow(WBFlowDescription flow, boolean overwrite, AsyncCallback<Boolean> callback);

    /**
     * Uploads flows in batch
     *
     * @param flows The set of flow descriptors to upload
     * @param overwrite true to overwrite, false to ignore the upload request if a flow already exists on the server
     * @return true if the operation succeeded, false otherwise
     * @throws @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void uploadFlowBatch(Set<WBFlowDescription> flows, boolean overwrite, AsyncCallback<Boolean> callback);

    /**
     * Removes a resource from the server
     *
     * @param resourceURL The resource url
     * @return true if the operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void removeResource(String resourceURL, AsyncCallback<Boolean> callback);

    /////////////
    // Publish //
    /////////////

    /**
     * Publishes a resource
     *
     * @param resourceURL The resource url
     * @return true if the operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void publish(String resourceURL, AsyncCallback<Boolean> callback);

    /**
     * Unpublishes a resource
     *
     * @param resourceURL The resource url
     * @return true if the operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void unpublish(String resourceURL, AsyncCallback<Boolean> callback);

    ///////////////
    // Execution //
    ///////////////

    /**
     * Starts execution of a flow
     *
     * @param flowURL The flow id
     * @param verbose true to capture output, false otherwise
     * @return The execution output
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void runFlow(String flowURL, boolean verbose, AsyncCallback<String> callback);

    /**
     * Retrieves the running flows
     *
     * @return The map of running flows: flow id -> url where it is running
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveRunningFlows(AsyncCallback<Map<String, String>> callback);

    /**
     * Retrieves the WebUI info object for a specific token
     *
     * @param token The runtime token
     * @return The WebUI descriptor
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveWebUIInfo(String token, AsyncCallback<WBWebUIInfo> callback);

    ////////////
    // Public //
    ////////////

    /**
     * Retrieves all public components
     *
     * @return The set of all public components
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrievePublicComponents(AsyncCallback<Set<WBExecutableComponentDescription>> callback);

    /**
     * Retrieves all public flows
     *
     * @return The set of all public flows
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrievePublicFlows(AsyncCallback<Set<WBFlowDescription>> callback);

    ////////////////////////////
    // Admin of Running Flows //
    ////////////////////////////

    /**
     * Attempts to abort an executing flow
     *
     * @param runningFlowPort The port where the flow is running
     * @return true if the abort request was successfuly dispatched, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void abortFlow(int runningFlowPort, AsyncCallback<Boolean> callback);

    /**
     * Retrieves statistics about a running flow
     *
     * @param runningFlowPort The port where the flow is running
     * @return The runtime statistics for the specified flow
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public void retrieveRunningFlowStatistics(int runningFlowPort, AsyncCallback<String> callback);


}
