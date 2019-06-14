package smrl.mr.language.actions;

import smrl.mr.language.Action;

public abstract class InnerAction extends Action {

	protected Action mainAction;

	public Action getMainAction() {
		return mainAction;
	}

	public void setMainAction(Action mainAction) {
		this.mainAction = mainAction;
	}

	@Override
	public Action clone() throws CloneNotSupportedException {
		InnerAction clone = (InnerAction) super.clone();
		clone.setMainAction(this.getMainAction());
		return clone;
	}
	
}