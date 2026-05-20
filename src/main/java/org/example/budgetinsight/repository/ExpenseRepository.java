package org.example.budgetinsight.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.budgetinsight.model.Expense;
import org.example.budgetinsight.util.HibernateUtil;

import java.util.List;

public class ExpenseRepository {

    public boolean isEmpty() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            Long count = em.createQuery("SELECT COUNT(e) FROM Expense e", Long.class).getSingleResult();
            return count == 0;
        }
    }

    public List<Expense> findAll() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery("FROM Expense e ORDER BY e.date DESC", Expense.class).getResultList();
        }
    }

    public Expense save(Expense expense) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(expense);
            tx.commit();
            return expense;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void saveAll(List<Expense> expenses) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Expense e : expenses) em.persist(e);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Expense update(Expense expense) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Expense merged = em.merge(expense);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Expense e = em.find(Expense.class, id);
            if (e != null) em.remove(e);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}