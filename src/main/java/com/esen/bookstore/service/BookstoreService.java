package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.repository.BookstoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookstoreService {

    private final BookstoreRepository bookstoreRepository;

    public void save(Bookstore bookstore) {
        bookstoreRepository.save(bookstore);
    }

    /**
     * Eltávolít egy könyvet a bolt adatbázisából
     * @param book az eltávolítandó könyv
     */
    public void removeBookFromInventories(Book book) {
        bookstoreRepository.findAll()
                .forEach(bookstore -> {
                    bookstore.getInventory().remove(book);
                    bookstoreRepository.save(bookstore);
                });
    }

    public List<Bookstore> findAll() {
        return bookstoreRepository.findAll();
    }

    /**
     * Eltávolítja az adatbázisból az egész boltot
     * @param id a bolt id-ja
     */
    public void delete(Long id) {
        if (!bookstoreRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot find bookstore with id " + id);
        }

        var bookstore = bookstoreRepository.findById(id).get();
        bookstoreRepository.delete(bookstore);
    }

    /**
     * Frissíti egy meglévő bolt adatait
     * @param id a bolt id-ja ami alapján megtaláljuk
     * @param location a város ahol a bolt van
     * @param priceModifier az árat változtató változó
     * @param moneyInCashRegister készpénz a kasszában
     * @return a boltot a frissített adatokkal
     */
    public Bookstore update(Long id,String location,Double priceModifier,Double moneyInCashRegister){
        if (Stream.of(id, location, priceModifier, moneyInCashRegister).allMatch(Objects::isNull)) {
            throw new IllegalArgumentException("At least one input is required");
        }

        if (!bookstoreRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot find bookstore with id " + id);
        }

        var bookstore = bookstoreRepository.findById(id).get();

        if (location != null) { bookstore.setLocation(location); }
        if (priceModifier != null) { bookstore.setPriceModifier(priceModifier); }
        if (moneyInCashRegister != null) { bookstore.setMoneyInCashRegister(moneyInCashRegister); }

        return bookstoreRepository.save(bookstore);
    }
}
