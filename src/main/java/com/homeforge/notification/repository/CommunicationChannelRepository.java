package com.homeforge.notification.repository;

import com.homeforge.notification.domain.CommunicationChannel;
import com.homeforge.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommunicationChannelRepository extends JpaRepository<CommunicationChannel, UUID> {
    List<CommunicationChannel> findByCompanyIdAndDeletedAtIsNull(UUID companyId);

    Optional<CommunicationChannel> findByCompanyIdAndChannelTypeAndDeletedAtIsNull(UUID companyId, NotificationType channelType);

    List<CommunicationChannel> findByCompanyIdAndActiveAndDeletedAtIsNull(UUID companyId, Boolean active);
}
