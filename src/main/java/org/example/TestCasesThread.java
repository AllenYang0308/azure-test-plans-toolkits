package org.example;

import java.io.IOException;

public class TestCasesThread extends Thread {

    private String urlType;
    private String planId;
    private String suiteId;
    private TestPlansAutomation tsa;

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
