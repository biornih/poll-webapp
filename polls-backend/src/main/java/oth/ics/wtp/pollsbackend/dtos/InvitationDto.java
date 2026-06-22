package oth.ics.wtp.pollsbackend.dtos;

public record InvitationDto(
        long id,
        PollDto poll,
        AppUserDto invitee,
        boolean hasAnswered) {
}