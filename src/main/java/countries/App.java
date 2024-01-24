package countries;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class contains functionality to fetch and process country data from the Restcountries API and print the following:
 * - A list of the countries and their population densities sorted in descending order (visually formatted into a table).
 * - The country in Asia containing the most bordering countries of a different region.
 */
public class App
{

    // Fetch the json of all countries via the restcountries API and parse it into a list of Country objects
    public static List<Country> getCountries() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest getCountriesRequest = HttpRequest.newBuilder()
            .uri(new URI("https://restcountries.com/v3.1/all"))
            .build();

        HttpResponse<String> getCountriesResponse = httpClient.send(getCountriesRequest, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(getCountriesResponse.body(), new TypeReference<List<Country>>() {});
    }

    // Sort countrires by population density in descending order
    public static List<Country> sortByPopulationDensity(List<Country> countries) {
        Comparator<Country> densityComparator = Comparator.comparingDouble(Country::getPopulationDensity).reversed();
        Collections.sort(countries, densityComparator);

        return countries;
    }

    // Print a formatted table of countries and their population densities
    public static void printPopulationDensityTable(List<Country> countries) {
        System.out.println("------------------------------------------------------------------------");
        System.out.printf("| %-45s | %-20s |%n", "Country", "Population density");
        System.out.println("------------------------------------------------------------------------");

        for (Country country : countries) {
            System.out.printf(
                "| %-45s | %-20.2f |%n",
                country.getCommonName(),
                country.getPopulationDensity()
            );
        }

        System.out.println("------------------------------------------------------------------------");
    }

    // Print the asian country with the most bordering countries in a different region
    public static void printAsianCountryWithMaxNonAsianBorders(List<Country> countries) {
        // Make a map with country code => region for easy look-ups later
        Map<String, String> countryCodeToRegion = countries.stream()
            .collect(Collectors.toMap(Country::getCca3, Country::getRegion));

        List<Country> asianCountries = countries.stream()
            .filter(country -> "Asia".equals(country.getRegion()))
            .collect(Collectors.toList());

        int maxNonAsianBorders = 0;
        List<String> countryNames = new ArrayList<>(); // there could potentially be a tie between countries

        for (Country country : asianCountries) {
            if (country.getBorders() != null) {
                int numNonAsianBorders = (int) country.getBorders().stream()
                    .filter(border -> !country.getRegion().equals(countryCodeToRegion.get(border)))
                    .count();

                if (numNonAsianBorders > maxNonAsianBorders) {
                    maxNonAsianBorders = numNonAsianBorders;
                    countryNames.clear();
                    countryNames.add(country.getCommonName());
                }
                else if (numNonAsianBorders == maxNonAsianBorders) {
                    countryNames.add(country.getCommonName());
                }
            }
        }

        System.out.printf(
            "%nAsian country/Countries with the most (%d) countries in a different region: %s%n%n",
            maxNonAsianBorders,
            String.join(", ", countryNames)
        );
    }

    public static void main(String[] args) throws Exception {
        try {
            List<Country> countries = getCountries();
            List<Country> sortedCountries = sortByPopulationDensity(countries);
            printPopulationDensityTable(sortedCountries);
            printAsianCountryWithMaxNonAsianBorders(countries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
