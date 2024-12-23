package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Bot extends TelegramLongPollingBot {

    private final Database database = new Database();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            SendMessage message = new SendMessage();
            message.setChatId(chatId);

            try {
                switch (getCommand(messageText)) {
                    case "/start":
                        if (database.isDatabaseEmpty()) {
                            WebScraper scraper = new WebScraper();
                            scraper.fetchAndStoreMatchResults(database);
                            message.setText("Database popolato con i dati più recenti da SofaScore! Benvenuto! Usa i seguenti comandi:\n"
                                    + "/oggi - Visualizza i risultati delle partite di oggi\n"
                                    + "/calendario [data] - Visualizza le partite programmate\n"
                                    + "/squadra [nome squadra] - Prossime partite di una squadra\n"
                                    + "/aiuto - Mostra i comandi disponibili.");
                        } else {
                            message.setText("Benvenuto! \n Usa /aiuto per vedere i comandi disponibili.");
                        }
                        break;

                    case "/oggi":
                        if (database.isDatabaseEmpty()) {
                            WebScraper scraper = new WebScraper();
                            scraper.fetchAndStoreMatchResults(database);
                            message.setText("Database popolato con i dati più recenti!\nOra puoi usare /oggi.");
                        } else {
                            // Ottieni la data odierna
                            LocalDate today = LocalDate.now();
                            // Formatta la data odierna nel formato che usi nel database, es. "yyyy-MM-dd"
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String todayString = today.format(formatter);

                            // Ottieni le partite per oggi
                            String todayResults = database.getMatchesByDate(todayString);
                            message.setText(todayResults.isEmpty() ? "Nessuna partita oggi." : todayResults);
                        }
                        break;


                    case "/calendario":
                        String date = extractArgument(messageText);
                        if (date.isEmpty()) {
                            message.setText("Per favore, specifica una data nel formato AAAA-MM-GG.");
                        } else {
                            String schedule = database.getMatchesByDate(date);
                            message.setText(schedule.isEmpty() ? "Nessuna partita programmata per questa data." : schedule);
                        }
                        break;

                    case "/squadra":
                        String teamName = extractArgument(messageText);
                        if (teamName.isEmpty()) {
                            message.setText("Per favore, specifica il nome della squadra.");
                        } else {
                            String teamInfo = database.getTeamInfo(teamName);
                            message.setText(teamInfo.isEmpty() ? "Nessuna informazione trovata per questa squadra." : teamInfo);
                        }
                        break;

                    case "/aiuto":
                        message.setText("Elenco dei comandi disponibili:\n"
                                + "/oggi - Visualizza i risultati delle partite di oggi\n"
                                + "/calendario [data] - Visualizza le partite programmate\n"
                                + "/squadra [nome squadra] - Prossime partite di una squadra\n"
                                + "/aiuto - Mostra i comandi disponibili.");
                        break;

                    default:
                        message.setText("Comando non riconosciuto. Usa /aiuto per vedere l'elenco dei comandi.");
                }
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCommand(String messageText) {
        return messageText.split(" ")[0];
    }

    private String extractArgument(String messageText) {
        String[] parts = messageText.split(" ", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    @Override
    public String getBotUsername() {
        return "CalcioONLINE_bot";
    }

    @Override
    public String getBotToken() {
        return "7764716283:AAEKr-_5ZYjfTYzgSCdCK7vtd91Ld4b27Z8";
    }
}
