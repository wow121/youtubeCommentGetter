package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {

    public static final String KEY = "AIzaSyC2HYHpYlydZ1Id84XC6V9Q0evtGWnX9Vg";

    public static String VIDEO_ID = "qj30xA05ci0";

    static int commentCount = 0;

    static boolean isPrintUserName = false;
    static boolean isPrintCommentTime = false;

    static boolean isFormatByWeek = false;

    static Date date;

    static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    static SimpleDateFormat youtebeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    /**
     * args[0] video_id
     * args[1] isPrintUserName y/n
     * args[2] isPrintCommentTime y/n
     * args[3] isFormatByWeek y/n
     */
    public static void main(String[] args) {
        if (args.length > 0 && !TextUtils.isEmpty(args[0])) {
            if (args[0].equals("-help")) {
                updateMessage("args[0] video_id\n" +
                        "args[1] isPrintUserName y/n\n" +
                        "args[2] isPrintCommentTime y/n\n" +
                        "args[3] isFormatByWeek y/n");
            } else {
                VIDEO_ID = args[0];

                if (args.length > 1 && !TextUtils.isEmpty(args[1])) {
                    if (args[1].equals("y")) {
                        isPrintUserName = true;
                    } else {
                        isPrintUserName = false;
                    }

                }
                if (args.length > 2 && !TextUtils.isEmpty(args[2])) {
                    if (args[2].equals("y")) {
                        isPrintCommentTime = true;
                    } else {
                        isPrintCommentTime = false;
                    }
                }
                if (args.length > 3 && !TextUtils.isEmpty(args[3])) {
                    if (args[2].equals("y")) {
                        isFormatByWeek = true;
                    } else {
                        isFormatByWeek = false;
                    }
                }
                startRequest(null);
            }
        }

    }


    private static void startRequest(String pageToken) {
        updateMessage("request url: " + getUrl(VIDEO_ID, pageToken));
        String response = sendRequest(getUrl(VIDEO_ID, pageToken));

//        updateMessage("response " + response);
        updateMessage("request Success");
        YoutubeCommentDto youtubeCommentDto = YoutubeCommentDto.parserJson(response);
        writeMessage(youtubeCommentDto);
        updateMessage("write file success");

        updateMessage("comment count " + commentCount);
        if (!TextUtils.isEmpty(youtubeCommentDto.nextPageToken)) {
            startRequest(youtubeCommentDto.nextPageToken);
        } else {
            updateMessage("finish");
            updateMessage("save path ./" + VIDEO_ID + ".txt");
        }
    }


    public static String sendRequest(String URLpath) {
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try {
            // 1.创建URL对象（统一资源定位符) 定位到网络上了
            URL url = new URL(URLpath);
            // 2.创建连接对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 3.设置参数
            conn.setDoInput(true);// 设置能不能读
            conn.setDoOutput(true);// 设置能不能写
            conn.setRequestMethod("GET");// 请求方式必须大写
            conn.setReadTimeout(50000);// 连接上了读取超时的时间
            conn.setConnectTimeout(50000);// 设置连接超时时间5s
            // 获取响应码
            // 4.开始读取
            //5读取服务器资源的流
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            //准备内存输出流 临时存储的
            baos = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                baos.write(buff, 0, len);
                baos.flush();
            }
            return baos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关流
            if (is != null && baos != null) {
                try {
                    is.close();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    private static void updateMessage(String message) {
        System.out.println(message);
    }

    private static void writeMessage(YoutubeCommentDto youtubeCommentDto) {
        for (YoutubeCommentDto.Comment comment : youtubeCommentDto.items) {
            if (isFormatByWeek) {
                try {
                    date = youtebeDateFormat.parse(comment.snippet.topLevelComment.snippet.updatedAt);
                } catch (ParseException e) {
                    updateMessage("date format error");
                    e.printStackTrace();
                }
            }

            if (isPrintCommentTime) {
                writeFileSdcard(comment.snippet.topLevelComment.snippet.updatedAt + "  ");
            }
            if (isPrintUserName) {
                writeFileSdcard(comment.snippet.topLevelComment.snippet.authorDisplayName + " : ");
            }
            writeFileSdcard(comment.snippet.topLevelComment.snippet.textDisplay);
            commentCount += 1;
            writeFileSdcard("\n");
            writeFileSdcard("\n");
            writeFileSdcard("\n");
            if (comment.replies != null) {
                for (YoutubeCommentDto.Replies.CommentsEntity ce : comment.replies.comments) {
                    if (isFormatByWeek) {
                        try {
                            date = youtebeDateFormat.parse(ce.snippet.updatedAt);
                        } catch (ParseException e) {
                            updateMessage("date format error");
                            e.printStackTrace();
                        }
                    }
                    if (isPrintCommentTime) {
                        writeFileSdcard(ce.snippet.updatedAt + "  ");
                    }
                    if (isPrintUserName) {
                        writeFileSdcard(ce.snippet.authorDisplayName + " --> " + comment.snippet.topLevelComment.snippet.authorDisplayName + " : ");
                    }
                    writeFileSdcard(ce.snippet.textDisplay);
                    commentCount += 1;
                    writeFileSdcard("\n");
                    writeFileSdcard("\n");
                }
            }
        }
    }


    public static void writeFileSdcard(String message) {
        String fileName;
        if (isFormatByWeek && date != null) {
            fileName = "./" + yearDateFormat.format(date) + "_" + getWeekOfYear(date) + "_" + VIDEO_ID + ".txt";
        } else {
            fileName = "./" + VIDEO_ID + ".txt";
        }

        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
            FileOutputStream fout = new FileOutputStream(fileName, true);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUrl(String videoID, String pageToken) {

        String url = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet%2Creplies&textFormat=plainText";
        if (!TextUtils.isEmpty(pageToken)) {
            url += "&pageToken=" + pageToken;
        }
        url += "&videoId=" + videoID;
        url += "&key=" + KEY;
        return url;
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getWeekOfYear(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
    }


}
