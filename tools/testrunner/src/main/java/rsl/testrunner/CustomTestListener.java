package rsl.testrunner;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.Date;

public class CustomTestListener extends RunListener {

    private int totalTests = 0;
    private int currentTest = 0;
    private long startTime = 0;
    private long endTime = 0;

    private void startTimer()
    {
        startTime = System.currentTimeMillis();
    }

    private void stopTimer()
    {
        endTime = System.currentTimeMillis();
    }

    public void testRunStarted(Description description) throws Exception {
        totalTests = description.testCount();
        System.out.println("Number of tests to execute: " + description.testCount());
    }

    public void testRunFinished(Result result) throws Exception {
        //System.out.println("Number of tests executed: " + result.getRunCount());
    }

    public void testStarted(Description description) throws Exception {
        currentTest++;
        startTimer();
        System.out.print("[" + currentTest +  "/" + totalTests + "] " + description.getClassName() + " - " + description.getMethodName() + "()");
    }

    public void testFinished(Description description) throws Exception {
        stopTimer();
        double elapsedSeconds = (endTime-startTime) / 1000.0;
        System.out.println(" (" + elapsedSeconds + "s)");
    }

    public void testFailure(Failure failure) throws Exception {
        System.out.print("\nFailed: " + failure.getDescription().getMethodName());
    }

    public void testAssumptionFailure(Failure failure) {
        System.out.println("Failed: " + failure.getDescription().getMethodName());
    }

    public void testIgnored(Description description) throws Exception {
        System.out.println("Ignored: " + description.getMethodName());
    }

}