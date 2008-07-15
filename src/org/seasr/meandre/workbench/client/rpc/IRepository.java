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

public interface IRepository extends RemoteService {

    public static final String SERVICE_URI = "Repository";

    public static class Util {
        private static IRepositoryAsync _instance;

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

    public WBSession getSession()
        throws SessionExpiredException;

    public WBSession login(String userName, String password, String hostName, int port)
        throws LoginFailedException, MeandreCommunicationException;

    public Boolean logout()
        throws SessionExpiredException;

    public Boolean clearCache()
        throws SessionExpiredException;

    ///////////////
    // Locations //
    ///////////////

    public Set<WBLocation> retrieveLocations()
        throws SessionExpiredException, MeandreCommunicationException;

    public boolean addLocation(String locationURL, String description)
        throws SessionExpiredException, MeandreCommunicationException;

    public boolean removeLocation(String url)
        throws SessionExpiredException, MeandreCommunicationException;

    ////////////////
    // Repository //
    ////////////////

    public boolean regenerate()
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveComponentUrls()
        throws SessionExpiredException, MeandreCommunicationException;

    public WBExecutableComponentDescription retrieveComponentDescriptor(String componentURL)
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<WBExecutableComponentDescription> retrieveComponentDescriptors()
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveFlowUrls()
        throws SessionExpiredException, MeandreCommunicationException;

    public WBFlowDescription retrieveFlowDescriptor(String flowURL)
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<WBFlowDescription> retrieveFlowDescriptors()
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveAllTags()
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveComponentTags()
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveFlowTags()
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveComponentsByTag(String tag)
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveFlowsByTag(String tag)
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveComponentUrlsByQuery(String query)
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<String> retrieveFlowUrlsByQuery(String query)
        throws SessionExpiredException, MeandreCommunicationException;

    public WBFlowDescription uploadFlow(WBFlowDescription flow, boolean overwrite)
        throws SessionExpiredException, MeandreCommunicationException, CorruptedFlowException;

    public boolean uploadFlowBatch(Set<WBFlowDescription> flows, boolean overwrite)
        throws SessionExpiredException, MeandreCommunicationException;

    public boolean removeResource(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException;

    /////////////
    // Publish //
    /////////////

    public boolean publish(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException;

    public boolean unpublish(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException;

    ///////////////
    // Execution //
    ///////////////

    public String runFlow(String flowURL, boolean verbose)
        throws SessionExpiredException, MeandreCommunicationException;

    public Map<String, String> retrieveRunningFlows()
        throws SessionExpiredException, MeandreCommunicationException;

    public WBWebUIInfo retrieveWebUIInfo(String token)
        throws SessionExpiredException, MeandreCommunicationException;

    ////////////
    // Public //
    ////////////

    public Set<WBExecutableComponentDescription> retrievePublicComponents()
        throws SessionExpiredException, MeandreCommunicationException;

    public Set<WBFlowDescription> retrievePublicFlows()
        throws SessionExpiredException, MeandreCommunicationException;

    ////////////////////////////
    // Admin of Running Flows //
    ////////////////////////////

    public boolean abortFlow(int runningFlowPort)
        throws SessionExpiredException, MeandreCommunicationException;

    public String retrieveRunningFlowStatistics(int runningFlowPort)
        throws SessionExpiredException, MeandreCommunicationException;


}
