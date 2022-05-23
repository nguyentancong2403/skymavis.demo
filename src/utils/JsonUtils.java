package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JsonUtils {
    public static JSONObject convertJsonFileToJSONObject(String filePath){
        String jsonStr = "";
        try {
            if (!filePath.endsWith(".json")){
                return null;
            }
            File file = new File(System.getProperty("user.dir") + "/src/data/" + filePath);
            InputStream inputStream = new FileInputStream(file);
            jsonStr = org.apache.commons.io.IOUtils.toString(inputStream);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return new JSONObject(jsonStr);
    }

    public static JSONObject convertStringToJSONObject(String string){
        return new JSONObject(string);
    }
    public static JSONObject updateValueInJSONObject(JSONObject obj, String keyValue, String newValue) {

        Iterator<String> iterator = obj.keys();
        String key = "";

        while (iterator.hasNext()) {
            key = iterator.next();
            if ((obj.optJSONArray(key) == null) && (obj.optJSONObject(key) == null)) {
                if ((key.equals(keyValue))) {
                    // put new value
                    obj.put(key, newValue);
                    return obj;
                }
            }

            if (obj.optJSONObject(key) != null) {
                updateValueInJSONObject(obj.getJSONObject(key), keyValue, newValue);
            }

            if (obj.optJSONArray(key) != null) {
                JSONArray jArray = obj.getJSONArray(key);
                for (int i = 0; i < jArray.length(); i++) {
                    updateValueInJSONObject(jArray.getJSONObject(i), keyValue, newValue);
                }
            }
        }
        return obj;
    }

    public static Map<String, String> getMapFromJsonObject(JSONObject object) {
        Map<String, String> map = new HashMap<>();
        object.keys().forEachRemaining(key -> {
            Object value = object.get(key);
            map.put(key, value.toString());
        });

        return map;
    }

    public static boolean validateResponseSchema(String actualJsonString, String schemaFilePath){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // create an instance of the JsonSchemaFactory using version flag
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
            InputStream jsonStream = new ByteArrayInputStream(actualJsonString.getBytes());
            InputStream schemaStream = convertFileToInputStream(schemaFilePath);
            JsonNode json = objectMapper.readTree(jsonStream);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            Set<ValidationMessage> validationResult = schema.validate(json);
            if (validationResult.isEmpty()) {

                // show custom message if there is no validation error
                System.out.println( "There is no validation errors" );
                return true;

            } else {

                // show all the validation error
                validationResult.forEach(vm -> System.out.println(vm.getMessage()));
                return false;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static InputStream convertFileToInputStream(String filePath) throws IOException {
        if (!filePath.endsWith(".json")){
            return null;
        }
        File file = new File(System.getProperty("user.dir") + "/src/" + filePath);
        return new  FileInputStream(file);
    }
}
