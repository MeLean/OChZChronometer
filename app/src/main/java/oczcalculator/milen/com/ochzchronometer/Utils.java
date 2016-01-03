package oczcalculator.milen.com.ochzchronometer;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

public class Utils {
    static String SHARED_PREFERENCES_FILE_NAME = "ListViewTaskNames";
    static String SHARED_PREFERENCES_STRING_NAME = "stringList";
    static String TASK_SEPARATOR = ";";

    private Utils() {
    }

    ;

    static String getDateAsString(String pattern) {
        String result;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        result = formatter.format(Calendar.getInstance().getTime());
        return result;
    }

    static String[] removeDuplicateOrEmptyTasks(String[] tasksStringArray) {
        HashSet<String> set = new HashSet<>();
        for (int i = tasksStringArray.length - 1; i >= 0; i--) {
            String task = tasksStringArray[i];
            if (task.length() > 0) {
                set.add(task);
            }
        }

        String[] result = set.toArray(new String[set.size()]);
        Arrays.sort(result);
        return result;
    }

    static String[] splitBySeparator(String str) {
        return str.trim().split(String.format("\\s*%s\\s*", Utils.TASK_SEPARATOR));
    }

    static String purifyString(String preferenceString) {
        String[] pureTasks = Utils.removeDuplicateOrEmptyTasks(Utils.splitBySeparator(preferenceString));
        StringBuilder result = new StringBuilder();
        for (String task : pureTasks) {
            result.append(task);
            result.append(Utils.TASK_SEPARATOR);
        }

        return result.toString();
    }

    static String makeStringReport(Context context, String[] tasksStringArray, ArrayList<TaskEntity> taskMassiv) {
        StringBuilder report = new StringBuilder();
        report.append(context.getString(R.string.head_of_table_report));
        String[] uniqueTasks = new HashSet<String>(Arrays.asList(tasksStringArray)).toArray(new String[0]);
        for (String taskString : uniqueTasks) {
            int timesOccur = 0;
            long secondsTaskWorked = 0;

            for (TaskEntity task : taskMassiv) {
                boolean hasMatch = task.getTaskName().equalsIgnoreCase(taskString);
                if (hasMatch) {
                    secondsTaskWorked += task.getSecondsWorked();
                    if (task.isNotInterrupted()) {
                        timesOccur++;
                    }
                }
            }

            if (secondsTaskWorked != 0) {
                float averageTime = timesOccur != 0 ? (float) (secondsTaskWorked / timesOccur) : (float) secondsTaskWorked;
                report.append(String.format("%s:\ttimes: %d\tavg: %.2f\n", taskString, timesOccur, averageTime));
            }
        }
        return report.toString();
    }
}
