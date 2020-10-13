package cn.wode490390.mcbe.lobby.network;

import cn.wode490390.mcbe.lobby.Main;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;

@Log4j2
public class ServerEventHandler implements BedrockServerEventHandler {

    private static final BedrockPong pong = new BedrockPong();
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

    public ServerEventHandler(Main main) {
        this.main = main;
    }

    @Override
    public boolean onConnectionRequest(InetSocketAddress address) {
        return true; // TODO: blacklist
    }

    @Override
    public BedrockPong onQuery(InetSocketAddress address) {
        return pong;
    }

    @Override
    public void onSessionCreation(BedrockServerSession session) {
        InetSocketAddress address = session.getAddress();
        session.addDisconnectHandler(reason -> {
            this.main.getSessions().remove(address);
            log.info("{} logged out", address);
        });
        session.setPacketHandler(new ServerPacketHandler(this.main, session));
        this.main.getSessions().put(address, session);
    }
}
