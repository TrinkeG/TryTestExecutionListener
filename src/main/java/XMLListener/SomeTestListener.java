package XMLListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import javax.xml.stream.XMLStreamException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

public class SomeTestListener implements TestExecutionListener {

  private final Path reportsDir = Paths.get("build/test-results/test2");
  private final PrintWriter out = new PrintWriter(System.out);
  private final Clock clock = Clock.systemDefaultZone();

  private SomeXmlReportData reportData;

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    this.reportData = new SomeXmlReportData(testPlan, clock);
    try {
      Files.createDirectories(this.reportsDir);
    }
    catch (IOException e) {
      printException("Could not create reports directory: " + this.reportsDir, e);
    }
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    this.reportData = null;
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    this.reportData.markSkipped(testIdentifier, reason);
    writeXmlReportInCaseOfRoot(testIdentifier);
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    this.reportData.markStarted(testIdentifier);
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
    this.reportData.addReportEntry(testIdentifier, entry);
  }

  @Override
  public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult result) {
    this.reportData.markFinished(testIdentifier, result);
    writeXmlReportInCaseOfRoot(testIdentifier);
  }

  private void writeXmlReportInCaseOfRoot(TestIdentifier testIdentifier) {
    if (isRoot(testIdentifier)) {
      String rootName = UniqueId.parse(testIdentifier.getUniqueId()).getSegments().get(0).getValue();
      writeXmlReportSafely(testIdentifier, rootName);
    }
  }

  private void writeXmlReportSafely(TestIdentifier testIdentifier, String rootName) {
    Path xmlFile = this.reportsDir.resolve("TEST-" + rootName + ".xml");
    try (Writer fileWriter = Files.newBufferedWriter(xmlFile)) {
      new SomeXmlReportWriter(this.reportData).writeXmlReport(testIdentifier, fileWriter);
    }
    catch (XMLStreamException | IOException e) {
      printException("Could not write XML report: " + xmlFile, e);
    }
  }

  private boolean isRoot(TestIdentifier testIdentifier) {
    return !testIdentifier.getParentId().isPresent();
  }

  private void printException(String message, Exception exception) {
    out.println(message);
    exception.printStackTrace(out);
  }

}
