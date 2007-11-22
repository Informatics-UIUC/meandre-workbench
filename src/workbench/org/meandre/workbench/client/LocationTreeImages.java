package org.meandre.workbench.client;

import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
/**
   * Allows us to override Tree default images. If we don't override one of the
   * methods, the default will be used.
   */
  interface LocationTreeImages extends TreeImages {

    /**
     * @gwt.resource comp-tree-folder-open.png
     */
    AbstractImagePrototype treeOpen();

    /**
     * @gwt.resource comp-tree-folder-closed.png
     */
    AbstractImagePrototype treeClosed();

    /**
     * @gwt.resource flow-tree-leaf.png
     */
    AbstractImagePrototype treeLeaf();

  }
