package com.puzzly.api.service;

import com.puzzly.api.dto.request.CalReq;
import com.puzzly.api.dto.response.CalRes;
import com.puzzly.api.entity._PrivateCalender_Deprecated;
import com.puzzly.api.repository.ActiveCalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
public class CalService {

    private final ModelMapper modelMapper;
    private final ActiveCalRepository activeCalRepository;

    public CalRes saveCal(CalReq req) {
        _PrivateCalender_Deprecated cal = modelMapper.map(req, _PrivateCalender_Deprecated.class);

        return null;
        //return modelMapper.map(activeCalRepository.save(cal), CalRes.class);
    }

    public CalRes findAll(CalReq req) {
        return null;
        //return modelMapper.map(activeCalRepository.findAll(), CalRes.class);
    }

    public CalRes findById(String req) {
        return null;
        //return modelMapper.map(activeCalRepository.findByCalId(req), CalRes.class);
    }

}
