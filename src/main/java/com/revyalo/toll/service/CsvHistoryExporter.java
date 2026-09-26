package com.revyalo.toll.service;

import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.exception.PersistenceException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CsvHistoryExporter implements HistoryExporter {
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private final Path exportDirectory;
    private final Clock clock;

    public CsvHistoryExporter(Path exportDirectory, Clock clock) {
        this.exportDirectory = exportDirectory;
        this.clock = clock;
    }

    @Override
    public Path export(String licensePlate, List<TollTicket> tickets, List<TrafficFine> fines) {
        try {
            Files.createDirectories(exportDirectory);
            String timestamp = LocalDateTime.now(clock).format(FILE_TIME);
            Path output = exportDirectory.resolve(licensePlate + "-" + timestamp + ".csv");
            try (BufferedWriter writer = Files.newBufferedWriter(
                output,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW
            )) {
                writer.write("record_type,id,license_plate,date,detail,amount_eur,status");
                writer.newLine();
                for (TollTicket ticket : tickets) {
                    writeRow(writer, List.of(
                        "TICKET",
                        Long.toString(ticket.id()),
                        ticket.licensePlate(),
                        ticket.exitedAt().toString(),
                        "vehicle_size=" + ticket.vehicleSize(),
                        ticket.amount().toPlainString(),
                        "PAID"
                    ));
                }
                for (TrafficFine fine : fines) {
                    writeRow(writer, List.of(
                        "FINE",
                        Long.toString(fine.id()),
                        fine.licensePlate(),
                        fine.detectedAt().toString(),
                        fine.radarType() + " speed_kph=" + String.format(java.util.Locale.ROOT, "%.2f", fine.speedKph()),
                        fine.amount().toPlainString(),
                        fine.paid() ? "PAID" : "PENDING"
                    ));
                }
            }
            return output;
        } catch (IOException exception) {
            throw new PersistenceException("No se pudo exportar el historial", exception);
        }
    }

    private void writeRow(BufferedWriter writer, List<String> fields) throws IOException {
        for (int index = 0; index < fields.size(); index++) {
            if (index > 0) {
                writer.write(',');
            }
            writer.write(escape(fields.get(index)));
        }
        writer.newLine();
    }

    private String escape(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
