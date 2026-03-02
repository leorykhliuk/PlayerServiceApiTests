package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class LoggingTestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingTestListener.class);

    @Override
    public void onStart(ITestContext context) {
        int threadCount = context.getSuite().getXmlSuite().getThreadCount();
        String parallel = context.getSuite().getXmlSuite().getParallel().toString();
        log.info("Suite started: parallel={}, thread-count={}", parallel, threadCount);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        log.info("PASSED: {} [thread: {}]", tr.getMethod().getMethodName(), Thread.currentThread().getName());
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        String msg = tr.getThrowable() != null ? tr.getThrowable().getMessage() : "";
        log.error("FAILED: {} - {} [thread: {}]", tr.getMethod().getMethodName(), msg, Thread.currentThread().getName());
    }
}
