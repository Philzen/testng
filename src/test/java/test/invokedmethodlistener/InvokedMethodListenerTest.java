package test.invokedmethodlistener;

import org.testng.Assert;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.annotations.Test;

import test.SimpleBaseTest;

import java.util.List;

public class InvokedMethodListenerTest extends SimpleBaseTest {

  private static void run(Class[] classes, IInvokedMethodListener l) {
    TestNG tng = create();
    tng.setTestClasses(classes);

    tng.addInvokedMethodListener(l);
    tng.run();
  }

  private static void assertMethodCount(MyListener l) {
    Assert.assertEquals(l.getBeforeCount(), 9);
    Assert.assertEquals(l.getAfterCount(), 9);
  }

  @Test
  public void withSuccess() {
    MyListener l = new MyListener();
    run(new Class[]{Success.class}, l);
    assertMethodCount(l);
  }

  @Test
  public void withFailure() {
    MyListener l = new MyListener();
    run(new Class[] { Failure.class }, l);
    assertMethodCount(l);
    Assert.assertEquals(l.getSuiteStatus(), ITestResult.FAILURE);
    Assert.assertTrue(null != l.getSuiteThrowable());
    Assert.assertTrue(l.getSuiteThrowable().getClass() == RuntimeException.class);

    Assert.assertEquals(l.getMethodStatus(), ITestResult.FAILURE);
    Assert.assertTrue(null != l.getMethodThrowable());
    Assert.assertTrue(l.getMethodThrowable().getClass() == IllegalArgumentException.class);
  }

  /**
   * Fix for:
   * http://code.google.com/p/testng/issues/detail?id=7
   * http://code.google.com/p/testng/issues/detail?id=86
   */
  @Test
  public void sameMethodInvokedMultipleTimesShouldHaveDifferentTimeStamps() {
    TestNG tng = create(Sample.class);
    tng.addListener(new InvokedMethodListener());
    tng.run();
    List<IInvokedMethod> m = InvokedMethodListener.m_methods;
//    for (IInvokedMethod mm : m) {
//      System.out.println(mm.getTestMethod().getMethodName() + " " + mm.getDate());
//    }
    IInvokedMethod after1 = m.get(1);
    Assert.assertTrue(after1.getTestMethod().isAfterMethodConfiguration());
    IInvokedMethod after2 = m.get(3);
    Assert.assertTrue(after2.getTestMethod().isAfterMethodConfiguration());
    Assert.assertTrue(after1.getDate() != after2.getDate());
  }

  @Test(description = "Test methods with expected exceptions should show up as pass" +
  		" in IInvokedMethodListener's afterInvocaiton method")
  public void testMethodsWithExpectedExceptionsShouldShowUpAsPass() {
    TestNG tng = create(Sample2.class);
    Sample2.Sample2InvokedMethodListener l = new Sample2().new Sample2InvokedMethodListener();
    tng.addListener(l);
    tng.run();

    Assert.assertTrue(l.isSuccess);
  }

  @Test(description = "Invoked method does not recognize configuration method")
  public void issue629_InvokedMethodDoesNotRecognizeConfigurationMethod() {
    InvokedMethodNameListener l = new InvokedMethodNameListener();
    run(new Class[]{Success.class}, l);

    Assert.assertEquals(l.testMethods.size(), 1);
    Assert.assertTrue(l.testMethods.contains("a"));

    Assert.assertEquals(l.testMethodsFromTM.size(), 1);
    Assert.assertTrue(l.testMethodsFromTM.contains("a"));

    Assert.assertEquals(l.configurationMethods.size(), 8);
    Assert.assertTrue(l.configurationMethods.contains("beforeMethod"));
    Assert.assertTrue(l.configurationMethods.contains("afterMethod"));
    Assert.assertTrue(l.configurationMethods.contains("beforeTest"));
    Assert.assertTrue(l.configurationMethods.contains("afterTest"));
    Assert.assertTrue(l.configurationMethods.contains("beforeClass"));
    Assert.assertTrue(l.configurationMethods.contains("afterClass"));
    Assert.assertTrue(l.configurationMethods.contains("beforeSuite"));
    Assert.assertTrue(l.configurationMethods.contains("afterSuite"));

    Assert.assertEquals(l.configurationMethodsFromTM.size(), 8);
    Assert.assertTrue(l.configurationMethodsFromTM.contains("beforeMethod"));
    Assert.assertTrue(l.configurationMethodsFromTM.contains("afterMethod"));
    Assert.assertTrue(l.configurationMethodsFromTM.contains("beforeTest"));
    Assert.assertTrue(l.configurationMethodsFromTM.contains("afterTest"));
    Assert.assertTrue(l.configurationMethodsFromTM.contains("beforeClass"));
    Assert.assertTrue(l.configurationMethodsFromTM.contains("afterClass"));
    Assert.assertTrue(l.configurationMethodsFromTM.contains("beforeSuite"));
    Assert.assertTrue(l.configurationMethodsFromTM.contains("afterSuite"));
  }
}
