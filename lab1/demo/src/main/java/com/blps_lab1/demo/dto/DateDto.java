package com.blps_lab1.demo.dto;

import java.time.LocalDate;

public class DateDto {
    public static Builder builder() {
        return new Builder();
    }

    public DateDto(LocalDate arrival, LocalDate departure) {
        this.arrival = arrival;
        this.departure = departure;
    }

    private LocalDate arrival;
    private LocalDate departure;

    public LocalDate getArrival() {
        return arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDate departure) {
        this.departure = departure;
    }

    public static class Builder {
        private LocalDate arrival;
        private LocalDate departure;

        public Builder arrival(LocalDate arrival) {
            this.arrival = arrival;
            return this;
        }

        public Builder departure(LocalDate departure) {
            this.departure = departure;
            return this;
        }

        public DateDto build() {
            return new DateDto(arrival, departure);
        }
    }
}
