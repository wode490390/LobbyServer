package cn.wode490390.mcbe.lobby;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.data.GamePublishSetting;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.v361.Bedrock_v361;
import java.net.InetSocketAddress;

public class ServerPacketFactory {

    public static final BedrockPacketCodec CODEC = Bedrock_v361.V361_CODEC;

    private static final Vector2f vector2f = new Vector2f();
    private static final Vector3f vector3f = new Vector3f();
    private static final Vector3i vector3i = new Vector3i();

    private static final StartGamePacket startGamePacket = new StartGamePacket();
    static {
        startGamePacket.setUniqueEntityId(0);
        startGamePacket.setRuntimeEntityId(0);
        startGamePacket.setPlayerGamemode(0);
        startGamePacket.setPlayerPosition(vector3f);
        startGamePacket.setRotation(vector2f);
        startGamePacket.setSeed(0);
        startGamePacket.setDimensionId(0);
        startGamePacket.setGeneratorId(0);
        startGamePacket.setLevelGamemode(0);
        startGamePacket.setDifficulty(0);
        startGamePacket.setDefaultSpawn(vector3i);
        startGamePacket.setAcheivementsDisabled(true);
        startGamePacket.setTime(0);
        startGamePacket.setEduLevel(false);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setPlatformLockedContentConfirmed(false);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setCommandsEnabled(false);
        startGamePacket.setTexturePacksRequired(false);
        startGamePacket.setBonusChestEnabled(false);
        startGamePacket.setStartingWithMap(false);
        startGamePacket.setDefaultPlayerPermission(1);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setBehaviorPackLocked(false);
        startGamePacket.setResourcePackLocked(false);
        startGamePacket.setFromLockedWorldTemplate(false);
        startGamePacket.setUsingMsaGamertagsOnly(false);
        startGamePacket.setFromWorldTemplate(false);
        startGamePacket.setWorldTemplateOptionLocked(false);
        startGamePacket.setOnlySpawningV1Villagers(false);
        startGamePacket.setLevelId("");
        startGamePacket.setWorldName("");
        startGamePacket.setPremiumWorldTemplateId("");
        startGamePacket.setTrial(false);
        startGamePacket.setCurrentTick(0);
        startGamePacket.setEnchantmentSeed(0);
        startGamePacket.setMultiplayerCorrelationId("");
    }
    public static StartGamePacket getStartGamePacket() {
        return startGamePacket;
    }

    private static final PlayStatusPacket playStatusPacket0 = new PlayStatusPacket();
    static {
        playStatusPacket0.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
    }
    public static PlayStatusPacket getPlayStatusPacket0() {
        return playStatusPacket0;
    }

    private static final ResourcePacksInfoPacket resourcePacksInfoPacket = new ResourcePacksInfoPacket();
    static {
        resourcePacksInfoPacket.setForcedToAccept(false);
        resourcePacksInfoPacket.setScriptingEnabled(false);
    }
    public static ResourcePacksInfoPacket getResourcePacksInfoPacket() {
        return resourcePacksInfoPacket;
    }

    private static final ResourcePackStackPacket resourcePackStackPacket = new ResourcePackStackPacket();
    static {
        resourcePackStackPacket.setForcedToAccept(false);
        resourcePackStackPacket.setExperimental(false);
    }
    public static ResourcePackStackPacket getResourcePackStackPacket() {
        return resourcePackStackPacket;
    }

    private static final PlayStatusPacket playStatusPacket3 = new PlayStatusPacket();
    static {
        playStatusPacket3.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
    }
    public static PlayStatusPacket getPlayStatusPacket3() {
        return playStatusPacket3;
    }

    private static final ModalFormRequestPacket modalFormRequestPacket = new ModalFormRequestPacket();
    static {
        modalFormRequestPacket.setFormId(0);
        modalFormRequestPacket.setFormData(Main.getInstance().getMenu().toForm());
    }
    public static ModalFormRequestPacket createModalFormRequestPacket() {
        return modalFormRequestPacket;
    }

    public static TransferPacket createTransferPacket(InetSocketAddress address) {
        TransferPacket transferPacket = new TransferPacket();
        transferPacket.setAddress(address.getAddress().getHostName());
        transferPacket.setPort(address.getPort());
        return transferPacket;
    }

    public static DisconnectPacket createDisconnectPacket(String msg) {
        DisconnectPacket disconnectPacket = new DisconnectPacket();
        disconnectPacket.setKickMessage(msg);
        return disconnectPacket;
    }
}
