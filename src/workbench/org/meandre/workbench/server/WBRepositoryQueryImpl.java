package org.meandre.workbench.server;

//==============
// Java Imports
//==============

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

//===============
// Other Imports
//===============

import com.google.gwt.user.server.rpc.*;
import com.hp.hpl.jena.rdf.model.*;
import org.meandre.workbench.server.proxy.beans.repository.*;
import org.meandre.workbench.client.*;
import org.meandre.workbench.client.beans.*;
import org.meandre.workbench.server.proxy.MeandreProxy;


/**
 * <p>Title: Workbench Repository Query Implementation</p>
 *
 * <p>Description: This class implements the Workbench Repository Query
 * interface for talking to the Meandre back-end.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class WBRepositoryQueryImpl extends RemoteServiceServlet implements
        WBRepositoryQuery {

    //==============
    // Data Members
    //==============

    private static Map _proxies = new Hashtable();
    private static Map _runOutput = new Hashtable();
    static private WBRepositoryQueryImpl s_instance = null;

    //================
    // Constructor(s)
    //================

    public WBRepositoryQueryImpl() {
        s_instance = this;
    }

    //================
    // Static Methods
    //================

    public static MeandreProxy getProxy(String sid){
        return (MeandreProxy)_proxies.get(sid);
    }

    //=================
    // Private Methods
    //=================


    //===================================
    // Interface Impl: WBRepositoryQuery
    //===================================

    /**
     * Regenerate the repository from all of its locations.  NOTE: this
     * command will delete all unpublished components and flows.
     * @param sid String session id
     * @return WBCallbackObject Bean that contains return information.
     */
    public WBCallbackObject regenerateRepository(String sid){
        Object obj = _proxies.get(sid);
        boolean saveas = false;
        WBCallbackObject wbc = new WBCallbackObject();
        if (obj == null) {
            wbc.setSuccess(false);
            wbc.setMessage("Session ID no longer valid.");
         } else {
            MeandreProxy proxy = (MeandreProxy) obj;
            String msg = proxy.getRegenerate();
            if ((msg == null) || (msg.indexOf("successfully regenerated") == -1)) {
                wbc.setSuccess(false);
                wbc.setMessage("Unable to delete old flow.  " + msg);
            } else {
                wbc.setSuccess(true);
                wbc.setMessage(msg);
            }
        }
        return wbc;
    }


    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param flowid String flow uri.
     * @return WBCallbackObject Bean that contains return information.
     */
    public WBCallbackObject deleteFlowFromRepository(String sid, String flowid){
        Object obj = _proxies.get(sid);
         boolean saveas = false;
         WBCallbackObject wbc = new WBCallbackObject();
         if (obj == null) {
             wbc.setSuccess(false);
             wbc.setMessage("Session ID no longer valid.");
          } else {
             MeandreProxy proxy = (MeandreProxy) obj;
             String uri = proxy.getRemove(flowid);
             if ((uri == null) || (!uri.trim().equals(flowid.trim()))) {
                 wbc.setSuccess(false);
                 wbc.setMessage("Unable to delete old flow.  " + uri + " " +
                                flowid);
             } else {
                 wbc.setSuccess(true);
                 wbc.setMessage(uri);
             }
         }
         return wbc;
    }


    /**
     * Starts execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @param flowid String flow uri.
     * @return WBExecBean Bean that contains execution information.
     */
    public WBExecBean startInteractiveExecution(String sid,
                                                String execid,
                                                String flowid){
        Object obj = _proxies.get(sid);
        if (obj == null) {
            return new WBExecBean("No longer valid session ID.");
        } else {
            MeandreProxy proxy = (MeandreProxy) obj;
            Vector v = new Vector();
            _runOutput.put(execid, v);

            new Thread(new InteractiveExecutionRunner(flowid, "txt", v, proxy)).start();

            try {
                Thread.currentThread().sleep(500);
            } catch (Exception e){}

            StringBuffer buff = new StringBuffer("");
            while(!v.isEmpty()){
                char[] iarr = (char[])v.remove(0);
                if ((iarr.length == 1) && (iarr[0] == '`')){
                    _runOutput.remove(v);
                    return new WBExecBean(buff.toString(), execid, true);
                }
                buff.append(iarr);
            }
            return new WBExecBean(buff.toString(), execid, false);
        }
    }

    private class InteractiveExecutionRunner implements Runnable {

        private String _flowid = null;
        private String _fmt = null;
        private Vector _v = null;
        private MeandreProxy _proxy = null;

        public InteractiveExecutionRunner(String flowid, String fmt, Vector v, MeandreProxy proxy){
            _flowid = flowid;
            _fmt = fmt;
            _v = v;
            _proxy = proxy;
        }

        public void run(){
            _proxy.runWBFlowInteractively(_flowid, _fmt, _v);
        }
    }

    /**
     * Updates status of execution of a flow in interactive mode.
     * @param sid String session ID.
     * @param execid String execution ID.
     * @return WBExecBean
     */
    public WBExecBean updateInteractiveExecution(String sid, String execid){
        Object obj = _proxies.get(sid);
        if (obj == null) {
            return new WBExecBean("No longer valid session ID.");
        } else {
            WBExecBean eb = null;
            MeandreProxy proxy = (MeandreProxy) obj;
            Vector v = (Vector)_runOutput.get(execid);

            StringBuffer buff = new StringBuffer("");
            while(!v.isEmpty()){
                char[] iarr = (char[])v.remove(0);
                if ((iarr.length == 1) && (iarr[0] == '`')){
                    _runOutput.remove(v);
                    return new WBExecBean(buff.toString(), execid, true);
                }
                buff.append(iarr);
            }
            return new WBExecBean(buff.toString(), execid, false);
        }
    }

    /**
     * Log the user into the application.
     * @param sid String session id
     * @return LoginBean Bean containing login information.
     */
    public WBLoginBean checkSessionID(String sid) {
        Object obj = _proxies.get(sid);
        if (obj == null) {
            return new WBLoginBean("No longer valid session ID.");
        } else {
            MeandreProxy proxy = (MeandreProxy) obj;
            if (proxy.getRoles() != null) {
                return new WBLoginBean(proxy.getName(), sid, proxy.getBaseURL());
            }
        }
        return new WBLoginBean("No longer valid session ID.");
    }

    /**
     * Log the user into the application.
     * @param userid String user's id
     * @param password String user's password
     * @param url String URL of server to connect with.
     * @return LoginBean Bean containing login information.
     */
    public WBLoginBean login(String userid, String password, String url) {
        WBLoginBean wblb = null;
        MeandreProxy proxy = new MeandreProxy(userid, password, url);
        if (proxy.isReady()) {
            try {
                Thread.currentThread().sleep(5);
            } catch (Exception e) {}
            String sid = "sid:" + System.currentTimeMillis();
            wblb = new WBLoginBean(proxy.getName(), sid, url);
            _proxies.put(sid, proxy);
        } else {
            wblb = new WBLoginBean("Login failed.");
        }
        return wblb;
    }


    /**
     * Get the active components in the current user's repository that match
     * the search criteria.
     *
     * @param search String The search string for this query.
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
     * @return Set Returns set of active components matching search query.
     */
    public Set getActiveComponents(String search, String sid) {
        Object obj = _proxies.get(sid);
        if (obj == null) {
            return null;
        } else {
            MeandreProxy proxy = (MeandreProxy) obj;

            //acquire Repository object from current session
            QueryableRepository queryableRep = proxy.getRepository();
            Set ret = new HashSet();
            Set comps = queryableRep.getAvailableExecutableComponents(search);
            for (Iterator itty = comps.iterator(); itty.hasNext(); ) {
                Resource res = (Resource) itty.next();
                ExecutableComponentDescription ecd = queryableRep.
                        getExecutableComponentDescription(
                                res);
                ret.add(MeandreToWBBeanConverter.convertComponent(ecd));
            }

            comps = queryableRep.getAvailableFlows(search);
            for (Iterator itty = comps.iterator(); itty.hasNext(); ) {
                Resource res = (Resource) itty.next();
                FlowDescription flow = queryableRep.getFlowDescription(res);
                ret.add(MeandreToWBBeanConverter.convertFlow(flow, queryableRep));
            }
            return ret;
        }
    }

        /**
         * Returns the set of active components in the repository.
         *
         * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
         * @return Set Returns set of active components.
         */
        public Set getActiveComponents(String sid) {
            Object obj = _proxies.get(sid);
            if (obj == null) {
                return null;
            } else {
                MeandreProxy proxy = (MeandreProxy) obj;

                //acquire Repository object from current session
                QueryableRepository queryableRep = proxy.getRepository();

                Set ret = new HashSet();
                Set comps = queryableRep.getAvailableExecutableComponents();
                for (Iterator itty = comps.iterator(); itty.hasNext(); ) {
                    Resource res = (Resource) itty.next();
                    ExecutableComponentDescription ecd = queryableRep.
                            getExecutableComponentDescription(
                                    res);
                    ret.add(MeandreToWBBeanConverter.convertComponent(ecd));
                }

                return ret;
            }
        }

        /**
         *
         * @gwt.typeArgs <org.meandre.workbench.client.beans.WBFlow>
         */
        public Set getActiveFlows(String sid) {
            Object obj = _proxies.get(sid);
            if (obj == null) {
                return null;
            } else {
                MeandreProxy proxy = (MeandreProxy) obj;

                //acquire Repository object from current session
                QueryableRepository queryableRep = proxy.getRepository();
                Set ret = new HashSet();
                Set flows = queryableRep.getAvailableFlows();
                for (Iterator itty = flows.iterator(); itty.hasNext(); ) {
                    Resource res = (Resource) itty.next();
                    FlowDescription flow = queryableRep.getFlowDescription(res);
                    ret.add(MeandreToWBBeanConverter.convertFlow(flow,
                            queryableRep));
                }
                return ret;
            }
        }

        /**
         * Saves the flow and returns the callback object.
         *
         * @return WBCallbackObject Returns callback object.
         */
        public WBCallbackObject saveFlow(WBFlow flow, String sid) {
            Object obj = _proxies.get(sid);
            boolean saveas = false;
            WBCallbackObject wbc = new WBCallbackObject();
            if (obj == null) {
                wbc.setSuccess(false);
                wbc.setMessage("Session ID no longer valid.");
                return wbc;
            } else {
                MeandreProxy proxy = (MeandreProxy) obj;


                // Convert WBFlow to FlowDescription

                Model model = ModelFactory.createDefaultModel();
                Resource resExecutableComponent = null;
                if (flow.getFlowID().trim().length() > 0){
                    resExecutableComponent = model.createResource(flow.getFlowID());
                } else {
                    saveas = true;
                    if (flow.getBaseURL().trim().length() > 0) {
                        resExecutableComponent = model.createResource(flow.
                                getBaseURL() + "flow/"
                                + flow.getName().toLowerCase().replaceAll(" ", "-"));
                    } else {
                        resExecutableComponent = model.createResource("http://test.org/flow/"
                                + System.currentTimeMillis()
                                + flow.getName().toLowerCase().replaceAll(" ", "-"));
                    }
                }
                Set instances = new HashSet();
                for (Iterator itty = flow.getExecutableComponentInstances().
                                     iterator(); itty.hasNext(); ) {
                    WBComponentInstance ci = (WBComponentInstance) itty.next();
                    model = ModelFactory.createDefaultModel();
                    Resource res1 = model.createResource(ci.
                            getExecutableComponentInstance().trim());
                    model = ModelFactory.createDefaultModel();
                    Resource res2 = model.createResource(ci.
                            getExecutableComponent().getID().trim());
                    PropertiesDescription pd = new PropertiesDescription(new
                            Hashtable(ci.getProperties().getValuesMap()));
                    ExecutableComponentInstanceDescription ecid = new
                            ExecutableComponentInstanceDescription(res1,
                            res2,
                            ci.getName().trim(),
                            ci.getDescription().trim(),
                            pd);
                    instances.add(ecid);

                }
                Set connections = new HashSet();
                for (Iterator itty = flow.getConnectorDescriptions().
                                     iterator(); itty.hasNext(); ) {
                    WBComponentConnection cc = (WBComponentConnection) itty.
                                               next();
                    model = ModelFactory.createDefaultModel();
                    Resource res1 = model.createResource(cc.getConnector().trim());
                    model = ModelFactory.createDefaultModel();
                    Resource res2 = model.createResource(cc.getSourceInstance().trim());
                    model = ModelFactory.createDefaultModel();
                    Resource res3 = model.createResource(cc.
                            getSourceIntanceDataPort().trim());
                    model = ModelFactory.createDefaultModel();
                    Resource res4 = model.createResource(cc.getTargetInstance().trim());
                    model = ModelFactory.createDefaultModel();
                    Resource res5 = model.createResource(cc.
                            getTargetIntanceDataPort().trim());
                    ConnectorDescription cd = new ConnectorDescription(res1,
                            res2,
                            res3,
                            res4,
                            res5);
                    connections.add(cd);

                }
                FlowDescription flowdesc = new FlowDescription(
                        resExecutableComponent,
                        flow.getName(),
                        flow.getDescription(),
                        flow.getRights(),
                        flow.getCreator(),
                        flow.getCreationDate(),
                        instances,
                        connections,
                        new TagsDescription(new HashSet(flow.getTags().getTags())));

                Map params = new HashMap<String,String>();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                flowdesc.getModel().write(baos, "TTL");
                try {
                    baos.flush();
                } catch (Exception e){}
                //System.out.println(new String(baos.toByteArray()));
                params.put("repository", new String(baos.toByteArray()));
                try {
                    baos.close();
                } catch (Exception e){}

                if (!saveas){
                    String uri = proxy.getRemove(flow.getFlowID());
                    if ((uri == null) || (!uri.trim().equals(flow.getFlowID().trim()))){
                        wbc.setSuccess(false);
                        wbc.setMessage("Unable to delete old flow.  " + uri + " "  + flow.getFlowID());
                        return wbc;
                    }
                }

                proxy.executePost("services/repository/add_flow_descriptors.ttl", params);
                wbc.setMessage(flowdesc.getFlowComponent().getURI());

                /* This is necessary for the web app. */
                proxy.flushRepository();
            }
            return wbc;
        }

    }
