package ase2019.mr.test;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ase2019.mr.language.Action;
import ase2019.mr.language.Input;
import ase2019.mr.language.MRData;

public class BasicInput extends Input {

	public BasicInput(String string) {
		setID(string);
		
		for(int i = 0; i<10; i++){
			addAction(new BasicAction(string+":"+i));
		}
	}

	List<Action> actions = new ArrayList<Action>();
	
	@Override
	public void addAction(Action _basicAction) {
		addAction(actions.size(), _basicAction);
	}
	
	@Override
	public void addAction(int pos, Action _basicAction) {
		
		Action basicAction;
		try {
			basicAction = (Action) _basicAction.clone();
			basicAction.setInput(this);
			actions.add( pos, basicAction);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Action> actions() {
		// TODO Auto-generated method stub
		return actions;
	}

	@Override
	public void copyActionTo(int x, int y) {
		actions.add(y, actions.get(x));
	}


	@Override
	public String toString() {
		return "Input("+this.getId()+")" + actions.toString();
	}

	@Override
	public BasicInput clone() throws CloneNotSupportedException {
		
		BasicInput clone = (BasicInput) super.clone();
		clone.actions = new ArrayList<>();
		clone.actions.addAll(actions);
		
		return clone;
	}

	@Override
	public int indexOf(Action action) {
		return this.actions.indexOf(action);
	}

	@Override
	public JsonArray toJson() {
		// TODO Auto-generated method stub
		return null;
	}



	
}
