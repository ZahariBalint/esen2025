package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookstoreService bookstoreService;

    public void save(Book book) {
        bookRepository.save(book);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * Egy könyv törlése az adatbázisból
     * @param id a törlendő könyv id attributuma
     */
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot find book with id " + id);
        }

        var book = bookRepository.findById(id).get();
        bookstoreService.removeBookFromInventories(book);
        bookRepository.delete(book);
    }

    /**
     * Egy könyv frissítése az adatbázisban
     * @param id a könyv id-ja ami alapján megtaláljuk
     * @param title a könyv címe
     * @param author a könyv szerzője
     * @param publisher a könyv kiadója
     * @param price a könyv ára
     * @return könyv példány a módosított attributum értékekkel
     */
    public Book update(Long id, String title, String author, String publisher, Double price) {
        if (Stream.of(title, author, publisher, price).allMatch(Objects::isNull)) {
            throw new IllegalArgumentException("At least one input is required");
        }

        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot find book with id " + id);
        }

        var book = bookRepository.findById(id).get();

        if (title != null) { book.setTitle(title); }
        if (author != null) { book.setAuthor(author); }
        if (publisher != null) { book.setPublisher(publisher); }
        if (price != null) { book.setPrice(price); }

        return bookRepository.save(book);
    }

    /**
     * Egy metódus egy bizonyos könyv árainak kilistázására a különböző boltokban
     * @param id a könyv id-ja ami alapján megtaláljuk
     * @return egy (@code Map<String,Double>) objektumot a boltok nevével és a könyv árával az egyes boltokban
     */
    public Map<String,Double> findPrices(Long id){
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot find book with id " + id);
        }

        var book = bookRepository.findById(id).get();

        List<Bookstore> bookstores = bookstoreService.findAll();
        Map<String,Double> prices = new HashMap<>();
        for(Bookstore bookstore : bookstores){
            prices.put(bookstore.getLocation(),bookstore.getPriceModifier()*book.getPrice());
        }
        return prices;
    }

    /**
     * Egy kereső metódus, amivel cím,kiadó vagy író alapján kereshetjük a könyvet
     * @param title a könyv címe
     * @param publisher a könyv kiadója
     * @param author a könyv írója
     * @return egy (@code List) objektumot a potenciális könyvekkel
     */
    public List<Book> findByTitleOrPublisherOrAuthor(String title,String publisher,String author){
        return bookRepository.findAll()
                .stream()
                .filter(book -> {if(title != null){
                                    return book.getTitle().equals(title);}
                                 if(publisher != null){
                                     return book.getPublisher().equals(publisher);
                                 }
                                 if(author != null){
                                     return book.getAuthor().equals(author);
                                 }
                                 return false;
                                }).toList();
    }
}
