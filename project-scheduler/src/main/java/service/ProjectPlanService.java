package service;

import model.ProjectPlan;
import model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectPlanService {

    public ProjectPlan generateScheduleForPlan(ProjectPlan projectPlan) {
        List<Task> tasks = projectPlan.getTasks();
        LocalDate today = LocalDate.now();
        tasks.forEach(task -> {
            getMaxDurationOfTask(tasks, task);
            Integer startOffset = task.getDuration() == task.getMaxDuration()? 0: task.getMaxDuration() - task.getDuration();
            task.setStartDate(today.plusDays(startOffset).format(DateTimeFormatter.ISO_LOCAL_DATE));
            task.setEndDate(today.plusDays(task.getMaxDuration()).format(DateTimeFormatter.ISO_LOCAL_DATE));
        });
        return projectPlan;
    }

    /**
     * Recursive function to find total duration of task after it's dependencies
     * @param tasks - list of tasks
     * @param task - task to get duration
     * @return
     */
    private Integer getMaxDurationOfTask(List<Task> tasks, Task task) {
        if (task.getDependencyTaskIds().size() > 0) {
            SortedSet<Integer> dependencyDurations = new TreeSet<>();
            task.getDependencyTaskIds().forEach(dependencyId -> {
                Task dependencyTask = tasks.get(Integer.parseInt(dependencyId) - 1);
                dependencyDurations.add(getMaxDurationOfTask(tasks, dependencyTask));
            });
            task.setMaxDuration(task.getDuration() + dependencyDurations.last());
        }
        return task.getMaxDuration();
    }

    public ProjectPlan initProjectPlanObject() {
        ProjectPlan projectPlan = new ProjectPlan();
        projectPlan.setTasks(new ArrayList<>());
        return projectPlan;
    }

    public Task initTaskObject(ProjectPlan  projectPlan) {
        Task task = new Task();
        task.setDependencyTaskIds(new HashSet<>());
        task.setId(String.valueOf(projectPlan.getTasks().size() + 1));
        return task;
    }

    public Task findTaskById(ProjectPlan projectPlan, String id) {
        List<Task> tasks = projectPlan.getTasks().stream().filter(task -> task.getId().equals(id)).collect(Collectors.toList());
        return tasks.size() > 0? tasks.get(0): null;
    }

    public List<String> findDependentIds(List<Task> tasks, String idToCheck) {
        return tasks.stream().filter(task -> task.getDependencyTaskIds().contains(idToCheck)).map(task -> task.getId()).collect(Collectors.toList());
    }

    /**
     * Checks if idToCheck has circular dependency on dependentId
     * @param tasks list of tasks
     * @param dependentId Task to check for dependencies
     * @param idToCheck dependency Id to find
     * @return
     */
    public Boolean checkForCircularDependency(List<Task> tasks, String dependentId, String idToCheck) {
        List<String> dependentIds = new ArrayList<>();
        tasks.stream().forEach(task -> {
            task.getDependencyTaskIds().forEach(taskId -> System.out.println(taskId));
            if (task.getDependencyTaskIds().contains(dependentId)) {
                dependentIds.addAll(findDependentIds(tasks, task.getId()));
            }
        });
        dependentIds.forEach(s -> System.out.println("Dependents: " + s));
        return dependentIds.contains(idToCheck);
    }
}
