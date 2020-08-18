package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-26.
 */
public class UserWeightProfile {
    private Float weight;
    private Float goal_weight;
    private Integer diet_period;

    public void setUserWeightProfile(Float weight, Float goal_weight, Integer diet_period) {
        this.weight = weight;
        this.goal_weight = goal_weight;
        this.diet_period = diet_period;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getGoal_weight() {
        return goal_weight;
    }

    public void setGoal_weight(Float goal_weight) {
        this.goal_weight = goal_weight;
    }

    public Integer getDiet_period() {
        return diet_period;
    }

    public void setDiet_period(Integer diet_period) {
        this.diet_period = diet_period;
    }
}
