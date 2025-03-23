package com.philipe.app.commands;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class DateCommand {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Set<LocalDate> holidays;

    public DateCommand(){

        holidays = Set.of(  LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 25)
                        );
    }
    
    private LocalDate parseToDate(String date){
        return LocalDate.parse(date, formatter);
    }
    
    @ShellMethod(key="date-diff", value="Retorna a diferença entre as datas (X anos, Y meses e Z dias)")
    public String differenceBetweenDates(String date1, String date2){

        LocalDate firstDate = parseToDate(date1);
        LocalDate secondDate = parseToDate(date2);

        Period difference = Period.between(firstDate, secondDate);
        
        return String.format("Diferença entre as datas (%d anos, %d meses e %d dias)",difference.getYears(), difference.getMonths(), difference.getDays());
    }

    @ShellMethod(key="next-occurrences", value="Retorna os próximos N dias de um evento recorrente.")    
    public String nextOccurences(String start, int frequency, int numberOfOccurences){

        LocalDate startDate = parseToDate(start);

        return Stream.iterate(startDate, occurence-> occurence.plusDays(frequency))
                    .limit(numberOfOccurences)
                    .map(d -> d.format(this.formatter))
                    .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key="is-holiday", value="Retorna se a data é um feriado nacional (Sim, é Natal ou Não é feriado)")
    public String isDateAHoliday(String dateToCheck){

        LocalDate date1 = parseToDate(dateToCheck);
        
        boolean isHoliday = this.holidays.stream().anyMatch(dt -> dt.getDayOfMonth() == date1.getDayOfMonth() && dt.getMonth().equals(date1.getMonth()) );        

        return String.format("[ %s ] %s ",dateToCheck, (isHoliday ? "é feriado" : "não é feriado"));
    }
}
