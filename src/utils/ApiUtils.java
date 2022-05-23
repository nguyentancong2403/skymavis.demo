package utils;

import apis.Methods;
import config.ConfigData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiUtils {
    private RequestSpecification getRequestSpecification(Map<String, String> queryParameter, Map<String, String> header, Map<String, String> body, Map<String, String> cookie) {
        // Set Request Specification
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        // Add parameter
        if (queryParameter != null)
            requestSpecBuilder.addQueryParams(queryParameter);

        //Add header
        if (header != null)
            requestSpecBuilder.addHeaders(header);

        // Add request body
        if (body != null) {
            requestSpecBuilder.setBody(body);
        }

        // Add cookies
        if (cookie != null) {
            requestSpecBuilder.addCookies(cookie);
        }
        return requestSpecBuilder.build();
    }

    public Response callAPIWithDataFile(String apiPath, String filePath){

        Map<String, String> queryParameter = null, header = null, body = null, cookie = null;
        Method method = Methods.valueOf(JsonUtils.convertJsonFileToJSONObject(filePath).get("method").toString()).getMethod();
        String domain =  ConfigData.DOMAIN;
        String endPoint = domain + apiPath;

        if (JsonUtils.convertJsonFileToJSONObject(filePath).optJSONObject("headers") != null) {
            JSONObject headers = JsonUtils.convertJsonFileToJSONObject(filePath).getJSONObject("headers");
            header = JsonUtils.getMapFromJsonObject(headers);
        }

        if (JsonUtils.convertJsonFileToJSONObject(filePath).optJSONObject("parameters") != null) {
            JSONObject parameters = JsonUtils.convertJsonFileToJSONObject(filePath).getJSONObject("parameters");
            queryParameter = JsonUtils.getMapFromJsonObject(parameters);
        }

        if (JsonUtils.convertJsonFileToJSONObject(filePath).optJSONObject("body") != null) {
            JSONObject bodies = JsonUtils.convertJsonFileToJSONObject(filePath).getJSONObject("body");
            body = JsonUtils.getMapFromJsonObject(bodies);
        }

        if (JsonUtils.convertJsonFileToJSONObject(filePath).optJSONObject("cookies") != null) {
            JSONObject cookies = JsonUtils.convertJsonFileToJSONObject(filePath).getJSONObject("cookies");
            cookie = JsonUtils.getMapFromJsonObject(cookies);
        }
        return given()
                .config(RestAssuredConfig.config())
                .spec(getRequestSpecification(queryParameter, header, body, cookie))
                .request(method, endPoint);
    }

    public Response callAPIWithJsonObject(String apiPath, JSONObject jsonObj) {
        Map<String, String> queryParameter = null, header = null, body = null, cookie = null;
        Method method = Methods.valueOf(jsonObj.get("method").toString()).getMethod();
        String domain =  ConfigData.DOMAIN;
        String endPoint = domain + apiPath;

        if (jsonObj.optJSONObject("headers") != null) {
            JSONObject headers = jsonObj.getJSONObject("headers");
            header = JsonUtils.getMapFromJsonObject(headers);
        }

        if (jsonObj.optJSONObject("parameters") != null) {
            JSONObject parameters = jsonObj.getJSONObject("parameters");
            queryParameter = JsonUtils.getMapFromJsonObject(parameters);
        }

        if (jsonObj.optJSONObject("body") != null) {
            JSONObject bodies = jsonObj.getJSONObject("body");
            body = JsonUtils.getMapFromJsonObject(bodies);
        }

        if (jsonObj.optJSONObject("cookies") != null) {
            JSONObject cookies = jsonObj.getJSONObject("cookies");
            cookie = JsonUtils.getMapFromJsonObject(cookies);
        }
        return given()
                .config(RestAssuredConfig.config())
                .spec(getRequestSpecification(queryParameter, header, body, cookie))
                .request(method, endPoint);

    }

    public String getResponseBodyAsString(Response response){
        return response.getBody().asString();
    }
}
