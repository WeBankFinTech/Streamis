package com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Message filter of keyword
 */
public class KeywordMessageFilter implements LogMessageFilter{

    /**
     * Accept keywords
     */
    private final String[] acceptKeywords;

    /**
     * Regex pattern of accept keywords
     */
    private Pattern acceptPattern;
    /**
     * Exclude keywords
     */
    private final String[] excludeKeywords;

    /**
     * Regex pattern of exclude keywords
      */
    private Pattern excludePattern;

    /**
     * Flags for pattern
     */
    private int patternFlag = 0;

    public KeywordMessageFilter(String[] acceptKeywords, String[] excludeKeywords){
        this(acceptKeywords, excludeKeywords, null);
    }

    public KeywordMessageFilter(String[] acceptKeywords, String[] excludeKeywords, String[] patternFlags){
        this.acceptKeywords = acceptKeywords;
        this.excludeKeywords = excludeKeywords;
        try {
            this.patternFlag = toPatternFlags(patternFlags);
        } catch (IllegalAccessException e) {
            // Ignore
        }
        // Build regex pattern
        if (acceptKeywords != null && acceptKeywords.length > 0){
            this.acceptPattern = toMatchPattern(acceptKeywords, this.patternFlag);
        }
        if (excludeKeywords != null && excludeKeywords.length > 0){
            this.excludePattern = toMatchPattern(excludeKeywords, this.patternFlag);
        }
    }

    @Override
    public boolean doFilter(String logger, String message) {
        boolean accept = true;
        if (null != acceptPattern){
           accept =  acceptPattern.matcher(message).find();
        }
        if (accept && null != excludePattern){
            accept = !excludePattern.matcher(message).find();
        }
        return accept;
    }

    /**
     * Convert to pattern
     * @param keywords keyword array
     * @param flag pattern flag
     * @return Regex pattern
     */
    protected Pattern toMatchPattern(String[] keywords, int flag){
        StringBuilder patternStr = new StringBuilder("(");
        for(int i = 0; i < keywords.length; i++){
            patternStr.append(keywords[i]);
            if (i != keywords.length - 1){
                patternStr.append("|");
            }
        }
        patternStr.append(")");
        return Pattern.compile(patternStr.toString(), flag);
    }

    /**
     * Convert the pattern flag array to int
     * @param patternFlags flag string array
     * @return int value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static int toPatternFlags(final String[] patternFlags) throws IllegalArgumentException,
            IllegalAccessException {
        if (patternFlags == null || patternFlags.length == 0) {
            return 0;
        }
        final Field[] fields = Pattern.class.getDeclaredFields();
        final Comparator<Field> comparator = Comparator.comparing(Field::getName);
        Arrays.sort(fields, comparator);
        final String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        int flags = 0;
        for (final String test : patternFlags) {
            final int index = Arrays.binarySearch(fieldNames, test);
            if (index >= 0) {
                final Field field = fields[index];
                flags |= field.getInt(Pattern.class);
            }
        }
        return flags;
    }

    public final String[] getAcceptKeywords(){
        return this.acceptKeywords;
    }

    public final String[] getExcludeKeywords(){
        return this.excludeKeywords;
    }

}
