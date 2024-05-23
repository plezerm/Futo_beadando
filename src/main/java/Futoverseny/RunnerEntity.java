package Futoverseny;

import jakarta.persistence.*;

@Entity
public class RunnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int runnerId;
    private String runnerName;
    private int age;
    private int gender;

    public static final String[] genderTypes = {"n/a", "férfi", "nő"}; //az int 0, 1, 2 értékhez tartozó adatok


    public RunnerEntity() {
    }

    public long getRunnerId() {
        return runnerId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public String getGenderName() {
        return genderTypes[this.gender];
    }

    public int getGender() {
        return gender;
    }

    public void setRunnerId(int runnerId) {
        this.runnerId = runnerId;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return String.valueOf(runnerId);
    }
}
