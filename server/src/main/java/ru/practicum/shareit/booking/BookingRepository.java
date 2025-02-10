package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 order by b.start DESC ")
    List<Booking> findAllByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and CURRENT TIMESTAMP " +
            "between b.start and b.end order by b.start DESC ")
    List<Booking> findCurrentByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current timestamp " +
            "order by b.start DESC ")
    List<Booking> findPastByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current timestamp " +
            "order by b.start DESC ")
    List<Booking> findFutureByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'WAITING' " +
            "order by b.start DESC")
    List<Booking> findWaitingByBookerId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start DESC")
    List<Booking> findRejectedByBookerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start DESC")
    List<Booking> findAllByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and CURRENT TIMESTAMP " +
            "between b.start and b.end order by b.start DESC ")
    List<Booking> findCurrentByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current timestamp " +
            "order by b.start DESC ")
    List<Booking> findPastByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current timestamp " +
            "order by b.start DESC ")
    List<Booking> findFutureByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'WAITING' " +
            "order by b.start DESC ")
    List<Booking> findWaitingByOwnerId(long id);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start DESC ")
    List<Booking> findRejectedByOwnerId(long id);

    @Query("select b from Booking b where b.item.id = ?1 and b.end < current timestamp and b.status = 'APPROVED' " +
            "order by b.end DESC limit 1")
    Booking findLastBookingByItemId(long id);

    @Query("select b from Booking b where b.item.id = ?1 and b.start > current timestamp and b.status = 'APPROVED' " +
            "order by b.start ASC limit 1")
    Booking findNextBookingByItemId(long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2 and b.end < current timestamp " +
            "and b.status = 'APPROVED' ")
    List<Booking> findFinishedByUserAndItem(long userId, long itemId);
}


