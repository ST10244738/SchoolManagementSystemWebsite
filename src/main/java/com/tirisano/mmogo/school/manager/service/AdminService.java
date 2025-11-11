package com.tirisano.mmogo.school.manager.service;

import com.tirisano.mmogo.school.manager.enums.RequestStatus;
import com.tirisano.mmogo.school.manager.model.Announcement;
import com.tirisano.mmogo.school.manager.model.DocumentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final FirebaseService firebaseService;
    private final StudentService studentService;

    public List<Announcement> getAllAnnouncements() {
        return firebaseService.findAll("announcements", Announcement.class).join();
    }

    public Announcement createAnnouncement(Announcement announcement) {
        firebaseService.save("announcements", announcement).join();
        // FirebaseService automatically sets announcementId on the announcement object
        return announcement;
    }

    public Announcement getAnnouncementById(String announcementId) {
        return firebaseService.findById("announcements", announcementId, Announcement.class).join();
    }

    public Announcement updateAnnouncement(String announcementId, Announcement announcement) {
        Announcement existing = firebaseService.findById("announcements", announcementId, Announcement.class).join();
        if (existing != null) {
            announcement.setAnnouncementId(announcementId);
            if (announcement.getCreatedAt() == null) {
                announcement.setCreatedAt(existing.getCreatedAt());
            }
            firebaseService.save("announcements", announcement, announcementId).join();
            return announcement;
        }
        throw new RuntimeException("Announcement not found with ID: " + announcementId);
    }

    public void deleteAnnouncement(String announcementId) {
        Announcement existing = firebaseService.findById("announcements", announcementId, Announcement.class).join();
        if (existing != null) {
            firebaseService.delete("announcements", announcementId).join();
        } else {
            throw new RuntimeException("Announcement not found with ID: " + announcementId);
        }
    }

    public List<DocumentRequest> getAllDocumentRequests() {
        return firebaseService.findAll("documentRequests", DocumentRequest.class).join();
    }

    public List<DocumentRequest> getPendingDocumentRequests() {
        return firebaseService.findByField("documentRequests", "status", RequestStatus.PENDING, DocumentRequest.class).join();
    }

    public DocumentRequest approveDocumentRequest(String requestId) {
        DocumentRequest request = firebaseService.findById("documentRequests", requestId, DocumentRequest.class).join();
        if (request != null) {
            request.setStatus(RequestStatus.APPROVED);
            firebaseService.save("documentRequests", request, requestId);
        }
        return request;
    }
}
