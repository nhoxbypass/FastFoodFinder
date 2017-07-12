package com.iceteaviet.fastfoodfinder.utils;

import com.iceteaviet.fastfoodfinder.model.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by binhlt on 23/11/2016.
 */

public class DataUtils {

    public static List<Comment> getComments() {
        String[] names = new String[]{
                "Scarlett",
                "Bà Tưng",
                "Ngọc Trinh",
                "Chi Pu",
                "Trâm Anh"};

        String[] avatars = new String[]{
                "http://i.imgur.com/u9mpkNC.jpg",
                "http://i.imgur.com/qQxDOZT.jpg",
                "http://i.imgur.com/VJ3FAB2.jpg",
                "http://i.imgur.com/31vADfq.jpg",
                "http://i.imgur.com/XUem99n.jpg"};

        String[] contents = new String[]{
                "Octopus salad with bacon, sundries tomatoes, potatoes and some other magic ingredients was absolutely or of this world delicious. I loved it so much I forgot to snap an Instagram photo.",
                "Loved it here. Great atmosphere, was packed during lunch time. The smoked salmon eggs on bread and salted caramel cupcake were really good. The sunrise drink was like a lukewarm smoothie though.",
                "Perfect place to work, delicious food and coffee, little bit expensive but staff let you work peacefully. Quite busy/noisy but a wonderful place to discover. Hey, they make their own cupcakes :)",
                "Great atmosphere, very nice place for a quick snack or a meal. Ask for the meat pies. The lasagna is great too. And always check out the specials on the blackboard",
                "Very friendly stuff, the fruit juices and food are super good! Very trendy and the shop has nice quality things ! Happy to stumble into this cafeteria :D"};

        String[] mediaUrls = new String[]{
                "http://i.imgur.com/RHdsWRW.jpg",
                "http://i.imgur.com/IsfQQhd.jpg",
                "",
                "http://i.imgur.com/tNu5G5D.jpg",
                "http://i.imgur.com/QruogAF.jpg"};

        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < getRandomInt(3, 5); i++) {
            int index = getRandomInt(0, 4);
            comments.add(new Comment(names[index],
                    avatars[index],
                    contents[getRandomInt(0, 4)],
                    mediaUrls[getRandomInt(0, 4)],
                    getRamdomDate(),
                    getRandomInt(0, 4)));
        }
        return comments;
    }

    public static Comment createUserComment(String content) {
        SimpleDateFormat dfDateTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.getDefault());
        String date = dfDateTime.format(new Date());
        return new Comment("Tam Doan",
                "http://i.imgur.com/wfV3jkw.jpg",
                content,
                "",
                date,
                getRandomInt(0, 4));
    }

    public static int getRandomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static String getRamdomDate() {
        SimpleDateFormat dfDateTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.getDefault());
        int year = getRandomInt(2015, 2016);
        int month = getRandomInt(0, 11);
        int hour = getRandomInt(9, 22);
        int min = getRandomInt(0, 59);
        int sec = getRandomInt(0, 59);
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = getRandomInt(1, gc.getActualMaximum(gc.DAY_OF_MONTH));
        gc.set(year, month, day, hour, min, sec);
        return dfDateTime.format(gc.getTime());
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        try {
            Date date = sf.parse(rawJsonDate);
            then.setTime(date);
            long nowMs = now.getTimeInMillis();
            long thenMs = then.getTimeInMillis();
            long diff = nowMs - thenMs;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffMinutes <= 0) {
                return "just now";
            } else if (diffMinutes < 60) {
                return diffMinutes + "m";
            } else if (diffHours < 24) {
                return diffHours + "h";
            } else if (diffDays < 7) {
                return diffDays + "d";
            } else {
                sf = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
                return sf.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "unknown";
    }
}