package com.auth.server.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.core.convert.support.DefaultConversionService;

public class StringUtils {
    private static final String             GMAIL_EMAIL_SUFFIX        = "@gmail.com";
    private static final int                GMAIL_EMAIL_SUFFIX_LENGTH = GMAIL_EMAIL_SUFFIX.length();
    public static final String              EMPTY_STRING              = "";
    public static final char                CHAR_NEW_LINE             = '\n';
    public static final char                CHAR_NULL                 = '\0';
    private static DefaultConversionService conversionService         = new DefaultConversionService();

    public static String getRandom(int length) {
        String randomString = getRandom();
        return randomString.substring(randomString.length() - length);
    }

    public static String trimString(String str) {
        if (isNotBlank(str)) {
            return str.trim();
        }
        return str;
    }

    public static String getRandomAlphaNumeric(int length) {
        String aplhaNumberic = getRandom().toLowerCase().replaceAll("[^\\da-z]", "");
        return aplhaNumberic.substring(aplhaNumberic.length() - length);
    }

    public static String getRandomAlphaNumeric() {
        return getRandom().toLowerCase().replaceAll("[^\\da-z]", "");
    }

    public static String getRandomNumeric(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((int) (Math.random() * 10));
        }
        return builder.toString();
    }

    public static String getRandom() {
        return UUID.randomUUID().toString();
    }

    public static boolean isEmpty(String str) {
        return str == null ? true : "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        return !isBlank(str) && str.matches("^\\d+$");
    }

    public static boolean isStrictlyAlphaNumeric(String str) {
        return !isBlank(str) && str.matches("^(?=.*[a-z])(?=.*[0-9])[a-z0-9]+$");
    }

    public static boolean isAlphaNumeric(String str){
        return !isBlank(str) && str.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String getAccessorNameForField(String name) {
        return new StringBuilder("get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1)).toString();
    }

    public static String getModifierNameForField(String name) {
        return new StringBuilder("set").append(name.substring(0, 1).toUpperCase()).append(name.substring(1)).toString();
    }

    public static String getNotNullValue(String value) {
        return value != null ? value : "";
    }

    public static String join(char sep, Collection<String> strings) {
        if (strings == null || strings.size() == 0) {
            return EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s).append(sep);
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public static String join(Collection<String> strings) {
        return join(',', strings);
    }

    public static String extractBetweenDelimiters(String input, String startDelimiter, String endDelimiter) {
        int startIndex = input.indexOf(startDelimiter);
        if (startIndex != -1) {
            int endIndex = input.indexOf(endDelimiter, startIndex + startDelimiter.length());
            if (endIndex != -1) {
                return input.substring(startIndex + startDelimiter.length(), endIndex);
            }
        }
        return null;
    }

    public static String normalizeEmail(String email) {
        if (email.toLowerCase().endsWith(GMAIL_EMAIL_SUFFIX)) {
            return email.substring(0, email.length() - GMAIL_EMAIL_SUFFIX_LENGTH).split("\\+")[0].replaceAll("\\.", "") + GMAIL_EMAIL_SUFFIX;
        } else {
            return email;
        }
    }

    public static String getEmailDomain(String email) {
        return email.substring(email.lastIndexOf('@') + 1);
    }

    public static String getLocalPartFromEmail(String emailAdd) {
        return emailAdd.substring(0, emailAdd.lastIndexOf('@'));
    }

    public static List<String> split(String input, String regex) {
        return new ArrayList<>(Arrays.asList(input.split(regex)));
    }

    public static List<String> split(String input) {
        return split(input, ",");
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static Double parsePrice(String number) {
        number = number.replaceAll("[^\\.\\d,-]", "");
        DecimalFormat df = new DecimalFormat("#,##,###.##");
        Number n;
        try {
            n = df.parse(number);
        } catch (ParseException e) {
            return 0.0;
        }
        return n.doubleValue();
    }

    public static <T> boolean equalsAny(T input, T... values) {
        for (T value : values) {
            if (input.equals(value)) {
                return true;
            } else if(!input.getClass().equals(value.getClass())) {
                // checking type of input and values as well, as of now, we can pass input & values of different types
                // because compiler do not restrict it which make issue with code logic if someone mistakenly put input of other type and some/all items of values of different type
                throw new RuntimeException("Wrong type parameter passed for " + value + " of type " + value.getClass() + " while input type is " + input.getClass());
            }
        }
        return false;
    }
    
    public static boolean compareStrings(String s1, String s2) {
        if (s1 == null)
            return s2 == null;
        return s1.equals(s2);
    }

    public static boolean equalsIngoreCaseAny(String input, String... strings) {
        for (String string : strings) {
            if (input.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    private static final int INT  = 0;
    private static final int FRAC = 1;
    private static final int EXP  = 2;

    /**
     * Parse a number from a string. Finds the first recognizable base-10 number (integer or floating point) in the
     * string and returns it as a Number. Uses American English conventions (i.e., '.' as decimal point and ',' as
     * <<<<<<< HEAD thousands separator).
     * 
     * @param s String to parse ======= thousands separator). <<<<<<< Updated upstream
     * @param s String to parse >>>>>>> Stashed changes >>>>>>> cc5957b9e5f35a418b4f5af14be6805a0dc6a56a
     * @return first recognizable number
     * @throws NumberFormatException if no recognizable number is found
     */
    public static Number parseNumber(String s) throws NumberFormatException {
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int start = i;
                int end = ++i;
                int state = INT;

                if (start > 0 && s.charAt(start - 1) == '.') {
                    --start;
                    state = FRAC;
                }
                if (start > 0 && s.charAt(start - 1) == '-') {
                    --start;
                }

                foundEnd: while (i < s.length()) {
                    switch (s.charAt(i)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            end = ++i;
                            break;
                        case '.':
                            if (state != INT) {
                                break foundEnd;
                            }
                            state = FRAC;
                            ++i;
                            break;
                        case ',': // ignore commas
                            ++i;
                            break;
                        case 'e':
                        case 'E':
                            state = EXP;
                            ++i;
                            if (i < s.length() && ((c = s.charAt(i)) == '+' || c == '-')) {
                                ++i;
                            }
                            break;
                        default:
                            break foundEnd;
                    }
                }

                String num = s.substring(start, end);
                num = replace(num, ",", "");
                try {
                    if (state == INT) {
                        return  Long.valueOf(num);
                    } else {
                        return new Float(num);
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException("internal error: " + e);
                }
            }
        }
        throw new NumberFormatException(s);
    }

    /**
     * Replace all occurences of a string.
     * 
     * @param subject String in which to search
     * @param original String to search for in subject
     * @param replacement String to substitute
     * @return subject with all occurences of original replaced by replacement
     */
    public static String replace(String subject, String original, String replacement) {
        StringBuilder output = new StringBuilder();

        int p = 0;
        int i;
        while ((i = subject.indexOf(original, p)) != -1) {
            output.append(subject.substring(p, i));
            output.append(replacement);
            p = i + original.length();
        }
        if (p < subject.length()) {
            output.append(subject.substring(p));
        }
        return output.toString();
    }

    /**
     * Escapes metacharacters in a string.
     * 
     * @param subject String in which metacharacters are to be escaped
     * @param escapeChar the escape character (e.g., \)
     * @param metachars the metacharacters that should be escaped
     * @return subject with escapeChar inserted before every character found in metachars
     */
    public static String escape(String subject, char escapeChar, String metachars) {
        return escape(subject, metachars, escapeChar, metachars);
    }

    /**
     * Escapes characters in a string.
     * 
     * @param subject String in which metacharacters are to be escaped
     * @param chars Characters that need to be escaped (e.g. "\b\t\r\n\\")
     * @param escapeChar the escape character (e.g., '\\')
     * @param metachars escape code letters corresponding to each letter in chars (e.g. "btrn\\") <B>Must have
     *            metachars.length () == chars.length().</B>
     * @return subject where every occurence of c in chars is replaced by escapeChar followed the character
     *         corresponding to c in metachars.
     */
    public static String escape(String subject, String chars, char escapeChar, String metachars) {
        StringBuilder output = new StringBuilder();

        int p = 0;
        int i;
        while ((i = indexOfAnyChar(subject, chars, p)) != -1) {
            output.append(subject.substring(p, i));

            char c = subject.charAt(i); // character that needs escaping
            int k = chars.indexOf(c);
            char metac = metachars.charAt(k); // its corresponding metachar
            output.append(escapeChar);
            output.append(metac);

            p = i + 1;
        }
        if (p < subject.length()) {
            output.append(subject.substring(p));
        }
        return output.toString();
    }

    /**
     * Translate escape sequences (e.g. \r, \n) to characters.
     * 
     * @param subject String in which metacharacters are to be escaped
     * @param escapeChar the escape character (e.g., \)
     * @param metachars letters representing escape codes (typically "btrn\\")
     * @param chars characters corresponding to metachars (typically "\b\t\r\n\\"). <B>Must have chars.length () ==
     *            metachars.length().</B>
     * @param keepUntranslatedEscapes Controls behavior on unknown escape sequences (see below).
     * @return subject where every escapeChar followed by c in metachars is replaced by the character corresponding to c
     *         in chars. If an escape sequence is untranslatable (because escapeChar is followed by some character c not
     *         in metachars), then the escapeChar is kept if keepUntranslatedEscapes is true, otherwise the escapeChar
     *         is deleted. (The character c is always kept.)
     */
    public static String unescape(String subject, char escapeChar, String metachars, String chars, boolean keepUntranslatedEscapes) {
        StringBuilder output = new StringBuilder();

        int p = 0;
        int i;
        int len = subject.length();
        while ((i = subject.indexOf(escapeChar, p)) != -1) {
            output.append(subject.substring(p, i));
            if (i + 1 == len) {
                break;
            }

            char metac = subject.charAt(i + 1); // metachar to replace
            int k = metachars.indexOf(metac);
            if (k == -1) {
                // untranslatable sequence
                if (keepUntranslatedEscapes) {
                    output.append(escapeChar);
                }
                output.append(metac);
            } else {
                output.append(chars.charAt(k)); // its corresponding true char
            }

            p = i + 2; // skip over both escapeChar & metac
        }

        if (p < len) {
            output.append(subject.substring(p));
        }
        return output.toString();
    }

    /**
     * Find first occurrence of any of a set of characters.
     * 
     * @param subject String in which to search
     * @param chars Characters to search for
     * @return index of first occurrence in subject of a character from chars, or -1 if no match.
     */
    public static int indexOfAnyChar(String subject, String chars) {
        return indexOfAnyChar(subject, chars, 0);
    }

    /**
     * Find first occurrence of any of a set of characters, starting at a specified index.
     * 
     * @param subject String in which to search
     * @param chars Characters to search for
     * @param start Starting offset to search from
     * @return index of first occurrence (after start) in subject of a character from chars, or -1 if no match.
     */
    public static int indexOfAnyChar(String subject, String chars, int start) {
        for (int i = start; i < subject.length(); ++i) {
            if (chars.indexOf(subject.charAt(i)) != -1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * removes all characters which are not letter or digit (removes whitespaces as well!)
     *
     * @param input input string
     * @return string with all non-word chars removed
     */
    public static String removeNonWordChars(String input) {
        StringBuilder output = new StringBuilder();
        char[] cinput = input.toCharArray();
        for (char element : cinput) {
            if (Character.isLetterOrDigit(element)) {
                output.append(element);
            }
        }
        return output.toString();
    }

    /**
     * @param input boolean string
     * @return true if input is "yes" or "1" or "true"
     */
    public static boolean parseBoolean(String input) {
        input = input == null ? null : input.trim();
        return ((input != null) && (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("1")));
    }

    public static String pad(String input, int length, char c) {
        input = getNotNullValue(input);
        if (input.length() >= length) {
            return input;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length - input.length(); i++) {
                builder.append(c);
            }
            builder.append(input);
            return builder.toString();
        }
    }


    public static String underscorify(String input) {
        return input.replaceAll("[\\s\\W]", "_").replaceAll("[_]+", "_");
    }

    public static String escapeSql(String input) {
        if (input == null) {
            return "NULL";
        } else {
            return new StringBuilder().append('\'').append(
                    input.replaceAll("\\\\", "\\\\\\\\").replaceAll("\t", "\\\\t").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r").replaceAll("\"", "\\\\\"").replaceAll("'",
                            "\\\\'")).append('\'').toString();
        }
    }

    public static String normalizeCacheKey(String input) {
        if (input == null) {
            return StringUtils.EMPTY_STRING;
        }
        return StringUtils.removeNonWordChars(input).toLowerCase();
    }

    public static <T> T convert(Object source, Class<T> targetType) {
        return conversionService.convert(source, targetType);
    }

    /**
     * Providing string formatting to a pattern
     */
    public static String format(String pattern, Object... args) {
        return String.format(pattern, args);
    }

    public static String replaceByRegex(String input, String regexString, String replacementString) {
        Pattern pattern = Pattern.compile(regexString);
        return pattern.matcher(input).replaceAll(replacementString);
    }

}
