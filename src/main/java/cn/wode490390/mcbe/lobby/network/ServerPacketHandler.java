package cn.wode490390.mcbe.lobby.network;

import cn.wode490390.mcbe.lobby.Main;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.util.Map;

@Log4j2
public class ServerPacketHandler implements BedrockPacketHandler {

    private final Main main;
    private final BedrockServerSession session;

    public ServerPacketHandler(Main main, BedrockServerSession session) {
        this.main = main;
        this.session = session;
    }

    @Override
    public boolean handle(LoginPacket packet) { // TODO: whitelist
        this.session.setPacketCodec(ServerPacketFactory.CODEC);
        int protocol = packet.getProtocolVersion();
        int accept = ServerPacketFactory.CODEC.getProtocolVersion();
        if (protocol > accept) {
            this.session.sendPacket(ServerPacketFactory.getPlayStatusPacket2());
            if (protocol < 137) {
                this.session.sendPacketImmediately(ServerPacketFactory.createDisconnectPacket("disconnectionScreen.outdatedServer"));
            }
        } else if (protocol < accept) {
            this.session.sendPacket(ServerPacketFactory.getPlayStatusPacket1());
            if (protocol < 137) {
                this.session.sendPacketImmediately(ServerPacketFactory.createDisconnectPacket("disconnectionScreen.outdatedClient"));
            }
        } else {
            this.session.sendPacket(ServerPacketFactory.getPlayStatusPacket0());
            this.session.sendPacket(ServerPacketFactory.getResourcePacksInfoPacket());
            this.session.sendPacket(ServerPacketFactory.getNetworkSettingsPacket1());
            log.info(this.session.getAddress() + " logged in");
        }
        return true;
    }

    @Override
    public boolean handle(ResourcePackClientResponsePacket packet) {
        switch (packet.getStatus()) {
            case HAVE_ALL_PACKS:
                this.session.sendPacket(ServerPacketFactory.getResourcePackStackPacket());
                return true;
            case COMPLETED:
                this.session.sendPacket(ServerPacketFactory.getStartGamePacket());
                this.session.sendPacket(ServerPacketFactory.getBiomeDefinitionListPacket());
                return true;
        }
        return false;
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
                if (id == this.main.getMenu().getButtons().size()) {
                    this.session.sendPacket(ServerPacketFactory.createDisconnectPacket("Bye :)"));
                }
                Map<String, Object> button = this.main.getMenu().getButtons().get(id);
                InetSocketAddress address = new InetSocketAddress(String.valueOf(button.get("host")), ((Number) button.get("port")).intValue());
                this.session.sendPacket(ServerPacketFactory.createTransferPacket(address));
                log.info("{} has been transferred to {}", this.session.getAddress(), address);
            } catch (Exception e) {
                this.session.sendPacket(ServerPacketFactory.createModalFormRequestPacket());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {
        this.session.sendPacket(ServerPacketFactory.getChunkRadiusUpdatedPacket1());
        this.session.sendPacket(ServerPacketFactory.getPlayStatusPacket3());
        return true;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        this.session.sendPacket(ServerPacketFactory.createModalFormRequestPacket());
        return true;
    }
}
