package org.example;

import java.io.IOException;
import java.util.Map;
import org.example.plansType.*;

public class Main {
    public static void main(String[] args) throws IOException {
        TestPlansAutomation tsa = new TestPlansAutomation("musasiyang", "UITest");
        tsa.GetTestPlans("TestPlansUrl");
        Map<String, Map<String, PlansTypeImp>> response = tsa.GetPlansObjectMap();
        for (String id: tsa.GetAllPlanIds()) {
            Map<String, PlansTypeImp> r = response.get(id);
            PlansTypeString planid = (PlansTypeString) r.get("TestPlanId");
            PlansTypeString suiteid = (PlansTypeString) r.get("testSuiteId");
            tsa.GetTestCases("TestCasesUrl", planid.getValue(), suiteid.getValue());
        }
        Map<String, Map<String, PlansTypeImp>> r = tsa.GetPlansObjectMap();
        for (String planId: tsa.GetAllPlanIds()) {
            PlansTypeObjectList testCases = (PlansTypeObjectList) r.get(planId).get("TestCases");
            for (Map<String, PlansTypeImp> testCase : testCases.getValue()) {
                PlansTypeObjectList parameterList = (PlansTypeObjectList) testCase.get("StepParameter");
                for (Map<String, PlansTypeImp> parameter : parameterList.getValue()) {
                    PlansTypeString interval = (PlansTypeString) parameter.getOrDefault("interval", new PlansTypeString(""));
                    PlansTypeString elementName = (PlansTypeString) parameter.getOrDefault("elementName", new PlansTypeString(""));
                    PlansTypeString desc = (PlansTypeString) parameter.getOrDefault("desc", new PlansTypeString(""));
                    PlansTypeString module = (PlansTypeString) parameter.getOrDefault("module", new PlansTypeString(""));
                    PlansTypeString url = (PlansTypeString) parameter.getOrDefault("url", new PlansTypeString(""));
                    PlansTypeString by = (PlansTypeString) parameter.getOrDefault("by", new PlansTypeString(""));
                    PlansTypeString key = (PlansTypeString) parameter.getOrDefault("key", new PlansTypeString(""));
                    System.out.println("interval" + "-------------------" + interval.getValue());
                    System.out.println("elementName" + "-----------------------" + elementName.getValue());
                    System.out.println("desc" + "-----------------" + desc.getValue());
                    System.out.println("module" + "-------------------" + module.getValue());
                    System.out.println("url" + "------------------------" + url.getValue());
                    System.out.println("by" + "--------------------------" + by.getValue());
                    System.out.println("key" + "-------------------------------" + key.getValue());
                    System.out.println("--------------------------");
                }
            }
        }
    }
}