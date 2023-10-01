package pitheguy.autoblocks.util;

import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public class BufferUtil {
    public static <K, V> void writeMap(FriendlyByteBuf buffer, Map<K, V> map, Consumer<K> keyEncoder, Consumer<V> valueEncoder) {
        buffer.writeVarInt(map.size());
        map.forEach((key, value) -> {
            keyEncoder.accept(key);
            valueEncoder.accept(value);
        });
    }

    public static <K, V> Map<K, V> readMap(FriendlyByteBuf buffer, Supplier<K> keyDecoder, Supplier<V> valueDecoder) {
        int size = buffer.readVarInt();
        Map<K, V> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) map.put(keyDecoder.get(), valueDecoder.get());
        return map;
    }
}
