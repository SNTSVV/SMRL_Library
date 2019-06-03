package smrl.mr.language;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

public abstract class MRBaseTest {

	private OperationsProvider _provider;

	public void setProvider(OperationsProvider provider) {
		this._provider = provider;
	}

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
