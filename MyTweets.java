/*
 * $Id$
 *
 * Copyright (c) 2011 by DailyWars co., Ltd.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of DailyWars co., Ltd("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with DailyWars co., Ltd.
 */


import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 * class MyTweets
 *
 * show my tweets during given duration
 *
 * @version  $Revision$<br>
 *           created at 2011-01-09 15:00:32
 * @author   Yoon Kyung Koo
 */

public class MyTweets {
    /** standard logger object */
    private static Logger logger = Logger.getLogger(MyTweets.class.getName());

    private static final int BUFFER_SIZE = 8192;
    private static final String TWITTER_TIMELINE_JSON_API_URL = "http://api.twitter.com/1/statuses/user_timeline.json";

    public static void main(String[] args) throws IOException, java.text.ParseException {
        String screenName = null;
        int pageNum = -1;
        int fromPageNum = -1;
        if (args.length == 0) {
            System.err.println("You need pass the screen name of the twitter account");
            System.err.println("Usage : java MyTweets <screen name> [<to page num> [<from page num>]]");
            System.exit(0);
        }
        if (args.length >= 1) {
            screenName = args[0];
        }
        if (args.length >= 2) {
            pageNum = Integer.parseInt(args[1]);
        }
        if (args.length >= 3) {
            fromPageNum = Integer.parseInt(args[2]);
        }
        String url = TWITTER_TIMELINE_JSON_API_URL + "?screen_name=" + screenName + "&trim_user=1";
        // if (sinceId != null) {
        //     url += "&since_id=" + sinceId;
        // }

        StringBuilder fileContents = new StringBuilder();
        int tweetCount = 0;
        int callCount = 1;
        if (fromPageNum > 0) {
            callCount = fromPageNum;
        }

        for (; ; callCount++) {
            try {
                String currentURL = (pageNum <= 0 ? url : url + "&page=" + callCount);
                InputStream input = new URL(currentURL).openStream();
                String contents = readTextStream(input, null)[0];
                System.out.println("Called " + currentURL);

                List<TweetData> parsedTweetData = parseTweetData(contents);
                String recentTweetId = null;
                for (TweetData tweet : parsedTweetData) {
                    fileContents.append(tweet.toString()).append(System.getProperty("line.separator"));
                    if (recentTweetId == null) {
                        recentTweetId = tweet.idStr;
                    }
                    tweetCount++;
                }

                if (callCount > 100 || callCount >= pageNum) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        writeTextFile(new File("tweet.txt"), fileContents.toString(), "UTF8");
    }

    /**
     * read text from given input stream
     *
     * @param stream input stream to read
     * @param encoding initial encoding. if it is set to be null, this checks if unicode BOMs exist
     * @return string pair of contents and encoding
     */
    public static String[] readTextStream(InputStream stream, String encoding) throws IOException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("readTextStream(stream - " + stream + ", encoding - " + encoding + ")");
	}

	byte[] contents = readByteStream(stream);
	int offset = 0;

	// auto-detect utf strings by BOM(byte order mark)
	// EF BB BF (UTF 8), FE FF(UnicodeBig), FF FE (UnicodeLittle)
	if (encoding == null) {
	    if (contents.length >= 3 && contents[0] == (byte) 0xEF && contents[1] == (byte) 0xBB && contents[2] == (byte) 0xBF) {
		offset = 3;
		encoding = "UTF8";
	    } else if (contents.length >= 2 && contents[0] == (byte) 0xFE && contents[1] == (byte) 0xFF) {
		offset = 2;
		encoding = "UnicodeBig";
	    } else if (contents.length >= 2 && contents[0] == (byte) 0xFF && contents[1] == (byte) 0xFE) {
		offset = 2;
		encoding = "UnicodeLittle";
	    } else { // if encoding is not set then use default encoding
		encoding = System.getProperty("file.encoding");
	    }
	} else if ("UTF8".equalsIgnoreCase(encoding) || "UTF-8".equalsIgnoreCase(encoding)) {
	    if (contents.length >= 3 && contents[0] == (byte) 0xEF && contents[1] == (byte) 0xBB && contents[2] == (byte) 0xBF) {
		offset = 3;
		encoding = "UTF8";
	    }
	}

	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("readTextStream() found encoding as " + encoding
			  + (contents.length < 3 ? "" : Integer.toHexString(contents[0] & 0xFF)
			     + " " + Integer.toHexString(contents[1] & 0xFF)
			     + " " + Integer.toHexString(contents[2] & 0xFF)));
	}

	return new String[] { new String(contents, offset, contents.length - offset, encoding), encoding };
    }

    /**
     * read byte array from given input stream
     *
     * @param stream input stream to read
     * @return contents of the stream
     */
    public static byte[] readByteStream(InputStream stream) throws IOException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("readByteStream(stream - " + stream + ")");
        }

	ByteArrayOutputStream bout = new ByteArrayOutputStream();
	BufferedInputStream in = null;
	byte[] buffer = new byte[BUFFER_SIZE];

	try {
	    in = new BufferedInputStream(stream, BUFFER_SIZE);

	    int n = 0;
	    while ((n = in.read(buffer, 0, buffer.length)) >= 0) {
		bout.write(buffer, 0, n);
	    }
	} finally {
	    if (in != null) {
		in.close();
	    }
	    if (bout != null) {
		bout.close();
	    }
	}

	return bout.toByteArray();
    }

    /**
     * write text into given file
     */
    public static void writeTextFile(File file, String text, String encoding) throws IOException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("writeTextFile(file - " + file
			  + ", text - " + text
			  + ", encoding - " + encoding + ")");
	}

	BufferedWriter out = null;

	if (encoding == null) { // if encoding is not set then use default encoding
	    encoding = System.getProperty("file.encoding");
	} // end of if ()

	try {
	    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding),
				     BUFFER_SIZE);

	    out.write(text.toCharArray(), 0, text.length());
	    out.flush();
	} finally {
	    if (out != null) {
		out.close();
	    }
	}
    }

    public static List<TweetData> parseTweetData(String tweets) throws java.text.ParseException {
        List<TweetData> tweetList = new ArrayList<TweetData>();

        new TweetsParser(tweets, tweetList).parse();
        return tweetList;
    }

    static class TweetData {
        public String text;
        public Calendar createdAt;
        public String idStr;
        public String userIdStr;
        public String getBriefDate(Calendar cal) {
            if (cal == null) {
                return null;
            }

            return "" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1)
                + "/" + cal.get(Calendar.DAY_OF_MONTH);
        }
        public String toString() {
            return text + " (" + getBriefDate(createdAt) + ")";
        }
    }

    static class KeyValueGroup {
        public KeyValueGroup parent = null;
        public Map<String, Object> keyValueMap = new HashMap<String, Object>();
    }

    static enum UTFEscapeState {
        NONE, BK_SLASH, BKED_U, CH1, CH2, CH3
            }

    static class UTF8Unescaper {
        private String text;
        private UTFEscapeState state = UTFEscapeState.NONE;

        public UTF8Unescaper(String text) {
            this.text = text;
        }

        public String unescape() {
            StringBuilder buffer = new StringBuilder();
            StringBuilder utfCharBuffer = new StringBuilder();

            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);

                switch (ch) {
                case '\\' :
                    switch (state) {
                    case NONE :
                        state = UTFEscapeState.BK_SLASH;
                        break;

                    case BK_SLASH :
                        buffer.append('\\');
                        state = UTFEscapeState.NONE;
                        break;

                    default :
                        System.err.println("Wrong character sequence");
                        break;
                    }
                    break;

                case 'u' :
                    switch (state) {
                    case BK_SLASH :
                        state = UTFEscapeState.BKED_U;
                        utfCharBuffer.setLength(0);
                        break;

                    default :
                        buffer.append(ch);
                        break;
                    }
                    break;

                default :
                    switch (state) {
                    case BKED_U :
                        utfCharBuffer.append(ch);
                        state = UTFEscapeState.CH1;
                        break;

                    case CH1 :
                        utfCharBuffer.append(ch);
                        state = UTFEscapeState.CH2;
                        break;

                    case CH2 :
                        utfCharBuffer.append(ch);
                        state = UTFEscapeState.CH3;
                        break;

                    case CH3 :
                        utfCharBuffer.append(ch);
                        buffer.append((char) Integer.parseInt(utfCharBuffer.toString(), 16));
                        state = UTFEscapeState.NONE;
                        break;

                    case BK_SLASH :
                        switch (ch) {
                        case 'n' :
                            buffer.append('\n');
                            break;

                        case 't' :
                            buffer.append('\t');
                            break;

                        default :
                            buffer.append(ch);
                            break;
                        }
                        state = UTFEscapeState.NONE;
                        break;

                    default :
                        buffer.append(ch);
                        break;
                    }
                    break;
                }
            }

            return buffer.toString();
        }
    }

    static enum TweetParseState {
        NONE, KEY_START, VALUE_START, 
            }

    static class TweetsParser {
        private TweetParseState state = TweetParseState.NONE;
        private boolean underQuote = false;
        private String currentKey = null;
        private StringBuilder buffer = new StringBuilder();
        private KeyValueGroup currentGroup = null;

        private String tweets;
        private List<TweetData> list;

        public TweetsParser(String tweets, List<TweetData> list) {
            this.tweets = tweets;
            this.list = list;
        }

        public void parse() throws java.text.ParseException {
            char prevCh = 0;
            for (int i = 0; i < tweets.length(); i++) {
                char ch = tweets.charAt(i);
                
                switch (ch) {
                case '"' :
                    if (underQuote) {
                        if (prevCh == '\\') {
                            doDefault(ch);
                            break;
                        }
                        endOfString();
                    }
                    underQuote = !underQuote;
                    break;

                case ':' :
                    if (underQuote) {
                        doDefault(ch);
                        break;
                    }
                    storeKey();
                    state = TweetParseState.VALUE_START;
                    break;

                case ',' :
                    if (underQuote) {
                        doDefault(ch);
                        break;
                    }
                    if (currentGroup != null) {
                        storeValue();
                    }
                    state = TweetParseState.KEY_START;
                    break;

                case '{' :
                    if (underQuote) {
                        doDefault(ch);
                        break;
                    }
                    startNewGroup();
                    break;

                case '}' :
                    if (underQuote) {
                        doDefault(ch);
                        break;
                    }
                    endOfGroup();
                    break;

                default :
                    doDefault(ch);
                    break;
                }

                prevCh = ch;
            }
        }

        private void startNewGroup() {
            // System.out.println("startNewGroup");

            KeyValueGroup newGroup = new KeyValueGroup();
            if (currentGroup != null) {
                newGroup.parent = currentGroup;
                currentGroup.keyValueMap.put(currentKey, newGroup);
            }

            currentGroup = newGroup;
        }

        private void endOfString() {
            // System.out.println("endOfString");
        }

        private void endOfGroup() throws java.text.ParseException {
            // System.out.println("endOfGroup");
            storeValue();

            if (currentGroup.parent != null) {
                currentGroup = currentGroup.parent;
            } else {
                fillInList();
                currentGroup = null;
            }

            state = TweetParseState.KEY_START;
        }

        private void fillInList() throws java.text.ParseException {
            // System.out.println("fillInList - current map : " + currentGroup.keyValueMap);

            TweetData data = new TweetData();
            data.text = unescapeUTF8((String) currentGroup.keyValueMap.get("text"));
            String createdAtStr = String.valueOf(currentGroup.keyValueMap.get("created_at"));
            if (!createdAtStr.equals("null")) {
                data.createdAt = Calendar.getInstance();
                Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(createdAtStr);
                data.createdAt.setTime(date);
            }
            data.idStr = String.valueOf(currentGroup.keyValueMap.get("id_str"));
            KeyValueGroup kvGroup = (KeyValueGroup) currentGroup.keyValueMap.get("user");
            if (kvGroup != null) {
                data.userIdStr = (String) kvGroup.keyValueMap.get("id_str");
            }

            // System.out.println("add data : " + data);
            list.add(data);
        }

        private void storeKey() {
            // System.out.println("storeKey");

            currentKey = buffer.toString();
            buffer.setLength(0);
        }

        private void storeValue() {
            // System.out.println("storeValue");

            currentGroup.keyValueMap.put(currentKey, buffer.toString());
            buffer.setLength(0);
        }

        private void doDefault(char ch) {
            // System.out.println("doDefault(" + ch + ")");

            buffer.append(ch);
        }

        private String unescapeUTF8(String text) {
            if (text == null) {
                return null;
            }

            return new UTF8Unescaper(text).unescape();
        }
    }
}