package org.seasr.meandre.workbench.bootstrap.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
    public static void copyFileFromStream(InputStream in, File out) throws Exception {
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = in.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (in != null) in.close();
            if (fos != null) fos.close();
        }
      }
}
