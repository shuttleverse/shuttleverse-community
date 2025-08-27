package com.shuttleverse.community.dto;

import com.shuttleverse.community.constants.SVEntityType;
import com.shuttleverse.community.constants.SVVerificationStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVOwnershipClaimResponse {

    private UUID id;

    private SVEntityType entityType;

    private UUID entityId;

    private String userNotes;

    private SVVerificationStatus status;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private SVUserResponse creator;

    private List<SVVerificationFileResponse> files;
}
