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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FileDownloadServlet extends HttpServlet {

    private static final long serialVersionUID = -597734770217879997L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String flowURI = req.getParameter("uri");
        String flowName = req.getParameter("name");

        HttpSession session = req.getSession();
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            resp.flushBuffer();
            return;
        }

        Object exportData = session.getAttribute(flowURI);
        if (exportData == null) {
            resp.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            resp.flushBuffer();
            return;
        }

        String fileName = null;
        String contentType = null;
        int contentLength = -1;
        InputStream dataStream = null;

        if (exportData instanceof String) {
            // ZigZag
            byte[] scriptData = ((String)exportData).getBytes();
            dataStream = new ByteArrayInputStream(scriptData);
            fileName = flowName + ".zz";
            contentType = "application/x-zigzag";
            contentLength = scriptData.length;
        }

        else

        if (exportData instanceof File) {
            // MAU
            File mauFile = (File)exportData;
            dataStream = new FileInputStream(mauFile);
            fileName = flowName + ".mau";
            contentType = "application/x-mau";
            contentLength = (int) mauFile.length();
        }

        fileName = fileName.replaceAll(" ", "_");

        ServletOutputStream outputStream = resp.getOutputStream();
        resp.setContentType(contentType);
        resp.setContentLength(contentLength);
        resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        resp.setHeader("Pragma", "public");
        resp.setHeader("Cache-Control", "must-revalidate");

        int nRead;
        byte[] buffer = new byte[1024];
        while ((nRead = dataStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, nRead);

        session.removeAttribute(flowURI);

        if (exportData instanceof File)
            ((File)exportData).delete();

        resp.flushBuffer();
    }

}
