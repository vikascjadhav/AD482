package com.redhat.training;

public class Test {
    public static void main(String[] args) {
        String s1 = "abc";
        String s2 = new String("abc");
        String s3 = "abc";

        if(s1!=s2) {
            System.out.println("Not equal s1 s2");
        }

        if(s1==s3) {
            System.out.println("s1==s3");
        }
    }
    
}
