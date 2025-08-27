package com.shuttleverse.community.model;

import com.shuttleverse.community.mapper.MapToJsonConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "court")
@Data
public class SVCourt extends SVBaseModel {

  @Column(name = "website")
  private String website;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "other_contacts")
  @JdbcTypeCode(SqlTypes.JSON)
  @Convert(converter = MapToJsonConverter.class)
  private Map<String, String> otherContacts;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "court_id")
  @OrderBy("upvotes DESC")
  private List<SVCourtSchedule> scheduleList;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "court_id")
  @OrderBy("upvotes DESC")
  private List<SVCourtPrice> priceList;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private SVUser creator;

}
