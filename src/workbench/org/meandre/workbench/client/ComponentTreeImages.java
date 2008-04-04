package org.meandre.workbench.client;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * <p>Title: Component Tree Images</p>
 *
 * <p>Description: A class that implements the image bundle for the component
 * tree in the meandre workbench application.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA, Automated Learning Group</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */
/**
   * Allows us to override Tree default images. If we don't override one of the
   * methods, the default will be used.
   */
  interface ComponentTreeImages extends TreeImages {

    /**
     * @gwt.resource comp-tree-folder-open.png
     */
    AbstractImagePrototype treeOpen();

    /**
     * @gwt.resource comp-tree-folder-closed.png
     */
    AbstractImagePrototype treeClosed();

  }
