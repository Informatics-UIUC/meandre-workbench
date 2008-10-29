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
import org.seasr.meandre.workbench.client.exceptions.CorruptedFlowException;
import org.seasr.meandre.workbench.client.exceptions.LoginFailedException;
import org.seasr.meandre.workbench.client.exceptions.MeandreCommunicationException;
import org.seasr.meandre.workbench.client.exceptions.SessionExpiredException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Defines the AJAX service interface for working with the Meandre server
 *
 * @author Boris Capitanu
 *
 */
public interface IRepository extends RemoteService {

    public static final String SERVICE_URI = "Repository";

    public static class Util {
        private static IRepositoryAsync _instance;

        /**
         * Returns a singleton instance to an object implementing the service interface
         *
         * @return A singleton IRepositoryAsync object implementor
         */
        public static IRepositoryAsync getInstance() {
            if (_instance == null) {
                _instance = (IRepositoryAsync) GWT.create(IRepository.class);
                ServiceDefTarget target = (ServiceDefTarget) _instance;
                target.setServiceEntryPoint(GWT.getModuleBaseURL() + SERVICE_URI);
            }

            return _instance;
        }
    }

    ///////////////
    // Workbench //
    ///////////////

    /**
     * Returns the session object for the current session
     *
     * @return The session object for the current session
     * @throws SessionExpiredException Thrown if the user's session has expired
     */
    public WBSession getSession()
        throws SessionExpiredException;

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
    public WBSession login(String userName, String password, String hostName, int port)
        throws LoginFailedException, MeandreCommunicationException;

    /**
     * Logs out the current user
     *
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     */
    public Boolean logout()
        throws SessionExpiredException;

    /**
     * Clears the cached repository state from the Workbench server
     *
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     */
    public Boolean clearCache()
        throws SessionExpiredException;

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
    public Set<WBLocation> retrieveLocations()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Adds a new location
     *
     * @param locationURL The location url
     * @param description The location description
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public boolean addLocation(String locationURL, String description)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Removes a location
     *
     * @param url The url for the location to be removed
     * @return true if operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public boolean removeLocation(String url)
        throws SessionExpiredException, MeandreCommunicationException;

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
    public boolean regenerate()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all component urls known to the Meandre server
     *
     * @return The set of component urls
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveComponentUrls()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves the component description object for the specified component
     *
     * @param componentURL The component id
     * @return The object describing the specified component
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public WBExecutableComponentDescription retrieveComponentDescriptor(String componentURL)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all component descriptors
     *
     * @return The set of all component descriptors
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<WBExecutableComponentDescription> retrieveComponentDescriptors()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all flow urls known to the Meandre server
     *
     * @return The list of flow urls
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveFlowUrls()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves the flow descriptor for the specified flow
     *
     * @param flowURL The flow id
     * @return The descriptor object for the specified flow
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public WBFlowDescription retrieveFlowDescriptor(String flowURL)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all flow descriptors
     *
     * @return The set of all flow descriptors
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<WBFlowDescription> retrieveFlowDescriptors()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all tags known to the Meandre server
     *
     * @return The set of all tags
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveAllTags()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all component tags known to the Meandre server
     *
     * @return The set of all component tags
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveComponentTags()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all flow tags known to the Meandre server
     *
     * @return The set of all flow tags
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveFlowTags()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves components by tag
     *
     * @param tag The tag to search for
     * @return The set of components containing the specified tag
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveComponentsByTag(String tag)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves flows by tag
     *
     * @param tag The tag to search for
     * @return The set of flows containing the specified tag
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveFlowsByTag(String tag)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves component ids (urls) that match a specific query
     *
     * @param query The query
     * @return The set of all component ids matching the specified query
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveComponentUrlsByQuery(String query)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves flow ids (urls) that match a specific query
     * @param query The query
     * @return The set of all flow ids matching the specified query
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<String> retrieveFlowUrlsByQuery(String query)
        throws SessionExpiredException, MeandreCommunicationException;

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
    public WBFlowDescription uploadFlow(WBFlowDescription flow, boolean overwrite)
        throws SessionExpiredException, MeandreCommunicationException, CorruptedFlowException;

    /**
     * Uploads flows in batch
     *
     * @param flows The set of flow descriptors to upload
     * @param overwrite true to overwrite, false to ignore the upload request if a flow already exists on the server
     * @return true if the operation succeeded, false otherwise
     * @throws @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public boolean uploadFlowBatch(Set<WBFlowDescription> flows, boolean overwrite)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Removes a resource from the server
     *
     * @param resourceURL The resource url
     * @return true if the operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public boolean removeResource(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException;

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
    public boolean publish(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Unpublishes a resource
     *
     * @param resourceURL The resource url
     * @return true if the operation succeeded, false otherwise
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public boolean unpublish(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException;

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
    public String runFlow(String flowURL, boolean verbose)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves the running flows
     *
     * @return The map of running flows: flow id -> url where it is running
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Map<String, String> retrieveRunningFlows()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves the WebUI info object for a specific token
     *
     * @param token The runtime token
     * @return The WebUI descriptor
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public WBWebUIInfo retrieveWebUIInfo(String token)
        throws SessionExpiredException, MeandreCommunicationException;

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
    public Set<WBExecutableComponentDescription> retrievePublicComponents()
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves all public flows
     *
     * @return The set of all public flows
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public Set<WBFlowDescription> retrievePublicFlows()
        throws SessionExpiredException, MeandreCommunicationException;

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
    public boolean abortFlow(int runningFlowPort)
        throws SessionExpiredException, MeandreCommunicationException;

    /**
     * Retrieves statistics about a running flow
     *
     * @param runningFlowPort The port where the flow is running
     * @return The runtime statistics for the specified flow
     * @throws SessionExpiredException Thrown if the user's session has expired
     * @throws MeandreCommunicationException Thrown if a problem occurred while communicating with the Meandre server
     */
    public String retrieveRunningFlowStatistics(int runningFlowPort)
        throws SessionExpiredException, MeandreCommunicationException;


}
