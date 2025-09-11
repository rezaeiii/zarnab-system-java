package com.zarnab.panel.common.file.constants;

import java.util.regex.Pattern;

public class FileConstants {

    public static final String DOT = ".";
    public static final String SLASH = "/";
    public static final String DASH = "-";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final Pattern DISALLOWED_CHARACTERS = Pattern.compile("[\\\\/:*?\"<>|]");
}
