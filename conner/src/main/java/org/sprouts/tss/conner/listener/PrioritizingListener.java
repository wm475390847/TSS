package org.sprouts.tss.conner.listener;

import org.jetbrains.annotations.NotNull;
import org.testng.IAnnotationTransformer;
import org.testng.Reporter;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 解决priority跳过xml的类执行顺序而执行priority标定的顺序 Listener to fix TestNG Interleaving
 * issue. I had to re-write this as the original example I had did not allow for
 * priority to be manually set on export test level.
 *
 * @author wangmin
 */
public class PrioritizingListener implements IAnnotationTransformer {

    HashMap<Object, Integer> priorityMap = new HashMap<>();
    Integer classPriorityCounter = 10000;
    /**
     * The length of the final priority assigned to each method.
     */
    Integer maxTestatrixLength = 4;

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // class of the test method.
        Class<?> declaringClass = testMethod.getDeclaringClass();
        // Current priority of the test assigned at the test method.
        int testPriority = annotation.getPriority();
        // Current class priority.
        Integer currentClassPriority = priorityMap.get(declaringClass);

        if (currentClassPriority == null) {
            currentClassPriority = classPriorityCounter++;
            priorityMap.put(declaringClass, currentClassPriority);
        }

        StringBuilder concatenatedPriority = getStringBuilder(testPriority, currentClassPriority);

        // Sets the new priority to the test method.
        annotation.setPriority(Integer.parseInt(concatenatedPriority.toString()));

        String printText = testMethod.getName() + " Priority = " + concatenatedPriority;
        Reporter.log(printText);
    }

    private @NotNull StringBuilder getStringBuilder(int testPriority, Integer currentClassPriority) {
        StringBuilder concatenatedPriority = new StringBuilder(Integer.toString(testPriority));

        // Adds 0's to start of this number.
        while (concatenatedPriority.length() < maxTestatrixLength) {
            concatenatedPriority.insert(0, "0");
        }

        // Concatenates our class counter to the test level priority (example
        // for test with export priority of 1: 1000100001; same test class with export
        // priority of 2: 1000100002; next class with export priority of 1. 1000200001)
        concatenatedPriority.insert(0, currentClassPriority);
        return concatenatedPriority;
    }
}