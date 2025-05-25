package com.shuttleverse.community.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringerResponse extends BadmintonEntityDto {

  private List<StringerPriceResponse> priceList;
}
