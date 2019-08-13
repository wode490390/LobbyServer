package cn.wode490390.mcbe.lobby;

import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import java.net.InetSocketAddress;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ServerPacketHandler implements BedrockPacketHandler {

    private final Main main;
    private final BedrockServerSession session;

    public ServerPacketHandler(Main main, BedrockServerSession session) {
        this.main = main;
        this.session = session;
    }

    @Override
    public boolean handle(LoginPacket packet) {
        int protocol = packet.getProtocolVersion();
        int accept = ServerPacketFactory.CODEC.getProtocolVersion();
        if (protocol > accept) {
            //ignore?
        } else if (protocol < accept) {
            //ignore?
        } else {
            this.session.setPacketCodec(ServerPacketFactory.CODEC);
            this.session.sendPacket(ServerPacketFactory.getPlayStatusPacket0());
            this.session.sendPacket(ServerPacketFactory.getResourcePacksInfoPacket());
            log.info(this.session.getAddress() + " logged in");
        }
        return true;
    }

    @Override
    public boolean handle(ResourcePackClientResponsePacket packet) {
        switch (packet.getStatus()) {
            case HAVE_ALL_PACKS:
                this.session.sendPacket(ServerPacketFactory.getResourcePackStackPacket());
                break;
            case COMPLETED:
                this.session.sendPacket(ServerPacketFactory.getStartGamePacket());
                this.session.sendPacket(ServerPacketFactory.getPlayStatusPacket3());
                break;
        }
        return true;
    }

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        this.session.sendPacket(ServerPacketFactory.createModalFormRequestPacket());
        log.info(this.session.getAddress() + " joined the game");
        return true;
    }

    @Override
    public boolean handle(ModalFormResponsePacket packet) {
        if (packet.getFormId() == 0) {
            try {
                int id = Integer.parseInt(packet.getFormData().trim());
                if (id == main.getMenu().getButtons().size()) {
                    this.session.sendPacket(ServerPacketFactory.createDisconnectPacket("Bye :)"));
                }
                Map<String, Object> button = main.getMenu().getButtons().get(id);
                InetSocketAddress address = new InetSocketAddress(String.valueOf(button.get("host")), ((Number) button.get("port")).intValue());
                this.session.sendPacket(ServerPacketFactory.createTransferPacket(address));
                log.info("{} has been transferred to {}", this.session.getAddress(), address);
            } catch (Exception e) {
                this.session.sendPacket(ServerPacketFactory.createModalFormRequestPacket());
            }
        }
        return true;
    }
}
