package cn.wode490390.mcbe.lobby;

import cn.wode490390.mcbe.lobby.config.Menu;
import cn.wode490390.mcbe.lobby.config.ServerConfiguration;
import cn.wode490390.mcbe.lobby.console.Console;
import cn.wode490390.mcbe.lobby.console.ConsoleCommandManager;
import cn.wode490390.mcbe.lobby.network.ServerEventHandler;
import cn.wode490390.mcbe.lobby.network.ServerPacketFactory;
import com.nukkitx.protocol.bedrock.BedrockServer;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class Main {

    public final static Properties GIT_INFO = getGitInfo();
    public final static String VERSION = getVersion();

    private static boolean TITLE = false;

    private static Main instance;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private ServerConfiguration configuration;

    private final Thread consoleThread;
    private final Console console;

    private final ConsoleCommandManager commandManager;

    private final BedrockServer server;
    private final Map<InetSocketAddress, BedrockServerSession> sessions = new HashMap<>();

    private static String serverHost;
    private static Integer serverPort;

    private InetSocketAddress bindingAddress;

    private Menu menu;

    public static void main(String[] args) {
        if (instance != null) {
            throw new IllegalStateException("Already initialized");
        }

        System.setProperty("log4j.skipJansi", "false");

        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", "Shows this page").forHelp();
        OptionSpec<Void> titleSpec = parser.accepts("enable-title", "Enables title at the top of the window");
        OptionSpec<String> vSpec = parser.accepts("v", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> verbositySpec = parser.accepts("verbosity", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> hSpec = parser.accepts("h", "Host to listen on").withRequiredArg().ofType(String.class);
        OptionSpec<String> hostSpec = parser.accepts("host", "Host to listen on").withRequiredArg().ofType(String.class);
        OptionSpec<String> serveripSpec = parser.accepts("server-ip", "Host to listen on").withRequiredArg().ofType(String.class);
        OptionSpec<Integer> pSpec = parser.accepts("p", "Port to listen on").withRequiredArg().ofType(Integer.class);
        OptionSpec<Integer> portSpec = parser.accepts("port", "Port to listen on").withRequiredArg().ofType(Integer.class);
        OptionSpec<Integer> serverportSpec = parser.accepts("server-port", "Port to listen on").withRequiredArg().ofType(Integer.class);
        OptionSet options = parser.parse(args);

        if (options.has(helpSpec)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ignore) {

            }
            return;
        }

        TITLE = options.has(titleSpec);

        String verbosity = options.valueOf(vSpec);
        if (verbosity == null) {
            verbosity = options.valueOf(verbositySpec);
        }
        if (verbosity != null) {
            try {
                Level level = Level.valueOf(verbosity);
                setLogLevel(level);
            } catch (Exception ignore) {

            }
        }

        serverHost = options.valueOf(hSpec);
        if (serverHost == null) {
            serverHost = options.valueOf(hostSpec);
        }
        if (serverHost == null) {
            serverHost = options.valueOf(serveripSpec);
        }

        serverPort = options.valueOf(pSpec);
        if (serverPort == null) {
            serverPort = options.valueOf(portSpec);
        }
        if (serverPort == null) {
            serverPort = options.valueOf(serverportSpec);
        }

        try {
            if (TITLE) {
                System.out.print((char) 0x1b + "]0;Server is starting up..." + (char) 0x07);
            }
            new Main();
        } catch (Throwable t) {
            log.throwing(t);
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Server Stopped" + (char) 0x07);
        }

        System.exit(0);
    }

    private Main() throws IOException {
        instance = this;

        this.console = new Console(this);
        this.consoleThread = new Thread() {
            @Override
            public void run() {
                console.start();
            }
        };
        this.consoleThread.start();

        log.info("Loading configuration...");
        Path configPath = Paths.get(".").resolve("config.yml");
        if (Files.notExists(configPath) || !Files.isRegularFile(configPath)) {
            Files.copy(Main.class.getClassLoader().getResourceAsStream("config.yml"), configPath, StandardCopyOption.REPLACE_EXISTING);
        }
        this.configuration = ServerConfiguration.load(configPath);

        int level = this.configuration.getLogLevel();
        switch (level) {
            case 1:
                setLogLevel(Level.INFO);
                break;
            case 2:
                setLogLevel(Level.DEBUG);
                break;
            default:
                if (level <= 0) {
                    setLogLevel(Level.OFF);
                } else {
                    setLogLevel(Level.TRACE);
                }
        }

        this.commandManager = new ConsoleCommandManager(this);

        log.info("Loading menu...");
        Path menuPath = Paths.get(".").resolve("menu.json");
        if (Files.notExists(menuPath) || !Files.isRegularFile(menuPath)) {
            Files.copy(Main.class.getClassLoader().getResourceAsStream("menu.json"), menuPath, StandardCopyOption.REPLACE_EXISTING);
        }
        this.menu = Menu.load(menuPath);

        log.info("Starting Minecraft: Bedrock Edition server version {} (protocol version {})", ServerPacketFactory.CODEC.getMinecraftVersion(), ServerPacketFactory.CODEC.getProtocolVersion());

        try {
            this.bindingAddress = new InetSocketAddress(serverHost, serverPort);
        } catch (Exception ignore) {

        }
        if (this.bindingAddress == null) {
            this.bindingAddress = this.configuration.getServerAddress();
        }

        this.server = new BedrockServer(this.bindingAddress);
        this.server.setHandler(new ServerEventHandler(this));
        this.server.bind().join();

        log.info("Server started on {}", this.server.getBindAddress());

        this.loop();
    }

    public ServerConfiguration getConfiguration() {
        return this.configuration;
    }

    public ConsoleCommandManager getCommandManager() {
        return this.commandManager;
    }

    public Map<InetSocketAddress, BedrockServerSession> getSessions() {
        return this.sessions;
    }

    public BedrockServer getServer() {
        return this.server;
    }

    public Menu getMenu() {
        return this.menu;
    }

    private void loop() {
        while (this.running.get()) {
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException ignore) {

            }
        }

        this.server.close();

        this.consoleThread.interrupt();
    }

    public void shutdown() {
        if (this.running.compareAndSet(true, false)) {
            synchronized (this) {
                this.notify();
            }
        }
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public static Main getInstance() {
        return instance;
    }

    public static void setLogLevel(Level level) {
        if (level != null) {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration log4jConfig = ctx.getConfiguration();
            LoggerConfig loggerConfig = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
            loggerConfig.setLevel(level);
            ctx.updateLoggers();
        } else {
            throw new NullPointerException("Log level cannot be null");
        }
    }

    private static Properties getGitInfo() {
        InputStream gitFileStream = Main.class.getClassLoader().getResourceAsStream("git.properties");
        if (gitFileStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(gitFileStream);
            } catch (IOException e) {
                return null;
            }
            return properties;
        }
        return null;
    }

    private static String getVersion() {
        StringBuilder version = new StringBuilder("git-");
        String commitId;
        if (GIT_INFO == null || (commitId = GIT_INFO.getProperty("git.commit.id.abbrev")) == null) {
            return version.append("null").toString();
        }
        return version.append(commitId).toString();
    }
}
