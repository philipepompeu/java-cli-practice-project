package com.philipe.app.commands;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class EventManagerCommand {

    Map<LocalDate, List<String>> events = new TreeMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EventManagerCommand(){
    
    }

    private LocalDate parseToDate(String date){
        return LocalDate.parse(date, formatter);
    }

    @ShellMethod(key = "add-event", value = "Adiciona um evento.")
    public String addEvent(String event, String date) {

        LocalDate eventDate = parseToDate(date);
        
        if(events.containsKey(eventDate)){
            events.get(eventDate).add(event);
        }else{
            events.put(eventDate, new ArrayList<>(List.of(event)));
        }

        return String.format("Evento %s adicionado com data prevista de %s", event, date);
    }

    @ShellMethod(key = "list-events", value = "Lista eventos dentro do intervalo.")
    public String listEvents(String startDate, String endDate){

        LocalDate startLocalDate = parseToDate(startDate);
        LocalDate endLocalDate = parseToDate(endDate);        

        return events.entrySet().stream()
                        .filter(rec -> ( rec.getKey().isAfter(startLocalDate) || rec.getKey().isEqual(startLocalDate) ) && (rec.getKey().isBefore(endLocalDate) || rec.getKey().isEqual(endLocalDate)) )
                        .flatMap(rec -> rec.getValue().stream().map(event -> rec.getKey().format(this.formatter) + ":"+ event) )
                        .collect(Collectors.joining("\n"));        
    }


    
}
