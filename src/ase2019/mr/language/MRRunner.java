package ase2019.mr.language;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

public class MRRunner {

	public static void runInPackage(OperationsProvider provider, String packageName) {
		Class[] a = retrieveMRClassesArray(packageName);
		 
		 run(provider,a);
	}

	public static Class[] retrieveMRClassesArray(String packageName) {
		Set<Class<? extends MR>> allClasses = (Set<Class<? extends MR>>) retrieveMRClasses(packageName);
		 
		 Class[] a = new Class[allClasses.size()];
		 int i = 0;
		 for (  Class c : allClasses ){
			 a[i]=c;
			 i++;
		 }
		return a;
	}

	public static Collection<Class<? extends MR>> retrieveMRClasses(String packageName) {
		Reflections reflections = new Reflections(packageName);

		 Set<Class<? extends MR>> allClasses = reflections.getSubTypesOf(MR.class);
		return allClasses;
	}
	
	public static void run(OperationsProvider provider, Class... classes) {
		for( Class clazz : classes ){
			try {
				runAndGetFailures(provider, clazz);
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static List<String> runAndGetFailures(OperationsProvider provider, Class clazz)
			throws InstantiationException, IllegalAccessException {
		MR mr = (MR)clazz.newInstance();
		mr.setProvider(provider);
		mr.run();
		
		return mr.failures;
	}

}
