package org.example;

import java.io.IOException;

public class TestCasesThread extends Thread {

    private final String urlType;
    private final String planId;
    private final String suiteId;
    private final TestPlansAutomation tsa;

    public TestCasesThread(TestPlansAutomation tsa, String urlType, String planId, String suiteId) {
        this.tsa = tsa;
        this.urlType = urlType;
        this.suiteId = suiteId;
        this.planId = planId;
    }

    @Override
    public void run() {
        try {
            this.tsa.GetTestCases(this.urlType, this.planId, this.suiteId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
