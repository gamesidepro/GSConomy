package utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class Utils {
    public static void sendMessage(Player player, Object... args) {
        if (args.length != 0) {
            player.sendMessage(Text.of(args));
	}
    }
}
