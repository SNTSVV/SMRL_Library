package ase2019.mr.test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import ase2019.mr.language.MRRunner;
import ase2019.mr.language.OperationsProvider;

@RunWith(Parameterized.class)
public abstract class MRBasicTest {
	
	public static OperationsProvider _provider;
	public static Class[] MRs = {};
	
	@Parameter(0)
	public Class clazz;

	



	public static String packageName;

//	public static void setPackageName(String packageName){
////		MRBasicPackageTest.packageName = packageName;
//		System.out.println("set packageName "+MRBasicPackageTest.packageName);
//	}

	public static void setProvider(OperationsProvider provider) {
		_provider = provider;
	}

	public static void setMRs(Class[] mrs) {
		MRs = mrs;
	}

	

	
	
	@Parameters
	public static List retrieveParameters() {
		System.out.println(Thread.currentThread().getStackTrace()[2].getClassName());
		ArrayList<Object[]> pars = new ArrayList<Object[]>();
		
		for ( Class clazz : MRs ){
			Object[] o = new Object[1];
			o[0]=clazz;
			pars.add(o);
			
			System.out.println("!!! MR to be tested: "+clazz);
		}

		
		return pars;
	}


	@Test
	public void test(){


		
		//		Object[] testData = myProjectTestRule.getTestData();
		List<String> fails = new ArrayList<>();

		try {
			List<String> failuesMsgs = MRRunner.runAndGetFailures(_provider, clazz);
			if ( failuesMsgs.size() > 0 ){
				fails.add(clazz.getCanonicalName());
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if ( fails.size() > 0 ){
			fail(fails.toString());
		}
	}


}
