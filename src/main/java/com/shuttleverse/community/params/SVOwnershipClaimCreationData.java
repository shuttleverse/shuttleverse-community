package com.shuttleverse.community.params;

import com.shuttleverse.community.constants.SVEntityType;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SVOwnershipClaimCreationData {

  private SVEntityType entityType;
  private UUID entityId;
  private String userNotes;
  private List<MultipartFile> files;
}
