/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2020
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Xuan Phu MAI (xuanphu.mai@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smrl.mr.language;

import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.Cookie;

public class CookieSession implements Session {
	private Set<Cookie> cookies;
	
	public CookieSession() {
		this.cookies = new HashSet<Cookie>();
	}
	
	public CookieSession(Set<Cookie> cookies) {
		this.cookies = new HashSet<Cookie>();
		this.cookies.addAll(cookies);
	}
	
	public CookieSession(Cookie cookie) {
		this.cookies = new HashSet<Cookie>();
		this.cookies.add(cookie);
	}

	public Set<Cookie> getCookies() {
		return cookies;
	}
	
	public String getCookie(String key){
		for(Cookie ck:cookies){
			if(ck.getName().equals(key)){
				return ck.getValue();
			}
		}
		return null;
	}

	public void setCookies(Set<Cookie> cookies) {
		this.cookies.clear();
		this.cookies.addAll(cookies);
//		for(Cookie ck:cookies){
//			this.cookies.add(ck);
//		}
	}
	
//	public void update(Set<Cookie> cookies){
//		setCookies(cookies);
//	}

	@Override
	public long getTimeout() {
		for(Cookie ck:cookies){
			if(ck.getExpiry() !=null){
				return (ck.getExpiry().getTime()-System.currentTimeMillis())/1000;
				
			}
		}
		return -1;
	}

	@Override
	public void update(Object session) {
		boolean updated = false;
		if(session instanceof Set){
			if(((Set<?>)session).size()>0){
				Class<? extends Object> itemClass = null;
				for(Object o:((Set<?>)session)){
					itemClass = o.getClass();
					break;
				}
				if(itemClass.equals(Cookie.class)){
					setCookies((Set<Cookie>) session);
					updated = true;
				}
			}
		}
		
		if(!updated){
			this.cookies.clear();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj){
			return true;
		}
		
		if(obj==null || !(obj instanceof CookieSession)){
			return false;
		}

		CookieSession that = (CookieSession)obj;
		
		if(this.cookies.size() != that.cookies.size()){
			return false;
		}
		
		int triedTimes = 0;
		for(Cookie c1:this.cookies){
			for(Cookie c2:that.cookies){
				if(c1.getName().equals(c2.getName())){
					triedTimes++;
//					if(c1 != c2){
					if(!c1.equals(c2)){
						return false;
					}
					break;
				}
			}
		}
		
		if(triedTimes<this.cookies.size()){
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		if(cookies==null || cookies.size()<=0){
			return "";
		}
		String res = "";
		boolean first = true;
		for(Cookie ck:cookies){
			if(!first){
				res += "\n";
			}
			res += ck.toString();
			first = false;
		}
		return res;
	}
	
	

}
