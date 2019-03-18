package com.example.kirmi.ks1807;

class ReturnedDiaryEntry {
    private String entryOne, entryTwo, entryThree, entryFour;
    private int day, month, year;
    ReturnedDiaryEntry(String returnDiaryText){
        String[] splitRow = returnDiaryText.split("@@@");
        this.day = Integer.parseInt(splitRow[0].substring(8,10));
        this.month = Integer.parseInt(splitRow[0].substring(5,7));
        this.year = Integer.parseInt(splitRow[0].substring(0,4));
        this.entryOne = splitRow[1];
        this.entryTwo = splitRow[2];
        this.entryThree = splitRow[3];
        this.entryFour = splitRow[4];
    }

    int getMonth() {
        return month;
    }

    int getDay() {
        return day;
    }

    int getYear() { return year; }

    String getEntryOne() {
        return entryOne;
    }

    String getEntryTwo() {
        return entryTwo;
    }

    String getEntryThree() {
        return entryThree;
    }

    String getEntryFour() {
        return entryFour;
    }
}
