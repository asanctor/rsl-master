package rsl.testrunner;


import java.lang.reflect.Method;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import com.google.common.reflect.ClassPath;


public class Main {

    public static void main(String[] args) {

        String testJar = "tests";
        String testPackage = "";
        String testClass = "";

        if (args.length == 1) {
            testJar = args[0];
        } else if(args.length == 2) {
            testJar = args[0];
            testPackage = args[1];
        }  else if(args.length >= 3) {
            testJar = args[0];
            testPackage = args[1];
            testClass = args[2];
        }

        try {

            ClassPath cp = ClassPath.from(ClassLoader.getSystemClassLoader());

            // scan for all classes that begin with the 'testPackage' path
            ImmutableSet<ClassPath.ClassInfo> classInfos = cp.getTopLevelClassesRecursive(testPackage);

            // turn set of classinfo into an array of classes
            // only keep classes with JUnite @Test methods
            Stream<? extends Class<?>> classes = classInfos.stream().map(ClassPath.ClassInfo::load).filter(Main::hasTestMethods);
            if(!testClass.equals("")){
                final String filterName = testClass;
                classes = classes.filter( c -> c.getSimpleName().equals(filterName));
            }
            Class[] testClasses = classes.toArray(Class[]::new);

            JUnitCore junit = new JUnitCore();
            junit.addListener(new TextListener(System.out));
            junit.addListener(new CustomTestListener());
            junit.run(testClasses);

        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }


    private static boolean hasTestMethods(Class classObject)
    {
        for (Method method : classObject.getDeclaredMethods()) {
            if (method.isAnnotationPresent(org.junit.Test.class)) {
                return true;
            }
        }
        return false;
    }


}
