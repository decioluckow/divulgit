package org.divulgit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO mover para lugar melhor
public class HashTagIdentifierUtil {
	
    private static Pattern HASH_TAG_PATTERN = Pattern.compile("(?:\\s|\\A|^)[##]+([A-Za-z0-9-_]+)");
	
    public static boolean containsHashTag(String text) {
    	return text.contains("#");
    }
    
    public static List<String> extractHashTag(String text) {
        var hashTags = new ArrayList<String>();
        final Matcher matcher = HASH_TAG_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                hashTags.add(matcher.group(i));
            }
        }
        return hashTags;
    }

}
