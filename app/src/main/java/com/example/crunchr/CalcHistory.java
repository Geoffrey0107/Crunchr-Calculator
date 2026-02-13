package com.example.crunchr;

import java.util.ArrayList;
public class CalcHistory {
    public static final ArrayList<String> items = new ArrayList<>();

    public static void add(String infix, String resultStr) {
        items.add(infix + " = " + resultStr);
    }
}