package org.meandre.workbench.auxillary;

/*
 * @(#) HttpProxyServlet.java @VERSION@
 *
 * @author Amit Kumar
 * @modified D. Searsmith
 *
 */

//==============
// Java Imports
//==============

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;

//===============
// Other Imports
//===============

import org.meandre.workbench.server.proxy.MeandreProxy;
import org.meandre.workbench.server.*;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.meandre.workbench.client.Controller;


/**Used for CROSS DOMAIN AJAX
 *
 * @author Amit Kumar
 * @author D. Searsmith
 * Created on Nov 2, 2007 1:57:57 AM
 *
 */
public class WBHttpProxyServlet extends HttpServlet {

    //==============
    // Data Members
    //==============

    /** The logger for the bootstrapper */
    private static Logger log = null;

    /** The basic handler for all the loggers */
    public static Handler handler = null;

    // Initializing the logger and its handlers
//    static {
//        log = Logger.getLogger(WBHttpProxyServlet.class.getName());
//        log.setLevel(Level.FINEST);
//        try {
//            log.addHandler(handler = new FileHandler("proxy-plugin-log.xml"));
//        } catch (SecurityException e) {
//            System.err.println("Could not initialize proxy-plugin-log.xml");
//            System.exit(1);
//        } catch (IOException e) {
//            System.err.println("Could not initialize proxy-plugin-log.xml");
//            System.exit(1);
//        }
//
//        handler.setLevel(Level.FINEST);
//    }


    //================
    // Public Methods
    //================

    public void init() throws ServletException {

    }


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws
            ServletException, IOException {
        InputStream is = null;
        ServletOutputStream out = null;

        String sid = req.getParameter(Controller.s_GET_PARAM_SID_KEY);
        if (sid == null) {
            System.out.println("SID is null!");
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                          "SID is null!");
            return;
        }

        MeandreProxy proxy = null;
        proxy = (MeandreProxy) WBRepositoryQueryImpl.getProxy(sid);
        if (proxy == null) {
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                          "Session ID is no longer valid.");
        }

        String method = req.getParameter(Controller.s_PROXY_GET_METHOD_KEY);

        if (method == null) {
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                          "Method is null.");
        }

        if (method.equals(Controller.s_PROXY_GET_METHOD_WEBUI)) {

            //key
            //site
            String target = req.getParameter(Controller.s_PROXY_TARGET_KEY);
            //System.out.println("Calling: " + target + " by " + req.getRemoteAddr());
            if (target == null) {
                res.setStatus(404);
                return;
            }
            try {
                URL url = new URL(target);
                URLConnection uc = url.openConnection();
                res.setContentType(uc.getContentType());
                is = uc.getInputStream();
                out = res.getOutputStream();
                byte[] buf = new byte[4096];
                int bytesRead;
                StringBuffer sbuf = new StringBuffer("");
                while ((bytesRead = is.read(buf)) != -1) {
                    byte[] readbytes = new byte[bytesRead];
                    System.arraycopy(buf, 0, readbytes, 0, bytesRead);
                    sbuf.append(new String(readbytes));
                }
                //make replacement(s)
                int pos = -1;
                int ptr = 0;

                while ((pos = sbuf.indexOf("/http", ptr)) != -1) {
                    String sub = "http://" + req.getLocalAddr() + ":"
                                 + req.getLocalPort()
                                 + Controller.s_PROXY_SERVLET_PATH + "?"
                                 + Controller.s_GET_PARAM_SID_KEY + "="
                                 + URLEncoder.encode(sid, "UTF-8")
                                 + "&" + Controller.s_PROXY_GET_METHOD_KEY + "="
                                 + Controller.s_PROXY_GET_METHOD_WEBUI
                                 + "&" + Controller.s_PROXY_TARGET_KEY + "=" +
                                 URLEncoder.encode(target, "UTF-8");

                    //System.out.println("Subbing: " + sub + " for: " + sbuf.substring(pos, pos+1));
                    sbuf.replace(pos, pos + 1, sub);
                    ptr = pos + sub.length();
                    pos = -1;
                }

                out.write(sbuf.toString().getBytes());
                out.flush();
            } catch (MalformedURLException e) {
                //log.info("Error in the url: " + e.getMessage());
                res.setStatus(404);
            } catch (IOException e) {
                //log.info("IOException: " + e.getMessage());
                res.setStatus(404);
            } finally {
                if (is != null) {
                    is.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        } else {
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                          "Method is unknown.");
        }
        //log.info("done proxy call.");
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException,
            IOException {

        MeandreProxy proxy = null;
        ServletFileUpload sfu = null;
        List fparts = new ArrayList();
        String url = null;
        String method = null;
        String sid = null;
        try {
            sfu = new ServletFileUpload(new DiskFileItemFactory());

            for (Iterator itty = sfu.parseRequest(request).iterator();
                                 itty.hasNext(); ) {
                DiskFileItem fi = (DiskFileItem) itty.next();
                fparts.add(fi);
                System.out.println("Key: " + fi.getFieldName());

                if (fi.getFieldName().equals(Controller.s_GET_PARAM_SID_KEY)) {
                    sid = fi.getString();
                    if (sid == null) {
                        System.out.println("SID is null!");
                        response.sendError(response.SC_METHOD_NOT_ALLOWED,
                                           "SID is null!");
                        return;
                    }
                    proxy = (MeandreProxy) WBRepositoryQueryImpl.getProxy(sid);
                }
                if (fi.getFieldName().equals(Controller.s_PROXY_TARGET_KEY)) {
                    String s = fi.getString();
                    if (s == null) {
                        System.out.println("URL is null!");
                        response.sendError(response.SC_METHOD_NOT_ALLOWED,
                                           "URL is null!");
                        return;
                    }
                    url = s;
                }

                if (fi.getFieldName().equals(Controller.s_PROXY_POST_METHOD_KEY)) {
                    String s = fi.getString();
                    if (s == null) {
                        System.out.println("Post method is null!");
                        response.sendError(response.SC_METHOD_NOT_ALLOWED,
                                           "Post method is null!");
                        return;
                    }
                    method = s;
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        if (method == null) {
            response.sendError(response.SC_METHOD_NOT_ALLOWED,
                               "Post method not found");
        }
        if (sid == null) {
            response.sendError(response.SC_METHOD_NOT_ALLOWED,
                               "SID not found");
        }
        if (proxy == null) {
            response.sendError(response.SC_METHOD_NOT_ALLOWED,
                               "Session ID is no longer valid.");
        }

        /* BRANCH ON METHOD */

        if (method.equals(Controller.s_PROXY_POST_METHOD_REPO_UPLOAD)) {

            String sbuff = "";

            HttpClient client = new HttpClient();
            System.out.println("Base URL: " + proxy.getBaseURL());
            PostMethod post = new PostMethod(proxy.getBaseURL() + url);

            try {
                post.addRequestHeader("Authorization",
                                      "Basic " + proxy.getUPEncoding());
                Part[] parts = null;
                ArrayList lparts = new ArrayList();
                sfu = new ServletFileUpload(new DiskFileItemFactory());
                System.out.println("Size: " + sfu.parseRequest(request).size());
                for (int i = 0, n = fparts.size(); i < n; i++) {
                    DiskFileItem fi = (DiskFileItem) fparts.get(i);

                    String key = fi.getFieldName();
                    System.out.println("Key: " + key + " Store: " +
                                       fi.getStoreLocation() + " Name: " +
                                       fi.getName());
                    if (key.trim().equals(Controller.s_PROXY_POST_JAR_FIELDS_KEY) ||
                        key.trim().equals(Controller.s_PROXY_POST_REPO_FIELDS_KEY)) {
                        if (fi.getSize() > 0) {
                            if (fi.isInMemory()) {
                                fi.write(fi.getStoreLocation());
                            }
                            FilePartSource fps = new FilePartSource(fi.
                                    getStoreLocation());
                            System.out.println("Length: " + fps.getLength());

                            String addval = null;
                            if (key.trim().equals(Controller.s_PROXY_POST_JAR_FIELDS_KEY)){
                                addval = "jar";
                            }
                            if (key.trim().equals(Controller.s_PROXY_POST_REPO_FIELDS_KEY)) {
                                addval = "repository";
                            }
                            lparts.add(new FilePart(addval, fps));
                        }
                    }
                }
                parts = new Part[lparts.size()];
                for (int i = 0, n = lparts.size(); i < n; i++) {
                    parts[i] = (Part) lparts.get(i);
                }
                post.setRequestEntity(
                        new MultipartRequestEntity(parts, post.getParams())
                        );

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
            proxy.flushRepository();
            String resp = "Proxy post completed successfully.";
            response.getOutputStream().write(resp.getBytes());
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } else if (method.equals(Controller.s_PROXY_POST_METHOD_PEAR_UPLOAD)) {



        } else {
            response.sendError(response.SC_METHOD_NOT_ALLOWED,
                               "Unknown proxy post method.");
        }

    }
}
