package com.xc.fast_deploy.moduleTest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.harborMirror.MirrorProjectDTO;

import java.util.Iterator;
import java.util.List;

public class JSONTest {
  
  public static void main(String[] args) {
    String s = "[\n" +
        "  {\n" +
        "    \"project_id\": 1,\n" +
        "    \"owner_id\": 1,\n" +
        "    \"name\": \"library\",\n" +
        "    \"creation_time\": \"2019-04-03T02:46:10.4044Z\",\n" +
        "    \"update_time\": \"2019-04-03T02:46:10.4044Z\",\n" +
        "    \"deleted\": false,\n" +
        "    \"owner_name\": \"\",\n" +
        "    \"togglable\": false,\n" +
        "    \"current_user_role_id\": 0,\n" +
        "    \"repo_count\": 0,\n" +
        "    \"chart_count\": 0,\n" +
        "    \"metadata\": {\n" +
        "      \"public\": \"true\"\n" +
        "    }\n" +
        "  },\n" +
        "  {\n" +
        "    \"project_id\": 2,\n" +
        "    \"owner_id\": 1,\n" +
        "    \"name\": \"test\",\n" +
        "    \"creation_time\": \"2019-04-03T02:52:20Z\",\n" +
        "    \"update_time\": \"2019-04-03T02:52:20Z\",\n" +
        "    \"deleted\": false,\n" +
        "    \"owner_name\": \"\",\n" +
        "    \"togglable\": false,\n" +
        "    \"current_user_role_id\": 0,\n" +
        "    \"repo_count\": 4,\n" +
        "    \"chart_count\": 0,\n" +
        "    \"metadata\": {\n" +
        "      \"public\": \"true\"\n" +
        "    }\n" +
        "  },\n" +
        "  {\n" +
        "    \"project_id\": 3,\n" +
        "    \"owner_id\": 1,\n" +
        "    \"name\": \"cloud\",\n" +
        "    \"creation_time\": \"2019-04-18T02:07:41Z\",\n" +
        "    \"update_time\": \"2019-04-18T02:07:41Z\",\n" +
        "    \"deleted\": false,\n" +
        "    \"owner_name\": \"\",\n" +
        "    \"togglable\": false,\n" +
        "    \"current_user_role_id\": 0,\n" +
        "    \"repo_count\": 1,\n" +
        "    \"chart_count\": 0,\n" +
        "    \"metadata\": {\n" +
        "      \"auto_scan\": \"false\",\n" +
        "      \"enable_content_trust\": \"false\",\n" +
        "      \"prevent_vul\": \"false\",\n" +
        "      \"public\": \"true\",\n" +
        "      \"severity\": \"low\"\n" +
        "    }\n" +
        "  },\n" +
        "  {\n" +
        "    \"project_id\": 5,\n" +
        "    \"owner_id\": 1,\n" +
        "    \"name\": \"find_center\",\n" +
        "    \"creation_time\": \"2019-04-23T02:07:47Z\",\n" +
        "    \"update_time\": \"2019-04-23T02:07:47Z\",\n" +
        "    \"deleted\": false,\n" +
        "    \"owner_name\": \"\",\n" +
        "    \"togglable\": false,\n" +
        "    \"current_user_role_id\": 0,\n" +
        "    \"repo_count\": 1,\n" +
        "    \"chart_count\": 0,\n" +
        "    \"metadata\": {\n" +
        "      \"public\": \"true\"\n" +
        "    }\n" +
        "  },\n" +
        "  {\n" +
        "    \"project_id\": 6,\n" +
        "    \"owner_id\": 1,\n" +
        "    \"name\": \"test_center\",\n" +
        "    \"creation_time\": \"2019-05-05T01:57:07Z\",\n" +
        "    \"update_time\": \"2019-05-05T01:57:07Z\",\n" +
        "    \"deleted\": false,\n" +
        "    \"owner_name\": \"\",\n" +
        "    \"togglable\": false,\n" +
        "    \"current_user_role_id\": 0,\n" +
        "    \"repo_count\": 2,\n" +
        "    \"chart_count\": 0,\n" +
        "    \"metadata\": {\n" +
        "      \"public\": \"true\"\n" +
        "    }\n" +
        "  },\n" +
        "  {\n" +
        "    \"project_id\": 7,\n" +
        "    \"owner_id\": 1,\n" +
        "    \"name\": \"project_code\",\n" +
        "    \"creation_time\": \"2019-05-08T08:45:40Z\",\n" +
        "    \"update_time\": \"2019-05-08T08:45:40Z\",\n" +
        "    \"deleted\": false,\n" +
        "    \"owner_name\": \"\",\n" +
        "    \"togglable\": false,\n" +
        "    \"current_user_role_id\": 0,\n" +
        "    \"repo_count\": 3,\n" +
        "    \"chart_count\": 0,\n" +
        "    \"metadata\": {\n" +
        "      \"public\": \"true\"\n" +
        "    }\n" +
        "  }\n" +
        "]";
    
    List<MirrorProjectDTO> projectDTOS = JSONArray.parseArray(s, MirrorProjectDTO.class);
    System.out.println(projectDTOS);
    
  }
  
}
