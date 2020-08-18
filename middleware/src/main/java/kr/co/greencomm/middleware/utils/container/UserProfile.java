package kr.co.greencomm.middleware.utils.container;

/**
 * Created by jeyang on 2016-08-26.
 */
public class UserProfile {
    private Integer sex;
    private Integer age;
    private Integer height;
    private Float weight;
    private Float goalWeight;

    public void setUserProfile(Integer sex, Integer age, Integer height, Float weight, Float goalWeight) {
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.goalWeight = goalWeight;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(Float goalWeight) {
        this.goalWeight = goalWeight;
    }
}
