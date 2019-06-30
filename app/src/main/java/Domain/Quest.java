package Domain;

public class Quest {

    private QuestType type;
    private double done;
    private double goal;
    private UnitType unitType;
    private boolean isDone;
    private int id;

    public Quest() {
    }

    public Quest(QuestType type, double goal, UnitType unitType) {
        this.type = type;
        this.goal = goal;
        this.unitType = unitType;
        isDone = false;

    }

    public void setDone(double done) {
        this.done = done;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUnit(UnitType unit) {
        unitType = unit;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public QuestType getType() {
        return type;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public double getDone() {
        return done;
    }

    public double getGoal() {
        return goal;
    }

    public int getId() {
        return id;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public int completeQuestXp() {
        switch (type) {
            case Steps:
                return (int) (goal * 0.25);
            case Walking:
                if (unitType == UnitType.Meters) {
                    return (int) (goal * 0.25);
                } else {
                    return (int) ((goal * 1000) * 0.25);
                }
            case Running:
                if (unitType == UnitType.Meters) {
                    return (int) (goal * 0.35);
                } else {
                    return (int) ((goal * 1000) * 0.35);
                }
            default:
                return 0;
        }
    }

    public void addDone(double done) {
        if (isDone) {
            return;
        } else {
            if (this.done + done > goal) {
                this.done = goal;
            } else {
                this.done += done;
            }

            if (getDone() >= goal)
                isDone = true;
        }
    }
}
