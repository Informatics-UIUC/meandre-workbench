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

package org.seasr.meandre.workbench.server.rpc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.meandre.client.MeandreClient;
import org.meandre.client.TransmissionException;
import org.meandre.core.repository.ExecutableComponentDescription;
import org.meandre.core.repository.FlowDescription;
import org.meandre.core.repository.LocationBean;
import org.meandre.core.repository.QueryableRepository;
import org.meandre.core.repository.RepositoryImpl;
import org.meandre.core.security.Role;
import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBLocation;
import org.seasr.meandre.workbench.client.beans.session.WBSession;
import org.seasr.meandre.workbench.client.exceptions.CorruptedFlowException;
import org.seasr.meandre.workbench.client.exceptions.LoginFailedException;
import org.seasr.meandre.workbench.client.exceptions.MeandreCommunicationException;
import org.seasr.meandre.workbench.client.exceptions.SessionExpiredException;
import org.seasr.meandre.workbench.client.rpc.IRepository;
import org.seasr.meandre.workbench.server.beans.converters.IBeanConverter;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Boris Capitanu
 *
 */
public class Repository extends RemoteServiceServlet implements IRepository {

    private static final long serialVersionUID = -1606693690185487714L;
    private static final int SESSION_TIMEOUT = 24 * 60 * 60;  // 24 hours

    private static final IBeanConverter<URI, String> UriStringConverter =
        new IBeanConverter<URI, String>() {
            public String convert(URI url) {
                return url.toString();
            }
        };

    private final Map<String, InputStream> _flowConsoles = new HashMap<String, InputStream>();

    ///////////////
    // Workbench //
    ///////////////

    public WBSession getSession()
        throws SessionExpiredException {

        return (WBSession) checkSession().getAttribute("session");
    }

    public WBSession login(String userName, String password, String hostName, int port)
        throws LoginFailedException, MeandreCommunicationException {

        HttpSession session = getHttpSession();
        assert (session != null);

        if (session.getAttribute("session") == null) {
            session.setMaxInactiveInterval(SESSION_TIMEOUT);
            //session.setMaxInactiveInterval(40);  // For testing

            try {
                hostName = hostName.trim();

                if (hostName.equalsIgnoreCase("localhost") || hostName.equals("127.0.0.1")) {
                    InetAddress remoteAddr = Inet4Address.getByName(getThreadLocalRequest().getRemoteHost());
                    if (!remoteAddr.isLoopbackAddress())
                        hostName = remoteAddr.getCanonicalHostName();
                }

                MeandreClient client = new MeandreClient(hostName, port);
                client.setCredentials(userName, password);

                Set<String> userRoles = client.retrieveUserRoles();

                if(!userRoles.contains(Role.WORKBENCH.getUrl()))
                    throw new LoginFailedException("Insufficient permissions");

                WBSession wbSession =
                    new WBSession(session.getId(), userName, password, userRoles, hostName, port);

                session.setAttribute("client", client);
                session.setAttribute("session", wbSession);

                return wbSession;
            }
            catch (LoginFailedException e) {
                throw e;
            }

            catch (Exception e) {
                throw new MeandreCommunicationException(e);
            }
        }
        else
            return (WBSession) session.getAttribute("session");
    }

    public Boolean logout()
        throws SessionExpiredException {

        checkSession().invalidate();

        return true;
    }

    public Boolean clearCache()
        throws SessionExpiredException {

        HttpSession session = checkSession();

        session.removeAttribute("repository");
        session.removeAttribute("public_repository");

        return true;
    }

    ///////////////
    // Locations //
    ///////////////

    public Set<WBLocation> retrieveLocations()
        throws SessionExpiredException, MeandreCommunicationException {

        MeandreClient client = getClient();

        try {
            Set<LocationBean> locations = client.retrieveLocations();
            return MeandreConverter.convert(locations, MeandreConverter.LocationBeanConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public boolean addLocation(String locationURL, String description)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().addLocation(locationURL, description);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public boolean removeLocation(String url)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().removeLocation(url);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    ////////////////
    // Repository //
    ////////////////

    public boolean regenerate()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().regenerate();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveComponentUrls()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Set<URI> componentUrls = getClient().retrieveComponentUris();
            return MeandreConverter.convert(componentUrls, UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public WBExecutableComponentDescription retrieveComponentDescriptor(String componentURL)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return MeandreConverter.ExecutableComponentDescriptionConverter.convert(
                    getClient().retrieveComponentDescriptor(componentURL));
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<WBExecutableComponentDescription> retrieveComponentDescriptors()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            QueryableRepository repository = getRepository();
            return MeandreConverter.convert(
                    repository.getAvailableExecutableComponentDescriptions(),
                    MeandreConverter.ExecutableComponentDescriptionConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveFlowUrls()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return MeandreConverter.convert(getClient().retrieveFlowUris(), UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public WBFlowDescription retrieveFlowDescriptor(String flowURL)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return MeandreConverter.FlowDescriptionConverter.convert(
                    getClient().retrieveFlowDescriptor(flowURL));
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<WBFlowDescription> retrieveFlowDescriptors()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            QueryableRepository repository = getRepository();
            return MeandreConverter.convert(
                    repository.getAvailableFlowDescriptions(),
                    MeandreConverter.FlowDescriptionConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveAllTags()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().retrieveAllTags();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveComponentTags()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().retrieveComponentTags();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveFlowTags()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().retrieveFlowTags();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveComponentsByTag(String tag)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Set<URI> componentUrls = getClient().retrieveComponentsByTag(tag);
            return MeandreConverter.convert(componentUrls, UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveFlowsByTag(String tag)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Set<URI> flowUrls = getClient().retrieveFlowsByTag(tag);
            return MeandreConverter.convert(flowUrls, UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveComponentUrlsByQuery(String query)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Set<URI> componentUrls = getClient().retrieveComponentUrlsByQuery(query);
            return MeandreConverter.convert(componentUrls, UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<String> retrieveFlowUrlsByQuery(String query)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Set<URI> flowUrls = getClient().retrieveFlowUrlsByQuery(query);
            return MeandreConverter.convert(flowUrls, UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public boolean uploadFlow(WBFlowDescription wbFlow, boolean overwrite)
        throws SessionExpiredException, MeandreCommunicationException, CorruptedFlowException {

        FlowDescription flow = MeandreConverter.WBFlowDescriptionConverter.convert(wbFlow);

        String flowURI = flow.getFlowComponent().getURI();
        System.out.println("Uploading flow " + flowURI);

        String debug = System.getProperty("org.seasr.meandre.workbench.debug");
        if (debug != null && Boolean.parseBoolean(debug)) {
            String execStepMsg = "";
            try {
                Model flowModel = flow.getModel();

                String fName = flowURI.replaceAll(":|/", "_");
                String tempFolder = System.getProperty("java.io.tmpdir");
                if (!(tempFolder.endsWith("/") || tempFolder.endsWith("\\")))
                    tempFolder += System.getProperty("file.separator");

                FileOutputStream ntStream = new FileOutputStream(tempFolder + fName + ".nt");
                flowModel.write(ntStream, "N-TRIPLE");
                ntStream.close();

                FileOutputStream ttlStream = new FileOutputStream(tempFolder + fName + ".ttl");
                flowModel.write(ttlStream, "TTL");
                ttlStream.close();

                execStepMsg = "STEP1: Creating RepositoryImpl from flow model";
                RepositoryImpl repository = new RepositoryImpl(flowModel);
                execStepMsg = "STEP2: Retrieving available flows";
                Set<FlowDescription> flows = repository.getAvailableFlowDescriptions();
                execStepMsg = "STEP3: Getting flow";
                flow = flows.iterator().next();
                if (flow == null)
                    throw new CorruptedFlowException("The flow obtained is null!");
            }
            catch (Exception e) {

                CorruptedFlowException corruptedFlowException = (execStepMsg != null) ?
                        new CorruptedFlowException(execStepMsg, e) : (CorruptedFlowException) e;

                throw corruptedFlowException;
            }
        }

        try {
            getClient().uploadFlow(flow, overwrite);

            return true;
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public boolean uploadFlowBatch(Set<WBFlowDescription> flows, boolean overwrite)
        throws SessionExpiredException, MeandreCommunicationException {

        throw new RuntimeException("Not yet implemented");
    }

    public boolean removeResource(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().removeResource(resourceURL);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    /////////////
    // Publish //
    /////////////

    public boolean publish(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().publish(resourceURL);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public boolean unpublish(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().unpublish(resourceURL);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    ///////////////
    // Execution //
    ///////////////

    public boolean runFlow(String flowURL, String token, boolean verbose)
        throws SessionExpiredException, MeandreCommunicationException {

        if (_flowConsoles.containsKey(flowURL)) return false;

        try {
            _flowConsoles.put(flowURL, getClient().runFlowStreamOutput(flowURL, token, verbose));
            return true;
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public String retrieveFlowOutput(String flowURL)
        throws MeandreCommunicationException {

        InputStream consoleStream = _flowConsoles.get(flowURL);
        if (consoleStream == null)
            throw new MeandreCommunicationException(flowURL + " has not been executed");

        try {
            int bufferSize = 1000;

            byte[] data = new byte[bufferSize];
            int nRead = consoleStream.read(data);

            if (nRead == -1) {
                // EOF detected
                _flowConsoles.remove(flowURL);
                return null;
            }

            try {
                return new String(data, 0, nRead);
            }
            catch (Exception ex) {
                throw new MeandreCommunicationException("Cannot create string from stream", ex);
            }
        }
        catch (IOException e) {
            _flowConsoles.remove(flowURL);

            throw new MeandreCommunicationException(e);
        }
    }

    public Map<String, String> retrieveRunningFlows()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Map<URI, URI> runningFlowsMap = getClient().retrieveRunningFlows();
            return MeandreConverter.convert(runningFlowsMap, UriStringConverter, UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public WBWebUIInfo retrieveWebUIInfo(String token)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            JSONObject joWebUIInfo = getClient().retrieveWebUIInfo(token);
            return (joWebUIInfo != null && joWebUIInfo.has("port") && joWebUIInfo.getInt("port") > 0) ?
                new WBWebUIInfo(
                        joWebUIInfo.getString("hostname"),
                        joWebUIInfo.getInt("port"),
                        joWebUIInfo.getString("token"),
                        joWebUIInfo.getString("uri")
                        )
                : null;
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
        catch (JSONException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    ////////////
    // Public //
    ////////////

    public Set<WBExecutableComponentDescription> retrievePublicComponents()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Set<ExecutableComponentDescription> publicComponents =
                getClient().retrievePublicRepository().getAvailableExecutableComponentDescriptions();
            return MeandreConverter.convert(publicComponents, MeandreConverter.ExecutableComponentDescriptionConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public Set<WBFlowDescription> retrievePublicFlows()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            Set<FlowDescription> publicFlows = getPublicRepository().getAvailableFlowDescriptions();
            return MeandreConverter.convert(publicFlows, MeandreConverter.FlowDescriptionConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    ////////////////////////////
    // Admin of Running Flows //
    ////////////////////////////

    public boolean abortFlow(int runningFlowPort)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().abortFlow(runningFlowPort);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    public String retrieveRunningFlowStatistics(int runningFlowPort)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            JSONObject jsonStats = getClient().retrieveRunningFlowStatisitics(runningFlowPort);
            return jsonStats.toString();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    /////////////
    // Private //
    /////////////

    private HttpSession getHttpSession() {
        return getThreadLocalRequest().getSession();
    }

    private HttpSession checkSession() throws SessionExpiredException {
        HttpSession session = getHttpSession();
        assert(session != null);

        if (session.getAttribute("session") == null)
            throw new SessionExpiredException();

        return session;
    }

    private MeandreClient getClient()
        throws SessionExpiredException {

        return (MeandreClient) checkSession().getAttribute("client");
    }

    private QueryableRepository getRepository()
        throws SessionExpiredException, TransmissionException {

        QueryableRepository repository = (QueryableRepository) getHttpSession().getAttribute("repository");

        if (repository == null) {
            repository = getClient().retrieveRepository();
            getHttpSession().setAttribute("repository", repository);
        }

        return repository;
    }

    private QueryableRepository getPublicRepository()
        throws SessionExpiredException, TransmissionException {

        QueryableRepository repository = (QueryableRepository) getHttpSession().getAttribute("public_repository");

        if (repository == null) {
            repository = getClient().retrievePublicRepository();
            getHttpSession().setAttribute("public_repository", repository);
        }

        return repository;
    }
}
