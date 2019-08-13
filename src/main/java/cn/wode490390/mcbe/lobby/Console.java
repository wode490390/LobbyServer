package cn.wode490390.mcbe.lobby;

import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class Console extends SimpleTerminalConsole {

    private final Main main;

    public Console(Main main) {
        this.main = main;
    }

    @Override
    protected boolean isRunning() {
        return main.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        main.getCommandManager().dispatch(command);
    }

    @Override
    protected void shutdown() {
        main.shutdown();
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        builder.completer(new ConsoleCompleter(main));
        builder.appName("LobbyServer");
        builder.option(LineReader.Option.HISTORY_BEEP, false);
        builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true);
        builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true);
        return super.buildReader(builder);
    }
}
