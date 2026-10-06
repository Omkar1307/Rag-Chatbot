package com.om.rag_chatbot.repository;

import com.om.rag_chatbot.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndex(Long documentId);
}