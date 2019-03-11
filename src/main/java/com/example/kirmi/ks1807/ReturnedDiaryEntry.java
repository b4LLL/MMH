package com.example.kirmi.ks1807;

public class ReturnedDiaryEntry {
    private String date, entryOne, entryTwo, entryThree, entryFour;
    String[] splitRow;

    public ReturnedDiaryEntry(String returnDiaryText){
        splitRow = returnDiaryText.split("@@@");
        this.date = dateFilter(splitRow[0]);
        this.entryOne = splitRow[1];
        this.entryTwo = splitRow[2];
        this.entryThree = splitRow[3];
        this.entryFour = splitRow[4];
    }

    public ReturnedDiaryEntry(String date, String entryOne, String entryTwo, String entryThree, String entryFour){
        this.date = this.dateFilter(date); //this needs to be the day of the month.
        this.entryOne = entryOne;
        this.entryTwo = entryTwo;
        this.entryThree = entryThree;
        this.entryFour = entryFour;
    }

    private String dateFilter(String date){
        return date.substring(6,7);
    }

    public String getDate() {
        return date;
    }

    public String getEntryOne() {
        return entryOne;
    }

    public String getEntryTwo() {
        return entryTwo;
    }

    public String getEntryThree() {
        return entryThree;
    }

    public String getEntryFour() {
        return entryFour;
    }
}
