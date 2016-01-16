package oczcalculator.milen.com.ochzchronometer;

public class TaskEntity {

    private int _id;
    private String employeeName;
    private String taskName;
    private long secondsWorked;
    private boolean isNotInterrupted;
    private String dateAdded;

    private static final int DEFAULT_ID = 0;


    public TaskEntity(String employeeName, int taskId, String taskName, long secondsWorked, boolean isNotInterrupted, String dateAdded) {
        setEmployee(employeeName);
        setId(taskId);
        setTaskName(taskName);
        setSecondsWorked(secondsWorked);
        setIsNotInterrupted(isNotInterrupted);
        setDateAdded(dateAdded);
    }

    public TaskEntity(String employeeName, String taskName, long secondsWorked, boolean isNotInterrupted, String dateAdded) {
        this(employeeName, DEFAULT_ID, taskName, secondsWorked, isNotInterrupted, dateAdded);
    }

    public String getEmployee() {
        return employeeName;
    }

    public void setEmployee(String employee) {
        this.employeeName = employee;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getSecondsWorked() {
        return secondsWorked;
    }

    public void setSecondsWorked(long secondsWorked) {
        this.secondsWorked = secondsWorked;
    }

    public boolean isNotInterrupted() {
        return isNotInterrupted;
    }

    public void setIsNotInterrupted(boolean isNotInterrupted) {
        this.isNotInterrupted = isNotInterrupted;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        String entityFormat = "employee: %s\ntask: %s done for %d sec.\n%s\nadded date: %s";

        return  String.format(
                entityFormat,
                this.getEmployee(),
                this.getTaskName(),
                this.getSecondsWorked(),
                (isNotInterrupted() ? "is not interrupted" : "is interrupted"),
                this.getDateAdded()
        );
    }
}
