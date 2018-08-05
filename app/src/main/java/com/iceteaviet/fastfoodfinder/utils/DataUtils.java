package com.iceteaviet.fastfoodfinder.utils;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;

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

final public class DataUtils {
    private DataUtils() {

    }

    public static List<UserStoreList> getDefaultUserStoreLists() {
        List<UserStoreList> userStoreLists = new ArrayList<>();

        userStoreLists.add(new UserStoreList(0, new ArrayList<Integer>(), R.drawable.ic_profile_saved, "My Saved Places"));
        userStoreLists.add(new UserStoreList(1, new ArrayList<Integer>(), R.drawable.ic_profile_favourite, "My Favourite Places"));
        userStoreLists.add(new UserStoreList(2, new ArrayList<Integer>(), R.drawable.ic_profile_checkin, "My Checked in Places"));

        return userStoreLists;
    }

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

    public static int getStoreType(String key) {
        if (key.equals("circle_k"))
            return Constant.TYPE_CIRCLE_K;
        else if (key.equals("mini_stop"))
            return Constant.TYPE_MINI_STOP;
        else if (key.equals("family_mart"))
            return Constant.TYPE_FAMILY_MART;
        else if (key.equals("bsmart"))
            return Constant.TYPE_BSMART;
        else if (key.equals("shop_n_go"))
            return Constant.TYPE_SHOP_N_GO;
        else
            return Constant.TYPE_CIRCLE_K;
    }

    public static List<String> normalizeDistrictQuery(String queryString) {
        List<String> result = new ArrayList<>();
        String trimmedQuery = queryString.toLowerCase().trim();

        if (trimmedQuery.equals("gò vấp") || trimmedQuery.equals("go vap") || trimmedQuery.equals("govap")) {
            result.add("Gò Vấp");
            result.add("Go Vap");
        } else if (trimmedQuery.equals("tân bình") || trimmedQuery.equals("tan binh") || trimmedQuery.equals("tanbinh")) {
            result.add("Tân Bình");
            result.add("Tan Binh");
        } else if (trimmedQuery.equals("tân phú") || trimmedQuery.equals("tan phu") || trimmedQuery.equals("tanphu")) {
            result.add("Tân Phú");
            result.add("Tan Phu");
        } else if (trimmedQuery.equals("bình thạnh") || trimmedQuery.equals("binh thanh") || trimmedQuery.equals("binhthanh")) {
            result.add("Bình Thạnh");
            result.add("Binh Thanh");
        } else if (trimmedQuery.equals("phú nhuận") || trimmedQuery.equals("phu nhuan") || trimmedQuery.equals("phunhuan")) {
            result.add("Phú Nhuận");
            result.add("Phu Nhuan");
        } else if (trimmedQuery.equals("quận 9") || trimmedQuery.equals("quan 9")) {
            result.add("Quận 9");
            result.add("Quan 9");
            result.add("District 9");
        } else if (trimmedQuery.equals("quận 1") || trimmedQuery.equals("quan 1")) {
            result.add("Quận 1");
            result.add("Quan 1");
            result.add("District 1");
        } else if (trimmedQuery.equals("quận 2") || trimmedQuery.equals("quan 2")) {
            result.add("Quận 2");
            result.add("Quan 2");
            result.add("District 2");
        } else if (trimmedQuery.equals("quận 3") || trimmedQuery.equals("quan 3")) {
            result.add("Quận 3");
            result.add("Quan 3");
            result.add("District 3");
        } else if (trimmedQuery.equals("quận 4") || trimmedQuery.equals("quan 4")) {
            result.add("Quận 4");
            result.add("Quan 4");
            result.add("District 4");
        } else if (trimmedQuery.equals("quận 5") || trimmedQuery.equals("quan 5")) {
            result.add("Quận 5");
            result.add("Quan 5");
            result.add("District 5");
        } else if (trimmedQuery.equals("quận 6") || trimmedQuery.equals("quan 6")) {
            result.add("Quận 6");
            result.add("Quan 6");
            result.add("District 6");
        } else if (trimmedQuery.equals("quận 7") || trimmedQuery.equals("quan 7")) {
            result.add("Quận 7");
            result.add("Quan 7");
            result.add("District 7");
        } else if (trimmedQuery.equals("quận 8") || trimmedQuery.equals("quan 8")) {
            result.add("Quận 8");
            result.add("Quan 8");
            result.add("District 8");
        } else if (trimmedQuery.equals("quận 10") || trimmedQuery.equals("quan 10")) {
            result.add("Quận 10");
            result.add("Quan 10");
            result.add("District 10");
        } else if (trimmedQuery.equals("quận 11") || trimmedQuery.equals("quan 11")) {
            result.add("Quận 11");
            result.add("Quan 11");
            result.add("District 11");
        } else if (trimmedQuery.equals("quận 12") || trimmedQuery.equals("quan 12")) {
            result.add("Quận 12");
            result.add("Quan 12");
            result.add("District 12");
        }

        return result;
    }
}