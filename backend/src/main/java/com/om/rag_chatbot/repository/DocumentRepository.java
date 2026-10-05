package com.om.rag_chatbot.repository;

import com.om.rag_chatbot.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}