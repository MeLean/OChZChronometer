package oczcalculator.milen.com.ochzchronometer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskEntity {

    private int _id;
    private String taskName;
    private long secondsWorked;
    private boolean isNotInterrupted;
    private Date dateAdded;
    private String employee;

    public TaskEntity(String employee, int id, String name, long secondsWorked, boolean isNotInterrupted, Date dateAdded) {
        setEmployee(employee);
        setId(id);
        setTaskName(name);
        setSecondsWorked(secondsWorked);
        setIsNotInterrupted(isNotInterrupted);
        setDateAdded(dateAdded);
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        String entityFormat = "id: %d\temployee: %s\ttask: %s done for %d seconds\t%s\tdata added: %s";
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = dateFormater.format(this.getDateAdded());

        String result = String.format(entityFormat,
                this.getId(),
                this.getEmployee(),
                this.getTaskName(),
                this.getSecondsWorked(),
                (isNotInterrupted() ? "is not interrupted" : "is interrupted"),
                dateString

        );

        return result;
    }
}
