package com.wilkinsonbrian.ssfslunch;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wilkibr on 7/5/2016.
 */
public class LunchMenu {

    private String newMenu;
    ArrayList<String> individualDayMenus = new ArrayList<>();
    private String[] regExpForDays = {"MONDAY(.*?)TUESDAY","TUESDAY(.*?)WEDNESDAY",
            "WEDNESDAY(.*?)THURSDAY", "THURSDAY(.*?)FRIDAY", "FRIDAY.[0-9](.*?)DINNER ENTREE"};

    public LunchMenu(String rawXML) {
        newMenu = stripOutXML(rawXML);
        getCompleteMenuForEachDay();
    }

    private String stripOutXML(String rawXML) {
        StringBuilder menu = new StringBuilder();
        Pattern pattern = Pattern.compile("<w:t( .*?)?>(.*?)</w:t>");
        Matcher m = pattern.matcher(rawXML);
        while (m.find()) {
            menu.append(m.group(2));
        }
        return new String(menu);
    }

    public String getDailyMenu(String text, String expression) {
        Pattern pattern = Pattern.compile(expression);
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }

    private void getCompleteMenuForEachDay() {
        for (String expression : regExpForDays) {
            Pattern pattern = Pattern.compile(expression);
            Matcher m = pattern.matcher(newMenu);
            if (m.find()) {
                individualDayMenus.add(m.group(1));
            } else individualDayMenus.add("");
        }
    }

    public String getLunchEntree(int dayOfWeek) {
        Pattern pattern = Pattern.compile("LUNCH ENTRÉE(.*?)VEGETARIAN");
        Matcher m = pattern.matcher(individualDayMenus.get(dayOfWeek));
        if (m.find()) {
            return m.group(1);
        } else return "";
    }

    public String getVegetarianEntree(int dayOfWeek) {
        Pattern pattern = Pattern.compile("VEGETARIAN ENTRÉE(.*?)SIDES");
        Matcher m = pattern.matcher(individualDayMenus.get(dayOfWeek));
        if (m.find()) {
            return m.group(1);
        } else return "";
    }

    public String getSides(int dayOfWeek) {
        Pattern pattern = Pattern.compile("SIDES(.*?)SOUPER SOUPS");
        Matcher m = pattern.matcher(individualDayMenus.get(dayOfWeek));
        if (m.find()) {
            return m.group(1);
        } else return "";
    }

    public String getSoups(int dayOfWeek) {
        Pattern pattern = Pattern.compile("SOUPER SOUPS(.*?)DOWNTOWN");
        Matcher m = pattern.matcher(individualDayMenus.get(dayOfWeek));
        if (m.find()) {
            return m.group(1);
        } else return "";
    }

    public String getDeli(int dayOfWeek) {
        Pattern pattern = Pattern.compile("DOWNTOWN DELI(.*?)DINNER");
        Matcher m = pattern.matcher(individualDayMenus.get(dayOfWeek));
        if (m.find()) {
            return m.group(1);
        } else return "";
    }
}
