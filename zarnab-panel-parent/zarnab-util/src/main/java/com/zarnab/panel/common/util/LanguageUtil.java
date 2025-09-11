package com.zarnab.panel.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageUtil {

    public static Boolean hasArabicCharacter(String input) {
        if(input == null){
            return false;
        }
        Pattern pattern = Pattern.compile("(?s).*\\p{InArabic}.*");
        Matcher matcher = pattern.matcher(input);
        boolean isMatch = matcher.matches();

        return isMatch;
    }

    public static String replaceCharacter(String input) {
        String replaceAll = input.replaceAll("[\u06D0-\u06D3]", "\u06CC")//ی
                .replaceAll("[\uFDF0-\uFDFD]|[\u0600-\u061F]|[\u0621]|[\u064B-\u0660]|[\u066E-\u0671]", "")
                .replaceAll("[\u0620]|[\u0626]|[\u063D-\u063F]|[\u0649-\u064A]|[\u0678]", "\u06CC")//ی
                .replaceAll("[\u0622-\u0623]|[\u0625]|[\u0627]|[\u0672-\u0672]|[\u0675]", "\u0622")//ا
                .replaceAll("[\u0624]|[\u0676-\u0677]", "\u0648")//و
                .replaceAll("[\u0629]", "\u0647")//ه
                .replaceAll("[\u063B]", "\u06AF")//گ
                .replaceAll("[\u063C]|[\u0643]", "\u06A9")//ک

                .replaceAll("[\u0660]", "0")
                .replaceAll("[\u0661]", "1")
                .replaceAll("[\u0662]", "2")
                .replaceAll("[\u0663]", "3")
                .replaceAll("[\u0664]", "4")
                .replaceAll("[\u0665]", "5")
                .replaceAll("[\u0666]", "6")
                .replaceAll("[\u0667]", "7")
                .replaceAll("[\u0668]", "8")
                .replaceAll("[\u0669]", "9")

                .replaceAll("[\u0679]", "\u062B")//ث
                .replaceAll("[\u067A]|[\u067D]|[\u067F]", "\u062A")//ت
                .replaceAll("[\u067B]|[\u0680]", "\u0628")//ب
                .replaceAll("[\u067C]", "\u062A")//ت
                .replaceAll("[\u0681-\u0682]", "\u062E")//خ
                .replaceAll("[\u0683]", "\u062D")//ح
                .replaceAll("[\u0684]", "\u062C")//ج
                .replaceAll("[\u0685]", "\u062E")//خ
                .replaceAll("[\u0686-\u0687]", "\u0686")//چ
                .replaceAll("[\u0688-\u0690]", "\u062F")//د
                .replaceAll("[\u0691-\u0697]", "\u0631")//ر
                .replaceAll("[\u0698-\u0699]", "\u0698")//ژ
                .replaceAll("[\u069A]", "\u0633")//س
                .replaceAll("[\u069D-\u069E]", "\u0635")//ص
                .replaceAll("[\u069F]", "\u0637")//ط

                .replaceAll("[\u06A0]", "\u0639")//ع
                .replaceAll("[\u06A1-\u06A8]", "")
                .replaceAll("[\u06A9-\u06AE]", "\u06A9")//ک
                .replaceAll("[\u06AF-\u06B4]", "\u06AF")//گ
                .replaceAll("[\u06B5-\u06B8]", "\u0644")//ل
                .replaceAll("[\u06B9-\u06BD]", "\u0646")//ن

                .replaceAll("[\u06BE]", "\u0647")//ه
                .replaceAll("[\u06BF]", "\u062E")//خ
                .replaceAll("[\u06C0-\u06C2]", "\u0647")//ه
                .replaceAll("[\u06C3]", "\u062A")//ت
                .replaceAll("[\u06C4-\u06CB]", "\u0648")//و
                .replaceAll("[\u06CC-\u06CE]", "\u06CC")//ی
                .replaceAll("[\u06CF]", "\u0648")//و
                .replaceAll("[\u06D0-\u06D3]", "\u06CC")//ی
                .replaceAll("[\u06D4-\u06ED]", "")
                .replaceAll("[\u06EE]", "\u062F")//د
                .replaceAll("[\u06EF]", "\u0631")//ر
                .replaceAll("[\u06F0]", "0")
                .replaceAll("[\u06F1]", "1")
                .replaceAll("[\u06F2]", "2")
                .replaceAll("[\u06F3]", "3")
                .replaceAll("[\u06F4]", "4")
                .replaceAll("[\u06F5]", "5")
                .replaceAll("[\u06F6]", "6")
                .replaceAll("[\u06F7]", "7")
                .replaceAll("[\u06F8]", "8")
                .replaceAll("[\u06F9]", "9")

                .replaceAll("[\u06FA]", "\u0634")//ش
                .replaceAll("[\u06FB]", "\u0636")//ض
                .replaceAll("[\u06FC]", "\u063A")//غ
                .replaceAll("[\u06FD]", "")
                .replaceAll("[\u06FE]", "\u0645")//م
                .replaceAll("[\u06FF]", "\u0647");//ه
        return replaceAll;
    }

    public static String replaceIfHasArabicCharacter (String input) {
        return hasArabicCharacter(input) ? replaceCharacter(input) : input;
    }

}
