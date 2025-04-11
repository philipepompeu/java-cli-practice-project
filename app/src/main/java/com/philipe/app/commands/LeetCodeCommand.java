package com.philipe.app.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;

@ShellComponent
public class LeetCodeCommand {


    public List<List<String>> groupAnagrams(String[] strs)  {
        
        Map<String, Set<String>> groups = new HashMap<String, Set<String>>();

        List.of(strs).forEach(word ->{
            char[] tmp = word.toCharArray();
    
            Arrays.sort(tmp);
    
            String newWord = new String(tmp);            
            
            if(!groups.containsKey(newWord)){
                groups.put(newWord, new HashSet<String>(List.of(word)));

            }else{
                groups.get(newWord).add(word);
            }

        });

        return groups.values().stream().map(ArrayList::new).collect(Collectors.toList());       
    }

    
}
