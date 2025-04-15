package com.esen.bookstore.repository;


import com.esen.bookstore.model.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookStoreRepository extends JpaRepository<Bookstore,Long> {

}
