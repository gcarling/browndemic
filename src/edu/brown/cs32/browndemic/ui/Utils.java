package edu.brown.cs32.browndemic.ui;

import java.awt.Container;

/**
 * Contains various static utilities for the UI.
 * @author Ben
 *
 */
public class Utils {
	
	private Utils() { }
	
	/**
	 * Gets the parent BrowndemicFrame of a Container.
	 * If there is no BrowndemicFrame as a parent then this
	 * method will cause a NullPointerException.
	 * 
	 * @param c The Container to get the parent of.
	 * @return The BrowndemicFrame that is the parent of c.
	 */
	public static BrowndemicFrame getParentFrame(Container c) {
		if (!(c instanceof BrowndemicFrame)) {
			return getParentFrame(c.getParent());
		}
		return (BrowndemicFrame) c;
	}
}