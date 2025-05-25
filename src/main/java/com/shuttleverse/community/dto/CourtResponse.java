package com.shuttleverse.community.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtResponse extends BadmintonEntityDto {

  List<CourtScheduleResponse> scheduleList;
  List<CourtPriceResponse> priceList;
}
