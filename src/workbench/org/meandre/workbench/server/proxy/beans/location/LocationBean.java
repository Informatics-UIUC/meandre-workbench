package org.meandre.workbench.server.proxy.beans.location;

/** This bean wraps the location information
 *
 * @author Xavier Llor&agrave;
 *
 */
public class LocationBean {

	/** The location URL */
	private String sLocation;

	/** The location description */
	private String sDescription;

	/** Creates a bean and sets the location and description.
	 *
	 * @param sLocation The location URL
	 * @param sDescription The description
	 */
	public LocationBean ( String sLocation, String sDescription ) {
		setLocation(sLocation);
		setDescription(sDescription);
	}

	/** Sets the URL location
	 *
	 * @param sLocation the sLocation to set
	 */
	public void setLocation(String sLocation) {
		this.sLocation = sLocation;
	}

	/** Gets the URL location
	 *
	 * @return the sLocation
	 */
	public String getLocation() {
		return sLocation;
	}

	/** Sets the location description
	 *
	 * @param description the sDescription to set
	 */
	public void setDescription(String sDescription) {
		this.sDescription = sDescription;
	}

	/** Gets the location description
	 *
	 * @return the description
	 */
	public String getDescription() {
		return sDescription;
	}

}
