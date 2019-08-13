package cn.wode490390.mcbe.lobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerConfiguration {

    private static final YAMLMapper YAML_MAPPER = (YAMLMapper) new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static ServerConfiguration load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return YAML_MAPPER.readValue(reader, ServerConfiguration.class);
        }
    }

    @JsonProperty("host")
    private String host = "0.0.0.0";
    @JsonProperty("port")
    private int port = 19132;
    @JsonProperty("log-level")
    private int logLevel = 1;
    @JsonProperty("motd")
    private String motd = "wodeLobby";
    @JsonProperty("sub-motd")
    private String submotd = "lightweight lobby server";

    InetSocketAddress getServerAddress() {
        return new InetSocketAddress(host, port);
    }

    int getLogLevel() {
        return logLevel;
    }

    String getMotd() {
        return motd;
    }

    String getSubMotd() {
        return submotd;
    }
}
