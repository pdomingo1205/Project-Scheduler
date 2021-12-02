package domain;

import java.util.ArrayList;
import java.util.List;

public class ProjectPlan extends NamedObject{
    private List<Task> tasks;

    public ProjectPlan() {
        this.tasks = new ArrayList<>();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
