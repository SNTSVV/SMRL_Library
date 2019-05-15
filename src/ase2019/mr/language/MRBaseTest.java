package ase2019.mr.language;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

//@RunWith(Parameterized.class)
public abstract class MRBaseTest {

//	public class MyBaseTestRule implements TestRule { 
//
//		private final List totalTestData;
//
//		private final int totalTestsSize;
//
//		private int currentTestIndex;
//
//		public MyBaseTestRule(List list) {
//			this.totalTestsSize = list.size();
//			this.totalTestData = list;
//		}
//
//		public Object[] getTestData(){
//			return (Object[]) totalTestData.get(currentTestIndex);
//		}
//
//		@Override
//		public Statement apply(Statement stmt, Description desc) {
//
//			return new Statement() {
//
//				@Override
//				public void evaluate() throws Throwable {
//					for(int i=0; i<totalTestsSize; i++) {
//						currentTestIndex = i;
//						stmt.evaluate();
//					}
//				}
//			};
//		}
//
//
//	}


//	@Rule
//	public MyBaseTestRule myProjectTestRule = new MyBaseTestRule(parameters());




	private OperationsProvider _provider;




	private static String packageName;

//	public static void setPackageName(String packageName){
//		MRBaseTest.packageName = packageName;
//	}

	public void setProvider(OperationsProvider provider) {
		this._provider = provider;
	}




//	public static List parameters() {
//		ArrayList<Object[]> pars = new ArrayList<Object[]>();
//
//		Collection<Class<? extends MR>> clazzes = MRRunner.retrieveMRClasses(packageName);
//		for ( Class clazz : clazzes ){
//			Object[] o = new Object[1];
//			o[0]=clazz;
//			pars.add(o);
//		}
//
//		System.out.println(pars);
//		return pars;
//	}


//	public void test(){
//
//
//		//		Object[] testData = myProjectTestRule.getTestData();
//		List<String> fails = new ArrayList<>();
//		List testData = parameters();
//		for ( Object o : testData ){
//			Object[] oo = (Object[])o;
//			Class clazz = (Class)oo[0];
//			System.out.println("!!!EXECUTING "+clazz.getCanonicalName());
//			try {
//				fails.addAll( MRRunner.runAndGetFailures(_provider, clazz) );
//			} catch (InstantiationException | IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		if ( fails.size() > 0 ){
//			fail(fails.toString());
//		}
//	}

	public void test(OperationsProvider provider, Class clazz){


		//		Object[] testData = myProjectTestRule.getTestData();
		List<String> fails = new ArrayList<>();


		System.out.println("!!!EXECUTING "+clazz.getCanonicalName());
		try {
			fails.addAll( MRRunner.runAndGetFailures(provider, clazz) );
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if ( fails.size() > 0 ){
			fail(fails.toString());
		}
	}

}
