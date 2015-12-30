package oczcalculator.milen.com.ochzchronometer;
import java.text.SimpleDateFormat;
import java.util.Date;
// refactor the _
public class TaskEntity {

    private int _id;
    private String employeeName;
    private String taskName;
    private long secondsWorked;
    private boolean isNotInterrupted;
    private Date dateAdded;

    private static final int DEFAULT_ID = 0;


    public TaskEntity(String employeeName, int taskId, String taskName, long secondsWorked, boolean isNotInterrupted, Date dateAdded) {
        setEmployee(employeeName);
        setId(taskId);
        setTaskName(taskName);
        setSecondsWorked(secondsWorked);
        setIsNotInterrupted(isNotInterrupted);
        setDateAdded(dateAdded);
    }

    public TaskEntity(String employeeName, String taskName, long secondsWorked, boolean isNotInterrupted, Date dateAdded) {
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

    public void setTaskName(String _taskName) {
        this.taskName = _taskName;
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        String entityFormat = "employee: %s\ntask: %s done for %d sec.\n%s\nadded date: %s";
        SimpleDateFormat dateStringFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = dateStringFormatter.format(this.getDateAdded());

        String resultString = String.format(
                entityFormat,
                this.getEmployee(),
                this.getTaskName(),
                this.getSecondsWorked(),
                (isNotInterrupted() ? "is not interrupted" : "is interrupted"),
                dateString
        );

        return resultString;
    }
}
