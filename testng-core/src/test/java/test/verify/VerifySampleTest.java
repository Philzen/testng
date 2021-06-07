package test.verify;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Illustrate the implementation of a @Verify/@Verifier test.
 *
 * <p>One method should be annotated with @Verifier and then each test method annotated with @Verify
 * will be followed with a call to the @Verifier method.
 */
@Listeners(VerifyMethodInterceptor.class)
public class VerifySampleTest {

  @Verify
  @Test
  public void f1() {
    log("f1");
  }

  @Verify
  @Test
  public void f2() {
    log("f2");
  }

  @Verifier
  @Test
  public void verify() {
    log("Verifying");
  }

  private void log(String string) {
    if (false) {
      System.out.println(string);
    }
  }
}
