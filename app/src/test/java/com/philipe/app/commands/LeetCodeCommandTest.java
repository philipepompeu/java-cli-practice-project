package com.philipe.app.commands;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.bytebuddy.build.HashCodeAndEqualsPlugin.Sorted;

public class LeetCodeCommandTest {
    
    private LeetCodeCommand command;

    @BeforeEach
    void setUp() {
        command = new LeetCodeCommand();
    }


    @Test
    void groupAnagrams(){

        String[] input = new String[]{"eat", "tea", "tan", "ate", "nat", "bat"};        

        List<HashSet<String>> result = command.groupAnagrams(input).stream().map(HashSet::new).toList();
        
        assertTrue(result.stream().anyMatch(e -> Set.of("ate", "eat", "tea").equals(e) ));
        assertTrue(result.stream().anyMatch(e -> Set.of("bat").equals(e) ));
        assertTrue(result.stream().anyMatch(e -> Set.of("tan", "nat").equals(e) ));
        
    }
    
    @Test
    void groupAnagramsTwoEmptyStrings(){

        String[] input = new String[]{"", ""};        

        List<List<String>> result = command.groupAnagrams(input);        
        
        assertTrue(result.stream().anyMatch(e -> Arrays.asList("", "").equals(e) ));        
        
    }


    @Test
    void validParentheses(){        
        assertTrue(command.validParentheses("()"));        
        assertTrue(command.validParentheses("()[]{}"));          
        assertFalse(command.validParentheses("(]"));        
        assertTrue(command.validParentheses("([])"));        
        
        assertFalse(command.validParentheses("([)]"));        
    }
    
    @Test
    void pascalTriangule(){  
        
        List<List<Integer>> result = command.pascalTriangule(5);

        List<List<Integer>> expected = List.of(
                                            List.of(1),
                                            List.of(1, 1),
                                            List.of(1, 2, 1),
                                            List.of(1, 3, 3, 1),
                                            List.of(1, 4, 6, 4, 1)
                                        );
        
        assertTrue(result.size() == 5); 
        assertEquals(expected, result);       
        
        result = command.pascalTriangule(1);        
        assertEquals(List.of(List.of(1)), result);       
        
    }
    
    @Test
    void unionOfTwoSets(){  
        
        int[] result = command.unionOfTwoSets(new int[]{1, 2, 3}, new int[]{2, 3, 4});       
        
        assertArrayEquals(new int[]{1,2,3,4}, result);         
        
    }


    @Test
    void mergeSortedArray1(){
        int[] nums1 = new int[] {1,2,3,0,0,0};
        int m = 3;
        
        int[] nums2 = new int[] {2,5,6};
        int n = 3;

        command.merge(nums1, m, nums2, n);

        assertArrayEquals(new int[]{1,2,2,3,5,6}, nums1);

    }
    
    @Test
    void mergeSortedArray2(){
        int[] nums1 = new int[] {1};
        int m = 1;
        
        int[] nums2 = new int[] {};
        int n = 0;

        command.merge(nums1, m, nums2, n);

        assertArrayEquals(new int[]{1}, nums1);

    }
    

    @Test
    void majorityCount(){
        assertEquals(3, command.majorityElement(new int[] {3,2,3}));
        assertEquals(2, command.majorityElement(new int[] {2,2,1,1,1,2,2}));

    }


    @Test
    void removeDuplicates(){       

        int expected = 5;
        int[] input = new int[]{1,1,1,2,2,3};
        
        int result = command.removeDuplicates(input);

        assertEquals(expected, result);        
        
        assertArrayEquals(new int[]{1,1,2,2,3}, Arrays.copyOfRange(input, 0, result));

    }
    
    @Test
    void removeDuplicates2(){           

        int expected = 7;
        int[] input = new int[]{0,0,1,1,1,1,2,3,3};
        
        int result = command.removeDuplicates(input);

        assertEquals(expected, result);        
        
        assertArrayEquals(new int[]{0,0,1,1,2,3,3}, Arrays.copyOfRange(input, 0, result));

    }
    
    @Test
    void rotate(){           

        
        int[] input = new int[]{1,2,3,4,5,6,7};
        
        command.rotate(input, 3);        
        
        assertArrayEquals(new int[]{5,6,7,1,2,3,4}, input);
        

    }
    @Test
    void rotate2(){                 
        
        
        int[] onePositionInput = new int[]{-1};
        command.rotate(onePositionInput, 3);

        assertArrayEquals(new int[]{-1}, onePositionInput);

    }

    @Test
    void maxProfit(){           

        int expected = 5;
        int[] input = new int[]{7,1,5,3,6,4};
        
        int result = command.maxProfit(input);

        assertEquals(expected, result);
        
        assertEquals(0, command.maxProfit(new int[]{7,6,4,3,1}));

    }

    @Test
    void productExceptSelf(){                 
        
        
        int[] input = new int[]{1,2,3,4};

        int[]output = command.productExceptSelf(input);

        assertArrayEquals(new int[]{24,12,8,6}, output);

    }

    @Test
    void lengthOfLastWord(){
        assertEquals(5, command.lengthOfLastWord("Hello World"));
        assertEquals(4, command.lengthOfLastWord("   fly me   to   the moon  "));
        assertEquals(6, command.lengthOfLastWord("luffy is still joyboy"));
    }

    @Test
    void canJump(){
        assertTrue(command.canJump(new int[]{2,3,1,1,4}));
        assertFalse(command.canJump(new int[]{3,2,1,0,4}));
        assertTrue(command.canJump(new int[]{2,5,0,0}));
    }

    @Test
    void hIndex(){        
        assertEquals(3, command.hIndex(new int[]{3,0,6,1,5}));
        assertEquals(1, command.hIndex(new int[]{1,3,1}));
    }

    @Test
    void longestCommonPrefix() {
        
        assertEquals("fl", command.longestCommonPrefix(new String[]{"flower","flow","flight"}));
        assertEquals("", command.longestCommonPrefix(new String[]{"dog","racecar","car"})); 
        assertEquals("a", command.longestCommonPrefix(new String[]{"a"}));
    }
    
    @Test
    void reverseWords() {
        
        assertEquals("blue is sky the", command.reverseWords("the sky is blue"));
        assertEquals("world hello", command.reverseWords("  hello world  ")); 
        assertEquals("example good a", command.reverseWords("a good   example"));
    }
    
    @Test
    void isPalindrome() {
        
        assertTrue(command.isPalindrome("A man, a plan, a canal: Panama"));
        assertTrue(command.isPalindrome(" "));
        assertFalse(command.isPalindrome("race a car")); 
        assertFalse(command.isPalindrome("0P")); 
    }
    @Test
    void isSubsequence() {
        
        assertTrue(command.isSubsequence("abc","ahbgdc"));
        assertFalse(command.isSubsequence("axc", "ahbgdc"));
        assertFalse(command.isSubsequence("acb", "ahbgdc"));
        assertFalse(command.isSubsequence("aaaaaa", "bbaaaa"));
        
    }
    
    @Test
    void RansomNote() {
        
        
        assertFalse(command.canConstruct("a", "b"));
        assertFalse(command.canConstruct("aa", "ab"));
        assertTrue(command.canConstruct("aa", "aab"));
        
    }
    
    @Test
    void isAnagram() {       
        
        assertFalse(command.isAnagram("rat", "car"));
        assertTrue(command.isAnagram("anagram", "nagaram"));
        
    }
   
    @Test
    void isIsomorphic() {       
        
        assertTrue(command.isIsomorphic("egg", "add"));
        assertFalse(command.isIsomorphic("foo", "bar"));
        assertFalse(command.isIsomorphic("badc", "baba"));
        
    }
    
    @Test
    void isHappy() {        
        assertTrue(command.isHappy(19));
        assertTrue(command.isHappy(1));
        assertTrue(command.isHappy(7));
        assertFalse(command.isHappy(2));        
        
    }

    @Test
    void spiralOrder(){        
        
        assertArrayEquals(new int[]{1,2,3,6,9,8,7,4,5}, command.spiralOrder(new int[][]{{1,2,3},{4,5,6},{7,8,9}}).stream().mapToInt(Integer::intValue).toArray());
        assertArrayEquals(new int[]{1,2,3,4,8,12,11,10,9,5,6,7}, command.spiralOrder(new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12}}).stream().mapToInt(Integer::intValue).toArray());        
        assertArrayEquals(new int[]{1}, command.spiralOrder(new int[][]{{1}}).stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    void romanToInt() {
        
        assertEquals(3      , command.romanToInt("III"));
        assertEquals(58     , command.romanToInt("LVIII")); 
        assertEquals(1994   , command.romanToInt("MCMXCIV"));
    }

}
