package cn.wode490390.mcbe.lobby;

import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import java.net.InetSocketAddress;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ServerEventHandler implements BedrockServerEventHandler {

    private static BedrockPong pong = new BedrockPong();
    static {
        pong.setEdition("MCPE");
        pong.setMotd(Main.getInstance().getConfiguration().getMotd());
        pong.setProtocolVersion(ServerPacketFactory.CODEC.getProtocolVersion());
        pong.setVersion(ServerPacketFactory.CODEC.getMinecraftVersion());
        pong.setPlayerCount(0);
        pong.setMaximumPlayerCount(1);
        pong.setSubMotd(Main.getInstance().getConfiguration().getSubMotd());
        pong.setGameType("Survival");
    }

    private final Main main;

    ServerEventHandler(Main main) {
        this.main = main;
    }

    @Override
    public boolean onConnectionRequest(InetSocketAddress address) {
        return true; // TODO: whitelist
    }

    @Override
    public BedrockPong onQuery(InetSocketAddress address) {
        return pong;
    }

    @Override
    public void onSessionCreation(BedrockServerSession session) {
        InetSocketAddress address = session.getAddress();
        session.addDisconnectHandler((reason) -> {
            main.getSessions().remove(address);
            log.info("{} logged out", address);
        });
        session.setPacketHandler(new ServerPacketHandler(main, session));
        main.getSessions().put(address, session);
    }
}
