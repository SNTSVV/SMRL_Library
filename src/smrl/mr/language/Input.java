package smrl.mr.language;

import java.util.List;

import com.google.gson.JsonArray;

import smrl.mr.crawljax.Account;

public abstract class Input extends MRData implements Cloneable {
	
	public Input() {}
	
	public Input( List<Action> a ) {}

	public abstract List<Action> actions();

	public abstract void copyActionTo(int x, int y);
	
	public abstract void addAction(int pos, Action action);
	
	public abstract void addAction(Action action);
	
	public abstract int indexOf(Action action);

	public abstract JsonArray toJson();

	/**
	 * @return whether the input contains any action containing form input(s) (except login and signup)
	 */
	public boolean containFormInput() {
		
		List<Action> acts = actions();
		
		if(acts ==null || acts.size()<1){
			return false;
		}
		
		for(Action act:acts){
			if(act.containFormInput()){
				return true;
			}
		}
		
		return false;
	}

	public boolean containAccount(Object user) {
		List<Action> acts = actions();
		
		if(acts==null || acts.size()<1 || !(user instanceof Account)){
			return false;
		}
		
		for(Action act:acts){
			if(act.containAccount((Account) user)){
				return true;
			}
		}
		return false;
	}

	public boolean containFormInputForFilePath() {
		List<Action> acts = actions();
		
		if(acts==null || acts.size()<1){
			return false;
		}
		
		for(Action act:acts){
			if(act.containFormInputForFilePath()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	
}
