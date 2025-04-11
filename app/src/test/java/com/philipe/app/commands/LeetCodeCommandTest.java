package com.philipe.app.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
