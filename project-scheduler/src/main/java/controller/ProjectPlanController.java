package controller;

import domain.ProjectPlan;
import domain.Task;
import repository.ProjectPlanRepository;
import service.ProjectPlanService;

import java.util.List;
import java.util.stream.Collectors;

import static util.InputUtil.*;

public class ProjectPlanController {
    private final static String ERR_NO_PROJ_PLAN = "NO EXISTING PROJECT PLANS!";
    private final static String ERR_NO_TASK = "NO EXISTING TASKS!";

    private ProjectPlanRepository projectPlanRepository = new ProjectPlanRepository();

    private ProjectPlanService projectPlanService = new ProjectPlanService();

    //Project Plan Functions
    private void addProjectPlan() {
        System.out.println();
        System.out.println("Input Project Plan Name");

        ProjectPlan projectPlan = new ProjectPlan();
        projectPlan.setName(getStringInput());
        System.out.println(projectPlanRepository.save(projectPlan).getName() + "has been saved.");
        System.out.println();
}

    private void editProjectPlan() {
        List<ProjectPlan> projectPlans = projectPlanRepository.getAll();

        if (!projectPlans.isEmpty()) {
            System.out.println();
            System.out.println("Select Project Plan to Edit: ");

            projectPlans.forEach(projectPlan -> System.out.println(generateMenuItem(projectPlan.getId(), projectPlan.getName())));

            Integer selectedId = getIntegerInput(projectPlans.size());
            ProjectPlan projectPlan = projectPlanRepository.findById(selectedId.toString());
            editPlanFieldOptions(projectPlan);

        } else {
            showErrorPrompt(ERR_NO_PROJ_PLAN);
        }

        System.out.println();
    }

    private void addEditPlanName(ProjectPlan projectPlan) {
        if (projectPlan.getName() != null) {
            System.out.println("Old Plan Name: " + projectPlan.getName());
        }
        System.out.println("Input Plan Name");
        projectPlan.setName(getStringInput());
    }

    private void editPlanFieldOptions(ProjectPlan projectPlan) {
        Integer ans;

        do {
            System.out.println();
            System.out.println("PLAN: " + projectPlan.getName());
            System.out.println("Select value to edit: ");
            System.out.println("1. Name");
            System.out.println("2. Tasks");
            System.out.println("3. Back");
            System.out.println();
            ans = getIntegerInput(3);

            switch (ans) {
                case 1:
                    System.out.println();
                    addEditPlanName(projectPlan);
                    break;
                case 2:
                    System.out.println();
                    showTaskOperations(projectPlan);
                    break;
                case 3:
                    break;

            }
        } while (ans != 3);
    }

    private void showTaskOperations(ProjectPlan projectPlan) {
        Integer ans;
        do {
            System.out.println();
            System.out.println("Choose Task Operation");
            System.out.println("1. Add Task");
            System.out.println("2. Edit Task");
            System.out.println("3. Back");
            System.out.println();
            ans = getIntegerInput(3);
            switch (ans) {
                case 1:
                    addTask(projectPlan);
                    break;
                case 2:
                    editTask(projectPlan);
                    break;
                case 3:
                    break;
            }
        } while (ans != 3);
    }

    private void generateSchedule() {
        List<ProjectPlan> projectPlans = projectPlanRepository.getAll();

        if (projectPlans.size() > 0) {
            System.out.println();
            System.out.println("Select Project Plan to generate schedule");

            projectPlans.forEach(projectPlan -> System.out.println(generateMenuItem(projectPlan.getId(), projectPlan.getName())));
            Integer selectedId = getIntegerInput(projectPlans.size());

            ProjectPlan projectPlan = projectPlanService.generateScheduleForPlan(projectPlanRepository.findById(String.valueOf(selectedId)));

            if (!projectPlan.getTasks().isEmpty()) {
                projectPlan.getTasks().forEach(task -> {
                    System.out.println();
                    System.out.println("TASK #" + task.getId());
                    System.out.println(task.getName() + ": ");
                    System.out.println("Duration: " + task.getDuration());
                    System.out.println("Start Date: " + task.getStartDate());
                    System.out.println("End Date: " + task.getEndDate());
                    System.out.println("Depended on tasks: " + task.listDependencies());
                    System.out.println();
                });
            } else {
                showErrorPrompt(ERR_NO_TASK);
            }

            System.out.println();

        } else {
            showErrorPrompt(ERR_NO_PROJ_PLAN);
        }
    }

    private Boolean showMainOptions() {
        System.out.println();
        System.out.println("Choose Operation");
        System.out.println("1. Add Project Plan");
        System.out.println("2. Edit Project Plan (Use to add tasks)");
        System.out.println("3. Generate Schedule");
        System.out.println("4. Exit");
        System.out.println();

        Integer ans = getIntegerInput(4);
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
                return true;
        }
        return false;
    }

    //Task Functions
    private void addTask(ProjectPlan projectPlan) {

        Task task = new Task(String.valueOf(projectPlan.getTasks().size() + 1));
        System.out.println();
        addEditTaskName(task);
        System.out.println();
        addEditTaskDuration(task);
        System.out.println();

        if (projectPlan.getTasks().size() > 0) {
            addTaskDependency(projectPlan, task);
        }
        projectPlan.getTasks().add(task);
    }

    private void addEditTaskName(Task task) {
        if (task.getName() != null) {
            System.out.println("Old Task Name: " + task.getName());
        }
        System.out.println("Input Task Name");
        task.setName(getStringInput());
    }

    private void addEditTaskDuration(Task task) {
        if (task.getDuration() != null) {
            System.out.println("Old Task Duration: " + task.getDuration());
        }
        System.out.println("Input Task Duration (Days)");
        task.setDuration(getIntegerInput(999));
        task.setMaxDuration(task.getDuration());
    }

    private void addTaskDependency(ProjectPlan projectPlan, Task task) {
        Boolean isEdit = !task.getDependencyTaskIds().isEmpty();

        if (isEdit) {
            System.out.println("Current Task Dependencies: " + task.listDependencies());
        }

        System.out.println();
        System.out.println("Add Task Dependencies");

        //Get Tasks in plan that is not the current task or does not already depend on task
        List<Task> tasks = projectPlan.getTasks().stream()
                .filter(taskToFilter ->
                        !taskToFilter.getId().equals(task.getId()) &&
                        !taskToFilter.getDependencyTaskIds().contains(task.getId()))
                .collect(Collectors.toList());

        Integer ans;
        Integer maxInput;
        do {
            maxInput = projectPlan.getTasks().size() + (isEdit ? 2 : 1);

            //Show Task Selection
            System.out.println();
            tasks.forEach(existingTasks -> System.out.println(generateMenuItem(existingTasks.getId(), existingTasks.getName())));

            if (isEdit) {
                System.out.println((maxInput - 1) + ". Clear Dependencies");
            }
            System.out.println((maxInput) + ". None");
            System.out.println();

            ans = getIntegerInput(maxInput);

            if (task.getDependencyTaskIds().contains(String.valueOf(ans))) {
                System.out.println(String.format("%s already depends on this task.", task.getName()));
            } else if (!tasks.stream().map(taskToMap -> taskToMap.getId())
                    .collect(Collectors.toList())
                    .contains(String.valueOf(ans)) && ans != maxInput) {
                if (ans == maxInput - 1) {
                    task.getDependencyTaskIds().clear();
                    System.out.println("Successfully cleared dependencies!");
                } else {
                    System.out.println("Not in list");
                }
            } else if (projectPlanService.checkForCircularDependency(projectPlan.getTasks(), task.getId(), String.valueOf(ans))) {
                System.out.println("Cannot add due to a circular dependency");
            } else if (ans != maxInput) {
                task.getDependencyTaskIds().add(String.valueOf(ans));
                System.out.println("Successfully added as dependency!");
            }

            isEdit = !task.getDependencyTaskIds().isEmpty();
        } while (ans != maxInput);

        System.out.println();
    }

    private void editTask(ProjectPlan projectPlan) {
        List<Task> tasks = projectPlan.getTasks();
        if (tasks.size() > 0) {
            Integer ans;
            Integer maxInput = tasks.size() + 1;
            do {
                System.out.println();
                System.out.println("Select task to edit");
                tasks.forEach(existingTasks -> System.out.println(generateMenuItem(existingTasks.getId(), existingTasks.getName())));
                System.out.println(generateMenuItem(String.valueOf(maxInput), "None"));
                System.out.println();
                ans = getIntegerInput(maxInput);
                if (ans != maxInput)
                    editTaskFieldOptions(projectPlan, projectPlanService.findTaskById(projectPlan, String.valueOf(ans)));
            } while (ans != maxInput);
        } else {
            showErrorPrompt(ERR_NO_TASK);
        }
    }

    private void editTaskFieldOptions(ProjectPlan projectPlan, Task task) {
        Integer ans;

        do {
            System.out.println();
            System.out.println("TASK: " + task.getName());
            System.out.println("Select value to edit: ");
            System.out.println("1. Name");
            System.out.println("2. Duration");
            System.out.println("3. Dependencies");
            System.out.println("4. Back");
            System.out.println();
            ans = getIntegerInput(4);

            switch (ans) {
                case 1:
                    System.out.println();
                    addEditTaskName(task);
                    break;
                case 2:
                    System.out.println();
                    addEditTaskDuration(task);
                    break;
                case 3:
                    System.out.println();
                    addTaskDependency(projectPlan, task);
                    break;
                case 4:
                    break;

            }
        } while (ans != 4);
    }

    private void showErrorPrompt(String message) {
        System.out.println();
        System.out.println(message);
        promptAnyKey();
        System.out.println();
    }

    private String generateMenuItem(String selector, String text) {
        return String.format("%s. %s", selector, text);
    }

    public void start() {
        Boolean hasEnded = false;
        do {
            hasEnded = showMainOptions();

        } while (hasEnded.equals(false));
    }

}
