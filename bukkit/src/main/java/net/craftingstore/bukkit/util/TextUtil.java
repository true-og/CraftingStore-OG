package net.craftingstore.bukkit.util;

import java.util.regex.Pattern;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TextUtil {

    // Regular expressions to match color codes starting with § or & in Strings.
    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("(?i)[§&][0-9A-FK-OR]");
    private static final Pattern MINIMESSAGE_TAG_PATTERN = Pattern.compile("<[^>]*>");

    // Sends a formatted message to the console or player.
    public static void craftingStoreMessage(CommandSender sender, String message) {

        if (sender instanceof Player) {

            Player p = (Player) sender;
            p.sendMessage(legacySerializerAnyCase((message)));

        } else {

            logToConsole(message);
        }
    }

    public static void logToConsole(String message) {

        TextComponent component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);

        String serialized = MiniMessage.miniMessage().serialize(component);
        String stripped = stripColors(serialized);

        CraftingStoreBukkit.getPlugin().getLogger().info(CraftingStoreBukkit.getPrefix() + stripped);
    }

    public static TextComponent legacySerializerAnyCase(String subject) {

        int count = 0;
        // Count the number of '&' characters to determine the size of the array
        for (char c : subject.toCharArray()) {

            if (c == '&') {

                count++;
            }
        }

        // Create an array to store the positions of '&' characters
        int[] positions = new int[count];
        int index = 0;
        // Find the positions of '&' characters and store in the array
        for (int i = 0; i < subject.length(); i++) {

            if (subject.charAt(i) == '&') {

                if (isUpperBukkitCode(subject.charAt(i + 1))) {

                    subject = replaceCharAtIndex(subject, (i + 1), Character.toLowerCase(subject.charAt(i + 1)));
                }

                positions[index++] = i;
            }
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(subject);
    }

    private static boolean isUpperBukkitCode(char input) {

        char[] bukkitColorCodes = {'A', 'B', 'C', 'D', 'E', 'F', 'K', 'L', 'M', 'N', 'O', 'R'};
        boolean match = false;

        // Loop through each character in the array.
        for (char c : bukkitColorCodes) {
            // Check if the current character in the array is equal to the input character.
            if (c == input) {

                match = true;
            }
        }

        return match;
    }

    private static String replaceCharAtIndex(String original, int index, char newChar) {

        // Check if the index is valid
        if (index >= 0 && index < original.length()) {

            // Create a new string with the replaced character
            return original.substring(0, index) + newChar + original.substring(index + 1);
        }

        // If the index is invalid, return the original string
        return original;
    }

    public static String stripColors(String input) {
        if (input == null) return null;

        // Remove MiniMessage tags
        String stripped = MINIMESSAGE_TAG_PATTERN.matcher(input).replaceAll("");

        // Remove legacy color codes
        stripped = LEGACY_COLOR_PATTERN.matcher(stripped).replaceAll("");

        return stripped;
    }
}
