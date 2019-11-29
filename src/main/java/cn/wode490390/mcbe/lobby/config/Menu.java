package cn.wode490390.mcbe.lobby.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Menu {

    private static final JsonMapper JSON_MAPPER = (JsonMapper) new JsonMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static Menu load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return JSON_MAPPER.readValue(reader, Menu.class);
        }
    }

    @JsonProperty("title")
    private String title = "Please choose a server";
    @JsonProperty("content")
    private String content = "";
    @JsonProperty("buttons")
    private List<Map<String, Object>> buttons = new ArrayList<>();

    public String toForm() {
        ArrayNode array = JSON_MAPPER.createArrayNode();
        this.buttons.forEach((map) -> {
            array.add(JSON_MAPPER.createObjectNode()
                    .put("text", String.valueOf(map.get("text"))));
        });
        array.add(JSON_MAPPER.createObjectNode()
                .put("text", "Exit"));
        ObjectNode node = JSON_MAPPER.createObjectNode()
                .put("type", "form")
                .put("title", this.title)
                .put("content", this.content)
                .set("buttons", array);
        try {
            return JSON_MAPPER.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"form\",\"title\":\"Please check the configuration\",\"content\":\"I let you down. Sorry :(\",\"buttons\":[{\"text\":\"Exit\"}]}";
        }
    }

    public List<Map<String, Object>> getButtons() {
        return this.buttons;
    }
}
