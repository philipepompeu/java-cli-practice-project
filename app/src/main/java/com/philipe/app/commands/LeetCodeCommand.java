package com.philipe.app.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
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

    public boolean isAnagram(String s, String t) {

        char[] s1 = s.toCharArray();
        char[] t2 = t.toCharArray();

        Arrays.sort(s1);
        Arrays.sort(t2);

        return (new String(s1)).equals( new String(t2) );

        
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
                                            .mapToObj(i -> {
                                                ArrayList<Integer> row = new ArrayList<>();
                                                for (int j = 0; j <= i; j++) {
                                                    row.add(1);
                                                }
                                                return row;
                                            })                                            
                                            .toList();        

        for (int i = 2; i < numRows; i++) {    
            
            ArrayList<Integer> previousRow = triangle.get(i-1);
            ArrayList<Integer> currentRow = triangle.get(i);
            for (int j = 1; j < currentRow.size()-1; j++) {
                currentRow.set(j, previousRow.get(j-1)+previousRow.get(j));                
            }
        }


        return new ArrayList<>(triangle);
        
    }

    public int[] unionOfTwoSets(int[] nums1, int[] nums2) {        

        Set<Integer> unionSet = Arrays.stream(nums1)
                                    .boxed()
                                    .collect(Collectors.toSet());

        for(int i : nums2) {
            if (!unionSet.contains(i)) {
                unionSet.add(i);
            }            
        }       

        int[] result = unionSet.stream().mapToInt(o -> (Integer)o).toArray();
        
        Arrays.sort(result);

        return result;
        
        
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        
        for (int i = 0; i < n; i++) {
            nums1[m + i] = nums2[i];
        }
        Arrays.sort(nums1);
    }

    public int majorityElement(int[] nums) {

        Map<Integer, Integer> countMap = new HashMap<>();

        int majorityCount = nums.length / 2;

        for (int i : nums) {
            
            if (!countMap.containsKey(i)) {
                countMap.put(i, 0);
            }
            countMap.put(i, countMap.get(i)+1);
            
            if (countMap.get(i) > majorityCount) {
                return i;
            }
        }

        return 0;
        
    }

    public int removeDuplicates(int[] nums) {
        
        int write = 0; // posição onde vamos gravar
        for (int read = 0; read < nums.length; read++) {
            // Aceita os dois primeiros elementos normalmente
            if (write < 2 || nums[read] != nums[write - 2]) {
                nums[write] = nums[read];
                write++;
            }
        }
        return write;
    }

    public void rotate(int[] nums, int k) {

        int[] copyOfNums = Arrays.copyOf(nums, nums.length);
        int n = nums.length;

        for(int i = 0; i< nums.length; i++) {
            int newIndex = (i + k) % n;
            
            nums[newIndex] = copyOfNums[i];
        }
    }


    public int maxProfit(int[] prices) {
        
        int maxProfit = 0;

        int cheapest = Integer.MAX_VALUE;

        for(int price : prices){
            if (cheapest > price) {
                cheapest = price;
            }

            int profit = price - cheapest;
            if (profit > maxProfit) {
                maxProfit = profit;
            }
        }          
        
        return maxProfit;
    }

    public int[] productExceptSelf(int[] nums) {
        
        int n = nums.length;
        int[] result = new int[n];        

        int calc = 1;
        for (int i = 0; i < n;i++){
            result[i] = calc;
            calc *= nums[i];
        }
        
        int suffix = 1;
        for (int i = n-1; i >= 0;i--){            
            result[i]*=suffix;
            suffix *= nums[i];
        }

        return result;
    }

    public int lengthOfLastWord(String s) {
        String[] words = s.split("\\s+");        
     
        return (words[words.length-1]).length();
    }

    public boolean canJump(int[] nums) {

        int maxReach = 0;
       

        for(int i = 0; i < nums.length;i++){
            if (i > maxReach) {
                return false;
            }
            maxReach = Math.max(maxReach, i+nums[i]);
        }
     
        return true;
    }

    public int hIndex(int[] citations) {
        int n = citations.length;
        int[] count = new int[n+1];

        // Contar quantos artigos têm x citações
        for (int c : citations) {
            if (c >= n) {
                count[n]++;
            } else {
                count[c]++;
            }
        }

        int total = 0;
        
        for(int i = n;i >= 0;i--){
            total += count[i];

            if (total >= i) {
                return i;
            }
        }

        return 0;
    }

    public String longestCommonPrefix(String[] strs) {

        String prefix = strs[0];
        for(int j=prefix.length();j >= 0;j--){
            String candidate = prefix.substring(0, j);
            boolean allMatch = true;
            for(int i = 0; i < strs.length;i++){                
                if (!strs[i].startsWith(candidate)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) return candidate;
        }

        return "";              
        
    }

    public String reverseWords(String s) {        
        
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) sb.append(' ');
        }
        return sb.toString();      
        
    }


    public boolean isPalindrome(String s) {

        String clean = s.toUpperCase().replaceAll("[^a-zA-Z0-9]","");
        char[] letters = clean.toCharArray();        
        
        int i = 0;
        for(int j=letters.length-1;j >= 0;j--){
            if (letters[i] != letters[j]) {
                return false;
            }
            i++;
        }        
        return true;        
    }


    public boolean isSubsequence(String s, String t) {

        char[] word = new char[s.length()];
        int start = 0;

        for(int i = 0; i < s.length();i++){
            char letter = s.charAt(i);
            
            for(int j = start; j < t.length();j++){    
                if (t.charAt(j) == letter) {
                    word[i] = letter;
                    start = j+1;
                    break;
                }
            }
        }

        return (new String(word)).equals(s);
        
    }

    public boolean canConstruct(String ransomNote, String magazine) {

        if (ransomNote.length() > magazine.length()) return false;

        int[] letterBag = new int[26];        
        
        for(char c: magazine.toCharArray()){                        
            letterBag[(int)c- 'a']+=1;
        }

        for(char c: ransomNote.toCharArray()){
            if (letterBag[(int)c- 'a'] == 0) {
                return false;
            }
            letterBag[(int)c- 'a']-=1;
        }

        return true;
        
    }

    public boolean isIsomorphic(String s, String t) {

        if (s.length() != t.length()) return false;
           
        int[][] mapOfChar = new int[2][256];

        for(int i = 0 ; i < s.length();i++){
            char cs = s.charAt(i);
            char ct = t.charAt(i);

            int indexCs = (int)cs;
            int indexCt = (int)ct;

            if (mapOfChar[0][indexCs] == 0) mapOfChar[0][indexCs] = ct;
            else if (mapOfChar[0][indexCs] != ct) return false;
            
            if (mapOfChar[1][indexCt] == 0) mapOfChar[1][indexCt] = cs;
            else if (mapOfChar[1][indexCt] != cs) return false;
            
        }
        
        return true;
        
    }
    
    public boolean isHappy(int n) {
        
        if(n == 1) return true;        

        Set<Integer> previous = new HashSet<Integer>();

        int happyNumber = 0;        
        
        while (happyNumber != 1) {

            happyNumber = 0;

            while (n > 0) {
                int digit = n % 10;
                happyNumber += (digit * digit);
                n /= 10;
            }            
            
            if(previous.contains(happyNumber)) return false;
            
            previous.add(happyNumber);
            n = happyNumber;            
        }

        return true;
        
    }


    public List<Integer> spiralOrder(int[][] matrix) {

        if(matrix.length < 2) return Arrays.stream(matrix[0]).mapToObj(n -> (Integer)n).toList();
        
        ArrayList<Integer> listOfInt = new ArrayList<>();

        for(int i = 0; i < matrix.length ; i++){
            int[] line = matrix[i];

            if (i == 0) {                
                for(int n : line){
                    listOfInt.add(n);
                }
            }else if(i == matrix.length - 1){
                for(int j = line.length-1;j >= 0;j--){
                    listOfInt.add(line[j]);
                }                
                line = matrix[i-1];
                for(int j = 0;j <= line.length-2;j++){
                    listOfInt.add(line[j]);
                }
            }else{
                listOfInt.add(line[line.length-1]);
            }
        }
        

        return listOfInt;
    }

    public int romanToInt(String s) {
        int result = 0;
        int[] romanMap = new int[256];

        romanMap['I'] = 1;
        romanMap['V'] = 5;
        romanMap['X'] = 10;
        romanMap['L'] = 50;
        romanMap['C'] = 100;
        romanMap['D'] = 500;
        romanMap['M'] = 1000;
        
        for(int i = 0; i < s.length() ; i++ ){
            char c = s.charAt(i);
            int current = romanMap[ c ];
            int next = (i +1 < s.length() ? romanMap[ s.charAt(i+1) ] : 0);

            result += current  * ( (current < next) ? -1 : 1 );                       
        }

        return result;
    }
    
}
