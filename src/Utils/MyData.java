package Utils;

import java.time.LocalDateTime;

public class MyData {
    public static Integer currentWeek;

    public static void initialize() {
        LocalDateTime date = LocalDateTime.now();
        String month = date.getMonth().toString();
        switch (month) {
            case "OCTOBER":
                if (date.getDayOfMonth() >= 1 && date.getDayOfMonth() <= 7) MyData.currentWeek = 1;
                if (date.getDayOfMonth() > 7 && date.getDayOfMonth() <= 14) MyData.currentWeek = 2;
                if (date.getDayOfMonth() > 14 && date.getDayOfMonth() <= 21) MyData.currentWeek = 3;
                if (date.getDayOfMonth() > 21 && date.getDayOfMonth() <= 28) MyData.currentWeek = 4;
                if (date.getDayOfMonth() > 28) MyData.currentWeek = 5;
            case "NOVEMBER":
                if (date.getDayOfMonth() <= 4) MyData.currentWeek = 5;
                if (date.getDayOfMonth() > 4 && date.getDayOfMonth() <= 11) MyData.currentWeek = 6;
                if (date.getDayOfMonth() > 14 && date.getDayOfMonth() <= 18) MyData.currentWeek = 7;
                if (date.getDayOfMonth() > 21 && date.getDayOfMonth() <= 25) MyData.currentWeek = 8;
                if (date.getDayOfMonth() > 25) MyData.currentWeek = 9;
            case "DECEMBER":
                if (date.getDayOfMonth() <= 2) MyData.currentWeek = 9;
                if (date.getDayOfMonth() > 2 && date.getDayOfMonth() <= 9) MyData.currentWeek = 10;
                if (date.getDayOfMonth() > 9 && date.getDayOfMonth() <= 16) MyData.currentWeek = 11;
                if (date.getDayOfMonth() > 16) MyData.currentWeek = 12;
            case "JANUARY":
                if (date.getDayOfMonth() <= 13) MyData.currentWeek = 13;
                else MyData.currentWeek = 14;
        }
    }
}
