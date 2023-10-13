package org.example;

import lombok.Getter;
import org.example.connection.ConnectionProperty;
import java.io.IOException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

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
                    new SimpleEntry<>("TestPlansUrl", "https://%s/%s/%s/_apis/test/plans?api-version=5.0"),
                    new SimpleEntry<>("TestPlanUrl", "https://%s/%s/%s/_apis/test/plans/%s?api-version=5.0"),
                    new SimpleEntry<>("TestSuiteUrl", "https://%s/%s/%s/_apis/test/Plans/%s/suites/%s?api-version=5.0"),
                    new SimpleEntry<>("TestCasesUrl", "https://%s/%s/%s/_apis/test/Plans/%s/suites/%s/testcases?api-version=5.0"),
                    new SimpleEntry<>("TestStepsUrl", "https://%s/%s/_apis/wit/workItems/%s"),
                    new SimpleEntry<>("StepsParameter", "https://%s/%s/%s/_apis/wit/workitems?ids=%s&api-version=5.0"),
                    new SimpleEntry<>("GetPointIds", "https://%s/%s/%s/_apis/test/Plans/%s/Suites/%s/points?api-version=5.0"),
                    new SimpleEntry<>("CreateRuns", "https://%s/%s/%s/_apis/test/runs?api-version=5.0"),
                    new SimpleEntry<>("UpdateResult", "https://%s/%s/%s/_apis/test/Runs/%s/results?api-version=5.0"),
                    new SimpleEntry<>("CreateSharedParameters", "https://%s/%s/%s/_apis/wit/workitems/%s")
            )
    );

    private String serviceDomain;

    @Getter
    private String organization;

    @Getter
    private final String project;

    @Getter
    private final Map<String, Map<String, PlansTypeImp>> plansTypeObjectMap;

    @Getter
    private Map<String, PlansTypeImp> testPlan;

    @Getter
    private Map<String, String> workItemId;

    protected TestPlansAutomation(String serviceDomain, String organization, String project) {

        this.serviceDomain = serviceDomain;
        this.plansTypeObjectMap = new HashMap<>();
        this.organization = organization;
        this.project = project;
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

    protected String CreateCaseRuns(String runsName, String planId, int pointId) throws IOException {
        String runsId = "0";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", runsName);
        JSONObject planObject = new JSONObject();
        planObject.put("id", planId);
        jsonObject.put("plan", planObject);
        Integer[] pointIds = new Integer[]{pointId};
        jsonObject.put("pointIds", pointIds);
        String outputString = jsonObject.toString();
        String runsUrl = this.urlType.getOrDefault("CreateRuns", "");
        runsUrl = runsUrl.formatted(this.serviceDomain, this.organization, this.project);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(runsUrl);
        cp.setMethod("POST");
        cp.setCertFile("");
        cp.setPostData(outputString);
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", cp);
        // Get run Id
        runsId = String.valueOf(new JSONObject(json).getInt("id"));
        return runsId;
    }

    protected String CreateSharedParameters(String postData) throws IOException {
        String runsUrl = this.urlType.getOrDefault("CreateSharedParameters", "");
        runsUrl = runsUrl.formatted(this.serviceDomain, this.organization, this.project, "%24Shared%20Parameter?api-version=6.0");
        System.out.println(runsUrl);
        System.out.println(postData);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(runsUrl);
        cp.setMethod("POST");
        cp.setCertFile("");
        cp.setPostData(postData);
        DemoApis apis = new DemoApis();
        String json = apis.getDemoParamApis("settings.yaml", cp);
        return json;
    }

    private PlansTypeObjectList getStepsParameter(String urlType, String planId, String workItemId) throws IOException {
        String parameterUrl = this.urlType.getOrDefault(urlType, "");
        PlansTypeObjectList parameterList = new PlansTypeObjectList();
        parameterUrl = parameterUrl.formatted(this.serviceDomain, this.organization, this.project, workItemId);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(parameterUrl);
        cp.setMethod("GET");
        cp.setCertFile("");
        cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", cp);
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
        Pattern parameterPattern = Pattern.compile("<kvp\\ key=\\\"([\\ \\w\\d.]+)\\\"\\ value=\\\"([\\:\\/\\ \\d\\w\\u4E00-\\u9FA5.\\?\\&\\;\\@\\#\\=\\[\\]\"\\*\\-\\_\\(\\)\\'\\,\\.]*)\\\"/>");
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

    protected void UpdateRunsResult(String runsId, int resultId, String state, String outcome, String comment) throws IOException {
        JSONObject[] postDataList;
        JSONObject postData = new JSONObject();
        postData.put("id", resultId);
        postData.put("state", state);
        postData.put("outcome", outcome);
        postData.put("comment", comment);
        postDataList = new JSONObject[]{postData};
        String postDataString = Arrays.toString(postDataList);

        String updateResultUrl = this.urlType.getOrDefault("UpdateResult", "");
        updateResultUrl = updateResultUrl.formatted(this.serviceDomain, this.organization, this.project, runsId);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(updateResultUrl);
        cp.setMethod("PATCH");
        cp.setCertFile("");
        cp.setPostData(postDataString);
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", cp);
        JSONObject jsonObject = new JSONObject(json);
    }


    private PlansTypeStringList getTestSteps(String urlType, String planId, String testCaseId) throws IOException {
        PlansTypeStringList stepList = new PlansTypeStringList();
        String stepsUrl = this.urlType.getOrDefault(urlType, "");
        stepsUrl = stepsUrl.formatted(this.serviceDomain, this.organization, testCaseId);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(stepsUrl);
        cp.setMethod("GET");
        cp.setCertFile("");
        cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", cp);
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

    protected Map<String, String> getPointIds(String urlType, String planId, String suiteId) throws IOException {
        Map<String, String> response = new HashMap<>();
        this.urlType.put("demoUrl", "https://www.google.com.tw/");
        this.urlType.put("demoUrl", "https://www.yuanta.com/");
        System.out.println("-------------------------------------------------");
        System.out.println(this.urlType);
        System.out.println("-------------------------------------------------");
        String pointIdsUrl = this.urlType.getOrDefault(urlType, "");
        pointIdsUrl = pointIdsUrl.formatted(this.serviceDomain, this.organization, this.project, planId, suiteId);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(pointIdsUrl);
        cp.setMethod("GET");
        cp.setCertFile("");
        cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", cp);
        JSONObject jsonObject = new JSONObject(json);
        JSONArray pointList = jsonObject.getJSONArray("value");
        for (int i=0; i<pointList.length(); i++) {
            Map<String, String> r = new HashMap<>();
            JSONObject res = pointList.getJSONObject(i);
            String pointId = res.get("id").toString();
            String testCaseId = res.getJSONObject("testCase").getString("id");
            response.put(testCaseId, pointId);
        }
        return response;
    }

    protected void GetTestCases(String urlType, String planId, String suiteId) throws IOException {
        Map<String, String> pointIds = this.getPointIds("GetPointIds", planId, suiteId);

        String testCasesUrl = this.urlType.getOrDefault(urlType, "");
        testCasesUrl = testCasesUrl.formatted(this.serviceDomain, this.organization, this.project, planId, suiteId);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(testCasesUrl);
        cp.setMethod("GET");
        cp.setCertFile("");
        cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", cp);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        PlansTypeObjectList testCaseList = new PlansTypeObjectList();
        List<Map<String, Object>> items;
        items = (List<Map<String, Object>>) map.get("value");
        for (Map<String, Object> item: items) {
            Map<String, PlansTypeImp> testCase = new HashMap<>();
            Map<String, Object> testCaseItem = (Map<String, Object>) item.get("testCase");
            String testCaseId = (String) testCaseItem.get("id");
            String workItemUrl = (String) testCaseItem.get("url");
            // Get test case name
            ConnectionProperty itemcp = new ConnectionProperty();
            itemcp.setApiUrl(workItemUrl);
            itemcp.setMethod("GET");
            itemcp.setCertFile("");
            itemcp.setPostData("");
            DemoApis itemApis = new DemoApis();
            String itemJson = itemApis.getDemoApis("settings.yaml", itemcp);
            String caseName = new JSONObject(itemJson).getJSONObject("fields").getString("System.Title");
            // End of get test case name
            String testCaseWorkItemUrl = (String) testCaseItem.get("url");
            testCase.put("TestCaseId", new PlansTypeString(testCaseId));
            testCase.put("TestCaseWorkItemUrl", new PlansTypeString(testCaseWorkItemUrl));
            testCase.put("PointId", new PlansTypeString(pointIds.get(testCaseId)));
            testCase.put("TestCaseName", new PlansTypeString(caseName));
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
        testPlansUrl = testPlansUrl.formatted(this.serviceDomain, this.organization, this.project);
        ConnectionProperty cp = new ConnectionProperty();
        cp.setApiUrl(testPlansUrl);
        cp.setMethod("GET");
        cp.setCertFile("");
        cp.setPostData("");
        DemoApis apis = new DemoApis();
        String json = apis.getDemoApis("settings.yaml", cp);

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