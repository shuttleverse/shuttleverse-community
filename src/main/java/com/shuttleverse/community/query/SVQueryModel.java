package com.shuttleverse.community.query;

import com.shuttleverse.community.model.QSVClub;
import com.shuttleverse.community.model.QSVCoach;
import com.shuttleverse.community.model.QSVCoachPrice;
import com.shuttleverse.community.model.QSVCoachSchedule;
import com.shuttleverse.community.model.QSVCourt;
import com.shuttleverse.community.model.QSVCourtPrice;
import com.shuttleverse.community.model.QSVCourtSchedule;
import com.shuttleverse.community.model.QSVStringer;
import com.shuttleverse.community.model.QSVStringerPrice;
import com.shuttleverse.community.model.QSVUpvote;

public class SVQueryModel {

  public static final QSVClub club = QSVClub.sVClub;
  public static final QSVCourt court = QSVCourt.sVCourt;
  public static final QSVStringer stringer = QSVStringer.sVStringer;
  public static final QSVUpvote upvote = QSVUpvote.sVUpvote;
  public static final QSVCoach coach = QSVCoach.sVCoach;
  public static final QSVCoachSchedule coachSchedule = QSVCoachSchedule.sVCoachSchedule;
  public static final QSVCoachPrice coachPrice = QSVCoachPrice.sVCoachPrice;
  public static final QSVCourtSchedule courtSchedule = QSVCourtSchedule.sVCourtSchedule;
  public static final QSVCourtPrice courtPrice = QSVCourtPrice.sVCourtPrice;
  public static final QSVStringerPrice stringerPrice = QSVStringerPrice.sVStringerPrice;
}
