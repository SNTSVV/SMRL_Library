package smrl.mr.test;

import java.io.File;

import smrl.mr.language.Output;
import smrl.mr.language.Session;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BasicOutput implements Output {

	private String value;

	public BasicOutput(String string) {
		this.value = string;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String redirectURL() {
		throw new NotImplementedException();
	}

	@Override
	public boolean isError() {
		throw new NotImplementedException();
	}

	@Override
	public boolean hasStrictTransportSecurityHeader() {
		throw new NotImplementedException();
	}

	@Override
	public String getChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getSession(int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmptyFile() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean noFile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public File file() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
