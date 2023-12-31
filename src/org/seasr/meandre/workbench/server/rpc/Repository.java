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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.meandre.core.repository.ExecutableComponentDescription;
import org.meandre.core.repository.ExecutableComponentInstanceDescription;
import org.meandre.core.repository.FlowDescription;
import org.meandre.core.repository.LocationBean;
import org.meandre.core.repository.QueryableRepository;
import org.meandre.core.repository.RepositoryImpl;
import org.meandre.core.security.Role;
import org.meandre.tools.client.AbstractMeandreClient;
import org.meandre.tools.client.exceptions.TransmissionException;
import org.meandre.tools.client.v2.MeandreClient;
import org.meandre.tools.zigzag.transformations.FlowNotFoundException;
import org.meandre.tools.zigzag.transformations.RDF2ZZConverter;
import org.meandre.zigzag.console.NullOuputStream;
import org.meandre.zigzag.parser.ParseException;
import org.meandre.zigzag.semantic.FlowGenerator;
import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBLocation;
import org.seasr.meandre.workbench.client.beans.session.WBSession;
import org.seasr.meandre.workbench.client.beans.session.WBVersion;
import org.seasr.meandre.workbench.client.exceptions.CorruptedFlowException;
import org.seasr.meandre.workbench.client.exceptions.LoginFailedException;
import org.seasr.meandre.workbench.client.exceptions.MeandreCommunicationException;
import org.seasr.meandre.workbench.client.exceptions.SessionExpiredException;
import org.seasr.meandre.workbench.client.rpc.IRepository;
import org.seasr.meandre.workbench.server.Version;
import org.seasr.meandre.workbench.server.beans.converters.IBeanConverter;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author Boris Capitanu
 *
 */
public class Repository extends RemoteServiceServlet implements IRepository {

    private static final long serialVersionUID = -1606693690185487714L;
    private static final int SESSION_TIMEOUT = 24 * 60 * 60;  // 24 hours

    private static final IBeanConverter<URI, String> UriStringConverter =
        new IBeanConverter<URI, String>() {
            @Override
			public String convert(URI url) {
                return url.toString();
            }
        };

    private final Map<String, InputStream> _flowConsoles =
        Collections.synchronizedMap(new HashMap<String, InputStream>());

    ///////////////
    // Workbench //
    ///////////////

    @Override
	public WBSession getSession()
        throws SessionExpiredException {

        return (WBSession) checkSession().getAttribute("session");
    }

    @Override
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

                AbstractMeandreClient client = AbstractMeandreClient.getClientForServer(hostName, port, userName, password);
                String serverVersion = client.getServerVersion().getString("version");
				session.setAttribute("version", serverVersion);

                Set<String> userRoles = client.retrieveUserRoles();

                if (!(userRoles.contains(Role.WORKBENCH.getUrl()) || userRoles.contains("user")))
                    throw new LoginFailedException("Insufficient permissions");

                WBVersion wbVersion = new WBVersion(Version.getVersion(), Version.getRevision(), Version.getBuildDate());

                WBSession wbSession =
                    new WBSession(session.getId(), userName, password, userRoles, hostName, port, wbVersion, serverVersion);

                session.setAttribute("client", client);
                session.setAttribute("session", wbSession);

                return wbSession;
            }
            catch (Exception e) {
                throw new MeandreCommunicationException(e);
            }
        }
        else
            return (WBSession) session.getAttribute("session");
    }

    @Override
	public Boolean logout()
        throws SessionExpiredException {

        checkSession().invalidate();

        return true;
    }

    @Override
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

    @Override
	public Set<WBLocation> retrieveLocations()
        throws SessionExpiredException, MeandreCommunicationException {

        AbstractMeandreClient client = getClient();

        try {
            Set<LocationBean> locations = client.retrieveLocations();
            return MeandreConverter.convert(locations, MeandreConverter.LocationBeanConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
	public boolean addLocation(String locationURL, String description)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().addLocation(locationURL, description);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
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

    @Override
	public boolean regenerate()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().regenerate();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
	public Set<String> retrieveFlowUrls()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return MeandreConverter.convert(getClient().retrieveFlowUris(), UriStringConverter);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
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

    @Override
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

    @Override
	public Set<String> retrieveAllTags()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().retrieveAllTags();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
	public Set<String> retrieveComponentTags()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().retrieveComponentTags();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
	public Set<String> retrieveFlowTags()
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().retrieveFlowTags();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
	public boolean uploadFlowBatch(Set<WBFlowDescription> flows, boolean overwrite)
        throws SessionExpiredException, MeandreCommunicationException {

        throw new RuntimeException("Not yet implemented");
    }

    @Override
	public boolean exportFlow(String flowURI, String format)
        throws SessionExpiredException, MeandreCommunicationException {

        QueryableRepository repository = null;

        try {
            repository = getClient().retrieveRepository();
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }

        if (format.equalsIgnoreCase("zz")) {
            try {
                RDF2ZZConverter converter = new RDF2ZZConverter(repository);
                String zzScript = converter.generateZZ(flowURI);
                getHttpSession().setAttribute(flowURI, zzScript);
                return true;
            }
            catch (FlowNotFoundException e) {
                return false;
            }
        }

        if (format.equalsIgnoreCase("mau")) {
            try {
                File tmpFile = File.createTempFile("mau", null);
                tmpFile.deleteOnExit();

                FlowDescription fd = repository.getAvailableFlowDescriptionsMap().get(flowURI);
                Model model = ModelFactory.createDefaultModel();
                model.add(fd.getModel());

                for (ExecutableComponentInstanceDescription ecid : fd.getExecutableComponentInstances())
                    model.add(repository.getExecutableComponentDescription(ecid.getExecutableComponent()).getModel());

                FlowGenerator fg = new FlowGenerator();
                fg.setPrintStream(new PrintStream(new NullOuputStream()));
                fg.init(null);
                fg.getRepository().refreshCache(model);
                fg.generateMAUBlindlyToFile(tmpFile.getPath(), flowURI);
                getHttpSession().setAttribute(flowURI, tmpFile);
                return true;
            }
            catch (IOException e) {
                throw new MeandreCommunicationException(e);
            }
            catch (ParseException e) {
                return false;
            }
        }

        throw new RuntimeException("Export format " + format + " unknown!");
    }

    @Override
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

    @Override
	public boolean publish(String resourceURL)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().publish(resourceURL);
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
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

    @Override
	public WBWebUIInfo runFlow(String flowURL, String token, boolean verbose)
        throws SessionExpiredException, MeandreCommunicationException {

        if (getHttpSession().getAttribute("version").toString().startsWith("1.4"))
            return runFlow14(flowURL, token, verbose);
        else
            return runFlow20(flowURL, token, verbose);
    }

    private WBWebUIInfo runFlow14(String flowURL, String token, boolean verbose)
        throws SessionExpiredException, MeandreCommunicationException {

        int TIMEOUT = 10000; // 10 seconds timeout waiting for flow execution status
        int POLL_FREQ = 500; // poll for execution status every 500ms

        try {
            InputStream flowInputStream = getClient().runFlowStreamOutput(flowURL, token, verbose);

            int counter = 0;
            WBWebUIInfo status = null;

            try {
                while ((status = retrieveWebUIInfo(token)) == null && counter < TIMEOUT) {
                    Thread.sleep(POLL_FREQ);
                    counter += POLL_FREQ;
                }
            }
            catch (InterruptedException e) {
                throw new MeandreCommunicationException(e);
            }

            if (counter >= TIMEOUT) {
                System.out.println("runFlow: Timeout in trying to obtain the flow execution status");
                return null;
            }

            if (status != null)
                _flowConsoles.put(status.getURI(), flowInputStream);
            else
                System.out.println("runFlow: retrieveWebUIInfo returned null - flow died?");

            return status;
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    private WBWebUIInfo runFlow20(String flowURL, String token, boolean verbose)
        throws SessionExpiredException, MeandreCommunicationException {

        int POLL_FREQ = 500; // poll for execution status every 500ms

        MeandreClient clientv2 = (MeandreClient)getClient();
        try {
            String jobID = clientv2.submitJob(flowURL);
            JSONObject status;

            // Wait for job to get to "Running" state
            while (!(status = clientv2.retrieveJobStatus(jobID)).getString("status").equals("Running")) {
            	String state = status.getString("status");
                if (state.equals("Aborted") || state.equals("Failed") || state.equals("Killed"))
                    return null;
                else
                    Thread.sleep(POLL_FREQ);
            }

            InputStream flowInputStream = clientv2.retrieveJobOutput(jobID);
            _flowConsoles.put(jobID, flowInputStream);

            // FIXME having a status of Running doesn't actually mean the flow is ready to serve requests;
            //       the execution engine takes time to actually start the flow;  how to reliably detect when
            //       flow is actually running?

            JSONObject execMeta = status.getJSONObject("wrapper_meta");
            String webuiHost = execMeta.getString("webui_host");
            int webuiPort = execMeta.getInt("webui_port");

            return new WBWebUIInfo(webuiHost, webuiPort, token, jobID);
        }
        catch (Exception e) {
            throw new MeandreCommunicationException(e);
        }
    }

    //TODO may need to start separate thread that retrieves the console output and uses
    //     the producer-consumer scenario to feed the results to the client

    @Override
	public String retrieveFlowOutput(String flowExecutionInstanceId)
        throws MeandreCommunicationException {

        if (getHttpSession().getAttribute("version").toString().startsWith("1.4"))
            return retrieveFlowOutput14(flowExecutionInstanceId);
        else
            return retrieveFlowOutput20(flowExecutionInstanceId);
    }

    private String retrieveFlowOutput20(String jobID) throws MeandreCommunicationException {
        try {
            InputStream flowOutputStream = _flowConsoles.get(jobID);
            if (flowOutputStream == null)
            	throw new MeandreCommunicationException(jobID + " has not been executed");

            try {
                int bufferSize = 1024*1024;

                byte[] data = new byte[bufferSize];
                int nRead = flowOutputStream.read(data);

                if (nRead < 0) {
                    // EOF detected
                    _flowConsoles.remove(jobID);
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
                _flowConsoles.remove(jobID);

                throw new MeandreCommunicationException(e);
            }
        }
        catch (Exception e) {
            throw new MeandreCommunicationException(e);
        }
    }

    private String retrieveFlowOutput14(String flowExecutionInstanceId) throws MeandreCommunicationException {
        InputStream consoleStream = _flowConsoles.get(flowExecutionInstanceId);
        if (consoleStream == null)
            throw new MeandreCommunicationException(flowExecutionInstanceId + " has not been executed");

        try {
            int bufferSize = 1024*1024;

            byte[] data = new byte[bufferSize];
            int nRead = consoleStream.read(data);

            if (nRead < 0) {
                // EOF detected
                _flowConsoles.remove(flowExecutionInstanceId);
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
            _flowConsoles.remove(flowExecutionInstanceId);

            throw new MeandreCommunicationException(e);
        }
    }

    @Override
	public WBWebUIInfo retrieveWebUIInfo(String token)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            JSONObject joWebUIInfo = getClient().retrieveWebUIInfo(token);
            return (joWebUIInfo != null && joWebUIInfo.has("uri") && !joWebUIInfo.getString("uri").startsWith("meandre://missing.uri")) ?
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

	@Override
	public boolean killFlow(String jobID)
		throws SessionExpiredException, MeandreCommunicationException {

		if (getHttpSession().getAttribute("version").toString().startsWith("1.4"))
            return false;  // can't kill flows in meandre 1.4.x

		MeandreClient clientv2 = (MeandreClient)getClient();
        try {
            return clientv2.killJob(jobID);
        }
        catch (TransmissionException e) {
			throw new MeandreCommunicationException(e);
		}
	}

    ////////////
    // Public //
    ////////////

    @Override
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

    @Override
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

    @Override
	public boolean abortFlow(WBWebUIInfo flowInfo)
        throws SessionExpiredException, MeandreCommunicationException {

        try {
            return getClient().abortFlow(flowInfo.getPort());
        }
        catch (TransmissionException e) {
            throw new MeandreCommunicationException(e);
        }
    }

    @Override
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

    private AbstractMeandreClient getClient()
        throws SessionExpiredException {

        return (AbstractMeandreClient) checkSession().getAttribute("client");
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
