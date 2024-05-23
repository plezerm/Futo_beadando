package Futoverseny;

import jakarta.persistence.*;

@Entity
public class CompetitionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int compId;
    private String compName;
    private int length;

    public int getCompId() {
        return compId;
    }

    public void setCompId(int id) {
        this.compId = id;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
