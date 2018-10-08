package dk.topping.handin2.util;


import java.util.HashMap;

public class HardcodedCities {

    private static HashMap<String, Integer> danishCities;

    // Shared this nice list of cities with Anders Knudsen, because copy-pasting all these cities/id's sucks.
    // http://bulk.openweathermap.org/sample/current.city.list.json.gz
    // Cities are ordered based on how cool I think they are.
    public static HashMap<String, Integer> getMajorDanishCities() {
        if(danishCities == null) {
            danishCities = new HashMap<String, Integer>() {{
                put("Randers",   2615006);
                put("Aalborg",   2624886);
                put("Aarhus",    2624652);
                put("Horsens",   2619771);
                put("Viborg",    2610319);
                put("Esbjerg",   2622447);
                put("Vejle",     2610613);
                put("Odense",    2615876);
                put("Kolding",   2618528);
                put("Roskilde",  2614481);
                put("København", 2618425);
            }};
        }

        return danishCities;
    }
}
