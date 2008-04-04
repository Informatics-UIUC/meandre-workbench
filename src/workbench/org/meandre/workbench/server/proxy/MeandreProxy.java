package org.meandre.workbench.server.proxy;

//==============
// Java Imports
//==============

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.jsp.JspWriter;
import java.util.Vector;

//===============
// Other Imports
//===============

//import org.meandre.workbench.bootstrap.jetty.Bootstrapper;
import org.meandre.workbench.server.proxy.beans.location.LocationBean;
import org.meandre.workbench.server.proxy.beans.repository.QueryableRepository;
import org.meandre.workbench.server.proxy.beans.repository.RepositoryImpl;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.meandre.workbench.server.proxy.beans.execute.RunningFlow;

/** Create a meandre autheticated proxy to access the web services
 *
 * @author Xavier Llor&agrave;
 * @author D. Searsmith
 */
public class MeandreProxy {

    //==============
    // Data Members
    //==============

    /** The logger for the bootstrapper */
    protected static Logger log = null;

    // Initializing the logger and its handlers
//    static {
//        log = Logger.getLogger(Bootstrapper.class.getName());
//        log.setLevel(Level.CONFIG);
//        log.addHandler(Bootstrapper.handler);
//    }

    /** The user name */
    private String sUserName;

    /** The password */
    private String sPassword;

    /** The password */
    private String sBaseURL;

    /** The password */
    private String sUPEncoding;

    /** Cached roles */
    private HashMap<String, Boolean> mapRoles;

    /** Cached repository */
    private QueryableRepository qrCached;

    /** Is the proxy ready? */
    private boolean bIsReady;

    /** Did the last call succeed */
    private boolean bWasCallOK;

    /** Creates a Meandre Proxy
     *
     * @param sUser The user of the proxy
     * @param sPasswd The password of the proxy
     * @param sURL The Meandre server URL
     */
    public MeandreProxy(String sUser, String sPasswd, String sURL) {
        this.sUserName = sUser;
        this.sPassword = sPasswd;
        this.sBaseURL = sURL;

        String sUserPassword = sUserName + ":" + sPassword;
        this.sUPEncoding = new sun.misc.BASE64Encoder().encode(sUserPassword.
                getBytes());

        // Force a first authetication for role caching
        this.bIsReady = null != getRoles();
        // Force the repository caching
        this.qrCached = getRepository();
    }

    /** Returns true if the proxy was successfully initialized; false otherwise.
     *
     * @return True is successfully initialized
     */
    public boolean isReady() {
        return bIsReady;
    }

    public String getBaseURL() {
        return this.sBaseURL;
    }

    public String getUPEncoding() {
        return this.sUPEncoding;
    }

    /** Returns true if the last call was completed successfully.
     *
     * @return True if everything when well. False otherwise
     */
    public boolean getCallOk() {
        return bWasCallOK;
    }

    /** Gets the user name.
     *
     * @return The user name
     */
    public String getName() {
        return sUserName;
    }

    /** Flushes the cached roles.
     *
     */
    public void flushRoles() {
        mapRoles = null;
    }


    /** Flushes the cached repository.
     *
     */
    public void flushRepository() {
        qrCached = null;
    }

    /** Return the list of running flows of this proxy.
     *
     * @return The array of running flows
     */
    @SuppressWarnings("unchecked")
            public RunningFlow[] getRunningFlows() {
        bWasCallOK = true;
        RunningFlow[] rfa = null;

        if (mapRoles != null) {
            String sLocations = executeGetRequest(sBaseURL +
                    "services/execute/list_running_flows.txt");
            if (sLocations == null) {
                bWasCallOK = false;
                return null;
            } else {
                // Parse and generate the sets
                String[] sa = sLocations.split("\n");
                int iMax = sa.length / 2;
                rfa = new RunningFlow[iMax];
                for (int i = 0; i < iMax; i++) {
                    rfa[i] = new RunningFlow(sa[i * 2].trim(),
                                             sa[i * 2 + 1].trim());
                }
            }
        }

        return rfa;
    }


    /** Return the roles for the user of this proxy.
     *
     * @return The set of granted role for the proxy user
     */
    public Map<String, Boolean> getRoles() {
        if (mapRoles == null) {
            String sRoles = executeGetRequest(sBaseURL +
                                              "services/about/user_roles.txt");
            if (sRoles == null) {
                return null;
            } else {
                // Parse and generate the sets
                String[] sa = sRoles.split("\n");
                mapRoles = new HashMap<String, Boolean>();
                for (String sRole : sa) {
                    mapRoles.put(sRole.trim(), true);
                }
            }
        }

        return mapRoles;
    }

    /** Gets the current repository.
     *
     * @return The cached queryable repository
     */
    public QueryableRepository getRepository() {
        if (this.qrCached == null) {
            // Caches the repository
            byte[] baResponse = executeGetRequestBytes(sBaseURL +
                    "services/repository/dump.nt");
            Model model = ModelFactory.createDefaultModel();
            model.read(new ByteArrayInputStream(baResponse), null, "N-TRIPLE");
            this.qrCached = new RepositoryImpl(model);
        }

        return this.qrCached;
    }


    /** Gets the public repository.
     *
     * @return The public queryable repository
     */
    public QueryableRepository getPublicRepository() {
        // The public repository
        byte[] baResponse = executeGetRequestBytes(sBaseURL +
                "public/services/repository.nt");
        Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(baResponse), null, "N-TRIPLE");
        return new RepositoryImpl(model);
    }

    /** Forces the repository to be recached.
     *
     * @return The recached repository
     */
    public QueryableRepository getRepositoryFlush() {
        this.qrCached = null;
        return getRepository();
    }

    /** Return the list of locations for the user of this proxy.
     *
     * @return The array of location for this user
     */
    @SuppressWarnings("unchecked")
            public LocationBean[] getLocations() {
        bWasCallOK = true;
        LocationBean[] loca = null;

        if (mapRoles != null) {
            String sLocations = executeGetRequest(sBaseURL +
                                                  "services/locations/list.txt");
            if (sLocations == null) {
                bWasCallOK = false;
                return null;
            } else {
                // Parse and generate the sets
                String[] sa = sLocations.split("\n");
                int iMax = sa.length / 2;
                loca = new LocationBean[iMax];
                for (int i = 0; i < iMax; i++) {
                    loca[i] = new LocationBean(sa[i * 2].trim(),
                                               sa[i * 2 + 1].trim());
                }
            }
        }

        return loca;
    }

    /** Gets the result of attempting to regenerate the user repository.
     *
     * @return The result of the process
     */
    public String getRegenerate() {
        bWasCallOK = true;
        String sRes = null;

        if (mapRoles != null) {
            sRes = executeGetRequest(sBaseURL +
                                     "services/repository/regenerate.txt");
            if (sRes == null) {
                bWasCallOK = false;
            }
            getRepositoryFlush();
        }

        return sRes;
    }


    /** Gets the result of attempting to add a new location to the user repository.
     *
     * @param sLocation The URL location
     * @param sDescription The location description
     * @return The result of the process. Returns the URL if everything when well,
     *         null otherwise
     */
    public String getAddLocation(String sLocation, String sDescription) {
        bWasCallOK = true;
        String sRes = null;

        if (mapRoles != null) {
            String sParams = "location=" + sLocation + "&description=" +
                             sDescription;
            try {
                sParams = "location=" + URLEncoder.encode(sLocation, "UTF-8") +
                          "&description=" +
                          URLEncoder.encode(sDescription, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unknow encoding problem: " + e);
            }
            sRes = executeGetRequest(sBaseURL + "services/locations/add.txt?" +
                                     sParams);
            if (sRes == null) {
                bWasCallOK = false;
            }
        }

        return sRes;
    }

    /** Gets the result of attempting to remove a location to the user repository.
     *
     * @param sLocation The URL location
     * @return The result of the process. Returns the URL if everything when well,
     *         empty otherwise
     */
    public String getRemoveLocation(String sLocation) {
        bWasCallOK = true;
        String sRes = null;

        if (mapRoles != null) {
            String sParams = "location=" + sLocation;
            try {
                sParams = "location=" + URLEncoder.encode(sLocation, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unknow encoding problem: " + e);
            }
            sRes = executeGetRequest(sBaseURL +
                                     "services/locations/remove.txt?" + sParams);
            if (sRes == null) {
                bWasCallOK = false;
            }
        }

        return sRes;
    }

    /** Runs a flow and streams the output.
     *
     * @param sURI The flow to execute
     * @param sFormat The format of the output
     * @param jw The writer to use
     */
    public void runFlowInteractively(String sURI, String sFormat, JspWriter jw) {
        String sRequest = sBaseURL + "services/execute/flow." + sFormat +
                          "?uri=" + sURI;
        executeSteamableGetRequest(sRequest, jw);
    }

    /** Gets the result of attempting to publish a URI from the user repository.
     *
     * @param sURI The URI to publish
     * @return The result of the process. Returns the URL if everything when well,
     *         empty otherwise
     */
    public String getPublish(String sURI) {
        bWasCallOK = true;
        String sRes = null;

        if (mapRoles != null) {
            String sParams = "uri=" + sURI;
            try {
                sParams = "uri=" + URLEncoder.encode(sURI, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unknow encoding problem: " + e);
            }
            sRes = executeGetRequest(sBaseURL + "services/publish/publish.txt?" +
                                     sParams);
            if (sRes == null) {
                bWasCallOK = false;
            }
        }

        return sRes;
    }


    /** Gets the result of attempting to unpublish a URI from the user repository.
     *
     * @param sURI The URI to publish
     * @return The result of the process. Returns the URL if everything when well,
     *         empty otherwise
     */
    public String getUnpublish(String sURI) {
        bWasCallOK = true;
        String sRes = null;

        if (mapRoles != null) {
            String sParams = "uri=" + sURI;
            try {
                sParams = "uri=" + URLEncoder.encode(sURI, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unknow encoding problem: " + e);
            }
            sRes = executeGetRequest(sBaseURL +
                                     "services/publish/unpublish.txt?" +
                                     sParams);
            if (sRes == null) {
                bWasCallOK = false;
            }
        }

        return sRes;
    }

    /** Gets the result of attempting to remove a URI from the user repository.
     *
     * @param sURI The URI to remove
     * @return The result of the process. Returns the URL if everything when well,
     *         empty otherwise
     */
    public String getRemove(String sURI) {
        bWasCallOK = true;
        String sRes = null;

        if (mapRoles != null) {
            String sParams = "uri=" + sURI;
            try {
                sParams = "uri=" + URLEncoder.encode(sURI, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unknow encoding problem: " + e);
            }
            sRes = executeGetRequest(sBaseURL +
                                     "services/repository/remove.txt?" +
                                     sParams);
            if (sRes == null) {
                bWasCallOK = false;
            } else {
                flushRepository();
            }
        }

        return sRes;
    }

    /** Does an authenticated get request against the provided URL. Returns null if the
     * request failed
     *
     * @param sURL The URL
     * @return The content
     */
    private String executeGetRequest(String sURL) {
        try {
            return new String(executeGetRequestBytes(sURL), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

    }

    /** Does an authenticated get request against the provided URL. Returns null if the
     * request failed
     *
     * @param sURL The URL
     * @return The content bytes
     */
    private byte[] executeGetRequestBytes(String sURL) {
        try {
            // Create the URL
            URL url = new URL(sURL);

            // Create and authenticated connection
            URLConnection uc = url.openConnection();
            uc.setRequestProperty("Authorization", "Basic " + sUPEncoding);

            // Pull the stuff out of the Meandre server
            InputStream is = (InputStream) uc.getInputStream();
            ArrayList<Byte> lstBytes = new ArrayList<Byte>();
            int iTmp;
            while ((iTmp = is.read()) != -1) {
                lstBytes.add((byte) iTmp);
            }

            is.close();

            // Get the returned text
            byte[] ba = new byte[lstBytes.size()];
            int i = 0;
            for (byte b : lstBytes) {
                ba[i++] = b;
            }
            return ba;
        } catch (IOException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /** Does an authenticated get request against the provided URL and stream back
     * the contents
     *
     * @param sURL The URL to request
     * @param jw The outpt writter
     */
    private void executeSteamableGetRequest(String sURL, JspWriter jw) {
        try {
            // Create the URL
            URL url = new URL(sURL);

            // Create and authenticated connection
            URLConnection uc = url.openConnection();
            uc.setRequestProperty("Authorization", "Basic " + sUPEncoding);

            // Pull the stuff out of the Meandre server
            InputStream is = (InputStream) uc.getInputStream();
            int iTmp;
            while ((iTmp = is.read()) != -1) {
                jw.write(iTmp);
            }

            is.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public String executePost(String sURL, Map<String, String> data) {
        String sbuff = "";

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(sBaseURL + sURL);

        try {
            post.addRequestHeader("Authorization",
                                  "Basic " + sUPEncoding);
            for (String key : data.keySet()) {
                post.addParameter(key,
                                  new String(data.get(key).trim().getBytes(), "UTF-8"));
            }

            // execute the post
            int status = client.executeMethod(post);

            if (status != -1) {
                // print the status and response
                sbuff = post.getResponseBodyAsString();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // release any connection resources used by the method
            post.releaseConnection();
        }
        return sbuff;
    }

    /** Runs a flow and streams the output.
     *
     * @param sURI The flow to execute
     * @param sFormat The format of the output
     * @param jw The writer to use
     */
    public void runWBFlowInteractively(String sURI, String sFormat, Vector v) {
        String sRequest = sBaseURL + "services/execute/flow." + sFormat +
                          "?uri=" + sURI;
        executeWBSteamableGetRequest(sRequest, v);
    }

    /** Does an authenticated get request against the provided URL and stream back
     * the contents
     *
     * @param sURL The URL to request
     * @param jw The outpt writter
     */
    private void executeWBSteamableGetRequest(String sURL, Vector v) {
        try {
            // Create the URL
            URL url = new URL(sURL);

            // Create and authenticated connection
            URLConnection uc = url.openConnection();
            uc.setRequestProperty("Authorization", "Basic " + sUPEncoding);

            // Pull the stuff out of the Meandre server
            InputStream is = (InputStream) uc.getInputStream();
            int iTmp;
            char[] iarr = new char[40];
            int cnt = 0;
            while ((iTmp = is.read()) != -1) {
                iarr[cnt++] = (char) iTmp;
                if (cnt == 40) {
                    v.add(iarr);
                    iarr = new char[40];
                    cnt = 0;
                }
            }
            if (cnt > 0) {
                char[] ifin = new char[cnt];
                System.arraycopy(iarr, 0, ifin, 0, cnt);
                v.add(ifin);
            }
            char[] end = new char[1];
            end[0] = '`';
            v.add(end);
            is.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

}
