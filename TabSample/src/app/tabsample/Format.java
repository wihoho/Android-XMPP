package app.tabsample;

/**
 * Represents a format in the "fmt_list" parameter 
 * Currently, only id is used
 *
 */
public class Format {

	protected int mId;
	
	/**
	 * Construct this object from one of the strings in the "fmt_list" parameter
	 * @param pFormatString one of the comma separated strings in the "fmt_list" parameter
	 */
	public Format(String pFormatString){
		String lFormatVars[] = pFormatString.split("/");
		mId = Integer.parseInt(lFormatVars[0]);
	}
	/**
	 * Construct this object using a format id
	 * @param pId id of this format
	 */
	public Format(int pId){
		this.mId = pId;
	}
	
	/**
	 * Retrieve the id of this format
	 * @return the id
	 */
	public int getId(){
		return mId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object pObject) {
		if(!(pObject instanceof Format)){
			return false;
		}
		return ((Format)pObject).mId == mId;
	}
}
