package countries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Represents a country with its relevant information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {

    private Map<String, Object> name; // various properties related to the country name
    private String cca3; // three letter country code
    private long population;
    private double area;
    private String region;
    private List<String> borders; // bordering countries in cca3 country codes

    public Map<String, Object> getName() {
        return this.name;
    }

    public String getCca3() {
        return this.cca3;
    }

    public long getPopulation() {
        return this.population;
    }

    public double getArea() {
        return this.area;
    }

    public String getRegion() {
        return this.region;
    }

    public List<String> getBorders() {
        return this.borders;
    }

    public String getCommonName() {
        Map<String, Object> nameMap = this.name;
        if (nameMap != null) {
            Object commonName = nameMap.get("common");
            if (commonName != null) {
                return commonName.toString();
            }
        }
        return "No common name specified";
    }

    public double getPopulationDensity() {
        if (this.area == 0) {
            return 0;
        }
        return this.population / this.area;
    }
}