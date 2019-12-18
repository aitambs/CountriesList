package com.example.countrieslist;

import java.text.Collator;
import java.util.Comparator;

class SortByName implements Comparator<Country> {
    private Collator collator=Collator.getInstance();
    @Override
    public int compare(Country o1, Country o2) {
        return collator.compare(o1.name,o2.name); //Compare ignoring Accents for easier Lookup
    }
}
