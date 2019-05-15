package ase2019.mr.language;

import java.util.ArrayList;
import java.util.List;

public class MRData implements Cloneable {

	String id;
	List<MRData> reassignments = new ArrayList<MRData>();
	boolean alreadyUsedInRHS = false;
	
	public void setAlreadyUsedInRHS() {
		alreadyUsedInRHS = true;
	}
	
	public MRData clone() throws CloneNotSupportedException{
		MRData cloned = (MRData) super.clone();
		cloned.reassignments = new ArrayList<>();
		
		return cloned;
	}

	public void addReassignment(MRData rhs) {
		reassignments.add(rhs);
	}

	public boolean isReassignable() {
		if ( alreadyUsedInRHS ) {
			return false;
		}
		
		return reassignments.size() == 0;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	
}
