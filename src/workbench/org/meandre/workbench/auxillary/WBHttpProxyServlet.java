package org.meandre.workbench.auxillary;

/*
 * @(#) HttpProxyServlet.java @VERSION@
 *
 * Copyright (c) 2007+ Amit Kumar
 *
 * The software is released under GNU GPL, Please
 * read License.txt
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
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

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
import java.net.URLEncoder;
import org.meandre.workbench.client.Controller;


/**Used for CROSS DOMAIN AJAX
 *
 * @author Amit Kumar
 * @author D. Searsmith
 * Created on Nov 2, 2007 1:57:57 AM
 *
 */
public class WBHttpProxyServlet extends HttpServlet {
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

    public void init() throws ServletException {

    }


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws
            ServletException, IOException {
        InputStream is = null;
        ServletOutputStream out = null;

        //authenticate
        Map proxies = (Map) req.getSession().getAttribute(
                WBRepositoryQueryImpl.s_PROXIES_KEY);
        if (proxies == null) {
            System.out.println("Proxies is null.");
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                               "Proxies is null.");
            return;
        }
        String sid = req.getParameter("sid");
        if (sid == null) {
            System.out.println("SID is null!");
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                               "SID is null!");
            return;
        }
        MeandreProxy proxy = null;
        proxy = (MeandreProxy) proxies.get(sid);
        if (proxy == null) {
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                               "Session ID is no longer valid.");
        }


        String method = req.getParameter(Controller.s_METHOD_KEY);

        if (method == null){
            res.sendError(res.SC_METHOD_NOT_ALLOWED,
                               "Method is null.");
        }

        if (method.equals(Controller.s_METHOD_WEBUI)){

            //key
            //site
            String target = req.getParameter("target");
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
                                 + "/meandre_core_proxy?sid="
                                 + URLEncoder.encode(sid, "UTF-8")
                                 + "&" + Controller.s_METHOD_KEY + "="
                                 + Controller.s_METHOD_WEBUI
                                 + "&target=" + URLEncoder.encode(target, "UTF-8");

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

        Map proxies = (Map) request.getSession().getAttribute(
                WBRepositoryQueryImpl.s_PROXIES_KEY);
        if (proxies == null) {
            System.out.println("Proxies is null.");
            response.sendError(response.SC_METHOD_NOT_ALLOWED,
                               "Proxies is null.");
            return;
        }

        MeandreProxy proxy = null;
        ServletFileUpload sfu = null;
        List fparts = new ArrayList();
        String url = null;
        try {
            sfu = new ServletFileUpload(new DiskFileItemFactory());

            for (Iterator itty = sfu.parseRequest(request).iterator();
                                 itty.hasNext(); ) {
                DiskFileItem fi = (DiskFileItem) itty.next();
                fparts.add(fi);
                System.out.println("Key: " + fi.getFieldName());


                if (fi.getFieldName().equals("sid")) {
                    String sid = fi.getString();
                    if (sid == null) {
                        System.out.println("SID is null!");
                        response.sendError(response.SC_METHOD_NOT_ALLOWED,
                                           "SID is null!");
                        return;
                    }
                    proxy = (MeandreProxy) proxies.get(sid);
                }
                if (fi.getFieldName().equals("url")) {
                    String s = fi.getString();
                    if (s == null) {
                        System.out.println("URL is null!");
                        response.sendError(response.SC_METHOD_NOT_ALLOWED,
                                           "URL is null!");
                        return;
                    }
                    url = s;
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        if (proxy == null) {
            response.sendError(response.SC_METHOD_NOT_ALLOWED,
                               "Session ID is no longer valid.");
        } else {

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
                    System.out.println("Key: " + key + " Store: " + fi.getStoreLocation() + " Name: " + fi.getName());
                    if (key.trim().equals("jar") ||
                        key.trim().equals("repository")) {
                        if (fi.getSize() > 0) {
                            if (fi.isInMemory()) {
                                fi.write(fi.getStoreLocation());
                            }
                            FilePartSource fps = new FilePartSource(fi.
                                    getStoreLocation());
                            System.out.println("Length: " + fps.getLength());
                            lparts.add(new FilePart(key, fps));
                        }
                    }
                }
                parts = new Part[lparts.size()];
                for (int i = 0, n = lparts.size(); i < n; i++){
                    parts[i] = (Part)lparts.get(i);
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
        }

    }
}
