package com.aston.blindfitness.Utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * NewsUtils will get certain news content for the news api
 */
public class NewsUtils {

    /**
     * ArrayList of colordrawables that will be used
     */
    public static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }

    /**
     * Convert Date into DateTime
     *
     * @param oldstringDate
     * @return
     */
    public static String DateToTimeFormat(String oldstringDate) {
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        String isTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }

    /**
     * @param oldstringDate
     * @return new date format
     */
    public static String DateFormat(String oldstringDate) {
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    /**
     * @return locale of country for the news source
     */
    public static String getCountry() {
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getCountry());
        return country.toLowerCase();
    }

    /**
     * @return category for the news source
     */
    public static String getCategory() {
        String category = "health";
        return category.toLowerCase();
    }

    /**
     * @return description of the news source
     */
    public static String getDescription() {
        String description = "visually impaired";
        return description.toLowerCase();
    }

    /**
     * @return language of the news source
     */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String language = String.valueOf(locale.getLanguage());
        return language.toLowerCase();
    }
}