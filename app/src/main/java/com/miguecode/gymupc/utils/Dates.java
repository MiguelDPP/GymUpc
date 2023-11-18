package com.miguecode.gymupc.utils;

import java.util.Date;

public class Dates {
    public static String getMes(int month) {
        switch (month) {
            case 0:
                return "Ene";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Abr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Ago";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dic";
            default:
                return "Mes";
        }
    }

    public static String getMesLong (int month) {
        switch (month) {
            case 0:
                return "Enero";
            case 1:
                return "Febrero";
            case 2:
                return "Marzo";
            case 3:
                return "Abril";
            case 4:
                return "Mayo";
            case 5:
                return "Junio";
            case 6:
                return "Julio";
            case 7:
                return "Agosto";
            case 8:
                return "Septiembre";
            case 9:
                return "Octubre";
            case 10:
                return "Noviembre";
            case 11:
                return "Diciembre";
            default:
                return "Mes";
        }
    }

    public static Date convertTODate(String date) {
        String[] dateSplit = date.split("/");
        return new Date(Integer.parseInt(dateSplit[2])-1900, Integer.parseInt(dateSplit[1])-1, Integer.parseInt(dateSplit[0]));
    }

    public static boolean isMayorQue(Date fecha1, Date fecha2) {
        if (fecha1.getYear() > fecha2.getYear()) {
            return true;
        } else if (fecha1.getYear() == fecha2.getYear()) {
            if (fecha1.getMonth() > fecha2.getMonth()) {
                return true;
            } else if (fecha1.getMonth() == fecha2.getMonth()) {
                if (fecha1.getDate() >= fecha2.getDate()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String toDateString(Date date) {
        return date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
    }

    public static String toShortDateString(Date date) {
        return (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
    }

    public static String toDateStringLong(Date date) {
        return date.getDate() + " de " + getMesLong(date.getMonth()) + " de " + (date.getYear() + 1900);
    }
}
