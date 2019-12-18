package com.example.countrieslist;

import java.util.Comparator;

class SortByArea implements Comparator<Country> {
    @Override
    public int compare(Country o1, Country o2) {
        return Double.compare(o1.area,o2.area);
    }
}
