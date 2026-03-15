package dto;

import java.time.LocalDate;

public class CreateReservationRequest {
    private LocalDate arrival;
    private LocalDate departure;
    private Integer guestsAmount;
    private Boolean agreedToReservation;
    private Integer petsAmount;

    public Integer getPetsAmount() {
        return petsAmount;
    }

    public void setPetsAmount(Integer petsAmount) {
        this.petsAmount = petsAmount;
    }

    public CreateReservationRequest() {
    }

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

    public Integer getGuestsAmount() {
        return guestsAmount;
    }

    public void setGuestsAmount(Integer guestsAmount) {
        this.guestsAmount = guestsAmount;
    }

    public Boolean getAgreedToReservation() {
        return agreedToReservation;
    }

    public void setAgreedToReservation(Boolean agreedToReservation) {
        this.agreedToReservation = agreedToReservation;
    }
}