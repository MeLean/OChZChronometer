package oczcalculator.milen.com.ochzchronometer;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

public class Utils {
    static String SHARED_PREFERENCES_FILE_NAME = "ListViewTaskNames";
    static String SHARED_PREFERENCES_STRING_NAME = "stringList";
    static String TASK_SEPARATOR = ";";

    private Utils() {
    }

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

    @NonNull
    static String[] splitBySeparator(String str) {
        return str.trim().split(String.format("\\s*%s\\s*", Utils.TASK_SEPARATOR));
    }

    @NonNull
    static String purifyString(String preferenceString) {
        String[] pureTasks = Utils.removeDuplicateOrEmptyTasks(Utils.splitBySeparator(preferenceString));
        StringBuilder result = new StringBuilder();
        for (String task : pureTasks) {
            result.append(task);
            result.append(Utils.TASK_SEPARATOR);
        }

        return result.toString();
    }


    public static String convertSecondsInTmeString(float averageTime) {
        int hours = (int) averageTime / 3600;
        int minutes = (int)((averageTime - (hours * 3600)) / 60);
        int seconds = (int)(averageTime - (hours * 3600) - (minutes * 60));
        return String.format("%s:%s:%s", putLeadingZero(hours), putLeadingZero(minutes), putLeadingZero(seconds));
    }

    private static String putLeadingZero(int value) {
        String leadingZero = "";
        if (value < 10){
            leadingZero = "0";
        }

        return leadingZero + value;
    }
}
