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
import org.meandre.workbench.server.proxy.MeandreProxy;
import org.meandre.workbench.server.WBRepositoryQueryImpl;
import org.apache.log4j.Logger;

/**
 * Used for CROSS DOMAIN AJAX
 * 
 * @author Amit Kumar
 * @author D. Searsmith Created on Nov 2, 2007 1:57:57 AM
 * 
 */
public class WBHttpProxyServlet extends HttpServlet {

	// ==============
	// Data Members
	// ==============

	/** The logger for the bootstrapper */
	private static Logger _logger = Logger.getLogger(WBHttpProxyServlet.class);

	// Initializing the logger and its handlers
	static {
		_logger = Logger.getLogger(WBHttpProxyServlet.class.getName());
		_logger.setLevel(org.apache.log4j.Level.DEBUG);
	}

	// ================
	// Public Methods
	// ================

	@Override
	public void init() throws ServletException {

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		_logger.debug("doGet() called.");
		InputStream is = null;
		ServletOutputStream out = null;

		String sid = req.getParameter(Controller.s_GET_PARAM_SID_KEY);
		if (sid == null) {
			_logger.error("SID is null!");
			res.sendError(res.SC_METHOD_NOT_ALLOWED, "SID is null!");
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
			res.sendError(res.SC_METHOD_NOT_ALLOWED, "Method is null.");
		}

		if (method.equals(Controller.s_PROXY_GET_METHOD_WEBUI)) {

			// key
			// site
			String target = req.getParameter(Controller.s_PROXY_TARGET_KEY);
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

				if (true)/*(uc.getContentType().equals("text/html"))*/ {
					// make replacement(s) but only if this is html
					int pos = -1;
					int ptr = 0;

					while ((pos = sbuf.indexOf("/http", ptr)) != -1) {
						String sub = "http://" + req.getLocalAddr() + ":"
								+ req.getLocalPort()
								+ Controller.s_PROXY_SERVLET_PATH + "?"
								+ Controller.s_GET_PARAM_SID_KEY + "="
								+ URLEncoder.encode(sid, "UTF-8") + "&"
								+ Controller.s_PROXY_GET_METHOD_KEY + "="
								+ Controller.s_PROXY_GET_METHOD_WEBUI + "&"
								+ Controller.s_PROXY_TARGET_KEY + "="
								+ URLEncoder.encode(target, "UTF-8");

						sbuf.replace(pos, pos + 1, sub);
						ptr = pos + sub.length();
						pos = -1;
					}
				}

				out.write(sbuf.toString().getBytes());
				out.flush();
			} catch (MalformedURLException e) {
				// log.info("Error in the url: " + e.getMessage());
				res.setStatus(404);
			} catch (IOException e) {
				// log.info("IOException: " + e.getMessage());
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
			res.sendError(res.SC_METHOD_NOT_ALLOWED, "Method is unknown.");
		}
		_logger.debug("done proxy call.");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		_logger.debug("doPost() called.");

		if ((!(request.getContentType().equals("multipart/form-data")))
				&& (!(request.getContentType().equals("multipart/mixed")))) {
			doGet(request, response);
		}

		MeandreProxy proxy = null;
		ServletFileUpload sfu = null;
		List fparts = new ArrayList();
		String url = null;
		String method = null;
		String sid = null;
		try {
			sfu = new ServletFileUpload(new DiskFileItemFactory());

			for (Iterator itty = sfu.parseRequest(request).iterator(); itty
					.hasNext();) {
				DiskFileItem fi = (DiskFileItem) itty.next();
				fparts.add(fi);
				_logger.debug("Key: " + fi.getFieldName());

				if (fi.getFieldName().equals(Controller.s_GET_PARAM_SID_KEY)) {
					sid = fi.getString();
					if (sid == null) {
						_logger.error("SID is null!");
						response.sendError(response.SC_METHOD_NOT_ALLOWED,
								"SID is null!");
						return;
					}
					proxy = (MeandreProxy) WBRepositoryQueryImpl.getProxy(sid);
				}
				if (fi.getFieldName().equals(Controller.s_PROXY_TARGET_KEY)) {
					String s = fi.getString();
					if (s == null) {
						_logger.error("URL is null!");
						response.sendError(response.SC_METHOD_NOT_ALLOWED,
								"URL is null!");
						return;
					}
					url = s;
				}

				if (fi.getFieldName()
						.equals(Controller.s_PROXY_POST_METHOD_KEY)) {
					String s = fi.getString();
					if (s == null) {
						_logger.error("Post method is null!");
						response.sendError(response.SC_METHOD_NOT_ALLOWED,
								"Post method is null!");
						return;
					}
					method = s;
				}

			}

		} catch (Exception e) {
			_logger.error(e.getMessage());
			e.printStackTrace();
		}

		if (method == null) {
			response.sendError(response.SC_METHOD_NOT_ALLOWED,
					"Post method not found");
		}
		if (sid == null) {
			response.sendError(response.SC_METHOD_NOT_ALLOWED, "SID not found");
		}
		if (proxy == null) {
			response.sendError(response.SC_METHOD_NOT_ALLOWED,
					"Session ID is no longer valid.");
		}

		/* BRANCH ON METHOD */

		if (method.equals(Controller.s_PROXY_POST_METHOD_REPO_UPLOAD)) {

			_logger.info("\n\n=======================");
			_logger.info(" UPLOADING COMPONENTS");
			_logger.info("=======================\n\n");

			String sbuff = "";

			HttpClient client = new HttpClient();
			_logger.debug("Base URL: " + proxy.getBaseURL());
			PostMethod post = new PostMethod(proxy.getBaseURL() + url);

			try {
				post.addRequestHeader("Authorization", "Basic "
						+ proxy.getUPEncoding());
				Part[] parts = null;
				ArrayList lparts = new ArrayList();
				sfu = new ServletFileUpload(new DiskFileItemFactory());
				_logger.debug("Size: " + sfu.parseRequest(request).size());
				for (int i = 0, n = fparts.size(); i < n; i++) {
					DiskFileItem fi = (DiskFileItem) fparts.get(i);

					String key = fi.getFieldName();
					_logger.debug("Key: " + key + " Store: "
							+ fi.getStoreLocation() + " Name: " + fi.getName());
					if (key.trim().equals(
							Controller.s_PROXY_POST_JAR_FIELDS_KEY)
							|| key.trim().equals(
									Controller.s_PROXY_POST_REPO_FIELDS_KEY)) {
						if (fi.getSize() > 0) {
							if (fi.isInMemory()) {
								fi.write(fi.getStoreLocation());
							}
							FilePartSource fps = new FilePartSource(fi
									.getStoreLocation());
							_logger.debug("Length: " + fps.getLength());

							String addval = null;
							if (key.trim().equals(
									Controller.s_PROXY_POST_JAR_FIELDS_KEY)) {
								addval = "context";
							}
							if (key.trim().equals(
									Controller.s_PROXY_POST_REPO_FIELDS_KEY)) {
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
				post.setRequestEntity(new MultipartRequestEntity(parts, post
						.getParams()));

				// execute the post
				int status = client.executeMethod(post);

				if (status != -1) {
					// print the status and response
					sbuff = post.getResponseBodyAsString();
				}
			} catch (Exception e) {
				_logger.error(e.getMessage());
			} finally {
				// release any connection resources used by the method
				post.releaseConnection();
			}
			proxy.flushRepository();
			int numbytes = sbuff.getBytes().length;
			String resp = "Proxy post completed successfully. Returned "
					+ numbytes + " bytes.";
			response.getOutputStream().write(resp.getBytes());
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} else {
			response.sendError(response.SC_METHOD_NOT_ALLOWED,
					"Unknown proxy post method.");
		}

	}

}
