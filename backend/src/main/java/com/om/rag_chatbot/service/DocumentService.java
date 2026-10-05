package com.om.rag_chatbot.service;

import com.om.rag_chatbot.entity.Document;
import com.om.rag_chatbot.entity.DocumentStatus;
import com.om.rag_chatbot.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PdfTextExtractorService pdfTextExtractorService;

    private final Path uploadDirectory =
            Paths.get("uploads");

    public DocumentService(
            DocumentRepository documentRepository,
            PdfTextExtractorService pdfTextExtractorService) {

        this.documentRepository = documentRepository;
        this.pdfTextExtractorService = pdfTextExtractorService;
    }


    public Document uploadDocument(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        Files.createDirectories(uploadDirectory);

        String storedFileName = UUID.randomUUID() + ".pdf";

        Path filePath = uploadDirectory.resolve(storedFileName);

        Files.copy(file.getInputStream(), filePath);

        Document document = new Document();

        document.setFileName(storedFileName);
        document.setOriginalFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStoragePath(filePath.toString());
        document.setStatus(DocumentStatus.PROCESSING);

        Document savedDocument = documentRepository.save(document);

        // Extract text from PDF
        String extractedText =
                pdfTextExtractorService.extractText(filePath);

        System.out.println("========== EXTRACTED TEXT ==========");
        System.out.println(extractedText);
        System.out.println("====================================");

        savedDocument.setStatus(DocumentStatus.PROCESSED);

        return documentRepository.save(savedDocument);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found with id: " + id));
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}