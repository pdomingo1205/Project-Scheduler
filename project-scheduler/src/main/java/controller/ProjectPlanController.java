package controller;

import model.ProjectPlan;
import model.Task;
import repository.ProjectPlanRepository;
import service.ProjectPlanService;
import util.InputUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectPlanController {

    private ProjectPlanRepository projectPlanRepository = new ProjectPlanRepository();
    private ProjectPlanService projectPlanService = new ProjectPlanService();

    //Project Plan Functions
    private void addProjectPlan() {
        ProjectPlan projectPlan = projectPlanService.initProjectPlanObject();
        print("Input Project Plan Name");
        projectPlan.setName(InputUtil.getStringInput());
        print(projectPlanRepository.save(projectPlan).getName() + "has been saved.");
    }

    private void editProjectPlan() {
        print("Select Project Plan to Edit");
        List<ProjectPlan> projectPlans = projectPlanRepository.getAll();
        projectPlans.forEach(projectPlan -> print(generateMenuItem(projectPlan.getId(), projectPlan.getName())));
        Integer selectedId = InputUtil.getIntegerInput(projectPlans.size());
        showTaskOperations(String.valueOf(selectedId));
    }

    private void showTaskOperations(String projectPlanId) {
        Integer ans;
        do {
            print("Choose Task Operation");
            print("1. Add Task");
            print("2. Edit Task");
            print("3. Back");
            ans = InputUtil.getIntegerInput(3);
            switch (ans) {
                case 1:
                    addTask(projectPlanId);
                    break;
                case 2:
                    editTask(projectPlanId);
                    break;
                case 3:
                    break;
            }
        } while (ans != 3);
    }

    private void generateSchedule() {
        List<ProjectPlan> projectPlans = projectPlanRepository.getAll();
        if (projectPlans.size() > 0 ) {
            print("Select Project Plan to generate schedule");
            projectPlans.forEach(projectPlan -> print(generateMenuItem(projectPlan.getId(), projectPlan.getName())));
            Integer selectedId = InputUtil.getIntegerInput(projectPlans.size());
            ProjectPlan projectPlan = projectPlanService.generateScheduleForPlan(projectPlanRepository.findById(String.valueOf(selectedId)));
            projectPlan.getTasks().forEach(task -> {
                print(task.getName() + ": ");
                print("Start Date: " + task.getStartDate());
                print("End Date: " + task.getEndDate());
            });

        } else {
            print("No existing plans.");
        }
    }

    private void showMainOptions(Boolean hasEnded) {
        print("Choose Operation");
        print("1. Add Project Plan");
        print("2. Edit Project Plan (Use to add tasks)");
        print("3. Generate Schedule");
        print("4. Exit");

        Integer ans = InputUtil.getIntegerInput(3);
        switch (ans) {
            case 1:
                addProjectPlan();
                break;
            case 2:
                editProjectPlan();
                break;
            case 3:
                generateSchedule();
                break;
            case 4:
                hasEnded = true;
                break;
        }
    }

    //Task Functions
    private void addTask(String projectPlanId) {
        ProjectPlan projectPlan = projectPlanRepository.findById(projectPlanId);

        Task task = projectPlanService.initTaskObject(projectPlan);
        addEditTaskName(task);
        addEditTaskDuration(task);
        if (projectPlan.getTasks().size() > 0) {
            addTaskDependency(projectPlan, task);
        }
        projectPlan.getTasks().add(task);
    }

    private void addEditTaskName(Task task) {
        print("Input Task Name");
        task.setName(InputUtil.getStringInput());
    }

    private void addEditTaskDuration(Task task) {
        print("Input Task Duration");
        task.setDuration(InputUtil.getIntegerInput(999));
        task.setMaxDuration(task.getDuration());
    }

    private void addTaskDependency(ProjectPlan projectPlan, Task task) {
        print("Add Task Dependencies");
        List<Task> tasks = projectPlan.getTasks().stream()
                .filter(taskToFilter -> {
                    return !taskToFilter.getId().equals(task.getId()) && !taskToFilter.getDependencyTaskIds().contains(task.getId());
                })
                .collect(Collectors.toList());
        List<Task> dependentTasks = projectPlan.getTasks().stream()
                .filter(taskToFilter -> {
                    return taskToFilter.getDependencyTaskIds().contains(task.getId());
                })
                .collect(Collectors.toList());
        Integer ans;
        Integer maxInput = projectPlan.getTasks().size() + 1;
        do {
            tasks.forEach(existingTasks -> print(generateMenuItem(existingTasks.getId(), existingTasks.getName())));
            print((maxInput) + ". None");
            ans = InputUtil.getIntegerInput(maxInput);
            if (task.getDependencyTaskIds().contains(String.valueOf(ans))) {
                print(String.format("%s already depends on this task.", task.getName()));
            } else if (!tasks.stream().map(taskToMap -> taskToMap.getId())
                    .collect(Collectors.toList())
                    .contains(String.valueOf(ans)) && ans != maxInput) {
                print("Not in list");
            }  else if (projectPlanService.checkForCircularDependency(projectPlan.getTasks(), task.getId(), String.valueOf(ans))) {
                print("Cannot add due to a circular dependency");
            } else if (ans != maxInput){
                task.getDependencyTaskIds().add(String.valueOf(ans));
                print("Successfully added as dependency!");
            }
        } while (ans != maxInput);
    }

    private void editTask(String projectPlanId) {
        ProjectPlan projectPlan = projectPlanRepository.findById(projectPlanId);
        List<Task> tasks = projectPlan.getTasks();
        if (tasks.size() > 0) {
            Integer ans;
            Integer maxInput = tasks.size() + 1;
            do {
                print("Select task to edit");
                tasks.forEach(existingTasks -> print(generateMenuItem(existingTasks.getId(), existingTasks.getName())));
                print(generateMenuItem(String.valueOf(maxInput), "None"));
                ans = InputUtil.getIntegerInput(maxInput);
                if (ans != maxInput)
                    editTaskFieldOptions(projectPlan, projectPlanService.findTaskById(projectPlan, String.valueOf(ans)));
            } while (ans != maxInput);
        }
    }

    private void editTaskFieldOptions(ProjectPlan projectPlan, Task task) {
        Integer ans;

        do {
            print("Select value to edit");
            print("1. Name");
            print("2. Duration");
            print("3. Dependencies");
            print("4. Back");
            ans = InputUtil.getIntegerInput(4);

            switch (ans) {
                case 1:
                    addEditTaskName(task);
                    break;
                case 2:
                    addEditTaskDuration(task);
                    break;
                case 3:
                    addTaskDependency(projectPlan, task);
                    break;
                case 4:
                    break;

            }
        } while (ans != 4);
    }

    private String generateMenuItem(String selector, String text) {
        return String.format("%s. %s", selector, text);
    }

    public void start() {
        Boolean hasEnded = false;

        do {
            showMainOptions(hasEnded);

        } while (hasEnded.equals(false));
    }

    /**
     * Used to shorten print command
     * @param text
     */
    private void print(String text) {
        System.out.println(text);
    }

}
