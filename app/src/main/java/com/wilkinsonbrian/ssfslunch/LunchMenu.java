package com.wilkinsonbrian.ssfslunch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wilkibr on 7/5/2016.
 */
public class LunchMenu {

    public String newMenu;

    public LunchMenu(String rawXML) {
        newMenu = stripOutXML(rawXML);
    }

    public String stripOutXML(String rawXML) {
        StringBuilder menu = new StringBuilder();
        Pattern pattern = Pattern.compile("<w:t( .*?)?>(.*?)</w:t>");
        Matcher m = pattern.matcher(rawXML);
        while (m.find()) {
            menu.append(m.group(2));
        }
        return new String(menu);
    }
}
