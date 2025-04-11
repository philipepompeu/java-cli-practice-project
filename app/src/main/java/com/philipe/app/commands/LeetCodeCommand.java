package com.philipe.app.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = Map.of(
            ')', '(', 
            ']', '[', 
            '}', '{'
        );

        for (char c : s.toCharArray()) {
            if (pairs.containsValue(c)) {
                stack.push(c);
            } else if (pairs.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }


    public List<List<Integer>> pascalTriangule(int numRows) {

        List<ArrayList<Integer>> triangle = IntStream.range(0, numRows)
                                            .mapToObj(num -> Collections.nCopies(num + 1, 1))
                                            .map(ArrayList::new)
                                            .toList();
        

        for (int i = 2; i < numRows; i++) {    
            
            ArrayList<Integer> previousRow = triangle.get(i-1);
            ArrayList<Integer> currentRow = triangle.get(i);
            for (int j = 1; j < currentRow.size()-1; j++) {
                currentRow.set(j, previousRow.get(j-1)+previousRow.get(j));                
            }
        }


        return triangle.stream().collect(Collectors.toList());
        
    }
    
}
