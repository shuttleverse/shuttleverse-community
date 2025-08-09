package com.shuttleverse.community.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "stringer")
@Data
public class SVStringer extends SVBaseModel {

  @ManyToOne
  @JoinColumn(name = "club_id")
  private SVClub club;

  @Column(name = "other_contacts", nullable = false)
  private String otherContacts;

  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Column(name = "additional_details")
  private String additionalDetails;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "stringer_id")
  @OrderBy("upvotes DESC")
  private List<SVStringerPrice> priceList;

  @ManyToOne
  @JoinColumn(name = "creator_id")
  private SVUser creator;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private SVUser owner;

}
