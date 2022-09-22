import com.thebluealliance.api.v3.TBA;
import com.thebluealliance.api.v3.models.Award;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;

public class Main {
    private static final String RESOURCE_PATH = "C:\\Users\\Ethan\\IdeaProjects\\APStatsData\\src\\main\\resources\\response_1663806805998.json";
    private static ArrayList<Double> winrate = new ArrayList<>();
    private static ArrayList<Integer> activeTeams = new ArrayList<>();

    private static ArrayList<Integer> yearsParticipated = new ArrayList<>();
    private static ArrayList<Integer> numAwards = new ArrayList<>();

    private static TBA tba = new TBA("EEHteiQi6dPrG6jsSCSyQ9q2zVN9hKfE829geuoasrIKjHOtC9P2C4BkwnfMBayX");

    public static void main(String[] args) throws Exception {
        setStatboticsData();
        setBannersAndYears();
        printToCSV();
    }

    private static void printToCSV() throws Exception {
        CSVPrinter printer = new CSVPrinter(new FileWriter("src/Data/ApStatsData.csv"), CSVFormat.DEFAULT);
        printer.printRecord("Teams", "Winrate", "Years Participated", "Blue Banners");
        for (int i = 0; i < activeTeams.size(); i++) {
            printer.printRecord(activeTeams.get(i), winrate.get(i), yearsParticipated.get(i), numAwards.get(i));
        }
    }

    private static void setBannersAndYears() throws Exception{
        for (int i = 0; i < activeTeams.size(); i++) {
            int years = tba.teamRequest.getYearsParticipated(activeTeams.get(i)).length;
            yearsParticipated.add(years);
            Award[] awards = tba.teamRequest.getAwards(activeTeams.get(i), 2022);
            int numBanners = 0;
            for (int j = 0; j < awards.length; j++) {
                numBanners = awards[j].getAward_type() == 1 ||
                        awards[j].getAward_type() == 0 ?
                        numBanners + 1 :
                        numBanners;
            }
            numAwards.add(numBanners);
        }
    }

    private static void setStatboticsData() throws Exception{
        File f = new File(RESOURCE_PATH);
        if (f.exists()) {
            InputStream is = new FileInputStream(RESOURCE_PATH);
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject json = new JSONObject(jsonTxt);
            JSONArray arr = json.getJSONArray("data");
            for (int i = 0; i < arr.length(); i++) {
                winrate.add(arr.getJSONObject(i).getDouble("winrate"));
                activeTeams.add(arr.getJSONObject(i).getInt("team"));
            }
        }
    }

}
