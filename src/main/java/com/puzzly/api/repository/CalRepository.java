package com.puzzly.api.repository;

import com.puzzly.api.entity._PrivateCalender_Deprecated;

import java.util.List;
import java.util.Optional;

public interface CalRepository {
    _PrivateCalender_Deprecated save(_PrivateCalender_Deprecated puzzlyCal);
    Optional<_PrivateCalender_Deprecated> findByCalId(String calId);
    List<_PrivateCalender_Deprecated> findAll();
}
