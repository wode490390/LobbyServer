package cn.wode490390.mcbe.lobby.console;

import cn.wode490390.mcbe.lobby.Main;
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
        return this.main.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        this.main.getCommandManager().dispatch(command);
    }

    @Override
    protected void shutdown() {
        this.main.shutdown();
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        builder.completer(new ConsoleCompleter(this.main));
        builder.appName("LobbyServer");
        builder.option(LineReader.Option.HISTORY_BEEP, false);
        builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true);
        builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true);
        return super.buildReader(builder);
    }
}
