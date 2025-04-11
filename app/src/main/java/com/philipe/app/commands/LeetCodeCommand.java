package com.philipe.app.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.shell.standard.ShellComponent;

@ShellComponent
public class LeetCodeCommand {


    public List<List<String>> groupAnagrams(String[] strs)  {
        
        Map<String, List<String>> groups = new HashMap<>();

        for (String word : strs) {
            char[] tmp = word.toCharArray();
            Arrays.sort(tmp);
            String key = new String(tmp);
            
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }

        return new ArrayList<>(groups.values());     
    }

    public boolean validParentheses(String s){

        String open = "([{";
        String close = ")]}";

        int count = 0;
        for (char c : s.toCharArray()) {
            
            
            if (open.indexOf(c) > 0) {
                count++;
            } else if (close.indexOf(c) > 0) {
                count--;
            }
            if (count < 0) {
                return false;
            }
        }
        return count == 0;
    }

    
}
