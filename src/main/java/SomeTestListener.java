import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

public class SomeTestListener implements TestExecutionListener  {

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    TestExecutionListener.super.executionStarted(testIdentifier);
    System.out.println("!!!!!SOMETHING");
  }
  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    TestExecutionListener.super.testPlanExecutionFinished(testPlan);
    System.out.println("!!!!ENDED");
  }
}
