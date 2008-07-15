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

public interface IRepositoryAsync {

    public void getSession(AsyncCallback<WBSession> callback);

    public void login(String userName, String password, String hostName, int port, AsyncCallback<WBSession> callback);

    public void logout(AsyncCallback<Boolean> callback);

    public void clearCache(AsyncCallback<Boolean> callback);

    ///////////////
    // Locations //
    ///////////////

    public void retrieveLocations(AsyncCallback<Set<WBLocation>> callback);

    public void addLocation(String locationURL, String description, AsyncCallback<Boolean> callback);

    public void removeLocation(String url, AsyncCallback<Boolean> callback);

    ////////////////
    // Repository //
    ////////////////

    public void regenerate(AsyncCallback<Boolean> callback);

    public void retrieveComponentUrls(AsyncCallback<Set<String>> callback);

    public void retrieveComponentDescriptor(String componentURL, AsyncCallback<WBExecutableComponentDescription> callback);

    public void retrieveComponentDescriptors(AsyncCallback<Set<WBExecutableComponentDescription>> callback);

    public void retrieveFlowUrls(AsyncCallback<Set<String>> callback);

    public void retrieveFlowDescriptor(String flowURL, AsyncCallback<WBFlowDescription> callback);

    public void retrieveFlowDescriptors(AsyncCallback<Set<WBFlowDescription>> callback);

    public void retrieveAllTags(AsyncCallback<Set<String>> callback);

    public void retrieveComponentTags(AsyncCallback<Set<String>> callback);

    public void retrieveFlowTags(AsyncCallback<Set<String>> callback);

    public void retrieveComponentsByTag(String tag, AsyncCallback<Set<String>> callback);

    public void retrieveFlowsByTag(String tag, AsyncCallback<Set<String>> callback);

    public void retrieveComponentUrlsByQuery(String query, AsyncCallback<Set<String>> callback);

    public void retrieveFlowUrlsByQuery(String query, AsyncCallback<Set<String>> callback);

    public void uploadFlow(WBFlowDescription flow, boolean overwrite, AsyncCallback<WBFlowDescription> callback);

    public void uploadFlowBatch(Set<WBFlowDescription> flows, boolean overwrite, AsyncCallback<Boolean> callback);

    public void removeResource(String resourceURL, AsyncCallback<Boolean> callback);

    /////////////
    // Publish //
    /////////////

    public void publish(String resourceURL, AsyncCallback<Boolean> callback);

    public void unpublish(String resourceURL, AsyncCallback<Boolean> callback);

    ///////////////
    // Execution //
    ///////////////

    public void runFlow(String flowURL, boolean verbose, AsyncCallback<String> callback);

    public void retrieveRunningFlows(AsyncCallback<Map<String, String>> callback);

    public void retrieveWebUIInfo(String token, AsyncCallback<WBWebUIInfo> callback);

    ////////////
    // Public //
    ////////////

    public void retrievePublicComponents(AsyncCallback<Set<WBExecutableComponentDescription>> callback);

    public void retrievePublicFlows(AsyncCallback<Set<WBFlowDescription>> callback);

    ////////////////////////////
    // Admin of Running Flows //
    ////////////////////////////

    public void abortFlow(int runningFlowPort, AsyncCallback<Boolean> callback);

    public void retrieveRunningFlowStatistics(int runningFlowPort, AsyncCallback<String> callback);


}
