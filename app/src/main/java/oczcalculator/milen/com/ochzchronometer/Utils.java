package oczcalculator.milen.com.ochzchronometer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

public class Utils {
   static String SHARED_PREFERENCES_FILE_NAME = "ListViewTaskNames";
   static String SHARED_PREFERENCES_STRING_NAME = "stringList";
   static String TASK_SEPARATOR = ";";

    private Utils(){};

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
}
