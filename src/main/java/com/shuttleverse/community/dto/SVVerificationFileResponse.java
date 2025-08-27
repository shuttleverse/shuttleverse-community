package com.shuttleverse.community.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVVerificationFileResponse {

    private UUID id;

    private String fileName;

    private String fileUrl;
}
