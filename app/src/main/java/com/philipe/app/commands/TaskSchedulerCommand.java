package com.philipe.app.commands;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class TaskSchedulerCommand {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Integer, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger taskIdGenerator = new AtomicInteger(1);

    @ShellMethod(key = "schedule-task", value = "Agenda uma tarefa para ser executada depois de X segundos.")
    public String scheduleTask(int seconds, String message) {
        int taskId = taskIdGenerator.getAndIncrement();
        
        ScheduledFuture<?> future = scheduler.schedule(() -> System.out.println("Tarefa #" + taskId + ": " + message),
            seconds, TimeUnit.SECONDS);
        tasks.put(taskId, future);
        return "Tarefa #" + taskId + " agendada para rodar em " + seconds + " segundos.";
    }

    @ShellMethod(key = "list-scheduled-tasks", value = "Lista todas as tarefas agendadas.")
    public String listScheduledTasks() {
        return tasks.isEmpty() ? "Nenhuma tarefa agendada." :
            tasks.keySet().stream()
                .map(id -> "Tarefa #" + id)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "cancel-task", value = "Cancela uma tarefa agendada.")
    public String cancelTask(int taskId) {
        ScheduledFuture<?> future = tasks.remove(taskId);
        if (future != null && !future.isDone()) {
            future.cancel(false);
            return "Tarefa #" + taskId + " cancelada.";
        }
        return "Tarefa #" + taskId + " não encontrada ou já executada.";
    }
}
