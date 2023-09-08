package org.example;

import lombok.Getter;
import org.example.connection.ConnectionProperty;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.plansType.PlansTypeObjectList;
import org.example.plansType.PlansTypeImp;
import org.example.plansType.PlansTypeString;
import org.example.plansType.PlansTypeStringList;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class TestPlansAutomation {
    HashMap<String, String> urlType = new HashMap<> (
            Map.ofEntries(
                    new SimpleEntry<>("TestPlansUrl", "https://dev.azure.com/%s/%s/_apis/test/plans?api-version=5.0"),
                    new SimpleEntry<>("TestPlanUrl", "https://dev.azure.com/%s/%s/_apis/test/plans/%s?api-version=5.0"),
                    new SimpleEntry<>("TestSuiteUrl", "https://dev.azure.com/%s/%s/_apis/test/Plans/%s/suites/%s?api-version=5.0"),
                    new SimpleEntry<>("TestCasesUrl", "https://dev.azure.com/%s/%s/_apis/test/Plans/%s/suites/%s/testcases?api-version=5.0"),
                    new SimpleEntry<>("TestStepsUrl", "https://dev.azure.com/%s/_apis/wit/workItems/%s"),
                    new SimpleEntry<>("StepsParameter", "https://dev.azure.com/%s/%s/_apis/wit/workitems?ids=%s&api-version=5.0")
            )
    );

    @Getter
    private String organization;

    @Getter
    private final String project;

    @Getter
    private final ConnectionProperty cp;

    @Getter
    private final Map<String, Map<String, PlansTypeImp>> plansTypeObjectMap;

    @Getter
    private Map<String, PlansTypeImp> testPlan;

    @Getter
    private Map<String, String> workItemId;

    protected TestPlansAutomation(String organization, String project) {

        this.plansTypeObjectMap = new HashMap<>();
        this.organization = organization;
        this.project = project;
        this.cp = new ConnectionProperty();
        this.workItemId = new HashMap<>();
    }

    public Map<String, Map<String, PlansTypeImp>> GetPlansObjectMap() {
        return this.plansTypeObjectMap;
    }

    public Map<String, PlansTypeImp> GetPlansObjectMapById(String planId) {
        return (Map<String,PlansTypeImp>) this.plansTypeObjectMap.get(planId);
    }


    public List<String> GetAllPlanIds() {
        List<String> rsp = new ArrayList<>();
        this.plansTypeObjectMap.forEach((key, value) -> {
            rsp.add(key);
        });
        return rsp;
    }

    private PlansTypeObjectList getStepsParameter(String urlType, String planId, String workItemId) throws IOException {
        String parameterUrl = this.urlType.getOrDefault(urlType, "");
        PlansTypeObjectList parameterList = new PlansTypeObjectList();
        parameterUrl = parameterUrl.formatted(this.organization, this.project, workItemId);
        System.out.println("parameterUrl: "+parameterUrl);
        this.cp.setApiUrl(parameterUrl);
        this.cp.setMethod("GET");
        this.cp.setCertFile("");
        this.cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", this.cp);
        String parameterItem = new JSONObject(
                json
        ).getJSONArray(
                "value"
        ).getJSONObject(
                0
        ).getJSONObject(
                "fields"
        ).getString(
                "Microsoft.VSTS.TCM.Parameters"
        );
        System.out.println("parameterItem: "+parameterItem);
        Pattern parameterPattern = Pattern.compile("<kvp\\ key=\\\"([\\ \\w\\d.]+)\\\"\\ value=\\\"([\\:\\/\\ \\d\\w\\u4E00-\\u9FA5.\\&\\;\\@\\=\\[\\]\"\\*]*)\\\"/>");
        Matcher matcher = parameterPattern.matcher(parameterItem);

        Map<String, PlansTypeImp> parameterObject = new HashMap<>();
        while (matcher.find()) {
            parameterObject.put(matcher.group(1), new PlansTypeString(matcher.group(2)));
            if (matcher.group(1).equalsIgnoreCase("NL")) {
                parameterList.addPlansTypeObjectList(parameterObject);
                parameterObject = new HashMap<>();
            }
        }
        return parameterList;
    }

    private PlansTypeStringList getTestSteps(String urlType, String planId, String testCaseId) throws IOException {
        PlansTypeStringList stepList = new PlansTypeStringList();
        String stepsUrl = this.urlType.getOrDefault(urlType, "");
        stepsUrl = stepsUrl.formatted(this.organization, testCaseId);
        this.cp.setApiUrl(stepsUrl);
        this.cp.setMethod("GET");
        this.cp.setCertFile("");
        this.cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", this.cp);
        JSONObject jsonObject = new JSONObject(json);
        JSONObject fields;
        fields = (JSONObject) jsonObject.get("fields");
        String stepsString = (String) fields.get("Microsoft.VSTS.TCM.Steps");
        String workItemString = (String) fields.get("Microsoft.VSTS.TCM.LocalDataSource");
        Pattern pattern = Pattern.compile("&gt;([\\ \\w\\d\\.\\u4E00-\\u9FA5]+)&lt;");
        Matcher matcher = pattern.matcher(stepsString);
        while (matcher.find()) {
            stepList.addPlansTypeStringList(matcher.group(1));
        }
        JSONArray workItemId = new JSONObject(workItemString).getJSONArray("sharedParameterDataSetIds");
        this.workItemId.put(testCaseId, workItemId.get(0).toString());
        return stepList;
    }

    protected void GetTestCases(String urlType, String planId, String suiteId) throws IOException {

        String testCasesUrl = this.urlType.getOrDefault(urlType, "");
        testCasesUrl = testCasesUrl.formatted(this.organization, this.project, planId, suiteId);
        this.cp.setApiUrl(testCasesUrl);
        this.cp.setMethod("GET");
        this.cp.setCertFile("");
        this.cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", this.cp);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        PlansTypeObjectList testCaseList = new PlansTypeObjectList();
        List<Map<String, Object>> items;
        items = (List<Map<String, Object>>) map.get("value");
        for (Map<String, Object> item: items) {
            Map<String, PlansTypeImp> testCase = new HashMap<>();
            Map<String, Object> testCaseItem = (Map<String, Object>) item.get("testCase");
            String testCaseId = (String) testCaseItem.get("id");
            String testCaseWorkItemUrl = (String) testCaseItem.get("url");
            testCase.put("TestCaseId", new PlansTypeString(testCaseId));
            testCase.put("TestCaseWorkItemUrl", new PlansTypeString(testCaseWorkItemUrl));
            PlansTypeStringList ptsl = this.getTestSteps("TestStepsUrl", planId, testCaseId);
            testCase.put("TestCaseSteps", ptsl);

            String workItemId = this.workItemId.get(testCaseId);
            // FIXME. Need to implement workItemId function.
            PlansTypeObjectList ptol = this.getStepsParameter("StepsParameter", planId, workItemId);
            // PlansTypeObjectList ptol = this.getStepsParameter("StepsParameter", planId, "72");
            testCase.put("StepParameter", ptol);

            testCaseList.addPlansTypeObjectList(testCase);

            this.getTestSteps("TestStepsUrl", planId, testCaseId);
        }
        Map<String, PlansTypeImp> testPlanTmp = this.plansTypeObjectMap.get(planId);
        testPlanTmp.put("TestCases", testCaseList);
        this.plansTypeObjectMap.put(planId, testPlanTmp);
    }

    public void GetTestPlans(String urlType) throws IOException {

        String testPlansUrl = this.urlType.getOrDefault(urlType, "");
        testPlansUrl = testPlansUrl.formatted(this.organization, this.project);
        this.cp.setApiUrl(testPlansUrl);
        this.cp.setMethod("GET");
        this.cp.setCertFile("");
        this.cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", this.cp);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        List<Map<String, Object>> items;
        items = (List<Map<String, Object>>) map.get("value");
        for (Map<String, Object> item: items) {
            Map<String, PlansTypeImp> testPlan = new HashMap<>();
            Map<String, String> testSuite = new HashMap<>();
            Map<String, String> rootSuite = (Map<String, String>) item.get("rootSuite");
            testPlan.put("TestPlanId",  new PlansTypeString(item.get("id").toString()));
            testPlan.put("TestPlanName", new PlansTypeString((String) item.get("name")));
            testPlan.put("testSuiteId", new PlansTypeString(rootSuite.get("id")));
            this.plansTypeObjectMap.put(item.get("id").toString(), testPlan);
        }
    }
}