package com.webank.wedatasphere.streamis.jobmanager.log.json;

import java.util.Locale;

public class JsonTool {
    static final char[] HEX_DIGITS = new char[] {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    /**
     * Avoid the special char
     * @param input input string
     * @return output string
     */
    public static String escapeStrValue(String input){
        char[] chars = input.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append((c < 32) ? escapeUnicode(c) : c);
            }
        }
        return sb.toString();
    }

    /**
     * Escape unicode
     * @param code char code
     * @return escaped string
     */
    private static String escapeUnicode(int code){
        if (code > 0xffff){
            return "\\u" + Integer.toHexString(code).toUpperCase(Locale.ENGLISH);
        }  else {
            return "\\u" + HEX_DIGITS[(code >> 12) & 15]
                    + HEX_DIGITS[(code >> 8) & 15] + HEX_DIGITS[(code >> 4) & 15] + HEX_DIGITS[code & 15];
        }
    }

}
