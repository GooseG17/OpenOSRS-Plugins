package net.runelite.client.plugins.miscplugins.killswitch.killtriggers;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.plugins.miscplugins.killswitch.KillTrigger;
import net.runelite.client.util.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MessageKillTrigger extends KillTrigger {
    private static final Splitter NEWLINE_SPLITTER = Splitter
            .on("\n")
            .omitEmptyStrings()
            .trimResults();
    private final List<Pattern> filteredPatterns = new ArrayList<>();

    public MessageKillTrigger(String words, String regex) {
        super(!words.isEmpty() || !regex.isEmpty());

        Text.fromCSV(words).stream()
                .map(s -> Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE))
                .forEach(filteredPatterns::add);

        NEWLINE_SPLITTER.splitToList(regex).stream()
                .map(s ->
                {
                    try
                    {
                        return Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    }
                    catch (PatternSyntaxException ex)
                    {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(filteredPatterns::add);
        for (Pattern filteredPattern : filteredPatterns) {
            System.out.println(filteredPattern.pattern());
        }
    }

    public boolean shouldKillInput(ChatMessage event) {
        if (!shouldCheck) return false;
        if (event.getType() == ChatMessageType.GAMEMESSAGE)
        {
            if (censorMessage(event.getMessage()))
            {
                shouldCheck = false;
                System.out.println(this.getClass().getSimpleName() + " kill triggered!");
                return true;
            }
        }
        return false;
    }


    private final CharMatcher jagexPrintableCharMatcher = Text.JAGEX_PRINTABLE_CHAR_MATCHER;
    boolean censorMessage(final String message)
    {
        String strippedMessage = jagexPrintableCharMatcher.retainFrom(message)
                .replace('\u00A0', ' ');
        for (Pattern pattern : filteredPatterns)
        {
            Matcher m = pattern.matcher(strippedMessage);
            if (m.matches())
            {
                return true;
            }
        }
        return false;
    }
}
