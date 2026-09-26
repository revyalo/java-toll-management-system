package com.revyalo.toll.service;

import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import java.nio.file.Path;
import java.util.List;

public interface HistoryExporter {
    Path export(String licensePlate, List<TollTicket> tickets, List<TrafficFine> fines);
}
