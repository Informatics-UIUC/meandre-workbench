package org.meandre.workbench.server;

//==============
// Java Imports
//==============

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;

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

    private Map _proxies = new Hashtable();

    //================
    // Constructor(s)
    //================

    public WBRepositoryQueryImpl() {

    }

    //=================
    // Private Methods
    //=================


    //===================================
    // Interface Impl: WBRepositoryQuery
    //===================================

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
                return new WBLoginBean(proxy.getName(), sid);
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
            wblb = new WBLoginBean(proxy.getName(), sid);
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
//        WBCallbackObject wbc = new WBCallbackObject();
//        //acquire Repository object from current session
//        RepositoryFactory queryableRep = getWorkRepositoryFact();
//
//        //publish flow
//        String sDir = null;
//
//        try {
//            String sPath = File.separator +
//                           "resources" + File.separator +
//                           "jetty" + File.separator +
//                           "meandre-app" + File.separator +
//                           "components" + File.separator +
//                           "description" + File.separator;
//
//            sDir = new File(".").getCanonicalPath() + sPath;
//
//            // Convert WBFlow to FlowDescription
//
//            Model model = ModelFactory.createDefaultModel();
//            Resource resExecutableComponent = null;
//            if (flow.getBaseURL().trim().length() > 0){
//                resExecutableComponent = model.createResource(flow.getBaseURL());
//            } else {
//                resExecutableComponent = model.createResource("file://");
//            }
//            Set instances = new HashSet();
//            for (Iterator itty = flow.getExecutableComponentInstances().
//                                 iterator(); itty.hasNext(); ) {
//                WBComponentInstance ci = (WBComponentInstance) itty.next();
//                model = ModelFactory.createDefaultModel();
//                Resource res1 = model.createResource(ci.
//                        getExecutableComponentInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res2 = model.createResource(ci.getExecutableComponent().getID());
//                PropertiesDescription pd = new PropertiesDescription(new
//                        Hashtable(ci.getProperties().getValuesMap()));
//                ExecutableComponentInstanceDescription ecid = new
//                        ExecutableComponentInstanceDescription(res1,
//                        res2,
//                        ci.getName(),
//                        ci.getDescription(),
//                        pd);
//                instances.add(ecid);
//
//            }
//            Set connections = new HashSet();
//            for (Iterator itty = flow.getConnectorDescriptions().
//                                 iterator(); itty.hasNext(); ) {
//                WBComponentConnection cc = (WBComponentConnection) itty.next();
//                model = ModelFactory.createDefaultModel();
//                Resource res1 = model.createResource(cc.getConnector());
//                model = ModelFactory.createDefaultModel();
//                Resource res2 = model.createResource(cc.getSourceInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res3 = model.createResource(cc.getSourceIntanceDataPort());
//                model = ModelFactory.createDefaultModel();
//                Resource res4 = model.createResource(cc.getTargetInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res5 = model.createResource(cc.getTargetIntanceDataPort());
//                ConnectorDescription cd = new ConnectorDescription(res1,
//                                          res2,
//                                          res3,
//                                          res4,
//                                          res5);
//                connections.add(cd);
//
//            }
//            FlowDescription flowdesc = new FlowDescription(
//                    resExecutableComponent,
//                    flow.getName(),
//                    flow.getDescription(),
//                    flow.getRights(),
//                    flow.getCreator(),
//                    flow.getCreationDate(),
//                    instances,
//                    connections,
//                    new TagsDescription(new HashSet(flow.getTags().getTags())));
//
//
//
//            //write file
//            queryableRep.publishFlowToDirectory(sDir, flowdesc);
//            wbc.setMessage(flowdesc.getFlowComponent().getURI());
//
//            /* This is necessary for the web app. */
//            queryableRep.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            wbc.setMessage(e.getMessage());
//            //log.warning("Could not write the system model!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            wbc.setMessage(e.getMessage());
//            //log.warning("Could not write the system model!");
//        }
//
//        return wbc;
            return null;
        }

    }
