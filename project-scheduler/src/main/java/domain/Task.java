package domain;

import java.util.HashSet;
import java.util.Set;

public class Task extends NamedObject {
    private Integer duration; //Duration in Days
    private Integer maxDuration; //Duration in Days
    private Set<String> dependencyTaskIds;
    private String startDate;
    private String endDate;

    public Task(String id) {
        this.id = id;
        this.dependencyTaskIds = new HashSet<>();
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Set<String> getDependencyTaskIds() {
        return dependencyTaskIds;
    }

    public void setDependencyTaskIds(Set<String> dependencyTaskIds) {
        this.dependencyTaskIds = dependencyTaskIds;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String listDependencies() {
        String dependencyList = "";
        for (String id : dependencyTaskIds) {
            dependencyList += (id + ", ");
        }
        return dependencyList;
    }
}
