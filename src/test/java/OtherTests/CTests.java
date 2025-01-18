package OtherTests;

import ClassTests.Utils;
import db.*;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.util.*;

public class CTests {
    @BeforeAll
    public static void init() {
        db.Init.setDB("LibraryManagementTestUnit");
        db.Init.getEntityManager();
    }

    @AfterEach
    public void reload() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage("db")
                        .addScanners(new SubTypesScanner(false)));

        reflections.getSubTypesOf(Object.class).stream()
                .filter(_class -> _class.getName().contains("db.")) //search for subclasses of package db
                .filter(_class -> _class.isAnnotationPresent(Entity.class))
                .forEach(_class -> {
                    try {
                        EntityManager em = db.Init.getEntityManager();
                        em.getTransaction().begin();
                        em.createQuery("DELETE FROM " + _class.getSimpleName()).executeUpdate();
                        em.getTransaction().commit();
                    } catch (Exception _){}
                });

        reflections.getSubTypesOf(Object.class).stream()
                .filter(_class -> _class.getName().contains("db.")) //search for subclasses of package db
                .filter(_class -> _class.isAnnotationPresent(Entity.class))
                .forEach(_class -> {
                    try {
                        EntityManager em = db.Init.getEntityManager();
                        em.getTransaction().begin();
                        em.createQuery("DELETE FROM " + _class.getSimpleName()).executeUpdate();
                        em.getTransaction().commit();
                    } catch (Exception _){}
                });

        reflections.getSubTypesOf(Object.class).stream()
                .filter(_class -> _class.getName().contains("db.")) //search for subclasses of package db
                .filter(_class -> _class.isAnnotationPresent(Entity.class))
                .forEach(_class -> {
                    try {
                        EntityManager em = db.Init.getEntityManager();
                        em.getTransaction().begin();
                        em.createQuery("DELETE FROM " + _class.getSimpleName()).executeUpdate();
                        em.getTransaction().commit();
                    } catch (Exception _){}
                });
    }

    @Test
    public void BorrowingUser() {
        User u = ClassTests.Utils.getUser();
        User u1 = new User("ASD", "kjdfgjkdfg@lfg.com", "+387458735346", "ASD");
        Publisher p = new Publisher("hello", "its", "+8475839405");
        Book b = ClassTests.Utils.getBook(p);
        ArrayList<Borrowing> bor = new ArrayList<>();
        int size = 10;
        HashMap<Borrowing,Integer> arr = new HashMap<>();
        HashMap<Borrowing,Integer> arra = new HashMap<>();
        for (int i = 0; i < size; i++) {
            if (i%2==0) {
                bor.add(new Borrowing(u, new Copy(b, i, "borrowed"), new Date()));
                arr.put(bor.getLast(), u.getId());
            } else {
                bor.add(new Borrowing(u1, new Copy(b, i, "free"), new Date()));
                arr.put(bor.getLast(), u1.getId());
            }
        }

        Optional<Field> user =
        Arrays.stream(Borrowing.class.getDeclaredFields())
                .filter(f -> f.getType() == User.class)
                .findFirst();

        Assertions.assertEquals(db.Init.getEntityManager().createQuery("SELECT b from Borrowing b", Borrowing.class).getResultList(), bor.stream().toList());

        if (user.isPresent()) {
            user.get().setAccessible(true);
            for (Borrowing key : arr.keySet()) {
                Borrowing bi = db.Init.getEntityManager().createQuery("SELECT uid from Borrowing uid WHERE uid.id = :i", Borrowing.class)
                        .setParameter("i", key.getId()).getSingleResult();
                try {
                    arra.put(bi, ((User) user.get().get(bi)).getId());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            Assertions.assertEquals(arr, arra);
        } else {
            throw new RuntimeException("No user field");
        }
    }

    @Test
    public void BookCopy() {
        Publisher p = new Publisher("hello", "its", "+3466436436");
        Book u = ClassTests.Utils.getBook(p);
        Book u1 = new Book("DSA","dfsdf",p,1243,"3425212345");

        ArrayList<Copy> bor = new ArrayList<>();
        int size = 10;
        HashMap<Copy,Integer> arr = new HashMap<>();
        HashMap<Copy,Integer> arra = new HashMap<>();
        for (int i = 0; i < size; i++) {
            if (i%2==0) {
                bor.add(new Copy(u, i, "borrowed idk"));
                arr.put(bor.getLast(), u.getId());
            } else {
                bor.add(new Copy(u1, i, "borrowed idk"));
                arr.put(bor.getLast(), u1.getId());
            }
        }

        Optional<Field> user =
                Arrays.stream(Copy.class.getDeclaredFields())
                        .filter(f -> f.getType() == Book.class)
                        .findFirst();

        Assertions.assertEquals(db.Init.getEntityManager().createQuery("SELECT b from Copy b", Copy.class).getResultList(), bor.stream().toList());

        if (user.isPresent()) {
            user.get().setAccessible(true);
            for (Copy key : arr.keySet()) {
                Copy bi = db.Init.getEntityManager().createQuery("SELECT uid from Copy uid WHERE uid.id = :i", Copy.class)
                        .setParameter("i", key.getId()).getSingleResult();
                try {
                    arra.put(bi, ((Book) user.get().get(bi)).getId());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            Assertions.assertEquals(arr, arra);
        } else {
            throw new RuntimeException("No book field");
        }
    }

    @Test
    public void UserLibrarian() {
        User u = Utils.getUser();
        Librarian l = new Librarian(u,new Date(),"std::vector<std::vector<std::string>>");

        List<Librarian> check = Init.getEntityManager().createQuery("SELECT l from Librarian l", Librarian.class).getResultList();
        List<User> checkU = Init.getEntityManager().createQuery("SELECT l.user from Librarian l", User.class).getResultList();

        Assertions.assertEquals(1,check.size());
        Assertions.assertEquals(1,checkU.size());

        Assertions.assertEquals(check.getFirst(),l);
        Assertions.assertEquals(checkU.getFirst(),u);
    }

    @Test
    public void BookPublisher() {
        Publisher p = new Publisher("hello", "its", "+3466436436");
        Publisher p1 = new Publisher("lol","dsfgsdfg","+47538459392");

        ArrayList<Book> bor = new ArrayList<>();
        int size = 10;
        long isbn = 1234567892;
        HashMap<Book,Integer> arr = new HashMap<>();
        HashMap<Book,Integer> arra = new HashMap<>();
        for (int i = 0; i < size; i++) {
            if (i%2==0) {
                Book b = new Book("some","person",p,2007,String.valueOf(isbn++));
                bor.add(b);
                arr.put(bor.getLast(), p.getId());
            } else {
                Book b = new Book("some","person",p1,2007,String.valueOf(isbn++));
                bor.add(b);
                arr.put(bor.getLast(), p1.getId());
            }
        }

        Optional<Field> user =
                Arrays.stream(Book.class.getDeclaredFields())
                        .filter(f -> f.getType() == Publisher.class)
                        .findFirst();

        Assertions.assertEquals(db.Init.getEntityManager().createQuery("SELECT b from Book b", Book.class).getResultList(), bor.stream().toList());

        if (user.isPresent()) {
            user.get().setAccessible(true);
            for (Book key : arr.keySet()) {
                Book bi = db.Init.getEntityManager().createQuery("SELECT uid from Book uid WHERE uid.id = :i", Book.class)
                        .setParameter("i", key.getId()).getSingleResult();
                try {
                    arra.put(bi, ((Publisher) user.get().get(bi)).getId());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            Assertions.assertEquals(arr, arra);
        } else {
            throw new RuntimeException("No publisher field");
        }
    }
}
