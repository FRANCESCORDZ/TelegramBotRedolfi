package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScraper {

    public void fetchAndStoreMatchResults(Database database) {
        try {
            Connection con = Jsoup.connect("https://www.legaseriea.it/it");
            Document doc = con.get();

            for (Element l : doc.select("div[class=hm-teams-game d-flex align-items-center justify-content-center]")) {
                System.out.println(l.selectFirst("p[class=p2 semibold black]").text());
            }

            // Seleziona tutte le partite
            Elements matches = doc.select(".event-wrapper");

            for (Element match : matches) {
                // Estrazione delle informazioni
                String team1 = match.select(".team-home .name").text();
                String team2 = match.select(".team-away .name").text();
                String result = match.select(".score").text();
                String date = match.select(".date").text();

                // Salva la partita nel database
                database.saveMatch(team1, team2, result, date, "SofaScore");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore durante lo scraping dal sito SofaScore.");
        }
    }
}
