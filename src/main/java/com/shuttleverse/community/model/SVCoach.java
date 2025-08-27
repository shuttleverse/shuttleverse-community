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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "coach")
@Data
public class SVCoach extends SVBaseModel {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "club_id")
  private SVClub club;

  @Column(name = "experience_years")
  private Integer experienceYears;

  @Column(name = "other_contacts")
  @JdbcTypeCode(SqlTypes.JSON)
  @Convert(converter = MapToJsonConverter.class)
  private Map<String, String> otherContacts;

  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "coach_id")
  @OrderBy("upvotes DESC")
  private List<SVCoachSchedule> scheduleList;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "coach_id")
  @OrderBy("upvotes DESC")
  private List<SVCoachPrice> priceList;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private SVUser creator;
}