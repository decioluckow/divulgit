package org.divulgit;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UniRestTest {
    public static void main(String[] args) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse
                = Unirest.get("https://git.neogrid.com/api/v4/user/")
                .header("Private-Token", "DT8XzN3Ac8yNow5zR54v")
                .asJson();

        System.out.println(jsonResponse);
    }
}
