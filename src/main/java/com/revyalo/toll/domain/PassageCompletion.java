package com.revyalo.toll.domain;

import java.util.Optional;

public record PassageCompletion(TollTicket ticket, Optional<TrafficFine> sectionFine) {
}
