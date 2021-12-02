package repository;

import domain.ProjectPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dummy Repository
 */
public class ProjectPlanRepository {

    private List<ProjectPlan> projectPlans = new ArrayList<>();

    public List<ProjectPlan> getAll() {
        return projectPlans;
    }

    public ProjectPlan findById(String id) {
        return projectPlans.stream().filter(projectPlan -> projectPlan.getId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public ProjectPlan save(ProjectPlan projectPlan) {
        projectPlan.setId(String.valueOf(projectPlans.size() + 1));
        projectPlans.add(projectPlan);
        return projectPlan;
    }

}
