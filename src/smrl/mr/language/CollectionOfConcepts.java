package smrl.mr.language;

import java.util.ArrayList;

public class CollectionOfConcepts {
	public String id;	//this id should be an ID, a Name or an XPATH of the specific parent element
	public ArrayList<String> concepts;
	
	public CollectionOfConcepts() {
		this.id = null;
		this.concepts = new ArrayList<String>();
	}
	
	public boolean isEmpty() {
		if(this.id==null && 
				(this.concepts ==null || this.concepts.isEmpty())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CollectionOfConcepts)) {
			return false;
		}
		
		CollectionOfConcepts that = (CollectionOfConcepts) obj;
		
		if(this.isEmpty() && ((CollectionOfConcepts)obj).isEmpty()) {
			return true;
		}
		
		if(this.concepts!=null && that.concepts!=null &&
				this.concepts.size()==that.concepts.size() &&
				this.concepts.containsAll(that.concepts)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return id + ":" + concepts.toString();
	}
	
	
	
	
}
