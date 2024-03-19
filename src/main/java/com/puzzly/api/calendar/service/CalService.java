package com.puzzly.api.calendar.service;

import com.puzzly.api.calendar.domain.CalReq;
import com.puzzly.api.calendar.domain.CalRes;
import com.puzzly.api.calendar.domain.PuzzlyCal;
import com.puzzly.api.calendar.repository.ActiveCalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalService {

    private final ModelMapper modelMapper;
    private final ActiveCalRepository activeCalRepository;

    public CalRes saveCal(CalReq req) {
        PuzzlyCal cal = modelMapper.map(req, PuzzlyCal.class);

        return modelMapper.map(activeCalRepository.save(cal), CalRes.class);
    }

    public CalRes findAll(CalReq req) {
        return modelMapper.map(activeCalRepository.findAll(), CalRes.class);
    }

    public CalRes findById(String req) {
        return modelMapper.map(activeCalRepository.findByCalId(req), CalRes.class);
    }

}
